
package vn.edu.hcmute.storemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "homepage_content")
public class HomePageContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Thanh thông báo đầu trang
    @Column(length = 500)
    private String announcement;

    // Nội dung phần giới thiệu chính
    @Column(length = 255)
    private String heroEyebrow;

    @Column(length = 255)
    private String heroTitle;

    @Column(length = 2000)
    private String heroDescription;

    @Column(length = 255)
    private String heroButtonText;

    @Column(length = 500)
    private String heroButtonLink;

    // Phần bộ sưu tập
    @Column(length = 255)
    private String collectionEyebrow;

    @Column(length = 255)
    private String collectionTitle;

    @Column(length = 1000)
    private String collectionDescription;

    // Phần sản phẩm nổi bật
    @Column(length = 255)
    private String productEyebrow;

    @Column(length = 255)
    private String productTitle;

    @Column(length = 1000)
    private String productDescription;

    // Phần giới thiệu cửa hàng
    @Column(length = 255)
    private String aboutEyebrow;

    @Column(length = 255)
    private String aboutTitle;

    @Column(length = 2000)
    private String aboutDescription;

    // Footer
    @Column(length = 1000)
    private String footerTagline;

    public HomePageContent() {
    }

    public Long getId() {
        return id;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public String getHeroEyebrow() {
        return heroEyebrow;
    }

    public void setHeroEyebrow(String heroEyebrow) {
        this.heroEyebrow = heroEyebrow;
    }

    public String getHeroTitle() {
        return heroTitle;
    }

    public void setHeroTitle(String heroTitle) {
        this.heroTitle = heroTitle;
    }

    public String getHeroDescription() {
        return heroDescription;
    }

    public void setHeroDescription(String heroDescription) {
        this.heroDescription = heroDescription;
    }

    public String getHeroButtonText() {
        return heroButtonText;
    }

    public void setHeroButtonText(String heroButtonText) {
        this.heroButtonText = heroButtonText;
    }

    public String getHeroButtonLink() {
        return heroButtonLink;
    }

    public void setHeroButtonLink(String heroButtonLink) {
        this.heroButtonLink = heroButtonLink;
    }

    public String getCollectionEyebrow() {
        return collectionEyebrow;
    }

    public void setCollectionEyebrow(String collectionEyebrow) {
        this.collectionEyebrow = collectionEyebrow;
    }

    public String getCollectionTitle() {
        return collectionTitle;
    }

    public void setCollectionTitle(String collectionTitle) {
        this.collectionTitle = collectionTitle;
    }

    public String getCollectionDescription() {
        return collectionDescription;
    }

    public void setCollectionDescription(String collectionDescription) {
        this.collectionDescription = collectionDescription;
    }

    public String getProductEyebrow() {
        return productEyebrow;
    }

    public void setProductEyebrow(String productEyebrow) {
        this.productEyebrow = productEyebrow;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getAboutEyebrow() {
        return aboutEyebrow;
    }

    public void setAboutEyebrow(String aboutEyebrow) {
        this.aboutEyebrow = aboutEyebrow;
    }

    public String getAboutTitle() {
        return aboutTitle;
    }

    public void setAboutTitle(String aboutTitle) {
        this.aboutTitle = aboutTitle;
    }

    public String getAboutDescription() {
        return aboutDescription;
    }

    public void setAboutDescription(String aboutDescription) {
        this.aboutDescription = aboutDescription;
    }

    public String getFooterTagline() {
        return footerTagline;
    }

    public void setFooterTagline(String footerTagline) {
        this.footerTagline = footerTagline;
    }
}
