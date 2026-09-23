# REST API Design

## 1. Mục tiêu thiết kế

Thêm một REST surface nhỏ vào `my-big-number-web`, giữ nguyên MVC/Thymeleaf và tái sử dụng toàn bộ logic tính toán từ `my-big-number-core`.

## 2. Thành phần kiến trúc

```text
HTTP Request (POST /api/calculations)
       │
       ▼
[BigNumberRestController] ──(Constructor Injection)──► [MyBigNumber (Core Bean)]
       │
       ├─► Validates via Jakarta Bean Validation (@Valid)
       ├─► Calls myBigNumber.sum(...) OR myBigNumber.sumWithProgress(...)
       └─► Maps result to CalculationApiResponse
```

> **Lưu ý thiết kế:** Để giữ kiến trúc Vertical Slice tinh gọn, `BigNumberRestController` inject trực tiếp Bean `MyBigNumber` qua Constructor. Không tạo thêm lớp trung gian `CalculationApplicationService`.

### File dự kiến thêm

```text
my-big-number-web/src/main/java/com/bignumber/web/controller/BigNumberRestController.java
my-big-number-web/src/main/java/com/bignumber/web/dto/CalculationApiRequest.java
my-big-number-web/src/main/java/com/bignumber/web/dto/CalculationApiResponse.java
my-big-number-web/src/main/java/com/bignumber/web/dto/CalculationStepDto.java
my-big-number-web/src/main/java/com/bignumber/web/dto/ProblemDetailResponse.java
my-big-number-web/src/main/java/com/bignumber/web/exception/ApiExceptionHandler.java
```

## 2.1. Target File Mapping

### Create

- `C:\my-big-number\my-big-number-web\src\main\java\com\bignumber\web\controller\BigNumberRestController.java`
- `C:\my-big-number\my-big-number-web\src\main\java\com\bignumber\web\dto\CalculationApiRequest.java`
- `C:\my-big-number\my-big-number-web\src\main\java\com\bignumber\web\dto\CalculationApiResponse.java`
- `C:\my-big-number\my-big-number-web\src\main\java\com\bignumber\web\dto\CalculationStepDto.java`
- `C:\my-big-number\my-big-number-web\src\main\java\com\bignumber\web\dto\ProblemDetailResponse.java`
- `C:\my-big-number\my-big-number-web\src\main\java\com\bignumber\web\exception\ApiExceptionHandler.java`
- `C:\my-big-number\my-big-number-web\src\test\java\com\bignumber\web\controller\BigNumberRestControllerTest.java`

### Update only when required

- `C:\my-big-number\my-big-number-web\pom.xml`
- `C:\my-big-number\my-big-number-web\src\main\resources\application.properties`

### Must not modify

- `C:\my-big-number\my-big-number-core\src\main\java\com\bignumber\core\MyBigNumber.java`
- `C:\my-big-number\my-big-number-web\src\main\java\com\bignumber\web\controller\BigNumberController.java`
- `C:\my-big-number\my-big-number-web\src\main\resources\templates\index.html`

Tên package có thể điều chỉnh theo convention hiện có, nhưng không được trộn REST controller với controller render template nếu không có lý do rõ ràng.

## 3. Luồng xử lý chi tiết (Step-by-step Execution Logic)

1. Spring DispatcherServlet tiếp nhận HTTP request `POST /api/calculations`.
2. Jackson ObjectMapper deserialize JSON payload vào đối tượng `CalculationApiRequest`. Nếu JSON malformed hoặc chứa field lạ, ném `HttpMessageNotReadableException`.
3. Jakarta Bean Validation (`@Valid`) kiểm tra tính hợp lệ của `num1`, `num2`, `includeSteps`. Nếu vi phạm, ném `MethodArgumentNotValidException`.
4. `BigNumberRestController` kiểm tra cờ `includeSteps`:
   - Nếu `false` hoặc `null`: Gọi trực tiếp `myBigNumber.sum(request.getNum1(), request.getNum2())`. Gán `steps` là danh sách rỗng.
   - Nếu `true`: Gọi trực tiếp `myBigNumber.sumWithProgress(request.getNum1(), request.getNum2())`. Chuyển đổi từng `CalculationStep` sang `CalculationStepDto`.
5. Controller đóng gói kết quả vào `CalculationApiResponse` và trả về HTTP `200 OK`.
6. Tầng `ApiExceptionHandler` đón bắt các ngoại lệ nếu xảy ra:
   - `MethodArgumentNotValidException` -> Trích xuất field lỗi đầu tiên và trả về HTTP `400` Problem Details theo đúng Error Matrix.
   - `HttpMessageNotReadableException` -> Trả về HTTP `400` với code `MALFORMED_REQUEST`.
   - `HttpRequestMethodNotSupportedException` -> Trả về HTTP `405` với code `METHOD_NOT_ALLOWED`.
   - `Exception` -> Ghi log SEVERE và trả về HTTP `500` với code `INTERNAL_ERROR`.

## 3.1. Architectural Constraints

| Source | May call | Must not call |
|---|---|---|
| `BigNumberRestController` | `CalculationApiRequest`, `MyBigNumber` (Bean injection), `CalculationApiResponse`, `CalculationStepDto` | repository, template rendering, duplicated arithmetic, intermediate service |
| `CalculationApiRequest` | validation annotations | core service, controller, database |
| `CalculationApiResponse` | DTO fields only | business logic, Spring MVC routing |
| `ApiExceptionHandler` | `ProblemDetailResponse` | arithmetic logic, raw exception exposure |
| `MyBigNumber` | core models and standard Java APIs | Spring MVC, web DTOs, HTTP concepts |
| Web module | core public API | core implementation internals |

Controller chỉ xử lý routing và orchestration; thuật toán cộng phải được gọi qua public API của core.

## 4. Ràng buộc implementation

- Dùng constructor injection.
- Không sao chép thuật toán cộng vào web module.
- Không dùng `int`, `long` hoặc `BigInteger` cho input/output số lớn.
- Dùng `@Valid` cho request body.
- Không trả raw exception, stack trace hoặc raw string cho lỗi.
- Không thêm field JSON ngoài `api-spec.md`.
- Không sửa `BigNumberController` ngoài thay đổi thật sự cần thiết.
- Không thêm database, security framework hoặc authentication trong scope này.

## 5. Dependency

Chỉ thêm dependency nếu POM hiện tại chưa cung cấp:

- Spring MVC/Web.
- Jakarta Bean Validation.
- Jackson support thông qua Spring Boot Web.
- Spring Boot test và MockMvc test support.

Không nâng version Spring Boot hoặc Java trong task này.

## 6. Test strategy

- Controller contract tests bằng MockMvc.
- Unit test cho mapping response nếu mapping có logic riêng.
- Test validation và Problem Details.
- Regression test cho route Thymeleaf hiện có.
- Chạy `mvn -pl my-big-number-web -am test` trong quá trình phát triển.
- Chạy `mvn clean verify` trước khi bàn giao.

## 7. Không được triển khai trong task đầu tiên

- Pagination.
- Persistence/history.
- Authentication/authorization.
- Rate limiting implementation.
- Async calculation.
- Streaming response.