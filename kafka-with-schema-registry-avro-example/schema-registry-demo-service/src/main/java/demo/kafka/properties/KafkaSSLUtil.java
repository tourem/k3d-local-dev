package demo.kafka.properties;
;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.security.auth.SecurityProtocol;

import java.util.Map;

public class KafkaSSLUtil {
    private KafkaSSLUtil() {
    }

    /**
     * Adds the global keystore/truststore SSL parameters to Kafka properties
     * if SSL is enabled but the keystore/truststore SSL parameters
     * are not defined explicitly in Kafka properties.
     *
     * @param kafkaProps Kafka properties
     */
    public static void addGlobalParameters(Map<String, Object> kafkaProps, Ssl ssl) {

            addGlobalSSLParameter(kafkaProps, CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            addGlobalSSLParameter(kafkaProps, CommonClientConfigs.REQUEST_TIMEOUT_MS_CONFIG, "30000");
            addGlobalSSLParameter(kafkaProps, CommonClientConfigs.SESSION_TIMEOUT_MS_CONFIG, "30000");
            addGlobalSSLParameter(kafkaProps,
                    SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, ssl.getKeyStoreLocation());
            addGlobalSSLParameter(kafkaProps,
                    SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, ssl.getKeyStorePassword());
            addGlobalSSLParameter(kafkaProps,
                    SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "JKS");
            addGlobalSSLParameter(kafkaProps,
                    SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, ssl.getTrustStoreLocation());
            addGlobalSSLParameter(kafkaProps,
                    SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, ssl.getTrustStorePassword());
        addGlobalSSLParameter(kafkaProps,
                SslConfigs.SSL_KEY_PASSWORD_CONFIG, ssl.getKeyPassword());

            addGlobalSSLParameter(kafkaProps,
                    SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "JKS");
            setSystemSslProperties(ssl);
    }

    private static void setSystemSslProperties(Ssl ssl) {
        System.setProperty( "javax.net.ssl.keyStore",  ssl.getKeyStoreLocation());
        System.setProperty( "javax.net.ssl.trustStore", ssl.getTrustStoreLocation());
        System.setProperty( "javax.net.ssl.trustStorePassword", ssl.getTrustStorePassword() );
        System.setProperty( "javax.net.ssl.keyStorePassword", ssl.getKeyStorePassword());
        System.setProperty( "javax.net.ssl.keyStoreType", "JKS" );
        System.setProperty( "javax.net.ssl.trustStoreType", "JKS" );
    }

    private static void addGlobalSSLParameter(Map<String, Object>  kafkaProps,
                                              String propName, String globalValue) {
        if (!kafkaProps.containsKey(propName) && globalValue != null) {
            kafkaProps.put(propName, globalValue);
        }
    }

    public static boolean isSSLEnabled(Map<String, Object>  kafkaProps) {
        String securityProtocol =
                (String)kafkaProps.get(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG);

        return securityProtocol != null &&
                (securityProtocol.equals(SecurityProtocol.SSL.name) ||
                        securityProtocol.equals(SecurityProtocol.SASL_SSL.name));
    }

}