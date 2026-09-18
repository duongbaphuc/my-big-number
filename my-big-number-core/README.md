# MyBigNumber - Thuật toán cộng 2 số lớn (v0.0.1)

## 1. Giới thiệu (Introduction)
Dự án cung cấp thư viện lõi tính toán phép cộng 2 số nguyên lớn được biểu diễn dưới dạng chuỗi (`String`). Thuật toán mô phỏng trực quan theo phương pháp **đặt tính rồi tính** của học sinh tiểu học (cộng từng hàng từ phải sang trái, có nhớ và ghi lại toàn bộ nhật ký tính toán qua logger).

Mã nguồn được đóng gói độc lập để dễ dàng tích hợp vào các dự án giao diện Web (Spring Boot, Thymeleaf) hoặc ứng dụng dòng lệnh (CLI).

---

## 2. Yêu cầu môi trường (Prerequisites)
* **Java Development Kit (JDK):** Phiên bản 21 (hoặc tương thích từ JDK 17+).
* **Apache Maven:** Phiên bản 3.8.0 trở lên.
* **Hệ điều hành:** Windows / macOS / Linux.

---

## 3. Cấu trúc dự án (Project Structure)
```text
Task1/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   └── java/
    │       └── com/bignumber/core/
    │           └── MyBigNumber.java       # Lớp lõi xử lý thuật toán và ghi log
    └── test/
        └── java/
            └── com/bignumber/core/
                └── MyBigNumberTest.java   # Bộ kiểm thử Unit Test (JUnit 5)
```

---

## 4. Đặc tả kỹ thuật (Technical Specifications)

### 4.1. Chữ ký phương thức
```java
public String sum(String stn1, String stn2)
```

### 4.2. Nguyên lý hoạt động
1. **Duyệt chuỗi:** Duyệt song song 2 chuỗi `stn1` và `stn2` từ phải sang trái (hàng đơn vị $\rightarrow$ chục $\rightarrow$ trăm...).
2. **Kiểm tra dữ liệu & Chuyển đổi:** Lấy từng ký tự (`char`), kiểm tra ký tự hợp lệ (`'0'` - `'9'`), chuyển thành số nguyên (`int`).
3. **Thực hiện phép cộng:**
   - $\text{sumDigits} = \text{digit}_1 + \text{digit}_2$
   - $\text{total} = \text{sumDigits} + \text{carry}$ (cộng thêm số nhớ từ bước trước nếu có).
   - Chữ số ghi vào kết quả tạm: $\text{digit} = \text{total} \pmod{10}$.
   - Số nhớ cho bước tiếp theo: $\text{carry} = \lfloor\text{total} / 10\rfloor$.
4. **Ghi nhận lịch sử (Logging):** Sử dụng `java.util.logging.Logger` để ghi log chi tiết từng bước:
   - Các chữ số lấy ra và kết quả cộng.
   - Số nhớ hiện tại.
   - Kết quả chuỗi tích lũy sau mỗi bước.
5. **Bước kết thúc:** Nếu sau khi duyệt hết hai chuỗi mà số nhớ $\text{carry} > 0$, hạ số nhớ vào đầu kết quả.

### 4.3. Xử lý ngoại lệ (Exception Handling)
Nếu dữ liệu đầu vào chứa ký tự không hợp lệ (chữ cái, ký tự đặc biệt, dấu âm, dấu cách...) hoặc là `null` / rỗng:
- Hệ thống ghi nhận log cảnh báo mức độ nghiêm trọng (`SEVERE`).
- Lập tức ném ngoại lệ `IllegalArgumentException` kèm thông báo vị trí ký tự sai cụ thể.

---

## 5. Hướng dẫn biên dịch và kiểm thử (Build & Run Test)

Mở cửa sổ dòng lệnh (Terminal / Command Prompt / PowerShell) tại thư mục chứa file `pom.xml`:

### 5.1. Biên dịch mã nguồn
```bash
mvn clean compile
```

### 5.2. Chạy kiểm thử tự động (Unit Test)
Chạy toàn bộ các ca kiểm thử (gồm các ca tính toán chuẩn và các ca kiểm tra ném ngoại lệ):
```bash
mvn test
```

### 5.3. Đóng gói thư viện `.jar`
```bash
mvn clean package
```
*File `.jar` sẽ được sinh ra tại thư mục `target/my-big-number-0.0.1.jar`.*

---

## 6. Quy ước Clone mã nguồn từ Git Server

Để đảm bảo tính nhất quán theo quy chuẩn dự án, người nhận bàn giao clone mã nguồn về máy theo định dạng đường dẫn quy ước:

### Trên Windows
```cmd
mkdir D:\Projects\github.com\<youraccount>
cd /d D:\Projects\github.com\<youraccount>
git clone https://github.com/<youraccount>/<projectname>.git
cd <projectname>
mvn test
```

### Trên macOS / Linux
```bash
mkdir -p ~/Projects/github.com/<youraccount>
cd ~/Projects/github.com/<youraccount>
git clone https://github.com/<youraccount>/<projectname>.git
cd <projectname>
mvn test
```

---

## 7. Thông tin phiên bản (Release Info)
* **Phiên bản:** `0.0.1`
* **Git Tag:** `0.0.1`