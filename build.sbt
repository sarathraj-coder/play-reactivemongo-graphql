name := """user-project-module"""
organization := "com.grossbit"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"
libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies ++= Seq(
  "org.reactivemongo" %% "play2-reactivemongo" % "0.12.6-play26",
"org.sangria-graphql" %% "sangria" % "1.3.0",
"org.sangria-graphql" %% "sangria-play-json" % "1.0.4",
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.grossbit.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.grossbit.binders._"
