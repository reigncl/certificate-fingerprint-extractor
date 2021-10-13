# Certificate Fingerprint Extractor

This tool extract SHA-256 Certificate Fingerprint from hostname or certificate (.crt, .der or .pem) file.

In order to implement <strong>Certificate Pinning</strong> during an SSL connection, a certificate fingerprint needs to be provided.

### Usage examples:
```bash
   $ peer-certificate-extractor -n, --hostname <hostname> (e.g., google.com, facebook.com)
```
or
```bash
   $ peer-certificate-extractor -c, --certificate <certificate> (e.g., cert.der, cert.crt, cert.pem)
```
### Example output:
```bash
  Fingerprints from google.com certificates:
  sha256/We74o5ME3USRtL6+B2UhXnwY9FR91QPJMYDtUNk6tEc=
  sha256/zCTnfLwLKbS9S2sbp+uFz4KZOocFvXxkV06Ce9O5M2w=
  sha256/hxqRlPTu1bMS/0DITB1SSu0vd4u/8l8TjPgfaAp63Gc=
```

## Certificate Pinning with OkHttp

Enabling okhttp certificate pinning (Example in Kotlin):
```kotlin
  ...
  val certificatePinner = CertificatePinner.Builder()
    .add("google.com", "sha256/We74o5ME3USRtL6+B2UhXnwY9FR91QPJMYDtUNk6tEc=")
    .add("google.com", "sha256/zCTnfLwLKbS9S2sbp+uFz4KZOocFvXxkV06Ce9O5M2w=")
    .add("google.com", "sha256/hxqRlPTu1bMS/0DITB1SSu0vd4u/8l8TjPgfaAp63Gc=")
    .build()
    
  val client = OkHttpClient.Builder()
    .certificatePinner(certificatePinner)
    .build()
  ...
```
