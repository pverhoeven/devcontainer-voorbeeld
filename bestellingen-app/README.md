# bestellingen-app

Spring Boot 4.1 applicatie (Java 25) met Postgres.

| Wat | Hoe |
|---|---|
| `POST /api/bestellingen` | slaat een bestelling op |
| `GET /api/bestellingen/{id}` | één bestelling ophalen |
| `GET /api/bestellingen?klant=...` | bestellingen van een klant |

Het schema wordt beheerd met Flyway (`src/main/resources/db/migration`), Hibernate valideert alleen
(`ddl-auto: validate`). Verbindingsgegevens komen uit environment variabelen (`DB_HOST`, `DB_PORT`,
`DB_NAME`, `DB_USER`, `DB_PASSWORD`) met defaults die bij de devcontainer passen (`localhost:5432`).

## Devcontainer

Start `.devcontainer/devcontainer.json` (zie de algemene README). Je krijgt een dev-container met
Temurin 25 en Maven, plus Postgres 18. In de container:

```bash
mvn spring-boot:run      # app op localhost:8080
psql                     # staat al goed via PG* environment variabelen
```

De devcontainer publiceert de app (`8080`) en Postgres (`5432`) op `localhost` van je eigen machine. Zo
kun je de applicatietesten (repo `bestellingen-applicatietesten`) lokaal draaien tegen de versie die je
hier gestart hebt, en met bijv. de Database-tool van IntelliJ in de database kijken.

## Proxy met TLS-inspectie

Zet de root-certificaten van de organisatie in `.devcontainer/certs/` (zie de README daar) en
bouw de devcontainer opnieuw.
