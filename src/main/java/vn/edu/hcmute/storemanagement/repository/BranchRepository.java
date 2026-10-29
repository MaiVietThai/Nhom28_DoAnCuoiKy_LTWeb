package vn.edu.hcmute.storemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.hcmute.storemanagement.entity.Branch;
import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    Optional<Branch> findByBranchCode(String branchCode);
}
