package uk.gov.hmrc.tests

import org.scalatest.BeforeAndAfterEach
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.support.ItSpec

class IaTests extends ItSpec with BeforeAndAfterEach{
  val iaBaseUrl = "http://localhost:8051"
  val uploadUrl: String = iaBaseUrl + "/ia/upload"
  val switchUrl: String = iaBaseUrl + "/ia/switch"
  val countUrl: String = iaBaseUrl + "/ia/count"
  val dropUrl: String = iaBaseUrl + "/ia/drop-all"
  val setDbUrl: String = iaBaseUrl + "/ia/set/"
  val findUrlBase: String = iaBaseUrl + "/ia/"

  val utrListJson = Json.arr(
    Json.obj("utr" -> "1234567890"),
    Json.obj("utr" -> "1234567891")
  )

  def uploadUtrs(request: JsValue) = {
    httpClient.POST(uploadUrl, request).futureValue
  }
  def uploadUtr(utr: String) = {
    httpClient.POSTEmpty(uploadUrl + s"/$utr").futureValue
  }
  def switch() = {
    httpClient.POSTEmpty(switchUrl).futureValue
  }
  def count() = {
    httpClient.GET(countUrl).futureValue
  }
  def drop() = {
    httpClient.POSTEmpty(dropUrl).futureValue
  }
  def setDb(dbName:String) = {
    httpClient.POSTEmpty(setDbUrl+dbName).futureValue
  }

  def find(utr: String) = {
    httpClient.GET(findUrlBase + utr).futureValue
  }
//todo fix on mpnday make more rbust

  override def beforeEach() = {
    drop()
    setDb("DB1")
  }

  "client" should {

    "be able to upload the utrs and get how many where updated back" in {
      val resultPost = uploadUtrs(utrListJson)
      resultPost.status shouldBe 200
      resultPost.body shouldBe "2"
    }


    "be able to upload a single urt" in {
      val resultPost = uploadUtr("999999999")
      resultPost.status shouldBe 200
    }

    "be able to check if a utr is in the db" in {
      uploadUtr("1234567890")
      val findResult = find("1234567890")
      findResult.status shouldBe 200
    }
    "be able to check if a utr not in the db" in {
      uploadUtrs(utrListJson)
      val findResult = find("9999999999")
      findResult.status shouldBe 204
    }

    "be able to drop the db" in {
      uploadUtrs(utrListJson)
      drop()
      val findResult = find("1234567890")
      findResult.status shouldBe 204
    }
    "be able see to upload to the inactive db" in {
      uploadUtrs(utrListJson)

      val countResult = count()
      countResult.status shouldBe 200
      countResult.body shouldBe "DbCount(SSTTP is currently pointed to DataBase DB1,count DataBase 1 is 0,count DataBase 2 is 2)"
    }
    //todo write more and better it tests no time to do this sprint
    "be able to switch to another db and drop the other one " in {
      val resultPost = uploadUtrs(utrListJson)
      switch()
      val countResult = count()
      countResult.status shouldBe 200
      countResult.body shouldBe "DbCount(SSTTP is currently pointed to DataBase DB2,count DataBase 1 is 0,count DataBase 2 is 2)"
    }
  }
}
