package demo.kafka.properties;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Configuration
@ConfigurationProperties("kafkademo")
@Getter
@Setter
@Validated
public class KafkaDemoProperties {
    @NotNull private String outboundTopic;
    @NotNull private String bootstrapServers;
    @NotNull private String schemaRegistryUrl;
    @NotNull private String inboundTopic;
    private Map<String, String> additionalConfig;
    private Ssl ssl;
}
