# CLAUDE.md — dialog

Project context for AI-assisted development. Keep this file updated as the project evolves.

## Purpose

A Kotlin dialog framework for gathering and refining information through structured sequences of questions and answers. A `Conversation` sequences one or more `Dialog`s between `Party` instances (target users) and the system. Each `Dialog` is triggered by a `Need`; a back-end server expresses unmet needs as `Issue`s (failure situations or missing information), each of which triggers a follow-up `Dialog`. The framework manages Dialog execution; answer accumulation and persistence between Dialog sessions are delegated to the `Context` framework (a separate project, published to mavenLocal). Financial situations (car loans, credit cards, mortgages) are one expected use of the framework but are not part of this project. Author: Fred George (MIT License).

## Technology Stack

| Tool                  | Version                       |
|-----------------------|-------------------------------|
| Kotlin                | 2.3.21                        |
| Java                  | 25                            |
| Gradle                | 9.5.0                         |
| JUnit Jupiter         | 6.0.3                         |
| kotlinx-serialization | 1.11.0                         |
| Target IDE            | IntelliJ IDEA 2026.1 Ultimate |

## External Dependencies (mavenLocal)

| Artifact                              | Provides                                                      |
|---------------------------------------|---------------------------------------------------------------|
| `com.nrkei.project.context:context-engine`      | `Context`, `ContextLabel`, `ContextLabelRegistry`, `ValueCodec` |
| `com.nrkei.project.context:context-persistence` | `Context.toMemento()` / `Context.Companion.fromMemento()` extension functions |

Both artifacts are published from the sibling `context` project and must be present in mavenLocal before building.

## Project Structure

```
dialog/
├── build.gradle.kts          # Root build — allprojects group/version, subprojects Kotlin+JUnit+toolchain
├── settings.gradle.kts       # Module includes, centralized repo management
├── gradle.properties         # javaVersion=25, config cache, Kotlin style
├── gradle/
│   ├── libs.versions.toml    # Version catalog (single source of truth for versions)
│   └── wrapper/              # Gradle 9.5.0
├── engine/                   # Core domain: Dialog, Question, Answer, Consequence, Party, Need, Conversation, Issue
├── persistence/              # JSON/Base64 encoding utilities for serialization
├── test_support/             # Shared test fixtures (sample Dialogs, Answers, populated Contexts)
└── tests/                    # Behavior tests (separate module, not inside engine)
```

## Module Responsibilities

### engine
Pure domain logic. No test code, no serialization concerns. Publishes to mavenLocal as `dialog-engine`. Depends on `context-engine` from mavenLocal. Key domain classes:

- `Dialog` — a structured conversation; may be a flat list of `Question`s, a decision tree, or a hybrid. Composite pattern: a `Dialog` node may contain `Question`s (leaves) or nested `Dialog`s (subtrees). Lives in the `dsl/` subpackage. Associated with a `Need` that it resolves.
- `Question` — the solicitation of a single piece of information. Each concrete type defines its own inner `Answer` enum and is associated with a `ContextLabel` so its answer can be stored into a `Context`. Concrete types: `YesNoQuestion` (YES/NO), `TextQuestion` (text with minimum length validation), `IntRangeQuestion` (integer mapped to caller-defined range enum via `IntRangeAnswer`).
- `Answer` — an interface implemented by inner enums within each `Question` type (e.g. `YesNoChoice`, `TextAnswer`, `IntRangeAnswer`). Represents a possible response value. Supports revision: a `Party` may change an `Answer` and pursue the alternative path, or revert and restore the original answer chain.
- `Consequence` — the interface for what follows a given `Answer`. Concrete terminals: `Acceptable` (SUCCESS) and `Unacceptable` (PROBLEMS). A `Question` is itself a `Consequence`, enabling chained and nested questions via the DSL.
- `Party` — a target user for a `Dialog`. A single `Conversation` may involve multiple `Party` instances (e.g., tentative answers pending review by a different role).
- `Need` — a requirement for information from a `Party`. Triggers a `Dialog`. Expressed externally as an `Issue`.
- `Conversation` — a sequence of `Dialog`s between `Party` instances and the system for a particular goal (e.g., evaluating a financial situation).
- `Issue` — models an unmet `Need` raised by the back-end server. Two subtypes: `FailureSituation` (changes or additional information required) and `MissingInformation` (new information needed). Each `Issue` maps to a follow-up `Dialog`.

`Context` (from `context-engine`) is the state object passed into and out of each `Dialog` execution. Answers are stored into it using `ContextLabel` keys. Persistence between sessions is provided by `context-persistence` and requires no code in this project.

### persistence
JSON and Base64 encoding utilities (`Encoding`) for serializing/deserializing domain objects. Uses `kotlinx-serialization`. Depends on `context-engine`. Publishes to mavenLocal as `dialog-persistence`.

### test_support
Shared test fixtures consumed by `tests`. Contains sample `Question`s, pre-built `Dialog` structures, and pre-populated `Context` instances. Depends on `:engine` and `context-engine`. No publication; consumed via `testImplementation(project(":test_support"))`.

### tests
Dedicated module for behavior verification. Depends on `:engine` and `:test_support` (test scope). Tests Dialog traversal, Answer revision and reversion, Context accumulation across sessions, and Issue-driven Dialog selection. Kept separate from `engine` to enforce testing of public interfaces only.

## Key Domain Concepts

**Question / Answer / Consequence:** A `Question` solicits one piece of information from a `Party`. Each concrete `Question` type declares its own inner `Answer` enum. Selecting an `Answer` triggers its associated `Consequence` — either a terminal (`Acceptable` / `Unacceptable`) or another `Question` (enabling chained and nested dialogs). A `Party` may revise an `Answer` and follow the alternative path, or revert to restore the original answer chain.

**Dialog:** A structured conversation; may be a flat list of `Question`s, a decision tree (branching based on `Answer`), or a combination. Associated with the `Need` it satisfies. Dialog blocks support reuse by plugging into other Dialog chains. Templates can generate multiple copies of a Dialog block for each item in a collection.

**Conversation:** A sequence of `Dialog`s between one or more `Party` instances and the system toward a particular goal (e.g., completing a financial situation assessment). A `Party` may be presented with multiple `Dialog`s if multiple `Need`s arise.

**Party:** A target user for a `Dialog`. Multiple roles may participate in a `Conversation` — for example, a tentative `Answer` may be submitted by one `Party` and reviewed by another.

**Need / Issue:** A `Need` is a requirement for information from a `Party` and is the trigger for a `Dialog`. The external analyzer expresses unmet needs as `Issue`s: `FailureSituation` (changes or additional information required) or `MissingInformation` (new information needed). Each `Issue` resolves to a follow-up `Dialog`.

**Context:** Provided by `context-engine`. A typed `ContextLabel<*> → Any` map that accumulates answers across one or more `Dialog` sessions for a given financial situation. Passed into each `Dialog` at the start of execution and updated as answers arrive. Also supports suspension and resumption of a `Dialog` mid-session. Serialized between sessions via `context-persistence` (`toMemento` / `fromMemento`) so state survives across runs.

## Capabilities

- Three question formats: `YesNoQuestion` (boolean), `TextQuestion` (string with minimum length), `IntRangeQuestion` (integer mapped to caller-defined range enum)
- Kotlin DSL for specifying `Dialog` structure: `Question`s, `Answer`s, and `Consequence`s
- Answer revision: change an `Answer` and pursue the alternative path, or revert and restore the original chain
- Reusable Dialog blocks: plug a `Dialog` subtree into other Dialog chains
- Tentative answers: allow flow to continue while flagging an `Answer` for review by another `Party`
- Dialog templates: generate multiple copies of a Dialog block for each item in a collection

## Key Architectural Patterns

**Composite Pattern (GoF):** `Dialog` is a composite. It uniformly treats a single `Question` (leaf) and a nested `Dialog` (branch) so that trees, lists, and hybrids are expressed with the same interface. Reusable Dialog blocks plug into other chains via the same interface.

**DSL (internal Kotlin DSL):** `Dialog` structures are specified using a Kotlin DSL (in the `dsl/` subpackage). The DSL expresses `Question`s, their `Answer`s, and the `Consequence` for each `Answer` (`Acceptable`, `Unacceptable`, or a chained `Question`).

**Memento Pattern (GoF):** Provided by `context-persistence`. `Context` exposes `toMemento(): String` and `Companion.fromMemento(memento: String)` as extension functions. The dialog project consumes this capability but does not implement it.

## Gradle Conventions

- **Version catalog:** All dependency versions declared in `gradle/libs.versions.toml`. Reference as `libs.xyz` in build files.
- **Java toolchain:** Configured via the `javaVersion` property (`gradle.properties`), read in the root build with `providers.gradleProperty(...)` and applied inside `subprojects { ... }` so every Kotlin/Java module inherits the same toolchain.
- **Group / version:** `group = "com.nrkei.project.dialog"`, `version = "0.1.0"` set in `allprojects {}` in the root build.
- **mavenLocal dependencies:** `context-engine` and `context-persistence` must be published from the `context` project before building (`./gradlew publishToMavenLocal` in that project).
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
    group = "com.nrkei.project.dialog"
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
- JUnit BOM + engine/launcher dependencies injected by the root build into every subproject's `testImplementation` / `testRuntimeOnly`.
- Backtick test method names (Kotlin style).
- Test module is a sibling of the engine, not nested inside it — deliberate design to test public API only.
- Context persistence is tested in the `context` project; no round-trip serialization tests needed here.
- Shared fixtures live in `:test_support` and are consumed via `testImplementation(project(":test_support"))`.

## Domain Package

`com.nrkei.project.dialog`

## No CI Configured

No `.github/`, `.gitlab-ci.yml`, or other CI configuration present. Add before using in production.

## Common Tasks

```bash
./gradlew build               # Build all modules
./gradlew test                # Run all tests
./gradlew publishToMavenLocal # Publish engine and persistence artifacts
```
