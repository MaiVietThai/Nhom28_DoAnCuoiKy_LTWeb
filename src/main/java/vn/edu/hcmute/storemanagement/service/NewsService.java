
package vn.edu.hcmute.storemanagement.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import vn.edu.hcmute.storemanagement.entity.NewsArticle;
import vn.edu.hcmute.storemanagement.entity.NewsArticle.ArticleStatus;
import vn.edu.hcmute.storemanagement.entity.NewsArticle.ArticleType;
import vn.edu.hcmute.storemanagement.repository.NewsArticleRepository;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class NewsService {

    private final NewsArticleRepository repository;

    public NewsService(NewsArticleRepository repository) {
        this.repository = repository;
    }

    // Danh sách bài viết dành cho trang quản trị
    @Transactional(readOnly = true)
    public List<NewsArticle> findAll() {
        return repository.findAll(
                org.springframework.data.domain.Sort
                        .by(org.springframework.data.domain.Sort.Direction.DESC,
                                "createdAt")
        );
    }

    // Tìm một bài viết theo ID
    @Transactional(readOnly = true)
    public NewsArticle findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Không tìm thấy bài viết có ID: " + id));
    }

    // Danh sách bài viết công khai trên trang chủ
    @Transactional(readOnly = true)
    public List<NewsArticle> findPublicArticles() {
        return repository.findPublicArticles(
                ArticleStatus.PUBLISHED,
                ArticleType.NEWS,
                ArticleType.PROMOTION,
                LocalDateTime.now()
        );
    }

    // Lấy bài viết nổi bật làm banner
    @Transactional(readOnly = true)
    public List<NewsArticle> findFeaturedArticles() {
        return repository.findFeaturedPublicArticles(
                ArticleStatus.PUBLISHED,
                ArticleType.NEWS,
                ArticleType.PROMOTION,
                LocalDateTime.now(),
                PageRequest.of(0, 1)
        );
    }

    // Tìm bài viết theo slug để hiển thị trang chi tiết
    @Transactional(readOnly = true)
    public NewsArticle findPublishedBySlug(String slug) {
        NewsArticle article = repository
                .findBySlugAndStatus(slug, ArticleStatus.PUBLISHED)
                .orElseThrow(() ->
                        new EntityNotFoundException("Không tìm thấy bài viết."));

        if (!isCurrentlyVisible(article, LocalDateTime.now())) {
            throw new EntityNotFoundException(
                    "Bài viết hoặc chương trình khuyến mãi không còn hiệu lực.");
        }

        return article;
    }

    // Tạo mới bài viết
    @Transactional
    public NewsArticle create(NewsArticle article) {
        validate(article);
        article.setSlug(createUniqueSlug(article.getTitle(), null));

        if (article.getStatus() == null) {
            article.setStatus(ArticleStatus.DRAFT);
        }

        if (article.getType() == null) {
            article.setType(ArticleType.NEWS);
        }

        if (article.isFeatured()) {
            clearFeatured();
        }

        return repository.save(article);
    }

    // Cập nhật bài viết, giữ lại ảnh cũ nếu không tải ảnh mới
    @Transactional
    public NewsArticle update(Long id, NewsArticle form) {
        NewsArticle article = findById(id);

        validate(form);

        article.setTitle(form.getTitle());
        article.setSlug(createUniqueSlug(form.getTitle(), id));
        article.setSummary(form.getSummary());
        article.setContent(form.getContent());
        article.setType(form.getType() == null
                ? ArticleType.NEWS : form.getType());
        article.setStatus(form.getStatus() == null
                ? ArticleStatus.DRAFT : form.getStatus());
        article.setStartAt(form.getStartAt());
        article.setEndAt(form.getEndAt());
        article.setFeatured(form.isFeatured());

        if (StringUtils.hasText(form.getImageUrl())) {
            article.setImageUrl(form.getImageUrl());
        }

        if (article.isFeatured()) {
            clearFeaturedExcept(id);
        }

        return repository.save(article);
    }

    // Xóa bài viết
    @Transactional
    public void delete(Long id) {
        NewsArticle article = findById(id);
        repository.delete(article);
    }

    private void validate(NewsArticle article) {
        if (article == null) {
            throw new IllegalArgumentException("Dữ liệu bài viết không hợp lệ.");
        }

        if (!StringUtils.hasText(article.getTitle())) {
            throw new IllegalArgumentException("Tiêu đề không được để trống.");
        }

        if (article.getTitle().trim().length() > 255) {
            throw new IllegalArgumentException(
                    "Tiêu đề không được vượt quá 255 ký tự.");
        }

        if (article.getType() == ArticleType.PROMOTION) {
            if (article.getStartAt() != null
                    && article.getEndAt() != null
                    && article.getEndAt().isBefore(article.getStartAt())) {
                throw new IllegalArgumentException(
                        "Thời gian kết thúc phải sau thời gian bắt đầu.");
            }
        }
    }

    private String createUniqueSlug(String title, Long currentId) {
        String baseSlug = toSlug(title);

        if (!StringUtils.hasText(baseSlug)) {
            baseSlug = "bai-viet";
        }

        String slug = baseSlug;
        int suffix = 2;

        while (slugExists(slug, currentId)) {
            slug = baseSlug + "-" + suffix++;
        }

        return slug;
    }

    private boolean slugExists(String slug, Long currentId) {
        if (currentId == null) {
            return repository.existsBySlug(slug);
        }

        return repository.existsBySlugAndIdNot(slug, currentId);
    }

    private String toSlug(String value) {
        String normalized = Normalizer.normalize(
                value.trim().toLowerCase(Locale.ROOT),
                Normalizer.Form.NFD
        );

        normalized = normalized.replaceAll("\\p{M}", "");
        normalized = normalized.replace('đ', 'd');

        return normalized
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");
    }

    private boolean isCurrentlyVisible(
            NewsArticle article, LocalDateTime now) {

        if (article.getType() != ArticleType.PROMOTION) {
            return true;
        }

        return (article.getStartAt() == null
                || !article.getStartAt().isAfter(now))
                && (article.getEndAt() == null
                || !article.getEndAt().isBefore(now));
    }

    private void clearFeatured() {
        clearFeaturedExcept(null);
    }

    private void clearFeaturedExcept(Long excludedId) {
        List<NewsArticle> articles = repository.findAll();

        for (NewsArticle item : articles) {
            if (item.isFeatured()
                    && (excludedId == null
                    || !item.getId().equals(excludedId))) {
                item.setFeatured(false);
            }
        }

        repository.saveAll(articles);
    }
}
