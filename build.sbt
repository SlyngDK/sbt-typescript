import sbt.Keys._
import sbt.ScriptedPlugin._
import sbt._

sbtPlugin := true

organization := "dk.slyng.sbt"

name := "sbt-typescript"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "org.webjars" % "coffee-script-node" % "1.7.1",
  "org.webjars" % "mkdirp" % "0.3.5"
)

resolvers ++= Seq(
  "Typesafe Releases Repository" at "http://repo.typesafe.com/typesafe/releases/",
  Resolver.url("sbt snapshot plugins", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots"))(Resolver.ivyStylePatterns),
  Resolver.sonatypeRepo("snapshots"),
  "Typesafe Snapshots Repository" at "http://repo.typesafe.com/typesafe/snapshots/",
  Resolver.mavenLocal
)

addSbtPlugin("com.typesafe.sbt" %% "sbt-js-engine" % "1.0.0")

scalacOptions += "-feature"

scriptedSettings

scriptedLaunchOpts <+= version apply { v => s"-Dproject.version=$v" }