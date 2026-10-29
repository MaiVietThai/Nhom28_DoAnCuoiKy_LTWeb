package vn.edu.hcmute.storemanagement.controller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.hcmute.storemanagement.entity.Category;
import vn.edu.hcmute.storemanagement.entity.Product;
import vn.edu.hcmute.storemanagement.repository.CategoryRepository;
import vn.edu.hcmute.storemanagement.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class AnimeProductSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private static final String DEFAULT_IMAGE =
            "/images/products/default.svg";

    public AnimeProductSeeder(
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    private record Anime(
            String code,
            String name,
            String description,
            String[] characters) {
    }

    @Override
    @Transactional
    public void run(String... args) {
        Anime[] animes = {
                new Anime("OP", "One Piece",
                        "Mô hình nhân vật anime One Piece",
                        new String[]{
                                "Monkey D. Luffy", "Roronoa Zoro", "Nami",
                                "Usopp", "Sanji", "Tony Tony Chopper",
                                "Nico Robin", "Franky", "Brook", "Jinbe"
                        }),

                new Anime("NAR", "Naruto",
                        "Mô hình nhân vật anime Naruto",
                        new String[]{
                                "Naruto Uzumaki", "Sasuke Uchiha", "Sakura Haruno",
                                "Kakashi Hatake", "Itachi Uchiha", "Madara Uchiha",
                                "Hinata Hyuga", "Minato Namikaze", "Gaara", "Jiraiya"
                        }),

                new Anime("DB", "Dragon Ball",
                        "Mô hình nhân vật anime Dragon Ball",
                        new String[]{
                                "Son Goku", "Vegeta", "Son Gohan",
                                "Piccolo", "Trunks", "Frieza",
                                "Cell", "Majin Buu", "Broly", "Future Trunks"
                        }),

                new Anime("DS", "Demon Slayer",
                        "Mô hình nhân vật anime Demon Slayer",
                        new String[]{
                                "Tanjiro Kamado", "Nezuko Kamado", "Zenitsu Agatsuma",
                                "Inosuke Hashibira", "Kyojuro Rengoku", "Giyu Tomioka",
                                "Shinobu Kocho", "Tengen Uzui", "Mitsuri Kanroji",
                                "Muichiro Tokito"
                        }),

                new Anime("JJK", "Jujutsu Kaisen",
                        "Mô hình nhân vật anime Jujutsu Kaisen",
                        new String[]{
                                "Satoru Gojo", "Yuji Itadori", "Megumi Fushiguro",
                                "Nobara Kugisaki", "Ryomen Sukuna", "Yuta Okkotsu",
                                "Suguru Geto", "Kento Nanami", "Toji Fushiguro",
                                "Maki Zenin"
                        }),

                new Anime("AOT", "Attack on Titan",
                        "Mô hình nhân vật anime Attack on Titan",
                        new String[]{
                                "Eren Yeager", "Mikasa Ackerman", "Armin Arlert",
                                "Levi Ackerman", "Erwin Smith", "Hange Zoe",
                                "Reiner Braun", "Annie Leonhart", "Jean Kirstein",
                                "Sasha Blouse"
                        }),

                new Anime("MHA", "My Hero Academia",
                        "Mô hình nhân vật anime My Hero Academia",
                        new String[]{
                                "Izuku Midoriya", "Katsuki Bakugo", "Shoto Todoroki",
                                "Ochaco Uraraka", "All Might", "Shota Aizawa",
                                "Endeavor", "Hawks", "Tomura Shigaraki",
                                "Eijiro Kirishima"
                        }),

                new Anime("BL", "Bleach",
                        "Mô hình nhân vật anime Bleach",
                        new String[]{
                                "Ichigo Kurosaki", "Rukia Kuchiki", "Renji Abarai",
                                "Byakuya Kuchiki", "Toshiro Hitsugaya",
                                "Kenpachi Zaraki", "Kisuke Urahara", "Sosuke Aizen",
                                "Yoruichi Shihouin", "Orihime Inoue"
                        }),

                new Anime("CSM", "Chainsaw Man",
                        "Mô hình nhân vật anime Chainsaw Man",
                        new String[]{
                                "Denji", "Power", "Makima",
                                "Aki Hayakawa", "Pochita", "Kobeni Higashiyama",
                                "Himeno", "Angel Devil", "Reze", "Katana Man"
                        }),

                new Anime("HXH", "Hunter x Hunter",
                        "Mô hình nhân vật anime Hunter x Hunter",
                        new String[]{
                                "Gon Freecss", "Killua Zoldyck", "Kurapika",
                                "Leorio Paradinight", "Hisoka", "Chrollo Lucilfer",
                                "Isaac Netero", "Biscuit Krueger", "Meruem",
                                "Neferpitou"
                        })
        };

        int added = 0;
        int skipped = 0;

        for (Anime anime : animes) {
            Category category = getOrCreateCategory(anime);

            for (int i = 0; i < anime.characters().length; i++) {
                String character = anime.characters()[i];

                // Mỗi anime có 10 SKU, ví dụ OP-001 đến OP-010.
                int number = i + 1;
                String sku = String.format(
                        "%s-%03d", anime.code(), number);

                // Bỏ qua nếu SKU đã tồn tại trong cơ sở dữ liệu.
                if (productRepository.existsBySku(sku)) {
                    skipped++;
                    continue;
                }

                // Giá mẫu: từ 300.000đ đến 500.000đ.
                BigDecimal price = BigDecimal.valueOf(
                        300000L + (number % 5) * 50000L
                );

                Product product = new Product(
                        sku,
                        character,
                        anime.description()
                                + ". Nhân vật: " + character
                                + ". Mô hình sưu tầm anime.",
                        price,
                        getImageByCharacter(character),
                        category
                );

                product.setActive(true);
                productRepository.save(product);
                added++;
            }
        }

        System.out.println("======================================");
        System.out.println("FIGUREVERSE - KẾT QUẢ NẠP DỮ LIỆU");
        System.out.println("Số chủ đề anime: " + animes.length);
        System.out.println("Số sản phẩm mục tiêu: 100");
        System.out.println("Sản phẩm mới thêm: " + added);
        System.out.println("Sản phẩm bỏ qua do SKU đã tồn tại: " + skipped);
        System.out.println("======================================");
    }

    private String getImageByCharacter(String character) {
        if (character == null || character.isBlank()) {
            return DEFAULT_IMAGE;
        }

        String name = character.toLowerCase().trim();

        if (name.contains("luffy")) {
            return DEFAULT_IMAGE;
        } else if (name.contains("zoro")) {
            return DEFAULT_IMAGE;
        } else if (name.contains("naruto")) {
            return DEFAULT_IMAGE;
        } else if (name.contains("gojo")) {
            return DEFAULT_IMAGE;
        }

        return DEFAULT_IMAGE;
    }

    private Category getOrCreateCategory(Anime anime) {
        Optional<Category> existing =
                categoryRepository.findByName(anime.name());

        if (existing.isPresent()) {
            Category category = existing.get();

            if (!category.isActive()) {
                category.setActive(true);
                return categoryRepository.save(category);
            }

            return category;
        }

        Category category = new Category(
                anime.name(),
                anime.description()
        );

        category.setActive(true);

        return categoryRepository.save(category);
    }
}
