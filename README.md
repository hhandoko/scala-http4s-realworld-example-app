# ![RealWorld Example App using Scala and http4s](media/http4s-realworld-logo.png)

> ### Scala + http4s codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API.

This codebase was created to demonstrate a fully fledged fullstack application built with **Scala + http4s** including CRUD operations, authentication, routing, pagination, and more.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.

# Getting started

### Develop and Compile Dependencies

Ensure the following dependencies are installed and configured:

  - [Java SDK] 8
  - [sbt] 1.2.x

### Setup Steps

  - Run `sbt test` to run the test suite
  - Run `sbt run` to run the web application

Now you can visit the app on [`localhost:8080`](http://localhost:8080) from your browser.

# Contributing

We follow the "[feature-branch]" Git workflow.

  1. Commit changes to a branch in your fork (use `snake_case` convention):
     - For technical chores, use `chore/` prefix followed by the short description, e.g. `chore/do_this_chore`
     - For new features, use `feature/` prefix followed by the feature name, e.g. `feature/feature_name`
     - For bug fixes, use `bug/` prefix followed by the short description, e.g. `bug/fix_this_bug`
  1. Rebase or merge from "upstream"
  1. Submit a PR "upstream" to `develop` branch with your changes

Please read [CONTRIBUTING] for more details.


## License

```
    Copyright (c) 2019 Herdy Handoko

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

`http4s-realworld` is released under the Apache Version 2.0 License. See the [LICENSE] file for further details.


[CONTRIBUTING]: https://github.com/hhandoko/scala-http4s-realworld-example-app/blob/master/CONTRIBUTING.md
[feature-branch]: http://nvie.com/posts/a-successful-git-branching-model/
[Java SDK]: https://adoptopenjdk.net/
[LICENSE]: https://github.com/hhandoko/scala-http4s-realworld-example-app/blob/master/LICENSE
[sbt]: https://www.scala-sbt.org/
