# Basis: Temurin JDK 25 op Ubuntu 26.04 ('resolute'). De Ubuntu-versie staat bewust vast in de tag,
# anders schuift het OS ongemerkt mee. Te vervangen door het eigen Temurin-image van de organisatie
# met --build-arg BASE_IMAGE=..., zolang dat ook op Debian/Ubuntu (apt) gebaseerd is.
ARG BASE_IMAGE=docker.io/library/eclipse-temurin:25-jdk-resolute
FROM ${BASE_IMAGE}

ARG USERNAME=developer
ARG USER_UID=1000
ARG USER_GID=1000
ARG MAVEN_VERSION=3.9.16

# Root-certificaten van de organisatie (TLS-inspectie) als eerste, zodat alle stappen hierna
# (apt, Maven download, Maven builds in de container) via de proxy naar buiten kunnen. Zie certs/README.md.
COPY certs/ /tmp/certs/
RUN sh /tmp/certs/installeer-certificaten.sh /tmp/certs && rm -rf /tmp/certs

# Basistools voor ontwikkelen in een container, plus psql (Ubuntu 26.04 levert Postgres 18).
RUN apt-get update \
    && export DEBIAN_FRONTEND=noninteractive \
    && apt-get install -y --no-install-recommends \
        bash-completion ca-certificates curl git less procps sudo unzip \
        postgresql-client \
    && rm -rf /var/lib/apt/lists/*

# Maven uit Maven Central (via een interne Nexus/Artifactory proxy kan dit ook), met checksumcontrole.
RUN curl -fsSLo /tmp/maven.tar.gz \
        https://repo1.maven.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    && echo "$(curl -fsSL https://repo1.maven.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.tar.gz.sha512)  /tmp/maven.tar.gz" | sha512sum -c - \
    && mkdir -p /opt/maven \
    && tar -xzf /tmp/maven.tar.gz -C /opt/maven --strip-components=1 \
    && ln -s /opt/maven/bin/mvn /usr/local/bin/mvn \
    && rm /tmp/maven.tar.gz
ENV MAVEN_HOME=/opt/maven

# Globale Maven settings: alle downloads via de Artifactory van de organisatie (zie maven/settings.xml).
COPY maven/settings.xml /opt/maven/conf/settings.xml

# Ubuntu images (vanaf 23.04) hebben standaard een gebruiker 'ubuntu' met uid 1000; die maakt plaats
# voor onze eigen gebruiker. uid 1000 komt overeen met de standaardgebruiker in WSL.
RUN if id ubuntu >/dev/null 2>&1; then userdel -r ubuntu; fi \
    && groupadd --gid ${USER_GID} ${USERNAME} \
    && useradd --uid ${USER_UID} --gid ${USER_GID} --create-home --shell /bin/bash ${USERNAME} \
    && echo "${USERNAME} ALL=(root) NOPASSWD:ALL" > /etc/sudoers.d/${USERNAME} \
    && chmod 0440 /etc/sudoers.d/${USERNAME}

# Map voor de lokale Maven repository vooraf aanmaken, zodat het named volume
# de juiste eigenaar krijgt (anders is het van root en kan Maven er niet in schrijven).
RUN mkdir -p /home/${USERNAME}/.m2/repository && chown -R ${USERNAME}:${USERNAME} /home/${USERNAME}/.m2

USER ${USERNAME}
