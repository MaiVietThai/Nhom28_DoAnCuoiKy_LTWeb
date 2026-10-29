package vn.edu.hcmute.storemanagement.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.edu.hcmute.storemanagement.entity.Category;
import vn.edu.hcmute.storemanagement.entity.Product;
import vn.edu.hcmute.storemanagement.repository.CategoryRepository;
import vn.edu.hcmute.storemanagement.repository.InventoryRepository;
import vn.edu.hcmute.storemanagement.repository.ProductRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class ProductAdminController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;
    private final Path uploadPath;

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    public ProductAdminController(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            InventoryRepository inventoryRepository,
            @Value("${app.upload-dir:uploads/products}") String uploadDir
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryRepository = inventoryRepository;
        this.uploadPath = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    @GetMapping({"", "/"})
    public String dashboard() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/products")
    public String listProducts(Model model) {
        List<Product> products = productRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(
                        Product::getId,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();

        model.addAttribute("products", products);
        return "admin/products/list";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        prepareForm(model, null);
        return "admin/products/form";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirect
    ) {
        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            redirect.addFlashAttribute(
                    "errorMessage", "Không tìm thấy sản phẩm.");
            return "redirect:/admin/products";
        }

        prepareForm(model, product);
        return "admin/products/form";
    }

    private void prepareForm(Model model, Product product) {
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
    }

    @PostMapping("/products/save")
    public String saveProduct(
            @RequestParam(required = false) Long id,
            @RequestParam String sku,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam BigDecimal price,
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "false") boolean active,
            @RequestParam(required = false) MultipartFile image,
            RedirectAttributes redirect
    ) {
        sku = sku.trim();
        name = name.trim();

        if (sku.isBlank() || name.isBlank() || price.signum() <= 0) {
            redirect.addFlashAttribute(
                    "errorMessage",
                    "Mã sản phẩm, tên và giá phải hợp lệ; giá phải lớn hơn 0."
            );
            return id == null
                    ? "redirect:/admin/products/new"
                    : "redirect:/admin/products/edit/" + id;
        }

        boolean duplicate = id == null
                ? productRepository.existsBySku(sku)
                : productRepository.existsBySkuAndIdNot(sku, id);

        if (duplicate) {
            redirect.addFlashAttribute(
                    "errorMessage", "Mã SKU đã tồn tại.");
            return id == null
                    ? "redirect:/admin/products/new"
                    : "redirect:/admin/products/edit/" + id;
        }

        Category category = categoryRepository.findById(categoryId)
                .orElse(null);

        if (category == null) {
            redirect.addFlashAttribute(
                    "errorMessage", "Danh mục không tồn tại.");
            return id == null
                    ? "redirect:/admin/products/new"
                    : "redirect:/admin/products/edit/" + id;
        }

        Product product;

        if (id == null) {
            product = new Product(
                    sku, name, description, price, null, category
            );
        } else {
            product = productRepository.findById(id).orElse(null);

            if (product == null) {
                redirect.addFlashAttribute(
                        "errorMessage", "Không tìm thấy sản phẩm.");
                return "redirect:/admin/products";
            }

            product.setSku(sku);
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(category);
        }

        product.setActive(active);

        if (image != null && !image.isEmpty()) {
            try {
                String newImageUrl = saveImage(image);
                String oldImageUrl = product.getImageUrl();

                product.setImageUrl(newImageUrl);
                productRepository.save(product);

                try {
                    deleteLocalImage(oldImageUrl);
                } catch (IOException ex) {
                    redirect.addFlashAttribute(
                            "errorMessage",
                            "Đã lưu sản phẩm nhưng không xóa được ảnh cũ."
                    );
                    return "redirect:/admin/products";
                }

                redirect.addFlashAttribute(
                        "successMessage", "Đã lưu sản phẩm và ảnh.");
                return "redirect:/admin/products";

            } catch (IOException | IllegalArgumentException ex) {
                redirect.addFlashAttribute(
                        "errorMessage",
                        "Không thể tải ảnh. Hãy dùng ảnh JPG, PNG hoặc WEBP."
                );
                return id == null
                        ? "redirect:/admin/products/new"
                        : "redirect:/admin/products/edit/" + id;
            }
        }

        productRepository.save(product);

        redirect.addFlashAttribute(
                "successMessage", "Đã lưu thông tin sản phẩm.");
        return "redirect:/admin/products";
    }

    private String saveImage(MultipartFile image) throws IOException {
        String contentType = image.getContentType();

        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Loại ảnh không được hỗ trợ.");
        }

        if (image.getSize() > 5L * 1024 * 1024) {
            throw new IllegalArgumentException("Ảnh vượt quá 5 MB.");
        }

        String extension = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException(
                    "Loại ảnh không hợp lệ.");
        };

        Files.createDirectories(uploadPath);

        String filename = UUID.randomUUID() + extension;
        Path destination = uploadPath.resolve(filename).normalize();

        if (!destination.startsWith(uploadPath)) {
            throw new IllegalArgumentException("Đường dẫn ảnh không hợp lệ.");
        }

        image.transferTo(destination);

        return "/uploads/products/" + filename;
    }

    // Xóa một sản phẩm
    @PostMapping("/products/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteProduct(
            @PathVariable Long id,
            RedirectAttributes redirect
    ) {
        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            redirect.addFlashAttribute(
                    "errorMessage", "Không tìm thấy sản phẩm.");
            return "redirect:/admin/products";
        }

        String imageUrl = product.getImageUrl();

        try {
            inventoryRepository.deleteByProduct(product);
            productRepository.delete(product);
            productRepository.flush();

            try {
                deleteLocalImage(imageUrl);
            } catch (IOException ex) {
                redirect.addFlashAttribute(
                        "errorMessage",
                        "Đã xóa sản phẩm nhưng không xóa được tệp ảnh.");
                return "redirect:/admin/products";
            }

            redirect.addFlashAttribute(
                    "successMessage", "Đã xóa sản phẩm.");

        } catch (DataIntegrityViolationException ex) {
            redirect.addFlashAttribute(
                    "errorMessage",
                    "Không thể xóa sản phẩm vì đang được dữ liệu khác tham chiếu."
            );
        }

        return "redirect:/admin/products";
    }

    // Xóa nhiều sản phẩm cùng lúc
    @PostMapping("/products/delete-selected")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String deleteSelectedProducts(
            @RequestParam(value = "ids", required = false) List<Long> ids,
            RedirectAttributes redirect
    ) {
        if (ids == null || ids.isEmpty()) {
            redirect.addFlashAttribute(
                    "errorMessage", "Vui lòng chọn ít nhất một sản phẩm.");
            return "redirect:/admin/products";
        }

        // Loại bỏ ID trùng lặp trong yêu cầu gửi lên.
        Set<Long> uniqueIds = new HashSet<>(ids);
        List<Product> productsToDelete = new ArrayList<>();
        List<String> imageUrls = new ArrayList<>();

        for (Long id : uniqueIds) {
            if (id == null) {
                continue;
            }

            Product product = productRepository.findById(id).orElse(null);

            if (product != null) {
                productsToDelete.add(product);
                imageUrls.add(product.getImageUrl());
            }
        }

        if (productsToDelete.isEmpty()) {
            redirect.addFlashAttribute(
                    "errorMessage",
                    "Không tìm thấy sản phẩm hợp lệ để xóa.");
            return "redirect:/admin/products";
        }

        try {
            inventoryRepository.deleteByProductIds(uniqueIds);
            productRepository.deleteAll(productsToDelete);
            productRepository.flush();

        } catch (DataIntegrityViolationException ex) {
            redirect.addFlashAttribute(
                    "errorMessage",
                    "Không thể xóa các sản phẩm đã chọn vì có sản phẩm đang được dữ liệu khác tham chiếu. Không có sản phẩm nào được xác nhận xóa."
            );
            return "redirect:/admin/products";
        }

        int deletedCount = productsToDelete.size();
        int imageErrorCount = 0;

        for (String imageUrl : imageUrls) {
            try {
                deleteLocalImage(imageUrl);
            } catch (IOException ex) {
                imageErrorCount++;
            }
        }

        if (imageErrorCount == 0) {
            redirect.addFlashAttribute(
                    "successMessage",
                    "Đã xóa thành công " + deletedCount + " sản phẩm.");
        } else {
            redirect.addFlashAttribute(
                    "successMessage",
                    "Đã xóa " + deletedCount + " sản phẩm. Có "
                            + imageErrorCount + " tệp ảnh chưa xóa được.");
        }

        return "redirect:/admin/products";
    }

    private void deleteLocalImage(String imageUrl) throws IOException {
        if (imageUrl == null
                || !imageUrl.startsWith("/uploads/products/")) {
            return;
        }

        String filename = imageUrl.substring(
                "/uploads/products/".length()
        );

        // Chỉ chấp nhận tên tệp, không cho phép đường dẫn con.
        if (filename.isBlank()
                || filename.contains("/")
                || filename.contains("\\")
                || filename.equals(".")
                || filename.equals("..")) {
            return;
        }

        Path file = uploadPath.resolve(filename).normalize();

        if (file.startsWith(uploadPath)) {
            Files.deleteIfExists(file);
        }
    }
}
