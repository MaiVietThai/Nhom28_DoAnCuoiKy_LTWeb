package vn.edu.hcmute.storemanagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shop_orders")
public class ShopOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=30) private String orderCode;
    @Column(nullable=false, length=50) private String customerUsername;
    @Column(nullable=false, length=150) private String customerName;
    @Column(nullable=false, length=30) private String phone;
    @Column(nullable=false, length=500) private String address;
    @Column(length=1000) private String note;
    @Column(nullable=false, precision=18, scale=2) private BigDecimal total;
    @Column(nullable=false, length=20) private String status="Đang xử lý";
    @Column(length=80) private String cancelReason;
    @Column(length=500) private String cancelNote;
    @Column(nullable=false, length=20) private String paymentMethod;
    @Column(nullable=false, columnDefinition="nvarchar(max)") private String itemsJson;
    @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
    public Long getId(){return id;} public String getOrderCode(){return orderCode;} public void setOrderCode(String v){orderCode=v;}
    public String getCustomerUsername(){return customerUsername;} public void setCustomerUsername(String v){customerUsername=v;}
    public String getCustomerName(){return customerName;} public void setCustomerName(String v){customerName=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getNote(){return note;} public void setNote(String v){note=v;} public BigDecimal getTotal(){return total;} public void setTotal(BigDecimal v){total=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public String getCancelReason(){return cancelReason;} public void setCancelReason(String v){cancelReason=v;}
    public String getCancelNote(){return cancelNote;} public void setCancelNote(String v){cancelNote=v;}
    public String getPaymentMethod(){return paymentMethod;} public void setPaymentMethod(String v){paymentMethod=v;}
    public String getItemsJson(){return itemsJson;} public void setItemsJson(String v){itemsJson=v;} public LocalDateTime getCreatedAt(){return createdAt;}
}
