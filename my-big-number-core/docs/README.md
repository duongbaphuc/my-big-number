# Tài Liệu Kỹ Thuật Module Core (`my-big-number-core`)

Thư viện Java lõi cài đặt thuật toán cộng hai số nguyên lớn dưới dạng chuỗi ký tự theo phương pháp **đặt tính cộng cột dọc của học sinh tiểu học**.

---

## 1. Mục lục tài liệu

| Tài liệu | Mô tả |
| :--- | :--- |
| 📖 [**API Reference (Đặc tả API)**](API_REFERENCE.md) | Hướng dẫn chi tiết cách gọi hàm, kiểu dữ liệu trả về, quy định ngoại lệ và code mẫu tích hợp. |
| ⚡ [**Thuật toán & Hiệu năng**](ALGORITHM_AND_PERFORMANCE.md) | Phân tích chi tiết giải thuật cộng cột dọc, sơ đồ luồng, phân tích độ phức tạp $O(N)$ và các kỹ thuật tối ưu hóa bộ nhớ. |
| 📐 [**Coding Rules (Quy chuẩn Lập trình)**](coding-rules.md) | Các quy định về Java 21, quy ước đặt tên, Constructor Injection và logging không lộ dữ liệu. |
| 🌐 [**API Rules (Thiết kế REST & Error Handling)**](api-rules.md) | Quy định định danh REST resource, cấm bịa trường JSON ngoài đặc tả, chuẩn lỗi Problem Details (RFC 7807). |
| 🛡️ [**Security Rules (Quy tắc Bảo mật)**](security-rules.md) | Chống hardcode secret, xác thực đầu vào, phòng chống SQL Injection, RBAC và bảo vệ thông tin hệ thống. |
| 📊 [**Scorecard Check (Chấm điểm tuân thủ)**](scorecard.md) | Bảng chấm điểm 12 tiêu chí đánh giá mã nguồn nháp do AI sinh ra dựa trên bộ Rules Pack (Đạt 100% Pass). |

---

## 2. Bắt đầu nhanh (Quick Start trong 30 giây)

### Khởi tạo đối tượng
```java
import com.bignumber.core.MyBigNumber;
import com.bignumber.core.CalculationResult;
import com.bignumber.core.CalculationStep;

MyBigNumber myBigNumber = new MyBigNumber();
```

### Trường hợp 1: Chỉ lấy kết quả phép tính (Task 1 - Tối ưu siêu tốc)
Phù hợp khi backend chỉ cần kết quả tính toán nhanh, không cần hiển thị giao diện diễn giải:
```java
String result = myBigNumber.sum("12345678901234567890", "98765432109876543210");
System.out.println("Tổng: " + result); 
// Output: 111111111011111111100
```

### Trường hợp 2: Lấy kết quả kèm chi tiết từng bước tính (Task 2 - Hiển thị UI)
Phù hợp để tích hợp lên Web App / Mobile App hiển thị hoạt ảnh hoặc bảng từng bước giải thích cho người dùng:
```java
CalculationResult result = myBigNumber.sumWithProgress("1234", "897");

System.out.println("Kết quả cuối: " + result.sum()); // 2131
for (CalculationStep step : result.steps()) {
    System.out.printf("Bước %d: %s (KQ tạm: %s, nhớ: %d)%n",
        step.getStepNumber(),
        step.getDescription(),
        step.getIntermediateResult(),
        step.getCarry()
    );
}
```

---

## 3. Tích hợp qua Maven

Để sử dụng module trong các module khác (ví dụ: `my-big-number-web`), thêm dependency vào file `pom.xml`:

```xml
<dependency>
    <groupId>com.bignumber</groupId>
    <artifactId>my-big-number-core</artifactId>
    <version>0.0.1</version>
</dependency>
```

---

## 4. Đặc tả môi trường yêu cầu
* **Java Version:** Java 17 trở lên (Hỗ trợ Java `record`).
* **Framework kiểm thử:** JUnit 5 (`junit-jupiter`).
* **Hệ thống Logging:** Sử dụng thư viện chuẩn `java.util.logging.Logger` (Không yêu cầu thêm thư viện ngoài như log4j hay slf4j).
