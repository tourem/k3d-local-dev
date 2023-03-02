package demo.kafka.component;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import demo.kafka.event.PaymentSent;
import demo.kafka.event.SendPayment;
import dev.lydtech.component.framework.client.kafka.KafkaAvroClient;
import dev.lydtech.component.framework.client.kafka.KafkaSchemaRegistryClient;
import dev.lydtech.component.framework.extension.TestContainersSetupExtension;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@ExtendWith(TestContainersSetupExtension.class)
public class PaymentEndToEndComponentTest {

    private static final String GROUP_ID = "PaymentEndToEndComponentTest";
    private final static String SEND_PAYMENT_TOPIC = "send-payment";
    private final static String PAYMENT_SENT_TOPIC = "payment-sent";

    private Consumer consumer;

    private static final DecimalFormat formatter = new DecimalFormat("0.00");

    @BeforeAll
    public static void beforeAll() throws Exception {
        // Register the message schemas with the Schema Registry.
        KafkaSchemaRegistryClient.getInstance().resetSchemaRegistry();
        KafkaSchemaRegistryClient.getInstance().registerSchema(SEND_PAYMENT_TOPIC, SendPayment.getClassSchema().toString());
        KafkaSchemaRegistryClient.getInstance().registerSchema(PAYMENT_SENT_TOPIC, PaymentSent.getClassSchema().toString());
    }

    @BeforeEach
    public void setup() {
        consumer = KafkaAvroClient.getInstance().createConsumer(GROUP_ID, PAYMENT_SENT_TOPIC);

        // Clear the topic.
        consumer.poll(Duration.ofSeconds(1));
    }

    @AfterEach
    public void tearDown() {
        consumer.close();
    }

    /**
     * Send in multiple events and ensure an outbound event is emitted for each.
     */
    @Test
    public void testFlow() throws Exception {
        int totalMessages = 1000;
        int delayMs = 0;

        for (int i = 1; i<= totalMessages; i++) {
            String key = UUID.randomUUID().toString();
            String payload = UUID.randomUUID().toString();
            KafkaAvroClient.getInstance().sendMessage(SEND_PAYMENT_TOPIC, key, buildSendPayment(payload));
            if(i % 100 == 0){
                log.info("Total events sent: {}", i);
            }
            TimeUnit.MILLISECONDS.sleep(delayMs);
        }
        List<ConsumerRecord<String, PaymentSent>> outboundEvents = KafkaAvroClient.getInstance().consumeAndAssert("testFlow", consumer, totalMessages, 3);
        outboundEvents.stream().forEach(outboundEvent -> {
            assertThat(outboundEvent.value().getPaymentId(), notNullValue());
            assertThat(outboundEvent.value().getToAccount().toString(), equalTo("toAcc"));
            assertThat(outboundEvent.value().getFromAccount().toString(), equalTo("fromAcc"));
        });
    }

    private static SendPayment buildSendPayment(String paymentId) {
        return SendPayment.newBuilder()
                .setPaymentId(paymentId)
                .setAmount(Double.parseDouble(formatter.format(new Random().nextDouble()*100)))
                .setCurrency("USD")
                .setToAccount("toAcc")
                .setFromAccount("fromAcc")
                .build();
    }
}
