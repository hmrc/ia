import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc" %% "simple-reactivemongo" % "7.19.0-play-26",
    ws,
    "uk.gov.hmrc" %% "bootstrap-play-26" % "0.41.0",
    "com.beachape" %% "enumeratum" % "1.5.13"
  )

  def test(scope: String = "it,test") = Seq(
    "org.scalatest" %% "scalatest" % "3.0.7" % scope,
    "org.pegdown" % "pegdown" % "1.6.0" % scope,
    "org.mockito" % "mockito-core" % "2.18.3" % scope,
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % scope,
    "com.typesafe.play" %% "play-test" % PlayVersion.current % scope
  )

}
