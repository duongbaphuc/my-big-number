# `coding-rules.md` — REST API & Spring Boot Coding Standards (`my-big-number-web`)

Tài liệu quy định toàn bộ tiêu chuẩn lập trình, quy ước thiết kế tầng Web/REST API, Dependency Injection, Validation, Exception Handling và Kiểm thử tự động dành cho module ứng dụng `my-big-number-web`.

---

## 1. Ranh giới Kiến trúc & Trách nhiệm Tầng (Separation of Concerns)

Tầng REST API đóng vai trò là **Application Layer** (cửa ngõ giao tiếp), tuyệt đối tuân thủ nguyên lý tách biệt trách nhiệm:

```text
HTTP Client (cURL / Frontend / Postman)
       │
       ▼ (HTTP POST /api/calculations)
[BigNumberRestController] ──(Validation @Valid)──► Jakarta Validator
       │
       ▼ (Constructor Injection: myBigNumber.sum / sumWithProgress)
[MyBigNumber (Core Domain Bean)]
       │
       ▼ (CalculationResult)
[BigNumberRestController] ──(Map to DTO)──► CalculationApiResponse
```

### Quy tắc bất biến:
1. **Controller không chứa logic tính toán:** Tuyệt đối không sao chép hoặc tự cài đặt thuật toán cộng số lớn trong Controller. Mọi phép toán phải ủy quyền (delegate) hoàn toàn cho bean `MyBigNumber` của module Core.
2. **Không trả Entity/Internal Model ra ngoài:** Tầng Controller chỉ giao tiếp với Client thông qua các DTO chuyên biệt (`CalculationApiRequest`, `CalculationApiResponse`, `ProblemDetailResponse`).
3. **Không trộn Controller HTML và REST Controller:** `BigNumberController` (Thymeleaf MVC) và `BigNumberRestController` (REST API) phải nằm ở 2 class độc lập.

---

## 2. Quy ước Đặt tên & Cấu trúc Package (Naming & Packaging Conventions)

Áp dụng chuẩn phong cách mã nguồn **Spring Framework Conventions**:

| Thành phần | Quy ước đặt tên | Ví dụ chuẩn | Ví dụ vi phạm |
| :--- | :--- | :--- | :--- |
| **REST Controller** | Hậu tố `RestController` | `BigNumberRestController` | `BigNumberController`, `BigNumberApi` |
| **Request DTO** | Hậu tố `ApiRequest` | `CalculationApiRequest` | `CalcReq`, `BigNumberForm` |
| **Response DTO** | Hậu tố `ApiResponse` hoặc `Dto` | `CalculationApiResponse`, `CalculationStepDto` | `CalcResult`, `ResponseData` |
| **Exception Handler** | Hậu tố `ExceptionHandler` | `ApiExceptionHandler` | `ErrorHandler`, `GlobalControllerAdvice` |
| **Error Response DTO**| Đặt tên theo chuẩn RFC 7807 | `ProblemDetailResponse` | `ErrorDto`, `ApiError` |
| **Package Structure** | Phân chia theo layer/chức năng | `com.bignumber.web.controller`<br>`com.bignumber.web.dto`<br>`com.bignumber.web.exception` | `com.bignumber.web.rest`<br>`com.bignumber.web.model` |

---

## 3. Quy chuẩn Dependency Injection (DI)

1. **Luôn dùng Constructor Injection:**
   * Tuyệt đối cấm sử dụng Field Injection (`@Autowired` trực tiếp trên trường thuộc tính) vì gây khó khăn khi viết unit test và làm mất tính bất biến (`final`).
   * Các trường phụ thuộc phải được khai báo với từ khóa `private final`.

```java
// [GOOD]: Constructor Injection chuẩn mực
@RestController
@RequestMapping("/api/calculations")
public class BigNumberRestController {

    private final MyBigNumber myBigNumber;

    public BigNumberRestController(MyBigNumber myBigNumber) {
        this.myBigNumber = myBigNumber;
    }
}

// [BAD]: Field Injection gây phụ thuộc ngầm và khó unit test
@RestController
public class BigNumberRestController {
    @Autowired
    private MyBigNumber myBigNumber;
}
```

2. **Đăng ký Bean cho thư viện Core:**
   * Lớp `MyBigNumber` thuộc module Core là Java thuần (POJO). Module Web phải đăng ký nó thành Spring Bean tại lớp cấu hình chính (`BigNumberWebApplication.java`):
   ```java
   @Bean
   public MyBigNumber myBigNumber() {
       return new MyBigNumber();
   }
   ```

---

## 4. Quy chuẩn Thiết kế DTO & Xác thực Dữ liệu (DTO & Validation)

### 4.1. Request DTO
* Sử dụng class Java thông thường có getter/setter và constructor.
* Khai báo đầy đủ các annotations của **Jakarta Bean Validation**:
  - `@NotBlank`: Ngăn chặn chuỗi null, rỗng hoặc chỉ chứa khoảng trắng.
  - `@Pattern`: Ràng buộc định dạng regex số nguyên không âm (`^[0-9]+$`).
  - `@Size`: Áp đặt ngưỡng tối đa để ngăn chặn tấn công DoS (`max = 100000`).

### 4.2. Response DTO
* Bắt buộc sử dụng **Java `record`** để đảm bảo tính bất biến (Immutability), tối ưu hiệu năng và mã nguồn ngắn gọn:
```java
// [GOOD]: Java Record cho Response
public record CalculationApiResponse(
    String sum,
    List<CalculationStepDto> steps
) {}
```

---

## 5. Quy chuẩn Xử lý Ngoại lệ Tầng Web (Global Exception Handling)

1. **Chuẩn hóa phản hồi lỗi theo RFC 7807 Problem Details:**
   * Mọi lỗi trả về cho client (4xx, 5xx) bắt buộc phải có `Content-Type: application/problem+json`.
   * Cấu trúc payload lỗi tối thiểu phải có 5 trường chuẩn: `type`, `title`, `status`, `detail`, `code`.
2. **Sử dụng `@RestControllerAdvice` tập trung:**
   * Không đặt khối `try-catch` nuốt lỗi hoặc trả về `ResponseEntity` thủ công trong từng phương thức của Controller.
   * Tất cả ngoại lệ được đẩy về [ApiExceptionHandler.java](file:///c:/my-big-number/my-big-number-web/src/main/java/com/bignumber/web/exception/ApiExceptionHandler.java) để xử lý tập trung:

| Ngoại lệ | HTTP Status | Mã lỗi nghiệp vụ (`code`) |
| :--- | :---: | :--- |
| `MethodArgumentNotValidException` | `400 Bad Request` | `INVALID_NUM1`, `INVALID_NUM2`, `NUM1_TOO_LONG`, `NUM2_TOO_LONG` |
| `HttpMessageNotReadableException` | `400 Bad Request` | `MALFORMED_REQUEST` |
| `NoResourceFoundException` | `404 Not Found` | `RESOURCE_NOT_FOUND` |
| `HttpRequestMethodNotSupportedException` | `405 Method Not Allowed`| `METHOD_NOT_ALLOWED` |
| `Exception` (Unhandled) | `500 Internal Error` | `INTERNAL_ERROR` |

---

## 6. Quy chuẩn Ghi nhật ký & An toàn Thông tin (Logging & Security)

1. **Tuyệt đối không log toàn bộ chuỗi số đầu vào:**
   * Với các phép toán có hàng chục nghìn chữ số, việc in chuỗi ra log sẽ gây tràn bộ nhớ đệm console và làm đầy ổ cứng máy chủ.
   * Chỉ ghi nhận độ dài chuỗi và mã định danh request:
   ```java
   LOGGER.info(String.format("Nhận yêu cầu tính toán. Len1: %d, Len2: %d, includeSteps: %b",
           request.getNum1().length(), request.getNum2().length(), request.getIncludeSteps()));
   ```
2. **Không để lộ Exception Stack Trace ra ngoài HTTP Response:**
   * Trong phương thức xử lý lỗi 500, chỉ trả về message tổng quát `"An unexpected error occurred"`, tuyệt đối không đưa `ex.getMessage()` hay stacktrace vào trường `detail`.

---

## 7. Quy chuẩn Kiểm thử Tự động Tầng Web (MockMvc Contract Testing)

Toàn bộ test cases cho REST API nằm tại `src/test/java/com/bignumber/web/controller/BigNumberRestControllerTest.java`:

1. **Sử dụng `@SpringBootTest` kết hợp `@AutoConfigureMockMvc`:**
   * Thực hiện giả lập HTTP request tới DispatcherServlet để kiểm tra toàn diện cả luồng: Routing $\rightarrow$ Serialization $\rightarrow$ Validation $\rightarrow$ Controller $\rightarrow$ ExceptionHandler.
2. **Khẳng định tính đúng đắn của Schema (JSON Path Assertions):**
   * Mọi ca test phải kiểm tra chính xác HTTP status, kiểu dữ liệu trả về (`MediaType.APPLICATION_JSON`), và các field cốt lõi qua `jsonPath(...)`.
   * Ví dụ mẫu:
   ```java
   mockMvc.perform(post("/api/calculations")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(validJson))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.sum").value("2131"))
           .andExpect(jsonPath("$.steps").isArray());
   ```
