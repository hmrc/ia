import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc" %% "simple-reactivemongo" % "7.20.0-play-26",
    ws,
    "uk.gov.hmrc" %% "bootstrap-play-26" % "0.41.0",
    "com.beachape" %% "enumeratum" % "1.5.13"
  )

  val test = Seq(
    "org.scalatest" %% "scalatest" % "3.0.7",
    "org.pegdown" % "pegdown" % "1.6.0",
    "org.mockito" % "mockito-core" % "2.18.3",
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2",
    "com.typesafe.play" %% "play-test" % PlayVersion.current
  )

}
