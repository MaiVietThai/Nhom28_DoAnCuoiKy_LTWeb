package vn.edu.hcmute.storemanagement.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.hcmute.storemanagement.entity.AppRole;
import vn.edu.hcmute.storemanagement.entity.AppUser;
import vn.edu.hcmute.storemanagement.repository.AppRoleRepository;
import vn.edu.hcmute.storemanagement.repository.AppUserRepository;

import java.util.Set;

@Controller
public class AuthController {

    private final AppUserRepository userRepository;
    private final AppRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AppUserRepository userRepository,
            AppRoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String fullName,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        username = username != null ? username.trim() : "";
        email = email != null ? email.trim().toLowerCase() : "";
        fullName = fullName != null ? fullName.trim() : "";

        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("fullName", fullName);

        if (username.isBlank() || email.isBlank() || fullName.isBlank()
                || password.isBlank() || confirmPassword.isBlank()) {
            model.addAttribute("error", "Vui lòng nhập đầy đủ thông tin.");
            return "register";
        }

        if (username.length() > 50 || fullName.length() > 150
                || email.length() > 255) {
            model.addAttribute("error", "Thông tin nhập vào quá dài.");
            return "register";
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            model.addAttribute("error", "Email không hợp lệ.");
            return "register";
        }

        if (password.length() < 8) {
            model.addAttribute("error", "Mật khẩu phải có ít nhất 8 ký tự.");
            return "register";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp.");
            return "register";
        }

        if (userRepository.existsByUsername(username)) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại.");
            return "register";
        }

        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email đã được sử dụng.");
            return "register";
        }

        AppRole customerRole = roleRepository.findByName("CUSTOMER")
                .orElseGet(() -> roleRepository.save(new AppRole("CUSTOMER")));

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setActive(true);
        user.setRoles(Set.of(customerRole));

        userRepository.save(user);

        return "redirect:/login?registered";
    }
}
