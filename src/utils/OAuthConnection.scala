package utils

import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.YahooApi
import org.scribe.exceptions.OAuthException
import org.scribe.model._
import org.scribe.oauth.OAuthService
import scala.collection.mutable
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger
import Cache.oAuthTokens
import utils._
import app._

case class YahooApiInfo(apiKey: String, apiSecret: String)

object OAuthConnection {
  
  private val log = Logger.getLogger(this.getClass)

  def connect: OAuthConnection = {
    val oAuthConn = new OAuthConnection(YahooApiInfo(Configs.Yahoo.apiKey, Configs.Yahoo.apiSecret))
    val requestUrl = oAuthConn.retrieveAuthorizationUrl
    val verifier = {
      println(s"\nGo to the following URL in a browser...\n\t$requestUrl\n...then enter the code from that site here:\n>> ")
      scala.io.StdIn.readLine
    }
    oAuthConn.retrieveAccessToken(verifier)
    log.info("Connected: " + oAuthConn.connect)
    log.info("Authorized: " + oAuthConn.authorized)
    oAuthConn
  }

}

class OAuthConnection(val info: YahooApiInfo, val callBackUrl: Option[String] = None) {
  val service: OAuthService = callBackUrl match {
    case Some(url) => new ServiceBuilder()
      .provider(classOf[YahooApi])
      .apiKey(info.apiKey)
      .apiSecret(info.apiSecret)
      .callback(url)
      .build()
    case None => new ServiceBuilder()
      .provider(classOf[YahooApi])
      .apiKey(info.apiKey)
      .apiSecret(info.apiSecret)
      .build()
  }

  var requestToken: Token = null
  var verifier: Verifier = null
  var accessToken: Token = null
  var oauthSessionHandle: String = ""
  var oAuthToken: OAuthToken = null
  var authorized: Boolean = false

  private val log = Logger.getLogger(this.getClass)

  def connect: Boolean = {
    if (oAuthTokens.cache.isEmpty) false
    else {
      val token = oAuthTokens.cache.head
      accessToken = new Token(token.token, token.secret)
      verifier = new Verifier(token.verifier)
      oauthSessionHandle = token.sessionHandle
      authorized = true
      true
    }
  }

  def retrieveAuthorizationUrl: String = {
    log.info("=== Yahoo's OAuth Workflow ===")
    log.info("Fetching the Request Token...")
    requestToken = service.getRequestToken
    val authUrl = service.getAuthorizationUrl(requestToken)
    log.info("Got the Request Token: " + authUrl)
    authUrl
  }

  def retrieveAccessToken(token: String): Boolean = {
    verifier = new Verifier(token)

    // Trade the Request Token and Verfier for the Access Token
    log.info("Trading the Request Token for an Access Token...")
    try {
      accessToken = service.getAccessToken(requestToken, verifier)
      val fullResponse = accessToken.getRawResponse
      log.info("[Raw Response] : " + fullResponse)

      // Gather the indices of the session handle
      val startIndex = fullResponse.indexOf("&oauth_session_handle=")
      val endIndex = fullResponse.indexOf("&oauth_authorization_expires_in", startIndex)

      oauthSessionHandle = fullResponse.substring(startIndex + 22, endIndex)
      log.info("[Session handle] :" + oauthSessionHandle)

      val newToken = OAuthToken(accessToken.getToken, verifier.getValue, accessToken.getSecret, oauthSessionHandle)
      oAuthTokens.add(newToken)
      authorized = true
      true
    } catch {
      case e: Exception =>
        log.error("Failed to get access token", e)
        false
    }
  }

  def requestData(url: String, v: Verb): String = {
    var request = new OAuthRequest(v, url)
    service.signRequest(accessToken, request) // the access token from step 4
    var response = request.send.asInstanceOf[Response]
    if (!response.isSuccessful) {
      refreshToken
      request = new OAuthRequest(v, url)
      service.signRequest(accessToken, request) // the access token from step 4
      response = request.send.asInstanceOf[Response]
    }
    return response.getBody
  }

  def refreshToken: Unit = {
    val cachedToken = oAuthTokens.cache.head
    val tempSessionHandle = cachedToken.sessionHandle

    connect
    val request = new OAuthRequest(Verb.GET, "https://api.login.yahoo.com/oauth/v2/get_token")
    request.addOAuthParameter("oauth_session_handle", oauthSessionHandle)
    service.signRequest(accessToken, request)
    val response = request.send
    try {
      accessToken = classOf[YahooApi].newInstance.getAccessTokenExtractor.extract(response.getBody)
    } catch {
      case e: Exception => log.error("Failed to get access token", e)
    }
    accessToken = service.getAccessToken(accessToken, verifier)
    oAuthTokens.remove(cachedToken)

    val newToken = OAuthToken(accessToken.getToken, verifier.getValue, accessToken.getSecret, oauthSessionHandle)
    oAuthToken = newToken // should we add this to the cache?

  }
}

case class OAuthToken(token: String, verifier: String, secret: String, sessionHandle: String) extends Serializable {
  val id: Int = OAuthToken.nextID
}

object OAuthToken {
  private var idSequence: Int = 0

  def nextID: Int = {
    idSequence = idSequence + 1
    idSequence
  }
}
