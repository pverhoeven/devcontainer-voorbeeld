package nl.voorbeeld.applicatietesten;

import io.cucumber.java.BeforeAll;
import io.restassured.RestAssured;

import java.time.Duration;

import static org.awaitility.Awaitility.await;

public class Hooks {

    @BeforeAll
    public static void wachtOpApplicatie() {
        RestAssured.baseURI = Configuratie.APP_URL;
        // Bij omgeving 'lokaal': start de app eerst in de devcontainer van bestellingen-app (mvn spring-boot:run).
        await("bestellingen-app op " + Configuratie.APP_URL + " (omgeving " + Configuratie.OMGEVING + ")")
                .atMost(Duration.ofSeconds(60))
                .pollInterval(Duration.ofSeconds(1))
                .ignoreExceptions()
                .until(() -> RestAssured.get("/actuator/health").statusCode() == 200);
    }
}
