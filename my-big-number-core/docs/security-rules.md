# MyBigNumber Security Rules

1. **No Hardcoded Secrets:** Never hardcode API keys, passwords, connection strings, or tokens in source code or comments. Use environment variables.
2. **Input Sanitization & Validation:** Validate `num1`, `num2`, and `includeSteps` at the REST controller boundary. Reject unknown JSON fields.
3. **Payload Limits:** Enforce the API v1 maximum of 100000 characters per numeric input and configure a suitable HTTP request body limit.
4. **Logging:** Never log full numeric inputs; log only request identifiers and input lengths when diagnostics are required.
5. **Error Disclosure:** Do not return stack traces, package names, or internal exception messages to clients.
6. **Scope exclusion:** Version 1 has no database, SQL, authentication, authorization, or RBAC requirement.
