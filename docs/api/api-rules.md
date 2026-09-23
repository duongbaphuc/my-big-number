# MyBigNumber REST API Specification & Implementation Rules

Tài liệu quy chuẩn kỹ thuật cho việc tự động sinh mã nguồn REST API `POST /api/calculations` thuộc module `my-big-number-web`.

---

## 1. Target File Mapping (Checklist đích đến của Code)

Mọi thay đổi code chỉ được phép tạo mới hoặc chỉnh sửa đúng các file theo danh sách tuyệt đối sau:

- [ ] **[CREATE]** `C:\my-big-number\my-big-number-web\src\main\java\com\bignumber\web\dto\CalculationApiRequest.java`
- [ ] **[CREATE]** `C:\my-big-number\my-big-number-web\src\main\java\com\bignumber\web\dto\CalculationApiResponse.java`
- [ ] **[CREATE]** `C:\my-big-number\my-big-number-web\src\main\java\com\bignumber\web\dto\CalculationStepDto.java`
- [ ] **[CREATE]** `C:\my-big-number\my-big-number-web\src\main\java\com\bignumber\web\dto\ProblemDetailResponse.java`
- [ ] **[CREATE]** `C:\my-big-number\my-big-number-web\src\main\java\com\bignumber\web\controller\BigNumberRestController.java`
- [ ] **[CREATE]** `C:\my-big-number\my-big-number-web\src\main\java\com\bignumber\web\exception\ApiExceptionHandler.java`
- [ ] **[CREATE]** `C:\my-big-number\my-big-number-web\src\test\java\com\bignumber\web\controller\BigNumberRestControllerTest.java`
- [ ] **[UPDATE-ONLY]** `C:\my-big-number\my-big-number-web\pom.xml` (Chỉ thêm dependency `spring-boot-starter-validation` nếu thiếu)

> [!CAUTION]
> **Tuyệt đối không sửa đổi:**
> - `C:\my-big-number\my-big-number-core\src\main\java\com\bignumber\core\MyBigNumber.java`
> - `C:\my-big-number\my-big-number-web\src\main\java\com\bignumber\web\controller\BigNumberController.java` (HTML MVC)
> - `C:\my-big-number\my-big-number-web\src\main\resources\templates\index.html`

---

## 2. Table-Driven Data Contracts

### 2.1. Request Schema (`CalculationApiRequest`)
* Endpoint: `POST /api/calculations`
* Content-Type: `application/json`

| Tên trường | Kiểu dữ liệu | Ràng buộc bắt buộc / Độ dài | Mô tả |
| :--- | :--- | :--- | :--- |
| `num1` | `String` | Bắt buộc; Not Blank; Độ dài: 1-100,000 ký tự; Regex: `^[0-9]+$` | Chuỗi số nguyên không âm thứ nhất |
| `num2` | `String` | Bắt buộc; Not Blank; Độ dài: 1-100,000 ký tự; Regex: `^[0-9]+$` | Chuỗi số nguyên không âm thứ hai |
| `includeSteps` | `Boolean` | Không bắt buộc; Mặc định `false`; Không được là `null` | Chỉ định có sinh danh sách từng bước hay không |

### 2.2. Success Response Schema (`CalculationApiResponse`)
* Status: `200 OK`
* Content-Type: `application/json`

| Tên trường | Kiểu dữ liệu | Ràng buộc bắt buộc / Độ dài | Mô tả |
| :--- | :--- | :--- | :--- |
| `sum` | `String` | Bắt buộc; Chuỗi chữ số chuẩn (canonical decimal); Không rỗng | Kết quả phép tính cộng |
| `steps` | `List<CalculationStepDto>` | Bắt buộc; Mảng rỗng `[]` khi `includeSteps=false` | Danh sách chi tiết từng bước tính toán |
| `steps[].stepNumber` | `Integer` | Bắt buộc; $\ge 1$ | Số thứ tự bước tính |
| `steps[].description` | `String` | Bắt buộc; Chuỗi tiếng Việt diễn giải bước tính | Lời diễn giải chi tiết |
| `steps[].intermediateResult`| `String` | Bắt buộc; Chuỗi ký tự số tích lũy | Kết quả tạm thời tính đến bước hiện tại |
| `steps[].carry` | `Integer` | Bắt buộc; Chỉ nhận giá trị `0` hoặc `1` | Số nhớ mang sang hàng tiếp theo |

### 2.3. RFC 7807 Error Response Schema (`ProblemDetailResponse`)
* Status: `400 Bad Request`, `405 Method Not Allowed`, `500 Internal Server Error`
* Content-Type: `application/problem+json`

| Tên trường | Kiểu dữ liệu | Ràng buộc bắt buộc / Độ dài | Mô tả |
| :--- | :--- | :--- | :--- |
| `type` | `String` | Bắt buộc; Định dạng URI (VD: `https://my-big-number.example/problems/...`) | Định danh định kiểu của loại lỗi |
| `title` | `String` | Bắt buộc; Tiêu đề ngắn gọn của lỗi theo chuẩn HTTP | Tiêu đề lỗi (VD: `Invalid request`) |
| `status` | `Integer` | Bắt buộc; Khớp với mã HTTP Status | Mã trạng thái HTTP |
| `detail` | `String` | Bắt buộc; Thông báo lỗi thân thiện, không lộ stack trace | Chi tiết nguyên nhân lỗi |
| `code` | `String` | Bắt buộc; Chữ hoa UPPER_SNAKE_CASE | Mã lỗi định danh nghiệp vụ cho client |

---

## 3. Step-by-Step Logic (Mã giả nghiệp vụ chi tiết)

### 3.1. Luồng xử lý tại `BigNumberRestController.calculate(...)`
1. Nhận HTTP request `POST /api/calculations` với body `@Valid @RequestBody CalculationApiRequest request`.
2. Kiểm tra cờ `request.getIncludeSteps()`:
   * **Nếu `false` hoặc `null`:** 
     - Thực thi hàm tối ưu: `String total = myBigNumber.sum(request.getNum1(), request.getNum2());`
     - Khởi tạo `CalculationApiResponse` với `sum = total` và `steps = Collections.emptyList()`.
   * **Nếu `true`:**
     - Thực thi: `CalculationResult result = myBigNumber.sumWithProgress(request.getNum1(), request.getNum2());`
     - Map danh sách `result.steps()` sang `List<CalculationStepDto>`.
     - Khởi tạo `CalculationApiResponse` với `sum = result.sum()` và `steps = mappedSteps`.
3. Trả về `ResponseEntity.ok(response)`.

### 3.2. Luồng xử lý tại `ApiExceptionHandler`
1. **Bắt `MethodArgumentNotValidException` (Lỗi validation Bean Validation):**
   - Trích xuất `FieldError` đầu tiên.
   - Map field vi phạm (`num1`, `num2`, `includeSteps`) sang HTTP `400`, `code` tương ứng từ Error Matrix, và message mô tả lỗi.
   - Đóng gói thành `ProblemDetailResponse` và trả về với `HttpStatus.BAD_REQUEST`.
2. **Bắt `HttpMessageNotReadableException` (JSON sai cú pháp, trường lạ, hoặc sai kiểu dữ liệu):**
   - Tạo `ProblemDetailResponse` với `status = 400`, `code = "MALFORMED_REQUEST"`, `detail = "Request body is invalid"`.
   - Trả về với `HttpStatus.BAD_REQUEST`.
3. **Bắt `HttpRequestMethodNotSupportedException` (Gọi sai Method như GET/PUT):**
   - Tạo `ProblemDetailResponse` với `status = 405`, `code = "METHOD_NOT_ALLOWED"`, `detail = "HTTP method is not supported"`.
   - Trả về với `HttpStatus.METHOD_NOT_ALLOWED`.
4. **Bắt `NoResourceFoundException` (Static resource hoặc endpoint không tồn tại, vd: `/favicon.ico`):**
   - Tạo `ProblemDetailResponse` với `status = 404`, `code = "RESOURCE_NOT_FOUND"`, `detail = ex.getMessage()`.
   - Trả về với `HttpStatus.NOT_FOUND` (không log SEVERE để tránh nhiễu log hệ thống).
5. **Bắt `Exception` (Lỗi hệ thống ngoài dự kiến):**
   - Ghi log nội bộ ở mức `SEVERE` (chỉ log error class và message, không log toàn bộ chuỗi số đầu vào nếu quá lớn).
   - Tạo `ProblemDetailResponse` với `status = 500`, `code = "INTERNAL_ERROR"`, `detail = "An unexpected error occurred"`.
   - Trả về với `HttpStatus.INTERNAL_SERVER_ERROR`.

---

## 4. Edge Cases & Error Matrix (Phủ các ngoại lệ)

| Điều kiện vi phạm | HTTP Status | Mã lỗi (`code`) | Message trả về (`detail`) |
| :--- | :---: | :--- | :--- |
| Thiếu trường `num1` | `400` | `INVALID_NUM1` | `num1 is required` |
| `num1` là chuỗi rỗng | `400` | `INVALID_NUM1` | `num1 must not be blank` |
| `num1` chứa chữ cái/ký tự lạ/âm | `400` | `INVALID_NUM1` | `num1 must contain digits only` |
| `num1` vượt quá 100,000 ký tự | `400` | `NUM1_TOO_LONG` | `num1 exceeds the maximum length` |
| Thiếu trường `num2` | `400` | `INVALID_NUM2` | `num2 is required` |
| `num2` là chuỗi rỗng | `400` | `INVALID_NUM2` | `num2 must not be blank` |
| `num2` chứa chữ cái/ký tự lạ/âm | `400` | `INVALID_NUM2` | `num2 must contain digits only` |
| `num2` vượt quá 100,000 ký tự | `400` | `NUM2_TOO_LONG` | `num2 exceeds the maximum length` |
| `includeSteps` là `null` hoặc không phải boolean | `400` | `INVALID_INCLUDE_STEPS` | `includeSteps must be a boolean` |
| JSON malformed hoặc chứa trường ngoài đặc tả | `400` | `MALFORMED_REQUEST` | `Request body is invalid` |
| Sử dụng phương thức khác `POST` (vd: `GET`) | `405` | `METHOD_NOT_ALLOWED` | `HTTP method is not supported` |
| Resource tĩnh hoặc endpoint không tồn tại | `404` | `RESOURCE_NOT_FOUND` | `No static resource...` |
| Lỗi runtime nội bộ không lường trước | `500` | `INTERNAL_ERROR` | `An unexpected error occurred` |

---

## 5. Architectural Constraints (Ràng buộc kiến trúc)

| Lớp/Thành phần | Được phép gọi (May Call) | Tuyệt đối cấm (Must NOT Call) |
| :--- | :--- | :--- |
| `BigNumberRestController` | `MyBigNumber` (Bean injection), `CalculationApiRequest`, `CalculationApiResponse`, `CalculationStepDto` | Không gọi `BigNumberController` (MVC), không tự cài đặt thuật toán cộng, không trả về raw Exception |
| `ApiExceptionHandler` | `ProblemDetailResponse` | Không import logic tính toán, không để lộ exception stacktrace ra payload |
| DTOs (`CalculationApiRequest/Response`) | Jackson & Jakarta Validation Annotations | Không chứa logic nghiệp vụ, không phụ thuộc vào Spring Context |
| `MyBigNumber` (Core Library) | Java standard library | Không được sửa đổi; không phụ thuộc vào `my-big-number-web` |
