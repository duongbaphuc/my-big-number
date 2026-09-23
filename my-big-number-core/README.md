# Sub-Module: MyBigNumber Core (Task 1)

Module thư viện lõi cài đặt giải thuật cộng hai số nguyên lớn dưới dạng chuỗi ký tự theo phương pháp mô phỏng cách tính đặt cột dọc của học sinh tiểu học. Module được đóng gói thành thư viện chuẩn (`.jar`), sẵn sàng bàn giao cho các nhóm khác tái sử dụng.

> 📖 **Dành cho Lập trình viên:** Xem tài liệu kỹ thuật, quy chuẩn và báo cáo kiểm thử tại:
> * [**API Reference (Đặc tả chi tiết các hàm, kiểu dữ liệu, ngoại lệ)**](docs/API_REFERENCE.md)
> * [**Thuật toán & Phân tích tối ưu hiệu năng**](docs/ALGORITHM_AND_PERFORMANCE.md)
> * [**Quy chuẩn Lập trình Dự án (Coding Rules)**](docs/coding-rules.md)
> * [**Báo cáo độ bao phủ kiểm thử (Test Coverage: 100% Line Coverage)**](coverage-report/README.md)

---

## 1. Mục lục
1. [Đặc tả kỹ thuật](#2-đặc-tả-kỹ-thuật)
2. [Nguyên lý giải thuật](#3-nguyên-lý-giải-thuật)
3. [Quy định Logging và Xử lý ngoại lệ](#4-quy-định-logging-và-xử-lý-ngoại-lệ)
4. [Bộ kiểm thử tự động (Unit Tests)](#5-bộ-kiểm-thử-tự-động-unit-tests)
5. [Hướng dẫn biên dịch độc lập](#6-hướng-dẫn-biên-dịch-độc-lập)

---

## 2. Đặc tả kỹ thuật

* **Package:** `com.bignumber.core`
* **Class chính:** `MyBigNumber`
* **Phương thức công khai:**
  ```java
  // Phục vụ tính toán và trả về chuỗi kết quả (Task 1)
  public String sum(String stn1, String stn2)

  // Phục vụ tích hợp giao diện hiển thị từng bước tính toán (Task 2)
  public CalculationResult sumWithProgress(String stn1, String stn2)
  ```

---

## 3. Nguyên lý giải thuật

Mô phỏng phép tính cộng của học sinh lớp 3:
1. Duyệt đồng thời hai chuỗi số từ **phải sang trái** (từ hàng đơn vị lên hàng cao hơn).
2. Lấy từng ký tự, kiểm tra tính hợp lệ và chuyển đổi thành số nguyên (`digit = char - '0'`). Nếu một trong hai chuỗi đã hết chữ số, giá trị tương ứng được gán mặc định bằng `0`.
3. Tính tổng hai chữ số cộng với biến nhớ từ bước trước:
   $$\text{total} = \text{digit1} + \text{digit2} + \text{carry}$$
4. Ký số kết quả tại bước hiện tại là $\text{total} \pmod{10}$, được chèn vào đầu chuỗi kết quả tạm thời.
5. Cập nhật số nhớ cho bước kế tiếp:
   $$\text{carry} = \lfloor\text{total} / 10\rfloor$$
6. Lặp lại toàn bộ quá trình cho đến khi xử lý xong độ dài lớn nhất của hai chuỗi.
7. Nếu sau vòng lặp vẫn còn số nhớ ($\text{carry} > 0$), hạ trực tiếp số nhớ vào đầu chuỗi kết quả.

---

## 4. Quy định Logging và Xử lý ngoại lệ

* **Logging:** Sử dụng thư viện chuẩn `java.util.logging.Logger` ghi nhận chi tiết nhật ký ở mức độ `INFO` cho từng bước tính toán (hai chữ số đang xét, tổng nhận được, số lưu vào kết quả tạm, giá trị nhớ chuyển giao).
* **Validation & Exception Handling:** Nếu dữ liệu đầu vào là `null`, chuỗi rỗng `""`, hoặc chứa bất kỳ ký tự nào nằm ngoài khoảng ký tự số `'0'` - `'9'` (chữ cái, dấu cách, ký tự đặc biệt, dấu âm, dấu thập phân):
  * Ghi nhật ký lỗi ở mức độ `SEVERE`.
  * Chủ động ném ngoại lệ `IllegalArgumentException` kèm thông báo chi tiết vị trí vi phạm.

---

## 5. Bộ kiểm thử tự động (Unit Tests)

Bộ kiểm thử nằm tại `src/test/java/com/bignumber/core/MyBigNumberTest.java`, ứng dụng **JUnit 5** với các ca kiểm thử:

1. **Ca kiểm thử mẫu:** Khẳng định kết quả phép toán `1234 + 897 = 2131`.
2. **Ca kiểm thử tham số hóa (`@ParameterizedTest` với `@CsvSource`):**
   * Phép cộng với `0` (`0 + 0 = 0`), phép cộng nhớ nhiều hàng liên tiếp (`999 + 1 = 1000`).
   * **Độ lệch độ dài vừa phải:** Chênh lệch từ 1 đến 8 chữ số (1 vs 2, 1 vs 4, 2 vs 5, 3 vs 6, 4 vs 8 chữ số và ngược lại).
   * **Độ lệch độ dài cực lớn:** Chênh lệch từ 20 đến 50 chữ số (1 vs 20, 1 vs 50, 5 vs 30, 10 vs 40 chữ số với nhớ dồn chuỗi sang hàng cao nhất).
   * **Cùng độ dài ở nhiều kích cỡ:** 1, 2, 5, 10, 15, 30 chữ số.
   * **Độ dài động siêu lớn:** Tự động sinh chuỗi 100 chữ số cộng chuỗi 5 chữ số.
   * **Kiểm thử chi tiết bước tính khi lệch độ dài:** Kiểm tra `sumWithProgress` cho chuỗi 5 chữ số và 1 chữ số trả về đúng 5 bước tính.
3. **Ca kiểm thử ngoại lệ:** Khẳng định ném `IllegalArgumentException` khi gặp dữ liệu không hợp lệ (`"12a4"`, `"abc"`, `"12.3"`, `"-123"`, `"12 3"`, `"@#$"`).

---

## 6. Hướng dẫn biên dịch độc lập

Đứng tại thư mục `my-big-number-core/` và thực hiện các lệnh:
* Chạy riêng Unit Test của module:
  ```bash
  mvn test
  ```
* Đóng gói thành file JAR:
  ```bash
  mvn clean package
  ```

## 7. Source and Test Mapping

| Artifact | Absolute path | Responsibility | Verification |
|---|---|---|---|
| `MyBigNumber` | `C:\my-big-number\my-big-number-core\src\main\java\com\bignumber\core\MyBigNumber.java` | Add numbers as strings | `MyBigNumberTest` |
| `CalculationResult` | `C:\my-big-number\my-big-number-core\src\main\java\com\bignumber\core\CalculationResult.java` | Return sum and steps | Core tests |
| `CalculationStep` | `C:\my-big-number\my-big-number-core\src\main\java\com\bignumber\core\CalculationStep.java` | Represent one calculation step | Core tests |
| Core tests | `C:\my-big-number\my-big-number-core\src\test\java\com\bignumber\core\MyBigNumberTest.java` | Verify valid and invalid input | `mvn test` |