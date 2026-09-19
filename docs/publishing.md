# Build and publish

Pull requests and pushes to `main` run `./gradlew clean build` on Java 21. The published bytecode targets Java 17; Kotlin consumers need a compiler compatible with Kotlin 2.4 metadata. The build includes the core and sample browser tests. The Ubuntu 24.04 runner provides Docker and Chrome; the tests start their pinned site container through Testcontainers. The Gradle wrapper pins the build tool version.

Only `selenide-pages-core` is published as `org.brewcode:selenide-pages-core`. Publishing uses the [Vanniktech Gradle plugin](https://vanniktech.github.io/gradle-maven-publish-plugin/central/) and the [Central Publisher Portal](https://central.sonatype.org/publish/publish-portal-guide/). The former OSSRH staging tasks are no longer used.

Configure these GitHub Actions repository secrets before publishing:

| Secret | Value |
| --- | --- |
| `CENTRAL_USERNAME` | Central Portal user-token username |
| `CENTRAL_PASSWORD` | Central Portal user-token password |
| `GPG_SIGNING_KEY` | ASCII-armored private signing key |
| `GPG_SIGNING_PASSWORD` | Signing-key passphrase |

Run the **Publish** workflow manually from `main` with an unused version such as `2.0.0-rc.1` to upload a signed deployment for validation. This path does not publish it to the public repository or create a GitHub release. Inspect the deployment and validation result in [Central Portal Deployments](https://central.sonatype.com/publishing/deployments). Central also provides [authenticated access to validated deployment artifacts](https://central.sonatype.org/publish/publish-portal-api/#manually-testing-a-deployment-bundle) for a consumer smoke test. Do not click Publish for a validation-only deployment.

For a public release, push a `vMAJOR.MINOR.PATCH` tag on a commit contained in `main`, using a version that has not been published. The tag version must match `gradle.properties` in the tagged commit. The workflow builds and tests, signs and publishes the core module, waits for Central to report `PUBLISHED`, verifies the public POM and JAR, then creates the GitHub release with the built JARs. Ordinary pushes do not publish. Central may take 10–30 minutes to expose artifacts publicly; if the final availability check times out after 30 minutes, inspect Central before retrying because the version may already be published.

The build and release workflow definitions are in [`.github/workflows`](../.github/workflows/). Keep the release version in `gradle.properties` aligned with the next intended tag for local builds. Never reuse a version already present on Maven Central.
