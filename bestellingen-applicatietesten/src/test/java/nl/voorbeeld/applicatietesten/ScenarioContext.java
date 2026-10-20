package nl.voorbeeld.applicatietesten;

import io.restassured.response.Response;

import java.util.UUID;

/**
 * Gedeelde state binnen één scenario (via picocontainer in alle step-klassen geïnjecteerd).
 * Elk scenario krijgt unieke klantnamen zodat scenario's en testruns elkaars data niet zien.
 */
public class ScenarioContext {

    private final String runId = UUID.randomUUID().toString().substring(0, 8);
    private Response laatsteAntwoord;
    private Long laatsteBestellingId;

    public String klant(String naam) {
        return naam + "-" + runId;
    }

    public Response laatsteAntwoord() {
        return laatsteAntwoord;
    }

    public void laatsteAntwoord(Response antwoord) {
        this.laatsteAntwoord = antwoord;
    }

    public Long laatsteBestellingId() {
        return laatsteBestellingId;
    }

    public void laatsteBestellingId(Long id) {
        this.laatsteBestellingId = id;
    }
}
