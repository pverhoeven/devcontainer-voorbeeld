# language: nl
Functionaliteit: Bestellingen plaatsen
  Als klant wil ik een bestelling kunnen plaatsen,
  zodat deze wordt opgeslagen en ik hem later terug kan vinden.

  Scenario: Bestelling plaatsen via de REST API
    Als klant "Jansen" via de API 2 x "fiets" bestelt
    Dan antwoordt de applicatie met status 201
    En staat de bestelling van 2 x "fiets" in de database

  Scenario: Bestellingen die al in de database staan opvragen
    Gegeven klant "De Vries" heeft in de database al een bestelling van 1 x "helm"
    Als ik de bestellingen van klant "De Vries" opvraag
    Dan antwoordt de applicatie met status 200
    En bevat het antwoord 1 bestelling van 1 x "helm"

  Abstract Scenario: Ongeldige bestellingen worden geweigerd
    Als klant "<klant>" via de API <aantal> x "<product>" bestelt
    Dan antwoordt de applicatie met status 400

    Voorbeelden:
      | klant  | aantal | product |
      | Bakker | 0      | slot    |
      | Bakker | 1      |         |

  Scenario: Onbekende bestelling opvragen
    Als ik bestelling 999999999 opvraag
    Dan antwoordt de applicatie met status 404
