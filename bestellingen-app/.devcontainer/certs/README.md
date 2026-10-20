# Root-certificaten van de organisatie

Zet hier de root-certificaten (en eventueel intermediate) waarmee de proxy het HTTPS-verkeer
inspecteert. Bij het bouwen van het image worden ze toegevoegd aan de Ubuntu certificate store én aan
de Java truststore (`cacerts`). Eén certificaat per bestand; `.crt`, `.cer` of `.pem`, Base64 of binair.

Dit zijn publieke certificaten (geen private keys), dus ze kunnen gewoon in git.

## Exporteren vanaf je Windows werkplek

PowerShell, pas het filter aan op de naam van jullie root-CA:

```powershell
Get-ChildItem Cert:\LocalMachine\Root |
  Where-Object Subject -like '*CN=Organisatie Root CA*' |
  ForEach-Object {
    $pem = "-----BEGIN CERTIFICATE-----`n" +
           [Convert]::ToBase64String($_.RawData, 'InsertLineBreaks') +
           "`n-----END CERTIFICATE-----`n"
    Set-Content -Path "organisatie-root-ca.crt" -Value $pem -Encoding ascii
  }
```

Of via de GUI: `certmgr.msc` → *Trusted Root Certification Authorities* → *Certificates* → certificaat →
*All Tasks → Export* → **Base-64 encoded X.509 (.CER)**.

Welk certificaat je nodig hebt zie je door in de browser een externe site te openen (bijv.
https://repo1.maven.org) en het slotje → certificaat te bekijken: het bovenste certificaat in de keten
is de root van de proxy.
