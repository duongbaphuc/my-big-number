# Đặc Tả API Chi Tiết (`API Reference`)

Tài liệu này cung cấp hướng dẫn đầy đủ về các lớp, phương thức, cấu trúc dữ liệu và xử lý ngoại lệ trong package `com.bignumber.core`.

---

## 1. Danh sách các lớp (Classes & Records)

| Tên lớp / Record | Kiểu | Mô tả nhiệm vụ |
| :--- | :--- | :--- |
| [`MyBigNumber`](#2-class-mybignumber) | `class` | Lớp chính chứa thuật toán cộng số lớn và logic kiểm tra tính hợp lệ của dữ liệu. |
| [`CalculationResult`](#3-record-calculationresult) | `record` | Đóng gói kết quả chuỗi cuối cùng và danh sách các bước tính toán chi tiết. |
| [`CalculationStep`](#4-class-calculationstep) | `class` | Đại diện cho một bước tính cụ thể (bước cộng hàng đơn vị, chục, trăm,...). |

---

## 2. Class `MyBigNumber`

Package: `com.bignumber.core.MyBigNumber`

### 2.1. Phương thức `sum(String stn1, String stn2)`

Tính toán và trả về kết quả phép cộng hai số nguyên lớn dưới dạng chuỗi.

* **Chữ ký phương thức:**
  ```java
  public String sum(String stn1, String stn2)
  ```
* **Mục đích:** Phục vụ **Task 1**. Sử dụng luồng tính toán siêu tốc (Fast Path) với độ phức tạp $O(N)$ thời gian và $O(1)$ bộ nhớ phụ, không tạo đối tượng bước tính trung gian.
* **Tham số đầu vào:**
  * `stn1` (`String`): Chuỗi biểu diễn số nguyên dương thứ nhất (chỉ gồm các ký tự số từ `'0'` đến `'9'`).
  * `stn2` (`String`): Chuỗi biểu diễn số nguyên dương thứ hai (chỉ gồm các ký tự số từ `'0'` đến `'9'`).
* **Giá trị trả về:**
  * `String`: Chuỗi số biểu diễn kết quả phép cộng `stn1 + stn2`.
* **Ngoại lệ ném ra (`Throws`):**
  * `IllegalArgumentException`: Nếu một trong hai tham số là `null`, chuỗi rỗng `""` hoặc chứa ký tự không hợp lệ.
* **Ví dụ:**
  ```java
  MyBigNumber mbn = new MyBigNumber();
  String sum = mbn.sum("1234", "897");
  System.out.println(sum); // In ra: 2131
  ```

---

### 2.2. Phương thức `sumWithProgress(String stn1, String stn2)`

Tính toán phép cộng hai số lớn, đồng thời ghi nhận chi tiết từng bước đặt tính cộng theo cột dọc của học sinh tiểu học.

* **Chữ ký phương thức:**
  ```java
  public CalculationResult sumWithProgress(String stn1, String stn2)
  ```
* **Mục đích:** Phục vụ **Task 2** (Tích hợp giao diện người dùng, bảng diễn giải, animation từng bước tính toán).
* **Tham số đầu vào:**
  * `stn1` (`String`): Chuỗi số thứ nhất.
  * `stn2` (`String`): Chuỗi số thứ hai.
* **Giá trị trả về:**
  * `CalculationResult`: Đối tượng chứa:
    * `sum`: Chuỗi kết quả cuối cùng.
    * `steps`: Danh sách các đối tượng `CalculationStep` theo thứ tự từ hàng đơn vị đến hàng cao nhất.
* **Ngoại lệ ném ra (`Throws`):**
  * `IllegalArgumentException`: Tương tự như hàm `sum`.
* **Ví dụ:**
  ```java
  MyBigNumber mbn = new MyBigNumber();
  CalculationResult result = mbn.sumWithProgress("1234", "897");
  System.out.println("Kết quả: " + result.sum()); // 2131
  System.out.println("Tổng số bước: " + result.steps().size()); // 4 bước
  ```

---

## 3. Record `CalculationResult`

Package: `com.bignumber.core.CalculationResult`

* **Khai báo:**
  ```java
  public record CalculationResult(String sum, List<CalculationStep> steps) {}
  ```
* **Các thuộc tính:**
  * `sum()` (`String`): Chuỗi kết quả cuối cùng của phép toán.
  * `steps()` (`List<CalculationStep>`): Danh sách bất biến/có thứ tự chứa chi tiết từng bước tính.

---

## 4. Class `CalculationStep`

Package: `com.bignumber.core.CalculationStep`

Đại diện cho trạng thái và diễn giải của một bước cộng cột dọc.

### Các trường dữ liệu và Phương thức Getter:

| Thuộc tính | Kiểu dữ liệu | Getter | Ý nghĩa | Ví dụ giá trị |
| :--- | :--- | :--- | :--- | :--- |
| `stepNumber` | `int` | `getStepNumber()` | Thứ tự bước tính toán (bắt đầu từ 1). | `1`, `2`, `3`... |
| `description` | `String` | `getDescription()` | Lời giải thích tự nhiên bằng tiếng Việt theo cách học sinh tiểu học. | `"Lấy 4 cộng với 7 được 11. Lưu 1 vào kết quả tạm \"1\". Ghi nhớ 1."` |
| `intermediateResult` | `String` | `getIntermediateResult()` | Chuỗi kết quả tích lũy tính đến bước hiện tại. | `"1"`, `"31"`, `"131"`, `"2131"` |
| `carry` | `int` | `getCarry()` | Số nhớ chuyển giao sang bước tiếp theo. | `0` hoặc `1` |

---

## 5. Quy chuẩn kiểm tra dữ liệu và Xử lý ngoại lệ (Validation & Exceptions)

Cả hai phương thức `sum` và `sumWithProgress` đều áp dụng chung một cơ chế kiểm tra dữ liệu nghiêm ngặt. Nếu dữ liệu không hợp lệ, hệ thống sẽ:
1. Ghi nhật ký lỗi mức độ `SEVERE` qua `LOGGER`.
2. Ném ngoại lệ `IllegalArgumentException` kèm thông báo chi tiết.

### Bảng kịch bản lỗi và thông báo tương ứng:

| Trường hợp vi phạm | Ví dụ đầu vào | Thông báo ngoại lệ (`getMessage()`) |
| :--- | :--- | :--- |
| Tham số là `null` | `sum(null, "123")` | `"Tham số truyền vào không được là null."` |
| Chuỗi số rỗng | `sum("", "123")` | `"Chuỗi số không được để trống."` |
| Chứa chữ cái trong chuỗi 1 | `sum("12a4", "897")` | `"Chuỗi 1 chứa ký tự không hợp lệ 'a' tại vị trí 2."` |
| Chứa chữ cái trong chuỗi 2 | `sum("1234", "8b7")` | `"Chuỗi 2 chứa ký tự không hợp lệ 'b' tại vị trí 1."` |
| Chứa dấu chấm thập phân | `sum("12.3", "456")` | `"Chuỗi 1 chứa ký tự không hợp lệ '.' tại vị trí 2."` |
| Chứa số âm (dấu trừ) | `sum("-123", "456")` | `"Chuỗi 1 chứa ký tự không hợp lệ '-' tại vị trí 0."` |
| Chứa khoảng trắng | `sum("12 3", "456")` | `"Chuỗi 1 chứa ký tự không hợp lệ ' ' tại vị trí 2."` |
| Chứa ký tự đặc biệt | `sum("@#$", "123")` | `"Chuỗi 1 chứa ký tự không hợp lệ '$' tại vị trí 2."` |

---

## 6. Code mẫu tích hợp (Integration Examples)

### Ví dụ 1: Tích hợp trong Spring Boot REST Controller (Task 2 API)
```java
@RestController
@RequestMapping("/api/big-number")
public class BigNumberController {

    private final MyBigNumber myBigNumber = new MyBigNumber();

    @GetMapping("/calculate")
    public ResponseEntity<?> calculate(
            @RequestParam String num1,
            @RequestParam String num2) {
        try {
            CalculationResult result = myBigNumber.sumWithProgress(num1, num2);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
```

### Ví dụ 2: Lựa chọn hàm tối ưu theo ngữ cảnh
```java
public void processTransaction(String amount1, String amount2, boolean isDebug) {
    MyBigNumber mbn = new MyBigNumber();
    if (isDebug) {
        // Cần log từng bước chi tiết
        CalculationResult debugInfo = mbn.sumWithProgress(amount1, amount2);
        debugInfo.steps().forEach(s -> System.out.println(s.getDescription()));
    } else {
        // Chế độ production tính toán cao tốc
        String totalAmount = mbn.sum(amount1, amount2);
        saveToDatabase(totalAmount);
    }
}
```
