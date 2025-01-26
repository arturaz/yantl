# Installation

Add the following to your `build.sbt`:

```scala
libraryDependencies += "io.github.arturaz" %% "yantl" % "@VERSION@"
```

Or `build.sc` if you are using [mill](https://mill-build.com):

```scala
override def ivyDeps = Agg(
  ivy"io.github.arturaz::yantl:@VERSION@"
)
```

The code from `main` branch can be obtained with:
```scala
resolvers ++= Resolver.sonatypeOssRepos("snapshots")
libraryDependencies += "io.github.arturaz" %% "yantl" % "@SNAPSHOT_VERSION@"
```

For [mill](https://mill-build.com):
```scala
  override def repositoriesTask = T.task {
    super.repositoriesTask() ++ Seq(
      coursier.Repositories.sonatype("snapshots")
    )
  }

override def ivyDeps = Agg(
  ivy"io.github.arturaz::yantl:@SNAPSHOT_VERSION@"
)
```

**The library is only published for Scala 3** due to the use of 
[Scala 3 union types](https://docs.scala-lang.org/scala3/book/types-union.html).

You can see all the published artifacts on 
[MVN Repository](https://mvnrepository.com/artifact/io.github.arturaz/yantl_3), 
[Maven Central](https://search.maven.org/artifact/io.github.arturaz/yantl_3),
[raw Maven repository](https://repo1.maven.org/maven2/io/github/arturaz/yantl_3/).
