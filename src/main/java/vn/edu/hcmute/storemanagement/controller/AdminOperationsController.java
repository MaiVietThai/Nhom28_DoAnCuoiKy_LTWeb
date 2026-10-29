package vn.edu.hcmute.storemanagement.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.hcmute.storemanagement.entity.AppUser;
import vn.edu.hcmute.storemanagement.entity.CustomerFeedback;
import vn.edu.hcmute.storemanagement.entity.ShopOrder;
import vn.edu.hcmute.storemanagement.repository.AppUserRepository;
import vn.edu.hcmute.storemanagement.repository.CustomerFeedbackRepository;
import vn.edu.hcmute.storemanagement.repository.ProductRepository;
import vn.edu.hcmute.storemanagement.repository.ShopOrderRepository;
import java.math.BigDecimal;
import java.util.List; import java.util.Map; import java.util.LinkedHashMap;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOperationsController {
 private final ShopOrderRepository orders; private final AppUserRepository users; private final ProductRepository products; private final CustomerFeedbackRepository feedback;
 public AdminOperationsController(ShopOrderRepository orders, AppUserRepository users, ProductRepository products, CustomerFeedbackRepository feedback){this.orders=orders;this.users=users;this.products=products;this.feedback=feedback;}
 @GetMapping("/dashboard") public String dashboard(Model model){
  List<ShopOrder> all=orders.findAllByOrderByCreatedAtDesc();
  BigDecimal revenue=all.stream().filter(o->!"Đã hủy".equals(o.getStatus())).map(ShopOrder::getTotal).reduce(BigDecimal.ZERO,BigDecimal::add);
  model.addAttribute("orderCount",all.size()); model.addAttribute("pendingCount",all.stream().filter(o->"Đang xử lý".equals(o.getStatus())).count());
  model.addAttribute("revenue",revenue); model.addAttribute("productCount",products.count()); model.addAttribute("customerCount",users.count());
  model.addAttribute("feedbackCount",feedback.count()); model.addAttribute("recentOrders",all.stream().limit(8).toList()); return "admin/dashboard";
 }
 @GetMapping("/orders") public String orderList(Model model){
  List<ShopOrder> all=orders.findAllByOrderByCreatedAtDesc(); Map<Long,String> details=new LinkedHashMap<>();
  for(ShopOrder o:all){StringBuilder label=new StringBuilder(); if(o.getItemsJson()!=null) for(String part:o.getItemsJson().split(",")){String[] pair=part.split(":"); if(pair.length==2) try {var p=products.findById(Long.parseLong(pair[0])).orElse(null); if(p!=null){if(label.length()>0)label.append("; ");label.append(p.getName()).append(" × ").append(pair[1]);}}catch(NumberFormatException ignored){}}
   details.put(o.getId(),label.length()==0?"Không còn dữ liệu sản phẩm":label.toString()); }
  model.addAttribute("orders",all); model.addAttribute("orderDetails",details); return "admin/orders";
 }
 @PostMapping("/orders/{id}/status") public String updateOrder(@PathVariable Long id,@RequestParam String status,RedirectAttributes ra){
  if(!List.of("Đang xử lý","Đã xác nhận","Đang giao","Hoàn thành","Đã hủy").contains(status)){ra.addFlashAttribute("errorMessage","Trạng thái không hợp lệ.");return "redirect:/admin/orders";}
  orders.findById(id).ifPresent(o->{o.setStatus(status);orders.save(o);}); ra.addFlashAttribute("successMessage","Đã cập nhật trạng thái đơn hàng.");return "redirect:/admin/orders";
 }
 @GetMapping("/customers") public String customerList(Model model){model.addAttribute("customers",users.findAll());return "admin/customers";}
 @PostMapping("/customers/{id}/toggle") public String toggleCustomer(@PathVariable Long id,RedirectAttributes ra){
  AppUser user=users.findById(id).orElse(null);
  if(user==null){ra.addFlashAttribute("errorMessage","Không tìm thấy tài khoản.");}
  else if(user.getRoles().stream().anyMatch(r->"ADMIN".equals(r.getName()))){ra.addFlashAttribute("errorMessage","Không thể khóa tài khoản quản trị bằng chức năng này.");}
  else {user.setActive(!user.isActive());users.save(user);ra.addFlashAttribute("successMessage","Đã cập nhật trạng thái tài khoản.");}
  return "redirect:/admin/customers";
 }
 @GetMapping("/feedback") public String feedbackList(Model model){model.addAttribute("feedbacks",feedback.findAllByOrderByCreatedAtDesc());return "admin/feedback";}
 @PostMapping("/feedback/{id}/status") public String feedbackStatus(@PathVariable Long id,
                                      @RequestParam(defaultValue = "Mới") String status,
                                      @RequestParam(required = false) String adminReply,
                                      RedirectAttributes ra){
  if(!List.of("Mới","Đã xem","Đã phản hồi","Ẩn").contains(status)){
   ra.addFlashAttribute("errorMessage","Trạng thái không hợp lệ.");return "redirect:/admin/feedback";
  }
  String reply = adminReply == null ? "" : adminReply.trim();
  if(reply.length() > 1200){ra.addFlashAttribute("errorMessage","Phản hồi không được vượt quá 1200 ký tự.");return "redirect:/admin/feedback";}
  feedback.findById(id).ifPresent(f->{
   if(!reply.isBlank()){f.setAdminReply(reply); f.setStatus("Đã phản hồi");}
   else {f.setStatus(status);}
   feedback.save(f);
  });
  ra.addFlashAttribute("successMessage","Đã cập nhật phản hồi khách hàng.");
  return "redirect:/admin/feedback";
 }
}
