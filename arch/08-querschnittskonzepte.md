# 8 — Querschnittliche Konzepte

Dieses Kapitel beschreibt die zentralen technischen Konzepte, die von allen Entwicklern im Projekt einheitlich umgesetzt werden müssen. Die Beschreibungen dienen als verbindliche Vorgabe und Programmieranleitung.

---

## 8.1 Authentifizierung und Autorisierung

### 8.1.1 Verwendete Technologie

- **Backend**: Spring Security + `jjwt` (Java JWT Library)
- **Token-Algorithmus**: HS256 (HMAC mit SHA-256)
- **Token-Ablaufzeit**: 24 Stunden
- **Token-Übertragung**: `Authorization: Bearer <token>` im HTTP-Header

### 8.1.2 Token-Ausstellung beim Login

Bei erfolgreicher Anmeldung erzeugt der `AuthService` ein JWT und gibt es an das Frontend zurück.

Token-Erzeugung erfolgt zentral im `AuthService`:

```java
// service/AuthService.java
public String generateToken(User user) {
    return Jwts.builder()
        .setSubject(user.getEmail())
        .claim("userId", user.getId())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24h
        .signWith(SignatureAlgorithm.HS256, jwtSecret)
        .compact();
}
```

**Hinweis zur `jjwt`-Version**: Der obige Code entspricht der Syntax der `jjwt`-Version 0.9.x. Neuere Versionen (0.11+) verlangen ein abweichendes API (z. B. `.signWith(Keys.hmacShaKeyFor(secretBytes))` statt `.signWith(SignatureAlgorithm.HS256, jwtSecret)`). Vor der Implementierung muss die im Projekt tatsächlich verwendete `jjwt`-Version festgelegt werden (`pom.xml`), damit dieser Code direkt übernommen werden kann.

**Wichtig**: Das `jwtSecret` wird über die Umgebungsvariable `JWT_SECRET` bereitgestellt und darf niemals im Quellcode stehen.

### 8.1.3 Token-Validierung

Jeder geschützte API-Endpunkt wird durch einen `JwtAuthenticationFilter` abgesichert. Der Filter prüft den `Authorization`-Header und setzt bei gültigem Token den authentifizierten Benutzer in den Security-Context.

Der Filter wird in `SecurityConfig` registriert und läuft vor jedem geschützten Request:

```java
// security/JwtAuthenticationFilter.java
@Override
protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain chain) throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
        String token = header.substring(7);
        if (jwtUtil.validateToken(token)) {
            String userId = jwtUtil.extractUserId(token);
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userId, null, List.of());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }
    chain.doFilter(request, response);
}
```

### 8.1.4 Authentifizierten Nutzer ermitteln

In jedem Controller oder Service kann der aktuell authentifizierte Nutzer über den Security-Context abgerufen werden.

Nutzer-ID aus dem Security-Context lesen:

```java
// In Controllern oder Services
public Long getCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
        throw new UnauthorizedException("Kein Nutzer authentifiziert");
    }
    return Long.valueOf(auth.getPrincipal().toString());
}
```

**Alternative mit `@AuthenticationPrincipal`** in Controllern:

```java
@GetMapping("/tasks")
public ResponseEntity<List<TaskDTO>> getTasks(
        @AuthenticationPrincipal Long userId) {
    return ResponseEntity.ok(taskService.findAllByUserId(userId));
}
```

### 8.1.5 Ownership-Prüfung

Jeder Service muss vor dem Zugriff auf eine Ressource prüfen, ob diese dem aktuellen Nutzer gehört.

Ownership-Prüfung zentral in der Service-Schicht:

```java
// service/TaskService.java
public TaskDTO getTask(Long taskId, Long currentUserId) {
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new ResourceNotFoundException("Aufgabe nicht gefunden"));
    if (!task.getUser().getId().equals(currentUserId)) {
        throw new AccessDeniedException("Zugriff auf fremde Ressource verweigert");
    }
    return taskMapper.toDto(task);
}
```

### 8.1.6 Anmeldestatus prüfen

Bevor eine geschützte Seite angezeigt wird, muss das Frontend prüfen, ob ein gültiger Token vorhanden ist.

Einfache Prüfung auf Vorhandensein eines Tokens:

```typescript
// services/auth.ts
export function isLoggedIn(): boolean {
    return localStorage.getItem("jwt") !== null;
}
```

**Hinweis**: Diese Prüfung stellt nur fest, ob überhaupt ein Token gespeichert ist — nicht, ob er noch gültig ist. Die eigentliche Gültigkeitsprüfung (Ablaufzeit, Signatur) erfolgt serverseitig bei jedem Request (siehe 8.1.3). Läuft der Token während der Nutzung ab, antwortet das Backend mit HTTP 401, woraufhin das Frontend gemäß 8.2.5 zur Login-Seite weiterleitet.

Vorgabe für Frontend-Entwickler: Geschützte Routen prüfen `isLoggedIn()`, z. B. beim Rendern der Seite oder in einem Route-Guard, und leiten bei `false` zur Login-Seite weiter.

### 8.1.7 Logout

Da JWT stateless ist, gibt es keinen serverseitigen Logout-Mechanismus. Das Token läuft nach 24 Stunden automatisch ab.

**Vorgabe für Frontend-Entwickler**: Logout erfolgt clientseitig:

```typescript
// services/auth.ts
export function logout(): void {
    localStorage.removeItem("jwt");   // oder sessionStorage
    window.location.href = "/login";
}
```

Vorgabe für Frontend-Entwickler: Bei jedem API-Request muss der Token mitgesendet werden:

```typescript
// services/api.ts
const response = await fetch("/api/v1/tasks", {
    headers: {
        "Authorization": `Bearer ${localStorage.getItem("jwt")}`
    }
});
```

---

## 8.2 Fehlerbehandlung

### 8.2.1 Ziel

Alle Fehler im Backend werden zentral gefangen und in ein einheitliches JSON-Format übersetzt. Das Frontend erhält damit immer vorhersehbare Fehlerantworten.

### 8.2.2 Einheitliches Fehler-Format

Jede Fehlerantwort vom Backend folgt diesem Schema:

```json
{
  "timestamp": "2025-01-21T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Titel darf nicht leer sein"
}
```

### 8.2.3 Zentrale Fehlerbehandlung im Backend

Vorgabe für Backend-Entwickler: Ein globaler `@ControllerAdvice` fängt alle unbehandelten Exceptions ab:

```java
// exception/GlobalExceptionHandler.java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
            "Ein interner Fehler ist aufgetreten");
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        ErrorResponse error = new ErrorResponse(
            Instant.now(), status.value(), status.getReasonPhrase(), message);
        return ResponseEntity.status(status).body(error);
    }
}
```

### 8.2.4 Verwendete HTTP-Statuscodes

| Status | Wann verwenden? |
|--------|-----------------|
| 400 | Ungültige Eingabe (Validierungsfehler) |
| 401 | Kein oder ungültiger JWT |
| 403 | JWT gültig, aber Nutzer darf nicht auf diese Ressource zugreifen (Ownership) |
| 404 | Ressource existiert nicht |
| 500 | Unerwarteter interner Fehler |

**Vorgabe**: Bei 401 und 403 darf keine Information preisgegeben werden, ob die E-Mail existiert oder das Passwort falsch war.

### 8.2.5 Fehlerbehandlung im Frontend

Vorgabe für Frontend-Entwickler: API-Fehler zentral über einen Response-Interceptor abfangen:

```typescript
// services/api.ts
async function apiRequest(url: string, options: RequestInit = {}) {
    const response = await fetch(url, options);
    if (!response.ok) {
        if (response.status === 401) {
            localStorage.removeItem("jwt");
            window.location.href = "/login";
            return;
        }
        const error = await response.json();
        throw new ApiError(error.status, error.message);
    }
    return response.json();
}
```

---

## 8.3 Logging

### 8.3.1 Verwendete Technologie

- **Backend**: SLF4J (API) + Logback (Implementierung)
- **Frontend**: Browser-Konsole (`console.log`, `console.error`) — nur für Entwicklung, nicht für Produktion

### 8.3.2 Logger beziehen

In jeder Klasse wird der Logger über SLF4J bezogen:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    // ...
}
```

**Wichtig**: Der Logger wird pro Klasse als `private static final` Feld angelegt. Niemals einen Logger über mehrere Klassen teilen.

### 8.3.3 Log-Level und Methoden

| Level | Methode | Wann verwenden? |
|-------|---------|-----------------|
| DEBUG | `logger.debug("...")` | Detaillierte Abläufe während der Entwicklung (z. B. SQL-Queries, Request-Details) |
| INFO | `logger.info("...")` | Wichtige Geschäftsereignisse (z. B. "Nutzer registriert", "Lernplan berechnet") |
| WARN | `logger.warn("...")` | Ungewöhnliche, aber behandelbare Situationen (z. B. SMTP nicht konfiguriert) |
| ERROR | `logger.error("...", exception)` | Fehler, die behandelt werden mussten (z. B. Datenbank nicht erreichbar) |

Vorgabe für Backend-Entwickler: Beispiele für korrektes Logging:

```java
// INFO: Geschäftsereignis
logger.info("Lernplan für Nutzer {} berechnet. {} Aufgaben.", userId, taskCount);

// DEBUG: Detailinformation
logger.debug("TaskRepository.findByUserId() returned {} tasks for user {}", tasks.size(), userId);

// WARN: Behandelbar, aber ungewöhnlich
logger.warn("SMTP nicht konfiguriert. E-Mail-Versand wird übersprungen.");

// ERROR: Mit Exception
logger.error("Datenbankfehler beim Speichern der Aufgabe {}", taskId, sqlException);
```

### 8.3.4 Ausgeschlossene Log-Inhalte

**Verboten**: Folgende Daten dürfen unter keinen Umständen in Logs geschrieben werden:

- Passwörter (auch nicht gehasht)
- JWT-Tokens (vollständig oder gekürzt)
- API-Keys oder Secrets
- E-Mail-Inhalte mit personenbezogenen Daten

**Falsch**:
```java
logger.info("Login-Versuch mit Passwort: {}", password);   
logger.debug("JWT: {}", token);                            
```

**Richtig**:
```java
logger.info("Login-Versuch für Nutzer: {}", email);        
logger.debug("Token für Nutzer {} ausgestellt", userId);   
```

### 8.3.5 Log-Konfiguration

Die Log-Konfiguration wird in `backend/src/main/resources/logback-spring.xml` zentral verwaltet.

**Standardkonfiguration**:

```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>

    <!-- Spring Security nur auf WARN -->
    <logger name="org.springframework.security" level="WARN"/>
</configuration>
```

**Vorgabe**: Im Entwicklungsmodus wird auf die Konsole geloggt. Für eine spätere Produktivumgebung kann ein Datei-Appender ergänzt werden. Aktuell ist dies nicht vorgesehen.

**Vorgabe**: Das Log-Level für eigene Klassen (`com.studyplanner`) ist `INFO`. Für Fremdbibliotheken wird `WARN` oder höher verwendet, um Log-Spam zu vermeiden.

---

## 8.4 Datenbankzugriff und Persistenz

### 8.4.1 Verwendete Technologie

- **Datenbank**: PostgreSQL
- **Zugriff**: Spring Data JPA + Hibernate
- **Schema-Migration**: Flyway

### 8.4.2 Repository-Struktur

Vorgabe für Backend-Entwickler: Für jede Entität wird ein `JpaRepository`-Interface angelegt:

```java
// repository/TaskRepository.java
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    Optional<Task> findByIdAndUserId(Long id, Long userId);
}
```

**Regeln**:
- Repositories enthalten keine Geschäftslogik.
- Komplexe Abfragen werden als `JPQL` mit `@Query` oder über Methodennamen (Derived Query Methods) umgesetzt.
- Native SQL wird nur verwendet, wenn JPQL die Abfrage nicht abbilden kann.

### 8.4.3 Transaktionen

Vorgabe für Backend-Entwickler: Jede Service-Methode, die lesend oder schreibend auf die Datenbank zugreift, wird mit `@Transactional` annotiert:

```java
// service/TaskService.java
@Service
public class TaskService {

    @Transactional(readOnly = true)
    public List<TaskDTO> findAllByUserId(Long userId) {
        return taskRepository.findByUserId(userId).stream()
            .map(taskMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public TaskDTO createTask(CreateTaskRequest request, Long userId) {
        Task task = taskMapper.toEntity(request);
        task.setUser(userRepository.getReferenceById(userId));
        Task saved = taskRepository.save(task);
        return taskMapper.toDto(saved);
    }
}
```

**Regeln**:
- Lesende Operationen: `@Transactional(readOnly = true)`
- Schreibende Operationen: `@Transactional`
- `@Transactional` wird ausschließlich in der Service-Schicht verwendet, niemals in Controllern oder Repositories.

### 8.4.4 Lazy Loading und N+1-Problem

Vorgabe für Backend-Entwickler: Bei Abfragen mit Beziehungen (z. B. Task → Subtasks) muss geprüft werden, ob Lazy Loading zu N+1-Queries führt.

**Falsch** (verursacht N+1):
```java
List<Task> tasks = taskRepository.findByUserId(userId);
for (Task t : tasks) {
    System.out.println(t.getSubtasks().size()); // Jeder Durchlauf = zusätzliche Query
}
```

**Richtig** (mit `EntityGraph` oder `JOIN FETCH`):
```java
// repository/TaskRepository.java
@Query("SELECT t FROM Task t LEFT JOIN FETCH t.subtasks WHERE t.user.id = :userId")
List<Task> findByUserIdWithSubtasks(@Param("userId") Long userId);
```

### 8.4.5 Datenbank-Migrationen mit Flyway

Schema-Änderungen werden ausschließlich über Flyway-Migrationen durchgeführt:

- Migrationen liegen unter `backend/src/main/resources/db/migration/`
- Benennung: `V<Version>__<Beschreibung>.sql`
- Beispiel: `V1__init_schema.sql`, `V2__add_reminder_table.sql`

**Regeln**:
- Migrationen sind immutable. Einmal eingecheckte Migrationen dürfen nicht mehr verändert werden.
- Neue Schema-Änderungen erhalten immer eine neue Versionsnummer.
- Die `ddl-auto`-Einstellung von Hibernate ist auf `validate` gesetzt, damit Hibernate nur prüft, ob das Schema mit den Entitäten übereinstimmt.

```yaml
# application.yml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
    locations: classpath:db/migration
```

### 8.4.6 Enum-Speicherung

Java-Enums werden als String in der Datenbank gespeichert:

```java
@Entity
public class Task {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;
}
```

**Wichtig**: `EnumType.ORDINAL` ist verboten, da es bei nachträglicher Änderung der Enum-Reihenfolge zu Dateninkonsistenzen führt.