package vn.edu.hcmute.storemanagement.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal; import java.util.List; import java.util.Optional;
import vn.edu.hcmute.storemanagement.entity.ShopOrder;
public interface ShopOrderRepository extends JpaRepository<ShopOrder,Long> {
 List<ShopOrder> findByCustomerUsernameOrderByCreatedAtDesc(String username);
 List<ShopOrder> findAllByOrderByCreatedAtDesc();
 Optional<ShopOrder> findByOrderCodeAndCustomerUsername(String code,String username);
 long countByStatusNot(String status);
}
