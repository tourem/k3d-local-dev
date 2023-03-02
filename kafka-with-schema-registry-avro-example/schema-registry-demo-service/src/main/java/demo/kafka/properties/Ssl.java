package demo.kafka.properties;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ssl {

    /**
     * Password of the private key in the key store file.
     */
    private String keyPassword;

    /**
     * Location of the key store file.
     */
    private String keyStoreLocation;

    /**
     * Store password for the key store file.
     */
    private String keyStorePassword;

    /**
     * Location of the trust store file.
     */
    private String trustStoreLocation;

    /**
     * Store password for the trust store file.
     */
    private String trustStorePassword;
}
