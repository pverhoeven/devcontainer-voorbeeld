package nl.voorbeeld.bestellingen;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record BestellingVerzoek(@NotBlank String klant, @NotBlank String product, @Min(1) int aantal) {
}
