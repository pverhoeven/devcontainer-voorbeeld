package nl.voorbeeld.bestellingen;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Slaat bestellingen op in Postgres.
 */
@Service
public class BestellingService {

    private final BestellingRepository repository;

    public BestellingService(BestellingRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Bestelling plaats(BestellingVerzoek verzoek) {
        return repository.save(new Bestelling(verzoek.klant(), verzoek.product(), verzoek.aantal()));
    }
}
