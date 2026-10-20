package nl.voorbeeld.bestellingen;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BestellingRepository extends JpaRepository<Bestelling, Long> {

    List<Bestelling> findByKlantOrderByIdAsc(String klant);
}
