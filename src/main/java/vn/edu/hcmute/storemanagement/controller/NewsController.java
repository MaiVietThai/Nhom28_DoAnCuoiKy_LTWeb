
package vn.edu.hcmute.storemanagement.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.edu.hcmute.storemanagement.entity.NewsArticle;
import vn.edu.hcmute.storemanagement.entity.NewsArticle.ArticleStatus;
import vn.edu.hcmute.storemanagement.entity.NewsArticle.ArticleType;
import vn.edu.hcmute.storemanagement.service.CloudinaryService;
import vn.edu.hcmute.storemanagement.service.NewsService;

import java.io.IOException;

@Controller
public class NewsController {

    private final NewsService newsService;
    private final CloudinaryService cloudinaryService;

    public NewsController(
            NewsService newsService,
            CloudinaryService cloudinaryService) {
        this.newsService = newsService;
        this.cloudinaryService = cloudinaryService;
    }

    // =========================
    // QUẢN TRỊ TIN TỨC
    // =========================

    @GetMapping("/admin/products/news")
    public String adminNews(Model model) {
        model.addAttribute("articles", newsService.findAll());
        return "admin/products/news/list";
    }

    @GetMapping("/admin/products/news/new")
    public String createForm(Model model) {
        model.addAttribute("article", new NewsArticle());
        addFormOptions(model);
        return "admin/products/news/form";
    }

    @GetMapping("/admin/products/news/edit/{id}")
    public String editForm(
            @PathVariable Long id,
            Model model) {
        model.addAttribute("article", newsService.findById(id));
        addFormOptions(model);
        return "admin/products/news/form";
    }

    @PostMapping("/admin/products/news/save")
    public String save(
            @ModelAttribute("article") NewsArticle article,
            @RequestParam(value = "imageFile", required = false)
            MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                article.setImageUrl(
                        cloudinaryService.uploadNewsImage(imageFile));
            }

            if (article.getId() == null) {
                newsService.create(article);
                redirectAttributes.addFlashAttribute(
                        "successMessage", "Đã tạo bài viết thành công!");
            } else {
                newsService.update(article.getId(), article);
                redirectAttributes.addFlashAttribute(
                        "successMessage", "Đã cập nhật bài viết thành công!");
            }

            return "redirect:/admin/products/news";

        } catch (IllegalArgumentException | IOException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            addFormOptions(model);
            return "admin/products/news/form";
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Không tìm thấy bài viết cần cập nhật.");
            return "redirect:/admin/products/news";
        }
    }

    @PostMapping("/admin/products/news/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            newsService.delete(id);
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Đã xóa bài viết thành công!");
        } catch (EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Bài viết không tồn tại.");
        }

        return "redirect:/admin/products/news";
    }

    private void addFormOptions(Model model) {
        model.addAttribute("articleTypes", ArticleType.values());
        model.addAttribute("articleStatuses", ArticleStatus.values());
    }

    // =========================
    // TRANG CÔNG KHAI
    // =========================

    @GetMapping("/news/{slug}")
    public String articleDetail(
            @PathVariable String slug,
            Model model) {
        try {
            model.addAttribute(
                    "article", newsService.findPublishedBySlug(slug));
            return "news/detail";
        } catch (EntityNotFoundException ex) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy bài viết.");
        }
    }
}
