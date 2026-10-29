package vn.edu.hcmute.storemanagement.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.hcmute.storemanagement.entity.AppUser;
import vn.edu.hcmute.storemanagement.entity.CustomerFeedback;
import vn.edu.hcmute.storemanagement.repository.AppUserRepository;
import vn.edu.hcmute.storemanagement.repository.CustomerFeedbackRepository;

import java.util.List;

@Controller
public class FeedbackController {
    private static final List<String> CATEGORIES = List.of(
            "Đánh giá sản phẩm", "Giao hàng và đóng gói", "Chăm sóc khách hàng",
            "Góp ý website", "Khiếu nại", "Góp ý chung"
    );

    private final CustomerFeedbackRepository feedback;
    private final AppUserRepository users;

    public FeedbackController(CustomerFeedbackRepository feedback, AppUserRepository users) {
        this.feedback = feedback;
        this.users = users;
    }

    @GetMapping("/feedback")
    public String form(Authentication auth, Model model) {
        model.addAttribute("myFeedbacks", feedback.findByUsernameOrderByCreatedAtDesc(auth.getName()));
        return "feedback";
    }

    @PostMapping("/feedback")
    public String submit(@RequestParam String message,
                         @RequestParam(defaultValue = "5") int rating,
                         @RequestParam(defaultValue = "Góp ý chung") String category,
                         Authentication auth,
                         RedirectAttributes ra) {
        String cleaned = message == null ? "" : message.trim();
        if (cleaned.isBlank() || cleaned.length() > 1200 || rating < 1 || rating > 5 || !CATEGORIES.contains(category)) {
            ra.addFlashAttribute("errorMessage", "Vui lòng kiểm tra loại phản hồi, nội dung (1–1200 ký tự) và số sao (1–5).");
            return "redirect:/feedback";
        }
        AppUser user = users.findByUsername(auth.getName()).orElse(null);
        CustomerFeedback f = new CustomerFeedback();
        f.setUsername(auth.getName());
        f.setDisplayName(user != null && user.getFullName() != null ? user.getFullName() : auth.getName());
        f.setCategory(category);
        f.setMessage(cleaned);
        f.setRating(rating);
        feedback.save(f);
        ra.addFlashAttribute("successMessage", "Cảm ơn bạn đã gửi phản hồi! Bạn có thể theo dõi trạng thái xử lý bên dưới.");
        return "redirect:/feedback";
    }
}
