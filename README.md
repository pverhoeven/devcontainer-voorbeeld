# Devcontainer voorbeeld: Spring Boot + Postgres

Voorbeeld van een devcontainer-setup met podman, voor de overstap van DB2 naar Postgres. Een eerste
opzet met alleen een database; ActiveMQ e.d. kunnen later als extra service in de compose file.

De twee mappen stellen **twee losse git repo's** voor:

```
.
├── bestellingen-app/                 repo van het ontwikkelteam
│   ├── .devcontainer/                dev-container + Postgres
│   └── src/ ...                      Spring Boot 4.1 (Java 25)
└── bestellingen-applicatietesten/    repo van het testteam (geen devcontainer)
    └── src/test/ ...                 Cucumber applicatietesten
```

Alleen de app heeft een devcontainer. Daarin start je de applicatie vanuit de broncode, en die devcontainer
publiceert de app en Postgres op `localhost` van je eigen machine. De applicatietesten draai je gewoon
lokaal (IntelliJ of Maven op je werkplek) tegen die versie van de app:

```
je eigen machine                                bestellingen-app devcontainer
┌──────────────────────────────┐                ┌──────────────────────────────────┐
│ applicatietesten             │ localhost:8080 │ dev: mvn spring-boot:run         │
│ (IntelliJ / mvn verify) ─────┼───────────────▶│   │                              │
│        │                     │                │   ▼ localhost:5432               │
│        └─────────────────────┼───────────────▶│ postgres                         │
│              localhost:5432  │                └──────────────────────────────────┘
└──────────────────────────────┘
```

Binnen de devcontainer is Postgres óók `localhost:5432` (de dev-container deelt het netwerk van de
postgres container), dus overal gelden dezelfde adressen.

## Werkwijze

1. Open `bestellingen-app` in zijn devcontainer (zie hieronder) en start de app: `mvn spring-boot:run`,
   of `BestellingenApplicatie` vanuit IntelliJ.
2. Open `bestellingen-applicatietesten` gewoon lokaal in IntelliJ en draai `RunCucumberTest`, of
   `mvn verify`. Zonder Maven-profiel testen ze tegen `localhost`; zie de README van de testrepo voor
   andere omgevingen.

## Starten in IntelliJ IDEA

1. **Settings → Build, Execution, Deployment → Docker**: voeg een verbinding toe naar podman (op Windows
   via **WSL**, of de Podman Desktop machine) en controleer dat er *Connection successful* staat. Zet onder
   **Tools** het Docker Compose pad op `podman-compose`.
2. Open `.devcontainer/devcontainer.json` van `bestellingen-app` en klik op het icoon in de kantlijn →
   **Create Dev Container and Mount Sources…**
3. IntelliJ bouwt het image, start de services en opent een IDE die in de container draait.

Bij de eerste start draait `postCreateCommand` een Maven build, zodat de dependencies in de cache staan.

## Starten zonder IDE (devcontainer CLI)

Deze route is getest:

```bash
cd bestellingen-app
npx @devcontainers/cli up   --workspace-folder . --docker-path podman --docker-compose-path podman-compose
npx @devcontainers/cli exec --workspace-folder . --docker-path podman --docker-compose-path podman-compose bash
```

## Het image

`.devcontainer/Dockerfile` gaat uit van `eclipse-temurin:25-jdk-resolute` (Temurin, Ubuntu 26.04) en voegt
daar alleen aan toe wat je nodig hebt om te ontwikkelen: git, curl, psql, Maven (uit Maven Central, met
checksumcontrole) en een gebruiker **`developer`** met uid 1000. Dat uid komt overeen met de
standaardgebruiker in WSL, zodat bestandsrechten op de gemounte code kloppen. De standaardgebruiker
`ubuntu` (ook uid 1000) wordt daarvoor verwijderd.

Het eigen Temurin-image van de organisatie gebruiken kan zonder de Dockerfile te wijzigen, zolang het op
Debian/Ubuntu gebaseerd is. Zet in `compose.yaml` bij `dev.build`:

```yaml
      args:
        BASE_IMAGE: registry.intern.example/temurin:25-jdk
```

## Podman op Windows met WSL

Getest op macOS met een **rootless** podman machine (podman 6.1, podman-compose 1.6, devcontainer CLI
0.89). Dat is dezelfde opzet als de standaard Podman Desktop machine op Windows (dat is ook een
rootless podman in een Linux VM, op Windows via WSL). Op een Windows werkplek zelf is dit nog
**niet** getest — dat is de volgende stap.

### Aanbevolen: alles binnen WSL

De snelste en minst foutgevoelige route:

1. Clone `bestellingen-app` **in het WSL bestandssysteem** (bijv. `~/code/...` in je Ubuntu distro), niet op
   `C:\` / `/mnt/c`. Bind mounts vanaf de Windows schijf zijn traag en geven rechtenproblemen.
2. Zorg dat `podman` en `podman-compose` in die WSL distro beschikbaar zijn
   (`sudo apt install podman podman-compose`, of `pipx install podman-compose` voor een recentere versie).
3. Kies in IntelliJ bij de Docker-verbinding die WSL distro.

De IntelliJ-route zelf is nog niet getest; de devcontainer CLI-route wel, net als testen vanaf de host
tegen de gepubliceerde poorten. Of die poorten uit de podman machine ook op `localhost` van Windows
uitkomen, moet op een Windows werkplek nog blijken (Podman Desktop stuurt gepubliceerde poorten normaal
door naar Windows).

### Waarom de compose file is zoals hij is

- **`userns_mode: keep-id`** op de dev-container: bij rootless podman is root in de container jouw
  gebruiker op de host, en de `developer` gebruiker (uid 1000) een "vreemde" sub-uid. Zonder `keep-id` kun
  je in de container niet in je eigen workspace schrijven, of krijg je op de host bestanden met rare
  eigenaren. Met `keep-id` + `updateRemoteUserUID` is `developer` in de container gelijk aan jouw gebruiker.
- **`x-podman: in_pod: false`**: podman-compose zet containers standaard in één pod, en dat gaat niet
  samen met `keep-id`. Docker Compose negeert dit blok.
- **`network_mode: service:postgres`** op de dev-container: dev en postgres delen één netwerk, dus de
  database is binnen de devcontainer `localhost:5432`, net als op je eigen machine. De poorten (8080 voor
  de app, 5432 voor Postgres) worden daarom bij de postgres service gepubliceerd, alleen op `127.0.0.1`.
  Draait er op je werkplek al iets op 5432 of 8080, pas dan de linkerkant van de poort aan.
- **Maven cache**: het volume `devcontainers-maven-repo` heeft een vaste naam, zodat de dependencies een
  rebuild of `down -v` van de devcontainer overleven.
- **Volledige image-namen** (`docker.io/library/postgres:18`): podman zoekt korte namen op in meerdere
  registries en vraagt dan interactief welke je bedoelt, of faalt.

### Werk-specifieke dingen om nog te regelen

- **Artifactory URL invullen**: alle Maven downloads in de devcontainer lopen via een mirror in
  `.devcontainer/maven/settings.xml` (in het image: `/opt/maven/conf/settings.xml`). Er staat nu nog
  Maven Central als placeholder; vervang die door de Artifactory URL en bouw de devcontainer opnieuw.
  Inloggen is niet nodig (alleen lezen). Persoonlijke aanvullingen kunnen in `~/.m2/settings.xml`.
- **Interne registry / proxy**: als docker.io niet direct bereikbaar is, de images vervangen door de
  gemirrorde varianten.
- **Root-certificaten (TLS-inspectie)**: zie hieronder.
- **Maven download**: gaat `repo1.maven.org` niet rechtstreeks, pas dan de URL in de `Dockerfile` aan
  naar Artifactory (`.../org/apache/maven/apache-maven/...`).

## Root-certificaten van de organisatie (TLS-inspectie)

Inspecteert de proxy HTTPS-verkeer, dan moet het root-certificaat van de organisatie op **drie plekken**
vertrouwd worden:

| Waar | Waarvoor | Hoe |
|---|---|---|
| podman machine / WSL distro | `podman pull` van docker.io e.d. | eenmalig per werkplek, zie hieronder |
| Ubuntu store in het devcontainer-image | curl, git, apt via https | `.devcontainer/certs/`, automatisch bij build |
| Java truststore (`cacerts`) in het image | Maven, de applicatie | `.devcontainer/certs/`, automatisch bij build |

**In het image**: zet het certificaat in `.devcontainer/certs/` (exporteren vanaf
Windows: zie `certs/README.md`). `installeer-certificaten.sh` voegt alles uit die map toe aan de Ubuntu
store én de Java truststore, als eerste stap in de `Dockerfile`, zodat ook apt en de Maven-download erna
werken. Zonder certificaten in de map wordt die stap overgeslagen; het werkt dus ook thuis.

Getest met een zelfgemaakte test-CA (zowel PEM als binair DER): curl, git en Java (`HttpClient`) als
gebruiker `developer` vertrouwen een HTTPS-server met een certificaat van die CA, en zonder de stap faalt
Java met `PKIX path building failed`.

**In de podman machine** (niet getest op Windows): de standaard Podman Desktop machine op Windows is een
WSL distro op basis van Fedora, waar je Windows schijf onder `/mnt/c` hangt:

```powershell
podman machine ssh "sudo cp /mnt/c/Users/<jouw-naam>/organisatie-root-ca.crt /etc/pki/ca-trust/source/anchors/ && sudo update-ca-trust"
podman machine stop; podman machine start
```

Draai je podman in je eigen Ubuntu WSL distro, dan daar:

```bash
sudo cp organisatie-root-ca.crt /usr/local/share/ca-certificates/ && sudo update-ca-certificates
```

## Opruimen

```bash
podman-compose -f bestellingen-app/.devcontainer/compose.yaml down      # containers weg, data blijft
podman-compose -f bestellingen-app/.devcontainer/compose.yaml down -v   # ook de database
```
