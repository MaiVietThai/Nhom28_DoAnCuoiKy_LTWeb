package vn.edu.hcmute.storemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.hcmute.storemanagement.entity.AppUser;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
