plugins {
    base
}

allprojects {
    group = "com.tty7"
    version = "0.3.1"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    tasks.withType<JavaCompile>().configureEach {
        // Allow local JDK 21/26 builds while emitting Java 21-compatible bytecode.
        options.release.set(21)
    }
    tasks.withType<org.gradle.api.tasks.testing.Test>().configureEach {
        useJUnitPlatform()
    }
}
