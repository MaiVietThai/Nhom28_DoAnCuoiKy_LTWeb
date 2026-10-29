package vn.edu.hcmute.storemanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.hcmute.storemanagement.repository.ProductRepository;

@Controller
public class HomeController {
    private final ProductRepository productRepository;

    public HomeController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/")
    public String home(@RequestParam(value = "view", required = false) String view, Model model) {
        if ("wishlist".equalsIgnoreCase(view)) {
            return "redirect:/wishlist";
        }
        model.addAttribute("products", productRepository.findByActiveTrueOrderByIdDesc());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
