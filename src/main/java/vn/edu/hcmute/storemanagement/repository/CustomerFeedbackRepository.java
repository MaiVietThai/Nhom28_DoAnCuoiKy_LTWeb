package vn.edu.hcmute.storemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import vn.edu.hcmute.storemanagement.entity.CustomerFeedback;

public interface CustomerFeedbackRepository extends JpaRepository<CustomerFeedback, Long> {
    List<CustomerFeedback> findAllByOrderByCreatedAtDesc();
    List<CustomerFeedback> findByUsernameOrderByCreatedAtDesc(String username);
}
