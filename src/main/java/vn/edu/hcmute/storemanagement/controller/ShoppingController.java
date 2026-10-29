
package vn.edu.hcmute.storemanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.hcmute.storemanagement.entity.AppUser;
import vn.edu.hcmute.storemanagement.entity.Product;
import vn.edu.hcmute.storemanagement.entity.ShopOrder;
import vn.edu.hcmute.storemanagement.repository.ShopOrderRepository;
import vn.edu.hcmute.storemanagement.repository.AppUserRepository;
import vn.edu.hcmute.storemanagement.repository.ProductRepository;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class ShoppingController {

    private final ProductRepository productRepository;
    private final AppUserRepository userRepository;
    private final ShopOrderRepository orderRepository;

    public ShoppingController(
            ProductRepository productRepository,
            AppUserRepository userRepository, ShopOrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getCart(HttpSession session) {
        Map<Long, Integer> cart =
                (Map<Long, Integer>) session.getAttribute("cart");

        if (cart == null) {
            cart = new LinkedHashMap<>();
            session.setAttribute("cart", cart);
        }

        return cart;
    }

    @SuppressWarnings("unchecked")
    private List<OrderSummary> getOrders(HttpSession session) {
        List<OrderSummary> orders =
                (List<OrderSummary>) session.getAttribute("orders");

        if (orders == null) {
            orders = new ArrayList<>();
            session.setAttribute("orders", orders);
        }

        return orders;
    }

    @GetMapping("/cart")
    public String cart(HttpSession session, Model model) {
        Map<Long, Integer> cart = getCart(session);
        List<CartLine> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;

        for (var entry : cart.entrySet()) {
            Product product = productRepository
                    .findById(entry.getKey()).orElse(null);

            if (product != null) {
                int quantity = entry.getValue();
                items.add(new CartLine(product, quantity));

                total = total.add(
                        product.getPrice()
                                .multiply(BigDecimal.valueOf(quantity))
                );

                count += quantity;
            }
        }

        model.addAttribute("cartItems", items);
        model.addAttribute("cartCount", count);
        model.addAttribute("total", total);

        return "cart";
    }

    @PostMapping("/cart/add/{id}")
    public String addToCart(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (productRepository.findById(id).isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Không tìm thấy sản phẩm."
            );
            return "redirect:/";
        }

        int addQty = Math.max(1, Math.min(quantity, 99));
        Map<Long, Integer> cart = getCart(session);

        cart.merge(id, addQty, (oldQty, newQty) ->
                Math.min(oldQty + newQty, 99));

        redirectAttributes.addFlashAttribute(
                "successMessage", "Đã thêm sản phẩm vào giỏ hàng."
        );

        return "redirect:/cart";
    }

    @PostMapping("/cart/update/{id}")
    public String updateCart(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int quantity,
            HttpSession session) {

        Map<Long, Integer> cart = getCart(session);

        if (quantity <= 0) {
            cart.remove(id);
        } else {
            cart.put(id, Math.min(quantity, 99));
        }

        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{id}")
    public String removeFromCart(
            @PathVariable Long id,
            HttpSession session) {

        getCart(session).remove(id);
        return "redirect:/cart";
    }

    @PostMapping("/cart/checkout")
    public String checkout(
            @RequestParam(required = false) String receiverName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String note,
            @RequestParam(required = false) String paymentMethod,
            Authentication authentication,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Map<Long, Integer> cart = getCart(session);

        if (cart.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Giỏ hàng của bạn đang trống."
            );
            return "redirect:/cart";
        }

        if (!"COD".equals(paymentMethod)
                && !"VNPAY".equals(paymentMethod)) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Vui lòng chọn phương thức thanh toán hợp lệ."
            );
            return "redirect:/cart";
        }

        List<CartLine> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        int totalQty = 0;

        for (var entry : cart.entrySet()) {
            Product product = productRepository
                    .findById(entry.getKey()).orElse(null);

            if (product != null) {
                int quantity = entry.getValue();

                if (quantity <= 0) {
                    continue;
                }

                orderItems.add(new CartLine(product, quantity));

                total = total.add(
                        product.getPrice()
                                .multiply(BigDecimal.valueOf(quantity))
                );

                totalQty += quantity;
            }
        }

        if (orderItems.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Không tìm thấy sản phẩm hợp lệ trong giỏ hàng."
            );
            return "redirect:/cart";
        }

        String customerName = receiverName;

        if ((customerName == null || customerName.isBlank())
                && authentication != null) {

            customerName = authentication.getName();

            Optional<AppUser> userOpt =
                    userRepository.findByUsername(authentication.getName());

            if (userOpt.isPresent()
                    && userOpt.get().getFullName() != null
                    && !userOpt.get().getFullName().isBlank()) {
                customerName = userOpt.get().getFullName();
            }
        }

        if (customerName == null || customerName.isBlank()) {
            customerName = "Khách hàng";
        }

        if (phone == null || phone.isBlank()
                || address == null || address.isBlank()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Vui lòng nhập số điện thoại và địa chỉ nhận hàng."
            );
            return "redirect:/cart";
        }

        String orderCode = "NF-" + UUID.randomUUID()
                .toString().substring(0, 8).toUpperCase();

        OrderSummary order = new OrderSummary(
                orderCode,
                LocalDateTime.now(),
                orderItems,
                total,
                totalQty,
                customerName.trim(),
                phone.trim(),
                address.trim(),
                note != null ? note.trim() : "",
                "Đang xử lý",
                paymentMethod,
                null,
                null
        );

        getOrders(session).add(0, order);
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập bằng tài khoản khách hàng trước khi đặt hàng.");
            return "redirect:/login";
        }
        ShopOrder savedOrder = new ShopOrder();
        savedOrder.setOrderCode(orderCode);
        savedOrder.setCustomerUsername(authentication.getName());
        savedOrder.setCustomerName(customerName.trim());
        savedOrder.setPhone(phone.trim());
        savedOrder.setAddress(address.trim());
        savedOrder.setNote(note != null ? note.trim() : "");
        savedOrder.setTotal(total);
        savedOrder.setStatus("Đang xử lý");
        savedOrder.setPaymentMethod(paymentMethod);
        StringBuilder itemData = new StringBuilder();
        for (CartLine line : orderItems) {
            if (itemData.length() > 0) itemData.append(",");
            itemData.append(line.getProduct().getId()).append(":").append(line.getQuantity());
        }
        savedOrder.setItemsJson(itemData.toString());
        orderRepository.save(savedOrder);
        cart.clear();

        String paymentLabel = "COD".equals(paymentMethod)
                ? "Thanh toán khi nhận hàng (COD)"
                : "VNPAY (chưa thực hiện giao dịch trực tuyến)";

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Đặt hàng thành công! Mã đơn hàng: "
                        + orderCode + ". Phương thức: " + paymentLabel
        );

        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orders(Authentication authentication, Model model) {
        List<OrderSummary> summaries = new ArrayList<>();
        if (authentication != null) {
            for (ShopOrder saved : orderRepository.findByCustomerUsernameOrderByCreatedAtDesc(authentication.getName())) {
                List<CartLine> lines = new ArrayList<>(); int qty = 0;
                if (saved.getItemsJson() != null && !saved.getItemsJson().isBlank()) {
                    for (String part : saved.getItemsJson().split(",")) {
                        String[] pair = part.split(":");
                        if (pair.length == 2) try {
                            Product p = productRepository.findById(Long.parseLong(pair[0])).orElse(null);
                            int q = Integer.parseInt(pair[1]);
                            if (p != null && q > 0) { lines.add(new CartLine(p,q)); qty += q; }
                        } catch (NumberFormatException ignored) { }
                    }
                }
                summaries.add(new OrderSummary(saved.getOrderCode(), saved.getCreatedAt(), lines, saved.getTotal(), qty,
                        saved.getCustomerName(), saved.getPhone(), saved.getAddress(), saved.getNote(), saved.getStatus(), saved.getPaymentMethod(),
                        saved.getCancelReason(), saved.getCancelNote()));
            }
        }
        model.addAttribute("orders", summaries);
        return "orders";
    }

    @PostMapping("/orders/cancel/{orderCode}")
    public String cancelOrder(@PathVariable String orderCode,
                              @RequestParam String cancelReason,
                              @RequestParam(required = false) String cancelNote,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        List<String> allowedReasons = List.of(
                "Đặt nhầm sản phẩm/số lượng", "Muốn đổi sang sản phẩm khác",
                "Thay đổi nhu cầu mua hàng", "Thời gian giao hàng quá lâu",
                "Tìm được nơi bán giá tốt hơn", "Sai thông tin nhận hàng",
                "Đặt trùng đơn hàng", "Không còn đủ ngân sách", "Lý do khác"
        );
        ShopOrder order = orderRepository.findByOrderCodeAndCustomerUsername(orderCode, authentication.getName()).orElse(null);
        String cleanedNote = cancelNote == null ? "" : cancelNote.trim();
        if (order == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đơn hàng.");
        } else if (!"Đang xử lý".equals(order.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chỉ có thể tự hủy đơn hàng khi đơn còn ở trạng thái Đang xử lý.");
        } else if (!allowedReasons.contains(cancelReason)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn lý do hủy hợp lệ.");
        } else if (cleanedNote.length() > 500) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lý do chi tiết không được vượt quá 500 ký tự.");
        } else if ("Lý do khác".equals(cancelReason) && cleanedNote.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng nhập lý do cụ thể khi chọn Lý do khác.");
        } else {
            order.setStatus("Đã hủy");
            order.setCancelReason(cancelReason);
            order.setCancelNote(cleanedNote.isBlank() ? null : cleanedNote);
            orderRepository.save(order);
            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy đơn hàng " + orderCode + ". Lý do: " + cancelReason);
        }
        return "redirect:/orders";
    }

    public static class CartLine implements Serializable {
        private final Product product;
        private final int quantity;

        public CartLine(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public BigDecimal getSubtotal() {
            return product.getPrice()
                    .multiply(BigDecimal.valueOf(quantity));
        }
    }

    public static class OrderSummary implements Serializable {
        private final String orderCode;
        private final LocalDateTime orderDate;
        private final List<CartLine> items;
        private final BigDecimal total;
        private final int totalQuantity;
        private final String customerName;
        private final String phone;
        private final String address;
        private final String note;
        private final String status;
        private final String paymentMethod;
        private final String cancelReason;
        private final String cancelNote;

        public OrderSummary(
                String orderCode,
                LocalDateTime orderDate,
                List<CartLine> items,
                BigDecimal total,
                int totalQuantity,
                String customerName,
                String phone,
                String address,
                String note,
                String status,
                String paymentMethod,
                String cancelReason,
                String cancelNote) {

            this.orderCode = orderCode;
            this.orderDate = orderDate;
            this.items = new ArrayList<>(items);
            this.total = total;
            this.totalQuantity = totalQuantity;
            this.customerName = customerName;
            this.phone = phone;
            this.address = address;
            this.note = note;
            this.status = status;
            this.paymentMethod = paymentMethod;
            this.cancelReason = cancelReason;
            this.cancelNote = cancelNote;
        }

        public OrderSummary withStatus(String newStatus) {
            return new OrderSummary(
                    orderCode,
                    orderDate,
                    items,
                    total,
                    totalQuantity,
                    customerName,
                    phone,
                    address,
                    note,
                    newStatus,
                    paymentMethod,
                    cancelReason,
                    cancelNote
            );
        }

        public String getOrderCode() {
            return orderCode;
        }

        public LocalDateTime getOrderDate() {
            return orderDate;
        }

        public String getFormattedDate() {
            return orderDate.format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            );
        }

        public List<CartLine> getItems() {
            return items;
        }

        public BigDecimal getTotal() {
            return total;
        }

        public int getTotalQuantity() {
            return totalQuantity;
        }

        public String getCustomerName() {
            return customerName;
        }

        public String getPhone() {
            return phone;
        }

        public String getAddress() {
            return address;
        }

        public String getNote() {
            return note;
        }

        public String getStatus() {
            return status;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public String getCancelReason() { return cancelReason; }
        public String getCancelNote() { return cancelNote; }
    }
}
