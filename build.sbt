name := """NotiLytics"""
organization := "com.NotiLytics"
version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.17"
javacOptions ++= Seq("-source", "17", "-target", "17")
//libraryDependencies += guice


libraryDependencies ++= Seq(
  //  javaWs
  guice,
  javaWs,

  // Test libraries
  "org.mockito" % "mockito-core" % "5.11.0" % Test,
  "org.assertj" % "assertj-core" % "3.25.3" % Test,

  // Use JUnit 4 + runner so sbt can discover @Test tests
  "junit" % "junit" % "4.13.2" % Test,
  "com.novocode" % "junit-interface" % "0.11" % Test
)

// Optional: exclude Play’s generated classes from coverage so they don’t dilute the %.
// This does NOT exclude your models or your controller implementations.
// Note: Ensure the sbt-jacoco plugin is added in project/plugins.sbt before using this key.
Test / jacocoExcludes ++= Seq(
  "router.*",
  "controllers.routes*",
  "controllers.Reverse*",
  "controllers.javascript.*",
  "controllers.ref.*",
  "utils.SentimentTuple*",
  "views.results*"
)


