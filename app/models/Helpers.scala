package models

import java.io.{ByteArrayOutputStream, InputStream}
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl._

object Helpers {
  /** Copy Stream https://stackoverflow.com/a/6928211 */
  def toArr(input: InputStream) = {
    val output = new ByteArrayOutputStream
    Iterator
      .continually(input.read)
      .takeWhile(_ != -1)
      .foreach(output.write)
    output.toByteArray
  }

  /** Ignores HTTPS Certificates https://stackoverflow.com/a/24501156 */
  def fixHttps() = {
    val trustAllCerts = new X509TrustManager {
      def getAcceptedIssuers: Array[X509Certificate] = null
      def checkClientTrusted(certs: Array[X509Certificate], authType: String) {}
      def checkServerTrusted(certs: Array[X509Certificate], authType: String) {}
    }
    val allHostsValid = new HostnameVerifier {
      def verify(hostname: String, session: SSLSession) = true
    }
    val sc = SSLContext.getInstance("SSL")
    sc.init(null, Array[TrustManager](trustAllCerts), new SecureRandom)
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory)
    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid)
  }
}
