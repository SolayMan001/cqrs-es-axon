package ma.oumouou.cqrsesaxon;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "CQRS et Event Sourcing — Comptes bancaires", version = "1.0"))
public class CqrsEsAxonApplication {

    public static void main(String[] args) {
        SpringApplication.run(CqrsEsAxonApplication.class, args);
    }

}
