import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.FileInputStream
import java.io.IOException
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.util.*
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size < 2) {
        printUsageAndExit("Invalid number of arguments")
    }
    when (args[0]) {
        "-n",
        "--hostname" -> fingerprintsFromHostname(args[1])
        "-c",
        "--certificate" -> fingerprintFromCertificate(args[1])
        else -> printUsageAndExit("Invalid option \"${args[0]}\"")
    }
}

fun printUsageAndExit(exitCause: String) {
    println(
        "Error: $exitCause\n\n" +
                "Tool to extract HTTPS Certificate Fingerprints\n" +
                "Usage examples:\n" +
                "peer-certificate-extractor -n <hostname> (e.g., google.com, facebook.com)\n" +
                "peer-certificate-extractor -c <certificate> (e.g., cert.der, cert.crt, cert.pem)"
    )
    exitProcess(1)
}

fun fingerprintsFromHostname(hostName: String) {
    val fingerprints = mutableListOf<String>()
    val client = OkHttpClient.Builder().build()
    val request = Request.Builder().url("https://$hostName").build()
    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        for (certificate in response.handshake!!.peerCertificates) {
            fingerprints.add(CertificatePinner.pin(certificate))
        }
    }
    println("Fingerprints from $hostName certificates:\n${fingerprints.joinToString("\n")}")
}

fun fingerprintFromCertificate(filePath: String) {
    var inputStream: FileInputStream? = null
    try {
        inputStream = FileInputStream(filePath)
        val certificate = CertificateFactory.getInstance("X509")
            .generateCertificate(inputStream)
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val publicKeySha256 = messageDigest.digest(certificate.publicKey.encoded)
        val publicKeyShaBase64 = Base64.getEncoder().encode(publicKeySha256)
        println("Fingerprint from $filePath certificate file:\nsha256/" + String(publicKeyShaBase64))
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            inputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}