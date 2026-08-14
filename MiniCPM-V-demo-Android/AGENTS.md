# Android build and installation rules

## Stable application signing

- Never install `com.example.minicpm_v_demo` with Gradle's generated debug key.
- Every device install and connected Android test must first run `verifyInstallationSigning`.
- Use the canonical certificate pinned in `app/build.gradle.kts`; keep the keystore and credentials outside Git via `signing.local.properties` or Gradle properties.
- Before changing the pinned certificate, compare it with the installed package certificate and obtain explicit approval for any uninstall that could erase application data.
- On `INSTALL_FAILED_UPDATE_INCOMPATIBLE`, stop. Do not uninstall automatically and do not generate another key.
- Do not run Gradle `connected*AndroidTest` tasks: AGP cleanup can uninstall the target package and erase app-private models, conversations, and knowledge bases even when tests pass.
- For device tests, build and verify signing first, install both APKs with `adb install -r`, then invoke the selected test with `adb shell am instrument`; never uninstall the target package as test cleanup.

## Canonical Windows Android environment

- Run Gradle through `gradlew.bat`; it loads `android-env.bat` and the ignored machine-specific `environment.local.bat`.
- Keep the single Gradle user home at the workspace root `.gradle-user-home` and the single Android user home at `.android`.
- Do not create `.gradle-user-home`, `.gradle-local`, `.android-local`, `.android`, or `.android-user-home` inside `MiniCPM-V-demo-Android`.
- Use JDK 21 and `D:\Android\Sdk`; do not rely on the older outer `D:\Android\platform-tools` PATH entry.
