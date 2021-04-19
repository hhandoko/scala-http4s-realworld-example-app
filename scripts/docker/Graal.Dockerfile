FROM        ghcr.io/graalvm/graalvm-ce:java11-21.0.0.2 as assembler
LABEL       maintainer="Herdy Handoko <herdy.handoko@gmail.com>"
LABEL       description="http4s GraalVM assembler"

WORKDIR     assembler
# Copy only the required files to setup sbt
COPY        project/*.properties project/*.sbt project/
RUN         (SBT_VERSION=$(cat project/build.properties | cut -d '=' -f 2 | tr -d '[:space:]') \
              && curl -L -O https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.tgz \
              && tar -xzf sbt-${SBT_VERSION}.tgz \
              && ./sbt/bin/sbt -mem 4096 sbtVersion)

# Copy the rest of the application source files
COPY        project/*.scala project/
COPY        src/ src/
COPY        build.sbt VERSION.txt ./
RUN         ./sbt/bin/sbt -mem 4096 clean stage

# ~~~~~~

FROM        ghcr.io/graalvm/graalvm-ce:java11-21.0.0.2 as packager_graal
LABEL       maintainer="Herdy Handoko <herdy.handoko@gmail.com>"
LABEL       description="http4s GraalVM packager"

WORKDIR     packager
RUN         gu install native-image
COPY        --from=assembler /assembler/target/universal/stage/lib/ ./lib/
COPY        --from=assembler /assembler/target/universal/stage/bin/ ./bin/
# Split app artifact from third-party dependencies so layering is a bit more
# optimal.
RUN         mkdir -p app \
              && mv lib/com.hhandoko.realworld-*.jar app

# ~~~~~~

FROM        ghcr.io/graalvm/graalvm-ce:java11-21.0.0.2
LABEL       maintainer="Herdy Handoko <herdy.handoko@gmail.com>"
LABEL       description="http4s GraalVM (JIT) runtime container"

WORKDIR     app
COPY        --from=packager_graal /packager/lib/ ./lib/
COPY        --from=packager_graal /packager/bin/ ./bin/
COPY        --from=packager_graal /packager/app/ ./lib/

EXPOSE      8080
ENTRYPOINT  ["./bin/realworld"]
