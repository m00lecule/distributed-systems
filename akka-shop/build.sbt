name := "akka-shop"

version := "0.1"

scalaVersion := "2.13.2"

lazy val akkaHttpVersion = "10.1.12"
lazy val akkaVersion     = "2.6.5"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.7.2"

libraryDependencies += "org.jsoup" % "jsoup" % "1.12.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
  "com.typesafe.akka" %% "akka-actor"               % akkaVersion
)

