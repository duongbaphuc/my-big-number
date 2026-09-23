# REST API Test Matrix

| ID | Input | Precondition | Expected HTTP | Expected response/error | Assertions |
|---|---|---|---:|---|---|
| API-001 | `{"num1":"1234","num2":"897","includeSteps":false}` | Valid JSON | `200` | `{"sum":"2131","steps":[]}` | Exact schema |
| API-002 | `{"num1":"0","num2":"0","includeSteps":false}` | Zero values | `200` | `{"sum":"0","steps":[]}` | Exact sum |
| API-003 | `{"num1":"999","num2":"1","includeSteps":false}` | Carry across digits | `200` | `{"sum":"1000","steps":[]}` | Exact sum |
| API-004 | `{"num1":"1","num2":"999999","includeSteps":false}` | Unequal lengths | `200` | `{"sum":"1000000","steps":[]}` | Exact string result |
| API-005 | Two explicit 100-digit strings | Large input | `200` | Exact independently calculated sum | No overflow |
| API-006 | `{"num1":"1234","num2":"897","includeSteps":true}` | Progress requested | `200` | `sum=2131`, exactly 4 ordered steps | Exact step fields |
| API-007 | Omit `includeSteps` | Default option | `200` | `{"sum":"3","steps":[]}` | Default is false |
| API-008 | `{"num1":"000123","num2":"001","includeSteps":false}` | Leading zeros | `200` | `{"sum":"124","steps":[]}` | Canonical output |
| API-009 | `{"num1":"","num2":"1"}` | Blank first value | `400` | `code=INVALID_NUM1` | Problem Details |
| API-010 | `{"num2":"1"}` | Missing first field | `400` | `code=INVALID_NUM1` | Exact error schema |
| API-011 | `{"num1":"12a4","num2":"1"}` | Invalid character | `400` | `code=INVALID_NUM1` | No stack trace |
| API-012 | `{"num1":"1","num2":"2","extra":"x"}` | Unknown field | `400` | `code=MALFORMED_REQUEST` | Unknown field rejected |
| API-013 | `{"num1":"1","num2":"2","includeSteps":null}` | Null option | `400` | `code=INVALID_INCLUDE_STEPS` | Exact error |
| API-014 | Invalid JSON body | Malformed JSON | `400` | `code=MALFORMED_REQUEST` | Problem Details |
| API-015 | `GET /api/calculations` | Unsupported method | `405` | `code=METHOD_NOT_ALLOWED` | Problem Details |
| API-016 | `GET /`, `POST /calculate` | UI regression | Existing status/template | Existing HTML behavior | No route break |

## Required assertions

Mỗi test phải kiểm tra, khi phù hợp:

- HTTP status.
- `Content-Type`.
- Exact `sum`.
- Presence and type of required fields.
- Absence of undocumented fields.
- Error fields `type`, `title`, `status`, `detail`.
- Error field `code` matches the error matrix.
- Không có stack trace hoặc exception class trong error response.