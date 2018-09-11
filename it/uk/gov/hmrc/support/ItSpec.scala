package uk.gov.hmrc.support

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{AppendedClues, Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.hooks.HttpHook
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.http.ws.WSHttp

import scala.concurrent.ExecutionContext

trait ItSpec extends WordSpec with ScalaFutures with IntegrationPatience with Matchers with AppendedClues
  with GuiceOneServerPerSuite {
  override def fakeApplication() =
    new GuiceApplicationBuilder()
      .configure(
      ).build()

  override lazy val port: Int = 8051
  implicit lazy val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global


  implicit val hc = HeaderCarrier()

  val httpClient: HttpClient = new HttpClient with WSHttp {
    override def wsClient: WSClient = app.injector.instanceOf(classOf[WSClient])

    override val hooks: Seq[HttpHook] = Nil
  }
}
