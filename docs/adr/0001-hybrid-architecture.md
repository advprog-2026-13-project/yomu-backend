# ADR-0001: Hybrid Architecture (Hexagonal for Core Domains, Layered for Auth)

**Status:** Accepted  
**Date:** 2026-05-18

## Context

Yomu is a reading platform with multiple bounded contexts: authentication, reading content, discussion forums, achievements, and social features. Each module has different levels of domain complexity and different coupling requirements.

Two architectural constraints were identified:

1. **No Direct State / Database Sharing**: Modules must not directly query another module's database tables. If a module needs data owned by another module, it must retrieve it via JWT claims (for the current user) or through formal inter-module communication (internal API calls or event-driven mechanisms).

2. **Well-Defined Communication**: The interface between the Auth module and other modules must remain clean and strictly decoupled. Other modules should primarily act as consumers that validate the JWT issued by the Auth module.

Applying a uniform architecture (fully hexagonal or fully layered) across all modules would impose unnecessary complexity on simple modules or insufficient isolation on complex ones.

## Decision

The project uses a **hybrid architecture**:

| Module | Architecture | Rationale |
|--------|-------------|-----------|
| **Auth** | Layered Architecture | Straightforward CRUD around Spring Security idioms. JPA entities can double as domain objects. No complex business rules warranting a separate domain layer. |
| **Forum** | Hexagonal (Ports & Adapters) | Complex domain with threaded comments, reactions, role-based authorization. Pure domain POJOs enable unit testing without infrastructure. Inbound (`ForumUseCase`) and outbound (`CommentRepositoryPort`, `ReactionRepositoryPort`, `ForumEventPublisherPort`) port interfaces provide clean extension points. |
| **Achievements** | Hexagonal (outbound ports) | Rich domain logic for progress tracking and milestone completion. Six outbound port interfaces isolate the domain from persistence and notification concerns. Inbound use-case interfaces are implicit (services serve as entry points for controllers and event listeners). |
| **Reading** | Layered with inbound interfaces | Moderate complexity. Service interfaces (`AdminContentService`, `StudentQuizService`) provide contract separation, but data access goes directly through Spring Data JPA. |

### Inter-Module Communication Rules

1. Auth module issues JWTs containing `userId`, `username`, `displayName`, and `role` claims.
2. Any module may validate the JWT and read its claims without contacting the Auth module.
3. For data scoped to the authenticated user (e.g., the author of a newly posted comment), the module extracts the needed fields directly from `@AuthenticationPrincipal SecurityUser` — no database cross-access required.
4. For resolving historical/persisted data (e.g., comment author names), the data is denormalized into the owning module's storage at write time, sourced from the JWT claims.
5. Event-driven communication (via Spring's `ApplicationEventPublisher`) is used for cross-cutting concerns like achievement tracking, where the Reading module publishes `AchievementEnvelope` events and the Achievements module listens asynchronously.

### Enforcement Mechanism

The `ForumUserAdapter` previously violated this rule by directly injecting `auth.infrastructure.UserRepository` and querying the `users` table. This was resolved by:

- Adding `authorName` to the `Comment` domain model
- Storing the author's `displayName` (from JWT/principal) at comment creation time
- Removing the `UserPort` interface and its `ForumUserAdapter` implementation
- Removing the `UserSummary` DTO from the forum module

The forum module is now fully decoupled from the auth module's database.

## Consequences

### Positive

- **Loose coupling**: Core domain modules (Forum, Achievements) can evolve independently of Auth's database schema.
- **Testability**: Forum and Achievements domain logic is testable in isolation without mocking Spring Data or database connections.
- **Clear boundaries**: Each module owns its data. No module can accidentally corrupt another module's tables.
- **JWT-first**: Authentication data flows via standard, verifiable tokens rather than ad-hoc database queries.
- **Gradual adoption**: Modules that benefit from hexagonal architecture use it; simpler modules use layered architecture without forced complexity.

### Negative

- **Denormalization**: `authorName` is stored in the `forum_comments` table. If a user changes their display name, existing comments retain the old name. This is a deliberate trade-off (historical accuracy vs. live consistency). If live consistency is required in the future, an event-driven synchronization mechanism can be added.
- **Inconsistency across modules**: Developers must understand two architectural styles. New contributors may mistakenly apply hexagonal patterns to the Auth module or bypass port interfaces in the Forum module.
- **No outbound ports in Reading**: The reading module's direct dependency on Spring Data JPA means its services cannot be unit-tested without a database or heavy mocking of repository interfaces. This is acceptable given the module's simplicity, but may warrant refactoring if complexity grows.

### Mitigations

- Code review guidelines should flag any direct cross-module repository usage.
- The `docs/adr/` directory serves as a reference for architectural conventions.
- Future modules should default to hexagonal architecture unless they are purely CRUD (like Auth).

## Alternatives Considered

1. **Fully Hexagonal Architecture**: Rejected because the Auth module is straightforward CRUD around Spring Security. Adding port interfaces to abstract `UserRepository`, `PasswordEncoder`, and `JwtService` would add boilerplate with no testability gain (Spring Security integration tests already need a full context).

2. **Fully Layered Architecture**: Rejected because the Forum module's threaded comment logic and reaction rules benefit from a pure domain layer that can be unit-tested without infrastructure. Layered architecture would place this logic in `@Service` classes coupled to JPA entities, making unit tests require an in-memory database.

3. **Microservices**: Rejected because the project scale does not warrant the operational complexity of separate deployables. The modular monolith with disciplined inter-module boundaries achieves the same isolation goal with lower overhead.
