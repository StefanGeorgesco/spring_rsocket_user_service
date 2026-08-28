package fr.stefangeorgesco.rsocketuserservice.config;

import fr.stefangeorgesco.rsocketuserservice.domain.OperationType;
import io.rsocket.metadata.WellKnownMimeType;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

@Configuration
public class RSocketServerConfig {

    @Bean
    public RSocketStrategiesCustomizer strategiesCustomizer() {
        MimeType mimeType = MimeTypeUtils.parseMimeType(WellKnownMimeType.APPLICATION_CBOR.getString());
        return builder -> builder.metadataExtractorRegistry(
                registry -> registry
                        .metadataToExtract(mimeType, OperationType.class, "operation-type")
        );
    }
}
