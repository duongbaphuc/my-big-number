# MyBigNumber REST API Rules

1. **REST Resource Naming:** Use the canonical plural resource path `/api/calculations`.
2. **HTTP Verbs:** Use standard verbs: `POST` for creation, `GET` for retrieval, `PUT`/`PATCH` for updates, `DELETE` for removal.
3. **Strict Schema Conformance:** DO NOT invent extra JSON fields or properties that are not explicitly defined in the requirements or API spec.
4. **Error Responses (RFC 7807):** All error responses must return Problem Details with fields: `type`, `title`, `status`, and `detail`.
5. **Validation:** The request body must use `@Valid`; `num1` and `num2` must be non-blank and match `^[0-9]+$`; `includeSteps` must be a non-null boolean when present.
6. **Endpoint:** The API endpoint is `POST /api/calculations`.
7. **Error schema:** Error responses use `application/problem+json` and fields `type`, `title`, `status`, `detail`, and `code`.
8. **Compatibility:** Do not change the existing HTML routes `GET /` and `POST /calculate`.

#### Example:
- **[GOOD]:** Returning `ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetails)`
- **[BAD]:** Returning raw strings or generic stack traces on validation failure.
