# AGENTS.md — dialog

## Quick Commands

| Command | Description |
|---------|-------------|
| `./gradlew test` | Run fast unit tests (`/unit/`) |
| `./gradlew integrationTest` | Run integration tests (`/integration/`) |
| `./gradlew check` | Run both unit and integration tests |
| `./gradlew build` | Build all modules (engine, persistence, api, test_support, tests) |
| `./gradlew api:run` | Start Ktor server (localhost:8080) |
| `docker compose up redis` | Start Redis dev dependency |

## Architecture by Module

**`engine`** — Core domain. Pure Kotlin. Publishes to mavenLocal as `dialog-engine`.
- `model/` — `Dialog` (composite of `QuestionConsequences`), `Question` (interface), `Conversation` (Map<MissingIssue, Dialog>), `Consequence`, `Result`
- `dsl/DialogBuilder.kt` — Kotlin DSL: `dialog { first ask q1 answers { -YES ask q2 answers { ... } then ask q2 answers { ... } } }`
- `questions/` — `YesNoQuestion`, `TextQuestion`, `IntRangeQuestion`, `DoubleRangeQuestion`
- `visitors/` — `DialogVisitor`, `QuestionFinder`, `ExtractValues`, `RestoreValues`

**`persistence`** — Redis (Lettuce) and JSON/Base64. Publishes as `dialog-persistence`. Depends on `:engine`, `context-persistence`, `issue-persistence`. Tests include `ConversationCloneTest`, `DialogPersistenceTest`, `CaptureAnswersTest`.

**`api`** — Ktor/Jetty server. `ApplicationSetup.kt` defines `main` and `module()`. Routes: health + issues. CORS and content negotiation configured.

**`test_support`** — Shared test fixtures. Consumed via `testImplementation(project(":test_support"))` in tests module.

**`tests`** — Unit tests only. Test `Question` types, DSL validation, visitor traversal.

## Test Conventions

- JUnit5 with backtick names: `fun \`Simple valid dialog()\``
- Always call `ContextLabelRegistry.reset()` in `@BeforeEach`
- Assertion order: `assertEquals(expected, actual)`
- DSL validation: `assertThrows<IllegalArgumentException>` for incomplete/duplicate/extra answers

## DSL Syntax Rules

- First question: `first ask questionName answers { ... }`
- Subsequent questions: `then ask questionName answers { ... }`
- Mark answer: `-ANSWER_NAME`
- Conclude with: `conclude Acceptable` or `conclude problem("reason")`
- Chain next question: `ask innerQuestionName answers { ... }`
- All possible answers MUST be covered or DSL throws at construction

## External Dependencies

Published to `mavenLocal` from sibling projects:
- `com.nrkei.project.context:context-engine:1.2.6`
- `com.nrkei.project.context:context-persistence:1.2.6`
- `com.nrkei.project.issue:issue-engine:1.1.13`
- `com.nrkei.project.issue:issue-persistence:1.1.13`

Publish from sibling before building: `./gradlew publishToMavenLocal` in each sibling project.

## Tech Version Summary

Kotlin 2.3.21 · Java 25 · Gradle 9.5.1 · Ktor 3.0.0 · Lettuce 7.5.2 · kotlinx-serialization 1.11.0 · JUnit 6.0.3
