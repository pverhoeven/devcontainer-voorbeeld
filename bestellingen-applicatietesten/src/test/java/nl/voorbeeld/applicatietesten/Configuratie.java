package nl.voorbeeld.applicatietesten;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Verbindingsgegevens van de applicatie en de database, per omgeving uit
 * {@code src/test/resources/omgevingen/<omgeving>.properties}.
 * <p>
 * De omgeving komt uit de system property {@code omgeving} (gezet door het Maven-profiel, bijv. -Ptst);
 * zonder profiel, ook als je de testen direct vanuit IntelliJ start, is dat {@code lokaal}.
 * Losse waarden zijn te overschrijven met een system property ({@code -Ddb.password=...}) of
 * environment variabele ({@code DB_PASSWORD}).
 */
final class Configuratie {

    static final String OMGEVING = System.getProperty("omgeving", "lokaal");

    private static final Properties PROPERTIES = laad(OMGEVING);

    static final String APP_URL = lees("app.url");
    static final String DB_URL = lees("db.url");
    static final String DB_USER = lees("db.user");
    static final String DB_PASSWORD = lees("db.password");

    private Configuratie() {
    }

    private static Properties laad(String omgeving) {
        String pad = "/omgevingen/" + omgeving + ".properties";
        try (InputStream in = Configuratie.class.getResourceAsStream(pad)) {
            if (in == null) {
                throw new IllegalStateException("Onbekende omgeving '" + omgeving + "': " + pad + " bestaat niet");
            }
            Properties properties = new Properties();
            properties.load(in);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String lees(String sleutel) {
        String envNaam = sleutel.toUpperCase().replace('.', '_');
        String waarde = System.getProperty(sleutel, System.getenv(envNaam));
        if (waarde == null || waarde.isBlank()) {
            waarde = PROPERTIES.getProperty(sleutel);
        }
        if (waarde == null || waarde.isBlank()) {
            throw new IllegalStateException("Geen waarde voor '" + sleutel + "' in omgeving '" + OMGEVING
                    + "'; zet hem in omgevingen/" + OMGEVING + ".properties, -D" + sleutel + " of " + envNaam);
        }
        return waarde;
    }
}
