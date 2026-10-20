package nl.voorbeeld.bestellingen;

import java.time.OffsetDateTime;

public record BestellingAntwoord(Long id, String klant, String product, int aantal, OffsetDateTime aangemaaktOp) {

    static BestellingAntwoord van(Bestelling bestelling) {
        return new BestellingAntwoord(bestelling.getId(), bestelling.getKlant(), bestelling.getProduct(),
                bestelling.getAantal(), bestelling.getAangemaaktOp());
    }
}
