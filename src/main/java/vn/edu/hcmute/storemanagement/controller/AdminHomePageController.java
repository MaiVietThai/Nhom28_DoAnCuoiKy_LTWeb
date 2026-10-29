package vn.edu.hcmute.storemanagement.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.edu.hcmute.storemanagement.entity.HomePageContent;
import vn.edu.hcmute.storemanagement.repository.HomePageContentRepository;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminHomePageController {

    private final HomePageContentRepository repository;

    public AdminHomePageController(HomePageContentRepository repository) {
        this.repository = repository;
    }

    private HomePageContent getOrCreateContent() {
        return repository.findFirstByOrderByIdAsc()
                .orElseGet(() -> {
                    HomePageContent c = new HomePageContent();
                    c.setAnnouncement("FIGUREVERSE // COLLECT YOUR UNIVERSE");
                    c.setHeroEyebrow("THE NEXT LEVEL OF COLLECTING");
                    c.setHeroTitle("YOUR ANIME. YOUR UNIVERSE.");
                    c.setHeroDescription("Không chỉ là mô hình. Đó là những huyền thoại bạn mang về thế giới của mình.");
                    c.setHeroButtonText("Khám phá bộ sưu tập");
                    c.setHeroButtonLink("#products");
                    c.setCollectionEyebrow("PICK YOUR WORLD");
                    c.setCollectionTitle("CHOOSE YOUR UNIVERSE");
                    c.setCollectionDescription("Mỗi vũ trụ, một huyền thoại. Chọn series bạn yêu thích.");
                    c.setProductEyebrow("THE COLLECTOR'S DROP");
                    c.setProductTitle("FEATURED FIGURES");
                    c.setProductDescription("Những nhân vật xứng đáng có một vị trí trong bộ sưu tập của bạn.");
                    c.setAboutEyebrow("BUILT FOR FANS, POWERED BY PASSION");
                    c.setAboutTitle("YOUR SHELF. YOUR STORY.");
                    c.setAboutDescription("Khởi đầu bộ sưu tập của bạn ngay hôm nay — một nhân vật, một câu chuyện, một thế giới.");
                    c.setFooterTagline("Made for the ones who never stopped dreaming.");
                    return repository.save(c);
                });
    }

    @GetMapping("/admin/products/homepage")
    public String editHomePage(Model model) {
        HomePageContent content = getOrCreateContent();
        model.addAttribute("homeContent", content);
        return "admin/products/homepage";
    }

    @PostMapping("/admin/products/homepage")
    public String updateHomePage(
            @ModelAttribute("homeContent") HomePageContent form,
            RedirectAttributes redirectAttributes) {

        HomePageContent content = getOrCreateContent();

        content.setAnnouncement(form.getAnnouncement());
        content.setHeroEyebrow(form.getHeroEyebrow());
        content.setHeroTitle(form.getHeroTitle());
        content.setHeroDescription(form.getHeroDescription());
        content.setHeroButtonText(form.getHeroButtonText());
        content.setHeroButtonLink(form.getHeroButtonLink());

        content.setCollectionEyebrow(form.getCollectionEyebrow());
        content.setCollectionTitle(form.getCollectionTitle());
        content.setCollectionDescription(form.getCollectionDescription());

        content.setProductEyebrow(form.getProductEyebrow());
        content.setProductTitle(form.getProductTitle());
        content.setProductDescription(form.getProductDescription());

        content.setAboutEyebrow(form.getAboutEyebrow());
        content.setAboutTitle(form.getAboutTitle());
        content.setAboutDescription(form.getAboutDescription());

        content.setFooterTagline(form.getFooterTagline());

        repository.save(content);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Đã lưu nội dung trang chủ thành công!"
        );

        return "redirect:/admin/products/homepage";
    }
}
