# Android build and installation rules

## Stable application signing

- Never install `com.example.minicpm_v_demo` with Gradle's generated debug key.
- Every device install and connected Android test must first run `verifyInstallationSigning`.
- Use the canonical certificate pinned in `app/build.gradle.kts`; keep the keystore and credentials outside Git via `signing.local.properties` or Gradle properties.
- Before changing the pinned certificate, compare it with the installed package certificate and obtain explicit approval for any uninstall that could erase application data.
- On `INSTALL_FAILED_UPDATE_INCOMPATIBLE`, stop. Do not uninstall automatically and do not generate another key.
