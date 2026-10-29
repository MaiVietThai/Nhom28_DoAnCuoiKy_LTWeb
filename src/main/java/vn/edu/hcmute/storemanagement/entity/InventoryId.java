package vn.edu.hcmute.storemanagement.entity;

import java.io.Serializable;
import java.util.Objects;

public class InventoryId implements Serializable {
    private Long branch;
    private Long product;

    public InventoryId() {}

    public InventoryId(Long branch, Long product) {
        this.branch = branch;
        this.product = product;
    }

    public Long getBranch() {
        return branch;
    }

    public void setBranch(Long branch) {
        this.branch = branch;
    }

    public Long getProduct() {
        return product;
    }

    public void setProduct(Long product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InventoryId that)) return false;
        return Objects.equals(branch, that.branch) && Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(branch, product);
    }
}

