package nl.voorbeeld.bestellingen;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/bestellingen")
public class BestellingController {

    private final BestellingService service;
    private final BestellingRepository repository;

    public BestellingController(BestellingService service, BestellingRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<BestellingAntwoord> plaats(@Valid @RequestBody BestellingVerzoek verzoek) {
        Bestelling bestelling = service.plaats(verzoek);
        var locatie = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(bestelling.getId());
        return ResponseEntity.created(locatie).body(BestellingAntwoord.van(bestelling));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BestellingAntwoord> haalOp(@PathVariable long id) {
        return ResponseEntity.of(repository.findById(id).map(BestellingAntwoord::van));
    }

    @GetMapping
    public List<BestellingAntwoord> zoekOpKlant(@RequestParam String klant) {
        return repository.findByKlantOrderByIdAsc(klant).stream().map(BestellingAntwoord::van).toList();
    }
}
