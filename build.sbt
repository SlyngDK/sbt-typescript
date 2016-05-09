import sbt.Keys._

sbtPlugin := true

organization := "dk.slyng.sbt"

name := "sbt-typescript"

version := "0.2-SNAPSHOT"

scalaVersion := "2.10.4"

resolvers += Resolver.url(
  "Peter Kolloch's sbt-plugins",
  url("http://dl.bintray.com/kolloch/sbt-plugins/"))(
  Resolver.ivyStylePatterns)

addSbtPlugin("com.typesafe.sbt" %% "sbt-js-engine" % "1.1.3", "0.13", "2.10")
