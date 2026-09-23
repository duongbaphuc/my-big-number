# `coding-rules.md` — Code Quality & Convention (`my-big-number-core`)

Tài liệu quy định toàn bộ các tiêu chuẩn lập trình, quy ước đặt tên, xử lý ngoại lệ, logging và ranh giới kiến trúc dành riêng cho module lõi `my-big-number-core`.

---

## 1. Quy ước Đặt tên & Định dạng Mã nguồn (Naming & Style Conventions)

Áp dụng chuẩn phong cách mã nguồn **Google Java Style** kết hợp các tính năng hiện đại của **Java 21 (LTS)**:

| Thành phần | Quy ước đặt tên | Ví dụ chuẩn | Ví dụ vi phạm |
| :--- | :--- | :--- | :--- |
| **Package** | Viết thường toàn bộ (`lowercase`), phân cấp rõ ràng | `com.bignumber.core` | `com.bignumber.Core`, `com.bignumber_core` |
| **Class** | `PascalCase`, danh từ mô tả chính xác trách nhiệm | `MyBigNumber`, `CalculationStep` | `myBigNumber`, `Big_Number_Helper` |
| **Record** | `PascalCase`, đại diện cho đối tượng mang dữ liệu | `CalculationResult` | `ResultDataRecord`, `calc_result` |
| **Method** | `camelCase`, bắt đầu bằng động từ hành động | `sum(...)`, `sumWithProgress(...)` | `Sum(...)`, `do_calculation(...)` |
| **Variable & Parameter**| `camelCase`, tên ngắn gọn, có ý nghĩa theo ngữ cảnh | `stn1`, `maxLen`, `intermediateResult` | `String1`, `temp_res`, `val` |
| **Constant / Logger** | `UPPER_SNAKE_CASE` đi kèm từ khóa `static final` | `LOGGER` | `logger`, `s_logger` |

### Quy định về phạm vi biến (Variable Scoping):
* Khai báo biến ở phạm vi hẹp nhất có thể. Tuy nhiên, trong các vòng lặp tính toán hiệu năng cao xử lý hàng trăm nghìn phần tử, việc tái sử dụng biến cục bộ nguyên thủy (`int digit1, digit2, total;`) được chấp nhận để giảm chi phí cấp phát lặp lại trong stack frame.

---

## 2. Ranh giới Kiến trúc & Quy tắc Tương tác (Architectural Boundaries)

Để bảo toàn tính độc lập và khả năng tái sử dụng tối đa của Core Module, mọi lập trình viên và AI Assistants phải tuân thủ nghiêm ngặt ma trận quyền tương tác sau:

```text
       ┌──────────────────────────────────────────────┐
       │   my-big-number-web / REST API Module        │
       │   (Spring Boot, Web MVC, DTOs, HTTP/JSON)    │
       └──────────────────────┬───────────────────────┘
                              │
                              ▼ (Chỉ được phụ thuộc 1 chiều qua Public API)
       ┌──────────────────────────────────────────────┐
       │   my-big-number-core                         │
       │   (Pure Java 21, POJO, Domain Logic, JUL)    │
       └──────────────────────────────────────────────┘
```

### Ma trận Quyền hạn Tương tác:

| Lớp / Module | ĐƯỢC PHÉP gọi (May Call) | TUYỆT ĐỐI CẤM gọi (Must NOT Call) |
| :--- | :--- | :--- |
| **`my-big-number-core`** | Thư viện chuẩn Java (`java.lang.*`, `java.util.*`, `java.util.logging.*`) | **Tuyệt đối không import bất kỳ class nào từ `my-big-number-web`**. Không sử dụng annotations của Spring (`@Service`, `@Component`, `@Autowired`). Không chứa khái niệm HTTP, JSON hay Servlet. |
| **`my-big-number-web`** | Các phương thức công khai: `MyBigNumber.sum(...)`, `MyBigNumber.sumWithProgress(...)`, `CalculationResult`, `CalculationStep`. | Không được sao chép (duplicate) thuật toán cộng vào tầng Controller/Service của Web. Không được can thiệp vào các phương thức `private` của Core. |

* **[GOOD]:** Khởi tạo `MyBigNumber` thành một Bean tại lớp cấu hình của Web module (`@Bean public MyBigNumber myBigNumber() { return new MyBigNumber(); }`) và inject qua Constructor Injection.
* **[BAD]:** Đánh dấu class `MyBigNumber` bằng `@Service` hoặc `@Component` để biến nó thành Spring Bean nội tại trong Core.

---

## 3. Quy chuẩn Xử lý Ngoại lệ (Exception Handling Rules)

1. **Tuyệt đối không nuốt ngoại lệ (Do NOT swallow exceptions):** Không sử dụng các khối `try-catch` rỗng hoặc chỉ `printStackTrace()` mà không giải quyết vấn đề.
2. **Không ném ngoại lệ chung chung (Generic Exceptions):** Tuyệt đối cấm ném `new Exception(...)` hoặc `new RuntimeException(...)`.
3. **Sử dụng chuẩn `IllegalArgumentException` kèm thông tin vị trí:**
   * Khi dữ liệu vi phạm (null, rỗng, ký tự lạ), phải ném `IllegalArgumentException`.
   * Thông báo ngoại lệ phải cung cấp chính xác ký tự gây lỗi và vị trí index (0-indexed) để client dễ dàng định vị lỗi.

```java
// [GOOD]: Xác định rõ ký tự và vị trí vi phạm
if (c1 < '0' || c1 > '9') {
    LOGGER.severe("LỖI: Chuỗi 1 chứa ký tự không hợp lệ '" + c1 + "' tại vị trí " + index1 + ".");
    throw new IllegalArgumentException(String.format("Chuỗi 1 chứa ký tự không hợp lệ '%c' tại vị trí %d.", c1, index1));
}

// [BAD]: Ném lỗi chung chung, mơ hồ
if (c1 < '0' || c1 > '9') {
    throw new RuntimeException("Dữ liệu đầu vào sai.");
}
```

---

## 4. Quy chuẩn Ghi nhật ký (Logging Standards)

1. **Thư viện logging bắt buộc:** Sử dụng thư viện chuẩn của JDK: `java.util.logging.Logger`. Không kéo thêm `log4j`, `logback` hay `slf4j` vào dependencies của Core.
2. **Khởi tạo Logger:**
   ```java
   private static final Logger LOGGER = Logger.getLogger(MyBigNumber.class.getName());
   ```
3. **Phân định mức độ log (Log Levels):**
   * `Level.SEVERE`: Chỉ dùng khi xảy ra lỗi dữ liệu đầu vào hoặc lỗi hệ thống không thể tiếp tục thực thi.
   * `Level.INFO`: Dùng để ghi nhận bắt đầu phép toán, các bước trung gian của `sumWithProgress` và kết quả cuối cùng.
4. **Bảo vệ hiệu năng bằng `isLoggable`:**
   * Với các phép toán có hàng vạn chữ số, việc ghép chuỗi log trong vòng lặp sẽ gây sụt giảm nghiêm trọng hiệu năng. Mọi câu lệnh log `INFO` phải được bọc trong điều kiện:
   ```java
   // [GOOD]: Bảo vệ bằng isLoggable
   if (LOGGER.isLoggable(Level.INFO)) {
       LOGGER.info("Bước " + step + ": " + desc);
   }

   // [BAD]: Nối chuỗi trực tiếp dù logger có thể đang bị tắt
   LOGGER.info("Bước " + step + ": " + desc);
   ```

---

## 5. Quy chuẩn Kiểm thử Tự động (Testing Standards)

Toàn bộ kiểm thử của Core module nằm tại `src/test/java/com/bignumber/core/MyBigNumberTest.java`, ứng dụng **JUnit 5 Jupiter**:

1. **Tiêu chí Coverage bắt buộc:** Đạt **100% Line Coverage**, **100% Branch Coverage** và **100% Method Coverage** (được kiểm chứng tự động bởi JaCoCo Engine).
2. **Sử dụng Parameterized Tests:**
   * Mọi kịch bản kiểm thử biên và dữ liệu đa dạng phải sử dụng `@ParameterizedTest` kết hợp `@CsvSource` để tăng độ phủ mã mà không trùng lặp code test:
   ```java
   @ParameterizedTest
   @CsvSource({
       "0, 0, 0",
       "999, 1, 1000",
       "1234, 897, 2131"
   })
   void testSumValidCases(String a, String b, String expected) {
       assertEquals(expected, myBigNumber.sum(a, b));
   }
   ```
3. **Các nhóm ca kiểm thử bắt buộc:**
   * **Test Case Cơ bản:** `0 + 0 = 0`, nhớ 1 hàng, nhớ dồn chuỗi liên tiếp qua nhiều hàng (`999 + 1 = 1000`).
   * **Lệch độ dài (Length Asymmetry):** 1 vs 2, 2 vs 5, 5 vs 30 chữ số và ngược lại.
   * **Số siêu lớn (Stress Test):** Tối thiểu 100 chữ số cộng 100 chữ số.
   * **Test Ngoại lệ (Exception Tests):** Sử dụng `assertThrows(IllegalArgumentException.class, ...)` cho các ca null, chuỗi rỗng `""`, chữ cái (`"12a4"`), số âm (`"-10"`), số thập phân (`"12.3"`), dấu cách (`"1 2"`), và ký tự đặc biệt (`"@#$"`).
