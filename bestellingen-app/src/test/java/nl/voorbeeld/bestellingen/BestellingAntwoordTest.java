package nl.voorbeeld.bestellingen;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BestellingAntwoordTest {

    @Test
    void neemtAlleVeldenOverVanBestelling() {
        Bestelling bestelling = new Bestelling("Jansen", "fiets", 2);

        BestellingAntwoord antwoord = BestellingAntwoord.van(bestelling);

        assertThat(antwoord.klant()).isEqualTo("Jansen");
        assertThat(antwoord.product()).isEqualTo("fiets");
        assertThat(antwoord.aantal()).isEqualTo(2);
        assertThat(antwoord.aangemaaktOp()).isNotNull();
    }
}
