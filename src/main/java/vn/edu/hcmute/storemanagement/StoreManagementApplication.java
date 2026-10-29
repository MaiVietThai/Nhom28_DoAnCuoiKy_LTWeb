package vn.edu.hcmute.storemanagement;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.hcmute.storemanagement.entity.AppRole;
import vn.edu.hcmute.storemanagement.entity.AppUser;
import vn.edu.hcmute.storemanagement.entity.Branch;
import vn.edu.hcmute.storemanagement.entity.Category;
import vn.edu.hcmute.storemanagement.entity.Inventory;
import vn.edu.hcmute.storemanagement.entity.Product;
import vn.edu.hcmute.storemanagement.repository.AppRoleRepository;
import vn.edu.hcmute.storemanagement.repository.AppUserRepository;
import vn.edu.hcmute.storemanagement.repository.BranchRepository;
import vn.edu.hcmute.storemanagement.repository.CategoryRepository;
import vn.edu.hcmute.storemanagement.repository.InventoryRepository;
import vn.edu.hcmute.storemanagement.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Set;

@SpringBootApplication
public class StoreManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(StoreManagementApplication.class, args);
    }

    @Bean
    CommandLineRunner seedData(
            AppRoleRepository roleRepository,
            AppUserRepository userRepository,
            BranchRepository branchRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            InventoryRepository inventoryRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            AppRole adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> roleRepository.save(new AppRole("ADMIN")));
            roleRepository.findByName("MANAGER").orElseGet(() -> roleRepository.save(new AppRole("MANAGER")));
            roleRepository.findByName("STAFF").orElseGet(() -> roleRepository.save(new AppRole("STAFF")));
            AppRole customerRole = roleRepository.findByName("CUSTOMER").orElseGet(() -> roleRepository.save(new AppRole("CUSTOMER")));

            var existingAdmin = userRepository.findByUsername("admin");
            if (existingAdmin.isEmpty()) {
                AppUser admin = new AppUser();
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setFullName("Quản trị viên");
                admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
                admin.setActive(true);
                admin.setRoles(new java.util.HashSet<>(Set.of(adminRole, customerRole)));
                userRepository.save(admin);
            } else {
                AppUser admin = existingAdmin.get();
                if (admin.getRoles().stream().noneMatch(r -> "CUSTOMER".equals(r.getName()))) {
                    admin.getRoles().add(customerRole);
                    userRepository.save(admin);
                }
            }

            Branch branch = branchRepository.findByBranchCode("HCM-Q1")
                    .orElseGet(() -> branchRepository.save(
                            new Branch("HCM-Q1", "Chi nhánh Quận 1", "123 Đường mẫu, Quận 1, TP.HCM")));

            Category category = categoryRepository.findByName("Mô hình Anime")
                    .orElseGet(() -> categoryRepository.save(
                            new Category("Mô hình Anime", "Mô hình sưu tầm anime và manga")));

            Object[][] figures = {
                    {"NF-DB-001", "Son Goku - Super Saiyan", "Mô hình Goku trong trạng thái Super Saiyan, dành cho fan Dragon Ball.", "699000"},
                    {"NF-DB-002", "Vegeta - Prince of Saiyans", "Tái hiện hoàng tử Saiyan Vegeta với phong thái chiến binh.", "649000"},
                    {"NF-OP-001", "Monkey D. Luffy - Future Pirate King", "Thuyền trưởng Mũ Rơm, biểu tượng của tự do và những chuyến phiêu lưu.", "729000"},
                    {"NF-OP-002", "Roronoa Zoro - Three Sword Style", "Kiếm sĩ băng Mũ Rơm với phong cách tam kiếm đặc trưng.", "679000"},
                    {"NF-NA-001", "Uzumaki Naruto - The Seventh Hokage", "Ninja làng Lá với ý chí không bao giờ bỏ cuộc.", "599000"},
                    {"NF-NA-002", "Uchiha Sasuke - Shadow Shinobi", "Sasuke Uchiha, chiến binh với sức mạnh và khí chất lạnh lùng.", "629000"},
                    {"NF-OT-001", "Gojo Satoru - Limitless", "Mô hình lấy cảm hứng từ chú thuật sư Gojo Satoru.", "759000"},
                    {"NF-OT-002", "Tanjiro Kamado - Demon Slayer", "Thợ săn quỷ Tanjiro với tinh thần kiên định và lòng trắc ẩn.", "589000"}
            };
            int stock = 24;
            for (Object[] figure : figures) {
                String sku = (String) figure[0];
                if (!productRepository.existsBySku(sku)) {
                    Product product = productRepository.save(new Product(
                            sku,
                            (String) figure[1],
                            (String) figure[2],
                            new BigDecimal((String) figure[3]),
                            null,
                            category));
                    inventoryRepository.save(new Inventory(branch, product, stock--));
                }
            }
        };
    }
}
