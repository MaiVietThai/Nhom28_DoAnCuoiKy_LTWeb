package vn.edu.hcmute.storemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.hcmute.storemanagement.entity.AppRole;
import java.util.Optional;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {
    Optional<AppRole> findByName(String name);
}
