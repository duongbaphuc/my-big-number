# MyBigNumber Java Coding & Logging Rules

1. **Language & Framework:** Use Java 21 and Spring Boot 3.2.5 as configured by the root POM.
2. **Naming Conventions:** Use `PascalCase` for classes, `camelCase` for methods and variables, `UPPER_SNAKE_CASE` for constants.
3. **Exception Handling:** Never throw generic `RuntimeException` or `Exception`. Use specific custom exceptions or standard ones like `IllegalArgumentException`.
4. **Logging Standards:**
  - DO NOT log sensitive data, PII, or full unbounded numeric inputs.
  - Preserve `java.util.logging.Logger` in `my-big-number-core` unless an explicit migration task is approved.
5. **Dependency Injection:** Always use constructor injection. Avoid field injection (`@Autowired` on fields).
6. **Code Simplicity:** Avoid over-engineering, design patterns factories unless requested. Write clean, flat vertical slices.
7. **Variable Declaration Scope:**
  - Declare variables at the narrowest scope that is clear and correct. Do not prohibit loop-local variables mechanically; loop-local declarations do not cause the claimed per-iteration allocation problem for ordinary local primitives and references.
#### Example (Dependency Injection):
```java
public class CalculationApplicationService {
  private final MyBigNumber myBigNumber;

  public CalculationApplicationService(MyBigNumber myBigNumber) {
    this.myBigNumber = myBigNumber;
  }
}
```
