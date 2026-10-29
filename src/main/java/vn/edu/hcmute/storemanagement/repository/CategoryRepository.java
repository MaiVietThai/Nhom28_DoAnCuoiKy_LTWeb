package vn.edu.hcmute.storemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.hcmute.storemanagement.entity.Category;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
