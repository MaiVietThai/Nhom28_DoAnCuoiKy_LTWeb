
package vn.edu.hcmute.storemanagement.repository;

import vn.edu.hcmute.storemanagement.entity.NewsArticle;
import vn.edu.hcmute.storemanagement.entity.NewsArticle.ArticleStatus;
import vn.edu.hcmute.storemanagement.entity.NewsArticle.ArticleType;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NewsArticleRepository
        extends JpaRepository<NewsArticle, Long> {

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    Optional<NewsArticle> findBySlugAndStatus(
            String slug, ArticleStatus status);

    List<NewsArticle> findByOrderByCreatedAtDesc();

    List<NewsArticle> findByTypeOrderByCreatedAtDesc(
            ArticleType type);

    @Query("""
        SELECT n FROM NewsArticle n
        WHERE n.status = :status
          AND (
              n.type = :newsType
              OR (
                  n.type = :promotionType
                  AND (n.startAt IS NULL OR n.startAt <= :now)
                  AND (n.endAt IS NULL OR n.endAt >= :now)
              )
          )
        ORDER BY n.createdAt DESC
        """)
    List<NewsArticle> findPublicArticles(
            @Param("status") ArticleStatus status,
            @Param("newsType") ArticleType newsType,
            @Param("promotionType") ArticleType promotionType,
            @Param("now") LocalDateTime now);

    @Query("""
        SELECT n FROM NewsArticle n
        WHERE n.status = :status
          AND n.featured = true
          AND (
              n.type = :newsType
              OR (
                  n.type = :promotionType
                  AND (n.startAt IS NULL OR n.startAt <= :now)
                  AND (n.endAt IS NULL OR n.endAt >= :now)
              )
          )
        ORDER BY n.updatedAt DESC
        """)
    List<NewsArticle> findFeaturedPublicArticles(
            @Param("status") ArticleStatus status,
            @Param("newsType") ArticleType newsType,
            @Param("promotionType") ArticleType promotionType,
            @Param("now") LocalDateTime now,
            Pageable pageable);
}
