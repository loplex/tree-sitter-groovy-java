# Releasing and Publishing

This project uses a fully automated release pipeline powered by GitHub Actions.

## Cross-Compilation Pipeline

When you push a git tag starting with `v` (e.g., `v0.2.2-1`), a GitHub Actions workflow (`.github/workflows/release.yml`) is triggered. 
It performs cross-compilation of the JNI native library for the following OS and architectures:
- **Linux** (`amd64`, `aarch64` via `gcc-aarch64-linux-gnu`)
- **macOS** (`amd64`, `aarch64` / Apple Silicon)
- **Windows** (`amd64`)

All native `.so`, `.dylib`, and `.dll` files are then collected and bundled into a single "Fat JAR".

## Publishing to Maven Central

The release pipeline automatically signs and deploys the packaged artifacts to Maven Central using the `central-publishing-maven-plugin`.

### Prerequisites (GitHub Secrets)
To authorize the deployment, the following GitHub Secrets must be configured in your repository:
- `CENTRAL_USERNAME`: Your Sonatype Central Portal user token (username).
- `CENTRAL_PASSWORD`: Your Sonatype Central Portal user token (password).
- `GPG_PRIVATE_KEY`: Your GPG subkey in ASCII-armor format (`gpg --armor --export-secret-keys <ID>`) for signing the artifacts.
- `GPG_PASSPHRASE`: The passphrase for your GPG subkey.

Note: Ensure your GPG public key is uploaded to public keyservers (e.g., `keyserver.ubuntu.com`), as Maven Central verifies signatures.

### How to Release
1. Create and push a new Git tag matching the version you want to release (e.g., `v0.2.2-1`).
   ```bash
   git tag v0.2.2-1
   git push origin v0.2.2-1
   ```
2. The GitHub Action will automatically:
   - Extract the version from the tag (stripping the `v`).
   - Run `mvn versions:set` to synchronize the POM version with the tag.
   - Cross-compile the JNI libraries across the matrix.
   - Build the compiled JAR, Source JAR, and Javadoc JAR.
   - Sign all artifacts with GPG.
   - Upload and publish the release directly to Maven Central.
   - Create a GitHub Release attaching the compiled Fat JAR.
