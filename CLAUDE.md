# CLAUDE.md — kotlin_gradle_template

Project context for AI-assisted development. Keep this file updated as the project evolves.

## Purpose

A Kotlin multi-project Gradle template demonstrating clean architectural separation, modern Gradle conventions, and the Memento pattern for persistence. Intended as a starting point for new Kotlin projects. Author: Fred George (MIT License).

## Technology Stack

| Tool                  | Version                       |
|-----------------------|-------------------------------|
| Kotlin                | 2.3.20                        |
| Java                  | 25                            |
| Gradle                | 9.4.1                         |
| JUnit Jupiter         | 6.0.3                         |
| kotlinx-serialization | 1.8.1                         |
| Target IDE            | IntelliJ IDEA 2026.1 Ultimate |

## Project Structure

```
kotlin_gradle_template/
├── build.gradle.kts          # Root build — allprojects group/version, subprojects Kotlin+JUnit+toolchain
├── settings.gradle.kts       # Module includes, centralized repo management
├── gradle.properties         # javaVersion=25, config cache, Kotlin style
├── gradle/
│   ├── libs.versions.toml    # Version catalog (single source of truth for versions)
│   └── wrapper/              # Gradle 9.4.1
├── engine/                   # Core domain logic
├── test_support/             # Shared test fixtures
├── tests/                    # Behavior tests (separate module, not inside engine)
└── persistence/              # Serialization layer (Memento pattern)
```

## Module Responsibilities

### engine
Pure domain logic. No test code, no serialization concerns. Publishes to mavenLocal as `template-engine`. Contains `Rectangle` as the sample domain class, with an inner `RectangleDto` (`@Serializable`) for DTO conversion.

### test_support
Shared test fixtures consumed by `tests` and `persistence`. Contains sample objects like `TestShapes` (reference `Rectangle` instances) under `com.nrkei.project.template.util`. Depends on `:engine`. No publication; consumed via `testImplementation(project(":test_support"))`.

### tests
Dedicated module for behavior verification. Depends on `:engine` and `:test_support` (test scope). Uses JUnit Jupiter. Kept separate from the engine to enforce testing of public interfaces only.

### persistence
Serialization layer using the **Memento pattern**. Injects behavior via **extension functions** on domain classes and their companion objects — keeping the domain model clean. Depends on `:engine` (and on `:test_support` for tests). Publishes to mavenLocal.

## Key Architectural Patterns

**Memento Pattern (GoF):** Domain objects expose `toMemento(): String` and `Companion.fromMemento(memento: String)` extension functions in the persistence module. The domain model itself has no serialization dependencies.

**DTO Pattern:** Domain classes convert to inner `@Serializable` DTO classes for serialization. Example: `Rectangle.toDto(): RectangleDto`.

**Extension functions for persistence injection:** `RectanglePersistence.kt` adds persistence capability to `Rectangle` and `Rectangle.Companion` without modifying the domain class.

**Encoding utilities:** `Encoding.kt` (persistence module) provides generic JSON + Base64 encode/decode utilities. Key instance: `defaultJson` with `prettyPrint=false`, `ignoreUnknownKeys=true`, `classDiscriminator="type"`. Supports polymorphic serialization via `SerializersModule`.

## Gradle Conventions

- **Version catalog:** All dependency versions declared in `gradle/libs.versions.toml`. Reference as `libs.xyz` in build files.
- **Java toolchain:** Configured via the `javaVersion` property (`gradle.properties`), read in the root build with `providers.gradleProperty(...)` and applied inside `subprojects { ... }` so every Kotlin/Java module inherits the same toolchain.
- **Group / version:** `group = "com.nrkei.project.template"`, `version = "0.1.0"` set in `allprojects {}` in the root build.
- **Configuration cache:** Enabled (`org.gradle.configuration-cache=true`). Avoid build script side effects that break cache compatibility.
- **Kotlin code style:** `official` (enforced via `kotlin.code.style=official`).
- **Incremental compilation:** Enabled.
- **Repositories:** `mavenLocal()` and `mavenCentral()` — centrally managed in `settings.gradle.kts` via `dependencyResolutionManagement`.
- **No buildSrc / convention plugins:** Cross-project config handled via `allprojects {}` / `subprojects {}` in the root build. Modules only declare what's specific to them (serialization plugin, maven-publish, project deps).

## Build File Layout (root build.gradle.kts)

The root build applies the Kotlin JVM plugin to every subproject and supplies the JUnit test dependencies and `useJUnitPlatform()` configuration once, so module files only declare their own plugins (kotlin-serialization, maven-publish), their own implementation deps, and their own publishing block. Plugins used by subprojects are declared at the root with `apply false` so they land on the classpath.

```kotlin
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

allprojects {
    group = "com.nrkei.project.template"
    version = "0.1.0"
}

val javaVersion = providers.gradleProperty("javaVersion").map(String::toInt).get()

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    val libs = rootProject.extensions.getByType<LibrariesForLibs>()

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        }
    }

    dependencies {
        "testImplementation"(platform(libs.junit.bom))
        "testImplementation"(libs.junit.jupiter)
        "testRuntimeOnly"(libs.junit.platform.launcher)
        "testRuntimeOnly"(libs.junit.jupiter.engine)
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
```

Configurations are referenced by name (`"testImplementation"`) inside `subprojects {}` because the type-safe accessors don't exist at that scope until after the plugin applies per-project. The version catalog is reached via `rootProject.extensions.getByType<LibrariesForLibs>()`.

## Testing Conventions

- JUnit Jupiter (JUnit 5) with `useJUnitPlatform()` configured once in the root `subprojects {}` block.
- JUnit BOM + engine/launcher dependencies are injected by the root build into every subproject's `testImplementation` / `testRuntimeOnly`.
- Backtick test method names (Kotlin style).
- Test module is a sibling of the engine, not nested inside it — deliberate design to test public API only.
- Persistence module has its own tests for round-trip serialization verification.
- Shared fixtures live in `:test_support` and are consumed via `testImplementation(project(":test_support"))`.

## Domain Package

`com.nrkei.project.template` — replace with actual domain package when using as a template.

## No CI Configured

No `.github/`, `.gitlab-ci.yml`, or other CI configuration present. Add before using in production.

## Common Tasks

```bash
./gradlew build               # Build all modules
./gradlew test                # Run all tests
./gradlew publishToMavenLocal # Publish engine and persistence artifacts
```
