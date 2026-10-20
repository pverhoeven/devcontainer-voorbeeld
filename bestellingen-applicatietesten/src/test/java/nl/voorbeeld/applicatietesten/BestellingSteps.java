package nl.voorbeeld.applicatietesten;

import io.cucumber.java.nl.Als;
import io.cucumber.java.nl.Dan;
import io.cucumber.java.nl.En;
import io.cucumber.java.nl.Gegeven;
import io.restassured.http.ContentType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class BestellingSteps {

    private final ScenarioContext context;
    private final Database database;

    public BestellingSteps(ScenarioContext context, Database database) {
        this.context = context;
        this.database = database;
    }

    @Gegeven("klant {string} heeft in de database al een bestelling van {int} x {string}")
    public void klantHeeftAlEenBestelling(String klant, int aantal, String product) {
        database.voerUit("insert into bestelling (klant, product, aantal, aangemaakt_op) values (?, ?, ?, ?)",
                context.klant(klant), product, aantal, OffsetDateTime.now());
    }

    @Als("klant {string} via de API {int} x {string} bestelt")
    public void klantBesteltViaApi(String klant, int aantal, String product) {
        var antwoord = given()
                .contentType(ContentType.JSON)
                .body(Map.of("klant", context.klant(klant), "product", product, "aantal", aantal))
                .post("/api/bestellingen");
        context.laatsteAntwoord(antwoord);
        if (antwoord.statusCode() == 201) {
            context.laatsteBestellingId(antwoord.jsonPath().getLong("id"));
        }
    }

    @Als("ik de bestellingen van klant {string} opvraag")
    public void ikDeBestellingenVanKlantOpvraag(String klant) {
        context.laatsteAntwoord(given().queryParam("klant", context.klant(klant)).get("/api/bestellingen"));
    }

    @Als("ik bestelling {long} opvraag")
    public void ikBestellingOpvraag(long id) {
        context.laatsteAntwoord(given().get("/api/bestellingen/{id}", id));
    }

    @Dan("antwoordt de applicatie met status {int}")
    public void antwoordtMetStatus(int status) {
        assertThat(context.laatsteAntwoord().statusCode()).isEqualTo(status);
    }

    @En("staat de bestelling van {int} x {string} in de database")
    public void staatDeBestellingInDeDatabase(int aantal, String product) {
        var rijen = database.query("select product, aantal from bestelling where id = ?", context.laatsteBestellingId());
        assertThat(rijen).singleElement().satisfies(rij -> {
            assertThat(rij).containsEntry("product", product);
            assertThat(rij).containsEntry("aantal", aantal);
        });
    }

    @En("bevat het antwoord {int} bestelling(en) van {int} x {string}")
    public void bevatHetAntwoordBestellingen(int aantalBestellingen, int aantal, String product) {
        List<Map<String, Object>> bestellingen = context.laatsteAntwoord().jsonPath().getList("$");
        assertThat(bestellingen).hasSize(aantalBestellingen).allSatisfy(b -> {
            assertThat(b).containsEntry("product", product);
            assertThat(b).containsEntry("aantal", aantal);
        });
    }
}
