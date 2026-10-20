package nl.voorbeeld.bestellingen;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "bestelling")
public class Bestelling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String klant;

    @Column(nullable = false)
    private String product;

    @Column(nullable = false)
    private int aantal;

    @Column(name = "aangemaakt_op", nullable = false)
    private OffsetDateTime aangemaaktOp;

    protected Bestelling() {
    }

    public Bestelling(String klant, String product, int aantal) {
        this.klant = klant;
        this.product = product;
        this.aantal = aantal;
        this.aangemaaktOp = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getKlant() {
        return klant;
    }

    public String getProduct() {
        return product;
    }

    public int getAantal() {
        return aantal;
    }

    public OffsetDateTime getAangemaaktOp() {
        return aangemaaktOp;
    }
}
