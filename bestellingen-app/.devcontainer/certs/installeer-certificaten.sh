#!/bin/sh
# Voegt de root-certificaten van de organisatie toe aan het image, zodat verkeer door de
# TLS-inspectie van de proxy vertrouwd wordt:
#   - Ubuntu store (/etc/ssl/certs)   voor curl, git, apt via https, ...
#   - Java truststore (cacerts)       voor Maven en de applicatie zelf
#
# Leest alle *.crt, *.cer en *.pem bestanden uit de opgegeven map (één certificaat per bestand),
# zowel Base64/PEM als binair/DER. Zonder certificaten doet het script niets.
set -eu

map="${1:?gebruik: $0 <map met certificaten>}"
aantal=0

for bestand in "$map"/*.crt "$map"/*.cer "$map"/*.pem; do
    [ -f "$bestand" ] || continue
    naam=$(basename "${bestand%.*}")
    doel="/usr/local/share/ca-certificates/$naam.crt"

    # update-ca-certificates wil PEM met extensie .crt; Windows exporteert vaak DER als .cer.
    if grep -q "BEGIN CERTIFICATE" "$bestand"; then
        openssl x509 -in "$bestand" -out "$doel"
    else
        openssl x509 -inform DER -in "$bestand" -out "$doel"
    fi

    keytool -importcert -noprompt -cacerts -storepass changeit -alias "$naam" -file "$doel"
    echo "Certificaat toegevoegd: $naam ($(openssl x509 -in "$doel" -noout -subject))"
    aantal=$((aantal + 1))
done

if [ "$aantal" -gt 0 ]; then
    update-ca-certificates
else
    echo "Geen certificaten gevonden in $map, niets toegevoegd."
fi
