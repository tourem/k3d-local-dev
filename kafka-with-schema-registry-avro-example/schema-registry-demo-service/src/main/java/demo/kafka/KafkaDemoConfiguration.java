package demo.kafka;

import java.util.HashMap;
import java.util.Map;

import demo.kafka.event.PaymentSent;
import demo.kafka.event.SendPayment;
import demo.kafka.properties.KafkaDemoProperties;
import demo.kafka.properties.KafkaSSLUtil;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

@Slf4j
@ComponentScan(basePackages = {"demo.kafka"})
@Configuration
public class KafkaDemoConfiguration {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SendPayment> kafkaListenerContainerFactory(final ConsumerFactory<String, SendPayment> consumerFactory) {
        final ConcurrentKafkaListenerContainerFactory<String, SendPayment> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public KafkaTemplate<String, PaymentSent> kafkaTemplate(final ProducerFactory<String, PaymentSent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConsumerFactory<String, SendPayment> consumerFactory(KafkaDemoProperties kafkaDemoProperties) {
        final Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaDemoProperties.getBootstrapServers());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-consumer-group");
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, KafkaAvroDeserializer.class);
        config.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaDemoProperties.getSchemaRegistryUrl());
        config.put(KafkaAvroDeserializerConfig.AUTO_REGISTER_SCHEMAS, false);
        config.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, false);
        config.putAll(kafkaDemoProperties.getAdditionalConfig());
        KafkaSSLUtil.addGlobalParameters(config, kafkaDemoProperties.getSsl());
        log.info("config consumer : "+config.entrySet().size());
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ProducerFactory<String, PaymentSent> producerFactory(KafkaDemoProperties kafkaDemoProperties) {
        final Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaDemoProperties.getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        config.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaDemoProperties.getSchemaRegistryUrl());
        config.put(KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS, false);
        config.putAll(kafkaDemoProperties.getAdditionalConfig());
        KafkaSSLUtil.addGlobalParameters(config,kafkaDemoProperties.getSsl());
        log.info("config consumer : "+config.entrySet().size());
        return new DefaultKafkaProducerFactory<>(config);
    }
}
