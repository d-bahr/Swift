Setup:

1) Download Oracle JDK 1.8 or OpenJDK 8 (Java 1.8 is recommended for best compatibility).
2) Download Eclipse IDE for Java Developers
3) Download Forge MDK (mod development kit) for the required Minecraft version

Build instructions:

1) gradlew genEclipseRuns
2) Create new Eclipse project; import Gradle launch configurations

In case of dependency changes:

In theory, this should be sufficient:

1) gradlew --refresh-dependencies
2) gradlew clean

If that doesn't work, then try this longer approach instead:

1) Delete %HOME%\.gradle\caches (may need to first run gradlew --stop)
2) Delete the Eclipse workspace.
3) Download the newest Forge MDK.
4) Remove everything in the project folder except the src and .git directories.
5) Unpack the Forge MDK zip into the project folder.
6) Run gradlew genEclipseRuns
7) Create new Eclipse project; import existing Gradle project and import debug/run launch configurations
