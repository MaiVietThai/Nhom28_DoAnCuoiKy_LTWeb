
package vn.edu.hcmute.storemanagement.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.edu.hcmute.storemanagement.entity.HomePageContent;
import vn.edu.hcmute.storemanagement.repository.HomePageContentRepository;

@Configuration
public class HomePageContentInitializer {

    @Bean
    CommandLineRunner initializeHomePageContent(
            HomePageContentRepository repository) {

        return args -> {
            // Chỉ tạo nội dung mặc định khi bảng chưa có dữ liệu.
            if (repository.count() > 0) {
                return;
            }

            HomePageContent content = new HomePageContent();

            content.setAnnouncement(
                    "FIGUREVERSE // COLLECT YOUR UNIVERSE"
            );

            content.setHeroEyebrow(
                    "THE NEXT LEVEL OF COLLECTING"
            );

            content.setHeroTitle(
                    "YOUR ANIME. YOUR UNIVERSE."
            );

            content.setHeroDescription(
                    "Khám phá thế giới mô hình anime và xây dựng bộ sưu tập mang dấu ấn riêng của bạn."
            );

            content.setHeroButtonText(
                    "KHÁM PHÁ BỘ SƯU TẬP"
            );

            content.setHeroButtonLink(
                    "/#collections"
            );

            content.setCollectionEyebrow(
                    "PICK YOUR WORLD"
            );

            content.setCollectionTitle(
                    "CHOOSE YOUR UNIVERSE"
            );

            content.setCollectionDescription(
                    "Tìm thế giới anime phù hợp với bộ sưu tập của bạn."
            );

            content.setProductEyebrow(
                    "THE COLLECTOR'S DROP"
            );

            content.setProductTitle(
                    "FEATURED FIGURES"
            );

            content.setProductDescription(
                    "Khám phá những mô hình nổi bật dành cho người sưu tầm."
            );

            content.setAboutEyebrow(
                    "BUILT FOR FANS, POWERED BY PASSION"
            );

            content.setAboutTitle(
                    "YOUR SHELF. YOUR STORY."
            );

            content.setAboutDescription(
                    "Mỗi mô hình là một câu chuyện, mỗi bộ sưu tập là một phần cá tính của bạn."
            );

            content.setFooterTagline(
                    "Made for the ones who never stopped dreaming."
            );

            repository.save(content);
        };
    }
}
