package ru.practicum.explorewithme.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.explorewithme.exception.NotFoundException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Configuration
public class FeignConfig {
    @Bean
    public Feign.Builder feignBuilder() {

        return Feign.builder()
                .errorDecoder(new FeignErrorDecoder());
    }

    public static class FeignErrorDecoder implements ErrorDecoder {
        private final ErrorDecoder defaultDecoder = new Default();
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public Exception decode(String methodKey, Response response) {
            if (response.status() == 404) {
                String defaultNoFoundErrorMessage = "Resource not found for method: " + methodKey;
                try {
                    if (response.body() != null) {
                        String body = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
                        JsonNode jsonNode = objectMapper.readTree(body);

                        String message = jsonNode.has("message") ? jsonNode.get("message").asText() : defaultNoFoundErrorMessage;

                        return new NotFoundException(message);
                    }
                } catch (IOException e) {
                    return new NotFoundException(defaultNoFoundErrorMessage);
                }
            }

            return defaultDecoder.decode(methodKey, response);
        }
    }
}
