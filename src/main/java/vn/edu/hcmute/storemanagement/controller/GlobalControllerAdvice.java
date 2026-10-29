package vn.edu.hcmute.storemanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vn.edu.hcmute.storemanagement.entity.HomePageContent;
import vn.edu.hcmute.storemanagement.repository.HomePageContentRepository;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final HomePageContentRepository homePageContentRepository;

    public GlobalControllerAdvice(HomePageContentRepository homePageContentRepository) {
        this.homePageContentRepository = homePageContentRepository;
    }

    @ModelAttribute("homeContent")
    public HomePageContent homeContent() {
        return homePageContentRepository.findFirstByOrderByIdAsc()
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
                    return c;
                });
    }

    @SuppressWarnings("unchecked")
    @ModelAttribute("cartCount")
    public int cartCount(HttpSession session) {
        if (session == null) {
            return 0;
        }
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return 0;
        }
        return cart.values().stream().filter(q -> q != null && q > 0).mapToInt(Integer::intValue).sum();
    }

    @SuppressWarnings("unchecked")
    @ModelAttribute("wishlistCount")
    public int wishlistCount(HttpSession session) {
        if (session == null) {
            return 0;
        }
        Set<Long> wishlist = (Set<Long>) session.getAttribute("wishlist");
        if (wishlist == null) {
            return 0;
        }
        return wishlist.size();
    }

    @SuppressWarnings("unchecked")
    @ModelAttribute("wishlistIds")
    public Set<Long> wishlistIds(HttpSession session) {
        if (session == null) {
            return Collections.emptySet();
        }
        Set<Long> wishlist = (Set<Long>) session.getAttribute("wishlist");
        if (wishlist == null) {
            return Collections.emptySet();
        }
        return wishlist;
    }
}
