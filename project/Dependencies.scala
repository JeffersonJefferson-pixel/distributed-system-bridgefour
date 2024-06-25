import sbt.*

object Dependencies {
  // Versions
  val CatsEffectVersion = "3.5.0"
  val CirceADTVersion = "0.11.0"
  val ScalafixOrganizeImportsVersion = "0.6.0"
  val MunitVersion = "0.7.29"
  val MunitCatsEffectVersion = "1.0.7"

  lazy val sharedDeps: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-effect" % CatsEffectVersion,
    "org.typelevel" %% "cats-effect-kernel" % CatsEffectVersion,
    "org.typelevel" %% "cats-effect-std" % CatsEffectVersion,
    
    "org.latestbit" %% "circe-tagged-adt-codec" % CirceADTVersion,

    "org.typelevel" %% "cats-effect-testing-specs2" % "1.4.0"                % Test,
    "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test,
    "org.scalameta" %% "munit" % MunitVersion % Test,
    "org.typelevel" %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test
  )
} 