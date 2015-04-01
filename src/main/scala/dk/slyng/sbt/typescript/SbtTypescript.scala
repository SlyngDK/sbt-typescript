package dk.slyng.sbt

import com.typesafe.sbt.jse.SbtJsTask
import sbt.Keys._
import sbt.{File, _}
import spray.json.{JsBoolean, JsObject, JsString}

object Import {


  object TypescriptKeys {
    val typescript = TaskKey[Seq[File]]("typescript", "Invoke the typescript compiler.")

    val tscPath = SettingKey[String]("typescript-tscPath", "Path to the tsc command.")
    val sourceMap = SettingKey[Boolean]("typescript-sourceMap", "Outputs a sourcemap.")
    val noEmitOnError = SettingKey[Boolean]("typescript-noEmitOnError", "Do not emit outputs if any type checking errors were reported.")
  }

}

object SbtTypescript extends AutoPlugin {

  override def requires = SbtJsTask

  override def trigger = AllRequirements

  val autoImport = Import

  import autoImport.TypescriptKeys._
  import com.typesafe.sbt.jse.SbtJsTask.autoImport.JsTaskKeys._
  import com.typesafe.sbt.web.Import.WebKeys._
  import com.typesafe.sbt.web.SbtWeb.autoImport._

  val typescriptUnscopedSettings = Seq(
    includeFilter := "*.ts",
    excludeFilter := "*.d.ts",

    jsOptions := JsObject(
      "tscPath" -> JsString(tscPath.value),
      "sourceMap" -> JsBoolean(sourceMap.value),
      "noEmitOnError" -> JsBoolean(noEmitOnError.value)

    ).toString()
  )

  override def projectSettings = Seq(
    tscPath := "",
    sourceMap := true,
    noEmitOnError := false

  ) ++ inTask(typescript)(
    SbtJsTask.jsTaskSpecificUnscopedSettings ++
      inConfig(Assets)(typescriptUnscopedSettings) ++
      inConfig(TestAssets)(typescriptUnscopedSettings) ++
      Seq(
        moduleName := "javascripts",
        shellFile := getClass.getClassLoader.getResource("typescript.js"),

        taskMessage in Assets := "TypeScript compiling",
        taskMessage in TestAssets := "TypeScript test compiling"
      )
  ) ++ SbtJsTask.addJsSourceFileTasks(typescript) ++ Seq(
    typescript in Assets := (typescript in Assets).dependsOn(webModules in Assets).value,
    typescript in Assets := (typescript in Assets).dependsOn(nodeModules in Assets).value,
    typescript in TestAssets := (typescript in TestAssets).dependsOn(webModules in TestAssets).value,
    typescript in TestAssets := (typescript in TestAssets).dependsOn(nodeModules in TestAssets).value
  )

}
