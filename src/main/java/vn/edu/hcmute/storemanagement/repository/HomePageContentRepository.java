
package vn.edu.hcmute.storemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.hcmute.storemanagement.entity.HomePageContent;

import java.util.Optional;

public interface HomePageContentRepository
        extends JpaRepository<HomePageContent, Long> {

    Optional<HomePageContent> findFirstByOrderByIdAsc();
}
