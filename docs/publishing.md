# Legacy build and publish

The `maintenance/1.x` branch provides the temporary 1.x compatibility line. Modern development and future dependency upgrades continue on `main`; do not merge this branch back into `main`.

Pull requests and pushes to `maintenance/1.x` run `./gradlew clean build` on Java 21. Published bytecode targets Java 17. The Gradle wrapper pins the build tool version.

Only `selenide-pages-core` is published as `org.brewcode:selenide-pages-core`. Publishing uses the [Vanniktech Gradle plugin](https://vanniktech.github.io/gradle-maven-publish-plugin/central/) and the [Central Publisher Portal](https://central.sonatype.org/publish/publish-portal-guide/).

Configure these GitHub Actions repository secrets before publishing:

| Secret | Value |
| --- | --- |
| `CENTRAL_USERNAME` | Central Portal user-token username |
| `CENTRAL_PASSWORD` | Central Portal user-token password |
| `GPG_SIGNING_KEY` | ASCII-armored private signing key |
| `GPG_SIGNING_PASSWORD` | Signing-key passphrase |

Run the **Publish 1.x** workflow manually from `maintenance/1.x` with an unused release-candidate version such as `1.4.0-rc.1`. Manual validation accepts only `1.x.y-rc.n` versions from this branch. It builds and tests the selected branch source, verifies the signed local publication, and uploads a private deployment for validation. Inspect the result in [Central Portal Deployments](https://central.sonatype.com/publishing/deployments) and run a consumer smoke test against the validated deployment. Do not publish the validation-only deployment.

For the public compatibility release, set `version=1.4.0` in `gradle.properties` and push the annotated `v1.4.0` tag on a reviewed commit contained in `maintenance/1.x`. The tag version must match the source version. The workflow repeats the build, tests, and signed-publication checks before publishing. It waits for Central to report `PUBLISHED`, verifies the public POM and JAR, and creates a GitHub release with the binary, sources, and Javadoc JARs. Release notes cover changes since `v1.3.0`, and the legacy release is not marked as the repository's latest release.

Ordinary branch pushes never publish. Never reuse a version already present on Maven Central. Keep `maintenance/1.x` after release so the compatibility artifact remains reproducible; direct new development and dependency upgrades to `main`.
