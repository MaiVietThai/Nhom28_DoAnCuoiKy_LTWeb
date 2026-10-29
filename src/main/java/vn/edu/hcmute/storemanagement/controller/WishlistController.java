package vn.edu.hcmute.storemanagement.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.hcmute.storemanagement.entity.Product;
import vn.edu.hcmute.storemanagement.repository.ProductRepository;

import java.util.*;

@Controller
public class WishlistController {

    private final ProductRepository productRepository;

    public WishlistController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @SuppressWarnings("unchecked")
    private Set<Long> getWishlist(HttpSession session) {
        Set<Long> wishlist = (Set<Long>) session.getAttribute("wishlist");
        if (wishlist == null) {
            wishlist = new LinkedHashSet<>();
            session.setAttribute("wishlist", wishlist);
        }
        return wishlist;
    }

    @GetMapping("/wishlist")
    public String wishlist(HttpSession session, Model model) {
        var products = new ArrayList<Product>();
        for (Long id : getWishlist(session)) {
            productRepository.findById(id).ifPresent(products::add);
        }
        model.addAttribute("wishlistProducts", products);
        model.addAttribute("wishlistCount", products.size());
        return "wishlist";
    }

    @PostMapping("/wishlist/toggle/{id}")
    @ResponseBody
    public ResponseEntity<?> toggleWishlistJson(
            @PathVariable Long id,
            HttpSession session,
            HttpServletRequest request,
            @RequestHeader(value = "Accept", defaultValue = "") String accept,
            @RequestHeader(value = "X-Requested-With", defaultValue = "") String requestedWith) {

        boolean isAjax = accept.contains("application/json") || "XMLHttpRequest".equalsIgnoreCase(requestedWith);

        boolean added = false;
        Set<Long> wishlist = getWishlist(session);
        if (productRepository.findById(id).isPresent()) {
            if (wishlist.contains(id)) {
                wishlist.remove(id);
                added = false;
            } else {
                wishlist.add(id);
                added = true;
            }
        }

        if (isAjax) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("inWishlist", added);
            response.put("count", wishlist.size());
            return ResponseEntity.ok(response);
        }

        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            return ResponseEntity.status(302).header("Location", referer).build();
        }
        return ResponseEntity.status(302).header("Location", "/wishlist").build();
    }

    @PostMapping("/wishlist/remove/{id}")
    public String removeWishlist(
            @PathVariable Long id,
            HttpSession session) {
        getWishlist(session).remove(id);
        return "redirect:/wishlist";
    }
}
