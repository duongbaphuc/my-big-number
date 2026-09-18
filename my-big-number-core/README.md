# Sub-Module: MyBigNumber Core (Task 1)

Module thư viện lõi cài đặt giải thuật cộng hai số nguyên lớn dưới dạng chuỗi ký tự theo phương pháp mô phỏng cách tính đặt cột dọc của học sinh tiểu học. Module được đóng gói thành thư viện chuẩn (`.jar`), sẵn sàng bàn giao cho các nhóm khác tái sử dụng.

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
   * Phép cộng với `0` (`0 + 0 = 0`).
   * Phép cộng nhớ nhiều hàng liên tiếp (`999 + 1 = 1000`).
   * Phép cộng số nguyên siêu lớn vượt ngưỡng 64-bit ($> 20$ chữ số).
   * Phép cộng hai số có số lượng chữ số chênh lệch lớn.
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