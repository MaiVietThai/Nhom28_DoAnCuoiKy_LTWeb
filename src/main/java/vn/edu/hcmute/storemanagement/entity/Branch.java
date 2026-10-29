package vn.edu.hcmute.storemanagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Branches")
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BranchId")
    private Long id;

    @Column(name = "BranchCode", nullable = false, unique = true, length = 20)
    private String branchCode;

    @Column(name = "BranchName", nullable = false, length = 150)
    private String branchName;

    @Column(name = "Address", nullable = false, length = 300)
    private String address;

    @Column(name = "Phone", length = 20)
    private String phone;

    @Column(name = "IsActive", nullable = false)
    private boolean active = true;

    protected Branch() {}

    public Branch(String branchCode, String branchName, String address) {
        this.branchCode = branchCode;
        this.branchName = branchName;
        this.address = address;
    }

    public Long getId() { return id; }
    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
