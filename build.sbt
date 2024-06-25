import Dependencies.*

ThisBuild / organization := "com.example"
ThisBuild / name := "bridgefour"
ThisBuild / scalaVersion := "3.4.2"

// Scalafix
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % ScalafixOrganizeImportsVersion


lazy val commonSettings = Seq(
  libraryDependencies ++= sharedDeps,
  semanticdbEnabled := true,
  semanticdbIncludeInJar := true
)


lazy val root = Project(id = "bridgefour", base = file("."))
  .aggregate(leader, worker)

lazy val shared = (project in file("modules/shared"))
  .settings(
    commonSettings,
    name := "shared"
  )

lazy val leader = (project in file("modules/kaladin"))
  .settings(
    commonSettings,
    name := "kaladin"
  )
  .dependsOn(shared)
  .enablePlugins(JavaAppPackaging)

lazy val worker = (project in(file("modules/spren")))
  .settings(
    commonSettings,
    name := "spren"
  )
  .dependsOn(shared)
  .enablePlugins(JavaAppPackaging)

addCommandAlias("lint", ";scalafixAll --rules OrganizeImports")

enablePlugins(JavaAppPackaging)