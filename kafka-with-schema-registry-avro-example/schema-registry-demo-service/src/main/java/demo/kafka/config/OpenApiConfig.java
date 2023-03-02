package demo.kafka.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;

import java.util.List;


public class OpenApiConfig {
    @Bean
    public OpenAPI myOpenAPI() {
        Contact contact = new Contact();
        contact.setEmail("larbotech@gao.com");
        contact.setName("Larbo de GAO");
        contact.setUrl("https://larbotech-kafka-ssal-ssl-api.com");

        Server localServer = new Server();
        localServer.setUrl("http://localhost:8081");
        localServer.setDescription("Server URL in Local environment");

        Server productionServer = new Server();
        productionServer.setUrl("https:/larbotech-kafka-ssal-ssl-api.com");
        productionServer.setDescription("Server URL in Production environment");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("PAYMENT MANAGER API")
                .contact(contact)
                .version("1.0")
                .description("This API exposes endpoints for users to manage their payments.")
                .termsOfService("http://larbotech-kafka-ssal-ssl-api.com/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, productionServer));
    }
}