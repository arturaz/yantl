// https://typelevel.org/sbt-typelevel/faq.html#what-is-a-base-version-anyway
ThisBuild / tlBaseVersion := "0.2" // your current series x.y

ThisBuild / organization := "io.github.arturaz"
ThisBuild / organizationName := "arturaz"
ThisBuild / startYear := Some(2025)
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / developers := List(
  // your GitHub handle and name
  tlGitHubDev("arturaz", "Artūras Šlajus")
)

// publish to s01.oss.sonatype.org (set to sonatypeLegacy to publish to oss.sonatype.org instead)
ThisBuild / sonatypeCredentialHost := xerial.sbt.Sonatype.sonatype01

// publish website from this branch
ThisBuild / tlSitePublishBranch := Some("main")

ThisBuild / scalacOptions ++= Seq(
  "-Werror",
  "-language:implicitConversions"
)

ThisBuild / scalaVersion := "3.3.4" // the default Scala

// Disable the checks, I don't want to deal with them right now.
ThisBuild / tlCiHeaderCheck := false

ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("11"))

lazy val root = tlCrossRootProject.aggregate(core)

lazy val core = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    name := "yantl",
    libraryDependencies ++= Seq(
      "org.scalameta" %%% "munit" % "1.1.0" % Test
    ),
    addCommandAlias(
      "prepareCi",
      "scalafmtAll;scalafmtSbt;scalafixAll;test;docs/tlSite;mimaReportBinaryIssues"
    )
  )

lazy val docs = project
  .in(file("site"))
  .dependsOn(core.jvm)
  .enablePlugins(TypelevelSitePlugin)
  .settings(
    scalacOptions --= Seq(
      // Disable unused import warnings for the docs as they report false positives.
      "-Wunused:imports"
    ),
    libraryDependencies ++= Seq(
      // Derivation of union types
      "io.github.irevive" %%% "union-derivation-core" % "0.2.1"
    )
  )
