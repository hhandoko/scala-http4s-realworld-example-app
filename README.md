[![License](https://img.shields.io/badge/license-Apache--2.0-brightgreen.svg)](LICENSE)
[![CI](https://github.com/hhandoko/scala-http4s-realworld-example-app/actions/workflows/ci.yml/badge.svg)](https://github.com/hhandoko/scala-http4s-realworld-example-app/actions/workflows/ci.yml)

# ![RealWorld Example App using Scala and http4s](media/http4s-realworld-logo.png)

> ### Scala + http4s codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API.

This codebase was created to demonstrate a fully fledged fullstack application built with **Scala + http4s** including CRUD operations, authentication, routing, pagination, and more.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.

# Getting started

### Develop and Compile Dependencies

Ensure the following dependencies are installed and configured:

  - [Java SDK] 8 or 11 or [GraalVM] 20.2.x (both Java 8 and 11 variants)
  - [sbt] 1.4.x

### Setup Steps

  - Run `sbt test` to run the test suite
  - Run `sbt run` to run the web application

The app is now accessible from [`localhost:8080`](http://localhost:8080).

### newman Testing

To run the API spec tests with [newman]:

  - Run `./scripts/newman/run.sh` to run the API spec test

_Note: Node.js 8+ and globally installed [newman] package are required_

### Production Packaging (universal format)

To package and run it as an uber-jar:

  1. Run `sbt stage` to package the application into a universal distribution format (at `target/universal/stage/`)
  2. Run `./target/universal/stage/bin/realworld` to run the web application

### Production Packaging (Graal Native Image)

Ensure Graal is downloaded and its binaries folder added to `PATH`. The most convenient way is to use [sdkman] to switch between different Java SDK versions (Graal included).

  - Run `sdk env` to initialise the shell session using [sdkman]

To generate assisted configuration for native image:

```shell
java \
  -agentlib:native-image-agent=config-output-dir=src/main/resources/graal/ \
  -cp "target/universal/stage/lib/*" \
  com.hhandoko.realworld.Application
```

To package and run it as a Graal native image:

```shell
native-image \
  --no-server \
  --class-path "target/universal/stage/lib/*" \
  com.hhandoko.realworld.Application
```

  1. Run `./realworld` to run the web application

Alternatively, some scripts are included in the repo to make it easy to download and create native image distribution (limited to Linux and macOS for now):

  1. Run `./scripts/graal/bin/setup.sh` to download and setup Graal.
  2. Run `./scripts/graal/bin/dist.sh` to create a native image distribution under the `/dist` directory. 

# Progress

Backend API implementation:

  - [ ] Auth (3 of 5)
  - [ ] Articles (0 of 4)
  - [ ] Article, Favorite, Comments (0 of 17)
  - [ ] Profiles (1 of 4)
  - [x] Tags

Please read [PROGRESS] for more details.

# Issues

  - Native image generation sometimes fail with non-initialized charset issue error message (simply retry until succeeds)
  - Native image generation with `jwt-scala` fails ([oracle/graal/#1152](https://github.com/oracle/graal/issues/1152))
  - JWT token decoding in native image fails ([oracle/graal/#1240](https://github.com/oracle/graal/issues/1240))

# Pending Updates

  - GraalVM `21.0.0.2`:
    - Pending `org.scalameta %% svm-subs` support
  - Scala `2.13.5`:
    - Pending `sbt-tpolecat` support

# Contributing

We follow the "[feature-branch]" Git workflow.

  1. Commit changes to a branch in your fork (use `snake_case` convention):
     - For technical chores, use `chore/` prefix followed by the short description, e.g. `chore/do_this_chore`
     - For new features, use `feature/` prefix followed by the feature name, e.g. `feature/feature_name`
     - For bug fixes, use `bug/` prefix followed by the short description, e.g. `bug/fix_this_bug`
  2. Rebase or merge from "upstream"
  3. Submit a PR "upstream" to `develop` branch with your changes

Please read [CONTRIBUTING] for more details.


## License

```
    Copyright (c) 2019-2021 Herdy Handoko

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```

`scala-http4s-realworld-example-app` is released under the Apache Version 2.0 License. See the [LICENSE] file for further details.


[CONTRIBUTING]: https://github.com/hhandoko/scala-http4s-realworld-example-app/blob/master/CONTRIBUTING.md
[feature-branch]: http://nvie.com/posts/a-successful-git-branching-model/
[GraalVM]: https://www.graalvm.org/
[Java SDK]: https://adoptopenjdk.net/
[LICENSE]: https://github.com/hhandoko/scala-http4s-realworld-example-app/blob/master/LICENSE
[newman]: https://github.com/postmanlabs/newman
[PROGRESS]: https://github.com/hhandoko/scala-http4s-realworld-example-app/blob/master/PROGRESS.md
[sbt]: https://www.scala-sbt.org/
[sdkman]: https://sdkman.io/
