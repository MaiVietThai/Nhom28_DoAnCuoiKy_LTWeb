package vn.edu.hcmute.storemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hcmute.storemanagement.entity.Inventory;
import vn.edu.hcmute.storemanagement.entity.InventoryId;
import vn.edu.hcmute.storemanagement.entity.Product;

import java.util.Collection;

public interface InventoryRepository extends JpaRepository<Inventory, InventoryId> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Inventory i WHERE i.product = :product")
    void deleteByProduct(@Param("product") Product product);

    @Modifying
    @Transactional
    @Query("DELETE FROM Inventory i WHERE i.product.id IN :productIds")
    void deleteByProductIds(@Param("productIds") Collection<Long> productIds);
}
