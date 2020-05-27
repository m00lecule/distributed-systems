name := "akka-shop"

version := "0.1"

scalaVersion := "2.13.2"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.31"

libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.7.2"