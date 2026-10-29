package vn.edu.hcmute.storemanagement.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Bean
    Cloudinary cloudinary(
            @Value("${cloudinary.cloud-name:}") String cloudName,
            @Value("${cloudinary.api-key:}") String apiKey,
            @Value("${cloudinary.api-secret:}") String apiSecret) {
        Map<String, String> config = new HashMap<>();
        if (!cloudName.isBlank()) config.put("cloud_name", cloudName);
        if (!apiKey.isBlank()) config.put("api_key", apiKey);
        if (!apiSecret.isBlank()) config.put("api_secret", apiSecret);
        return new Cloudinary(config);
    }
}
