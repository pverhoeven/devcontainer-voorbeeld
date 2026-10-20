# bestellingen-applicatietesten

Cucumber applicatietesten voor `bestellingen-app`, in het Nederlands (`# language: nl`), in
`src/test/resources/features`. De testen praten met de applicatie via REST en controleren daarnaast
**rechtstreeks** Postgres (JDBC), en zetten daar ook zelf testdata in. Elk scenario gebruikt unieke
klantnamen, dus de database hoeft niet leeg tussen runs.

Deze repo heeft geen eigen devcontainer: je draait de testen lokaal (IntelliJ of Maven, JDK 25) tegen
een draaiende applicatie.

## Lokaal draaien

1. Start de applicatie in de devcontainer van `bestellingen-app` (`mvn spring-boot:run`). Die devcontainer
   publiceert de app op `localhost:8080` en Postgres op `localhost:5432`.
2. Draai de testen:

```bash
mvn verify                                      # alle scenario's, rapport in target/cucumber-rapport.html
mvn verify -Dcucumber.filter.name="REST API"    # één scenario
```

of in IntelliJ: `RunCucumberTest` (of een losse `.feature`) starten. De testen wachten maximaal een minuut
tot `/actuator/health` van de app OK is.

## Omgevingen

Welke app en database gebruikt worden staat per omgeving in `src/test/resources/omgevingen/`:

| Omgeving | Kiezen met | Gebruik |
|---|---|---|
| `lokaal` | standaard (geen profiel), ook vanuit IntelliJ | de app en Postgres uit de devcontainer |
| `tst` | `mvn verify -Ptst` | voorbeeld van een gedeelde testomgeving |

Een omgeving toevoegen: een `<naam>.properties` in die map en een Maven-profiel met
`<omgeving><naam></omgeving>` in de `pom.xml` (kopieer het `tst`-profiel). Losse waarden kun je altijd
overschrijven met een system property (`-Ddb.password=...`) of environment variabele (`DB_PASSWORD`);
gebruik dat voor wachtwoorden, die horen niet in git.
