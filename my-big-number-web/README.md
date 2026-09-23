# Sub-Module: MyBigNumber Web Application (Task 2)

Ứng dụng web tương tác cho phép người dùng thực hiện phép cộng hai số nguyên lớn không giới hạn chiều dài và theo dõi trực quan tiến trình giải thuật theo thời gian thực.

---

## 1. Mục lục
1. [Công nghệ sử dụng](#2-công-nghệ-sử-dụng)
2. [Kiến trúc và Thành phần](#3-kiến-trúc-và-thành-phần)
3. [Tính năng chính](#4-tính-năng-chính)
4. [Cách khởi chạy và truy cập](#5-cách-khởi-chạy-và-truy-cập)

---

## 2. Công nghệ sử dụng

* **Backend Framework:** Spring Boot 3.x (Spring MVC, Java 21 LTS)
* **Thư viện tích hợp:** `my-big-number-core` (tái sử dụng từ Task 1)
* **Template Engine:** Thymeleaf
* **Frontend:** Bootstrap 5.3 & Bootstrap Icons
* **Validation:** Jakarta Bean Validation (`@NotBlank`, `@Pattern`)

---

## 3. Kiến trúc và Thành phần

* `BigNumberWebApplication.java`: Lớp cấu hình chính và đăng ký Bean `MyBigNumber`.
* `BigNumberController.java`: Tiếp nhận HTTP GET `/` hiển thị giao diện và HTTP POST `/calculate` nhận dữ liệu tính toán.
* `CalculationRequest.java`: DTO đối tượng truyền nhận và kiểm tra ràng buộc dữ liệu đầu vào.
* `templates/index.html`: Giao diện người dùng Responsive với Bootstrap 5, tự động hiển thị bảng kết quả và tiến trình từng bước.

---

## 4. Tính năng chính

1. **Form nhập số nguyên lớn:**
   * Cho phép nhập dữ liệu chuỗi số lớn qua trường nhập liệu định dạng phông chữ Monospace, đảm bảo hiển thị rõ ràng chuỗi số dài hàng chục, hàng trăm chữ số.
   * Kiểm tra tính hợp lệ của dữ liệu ngay tại tầng Controller: ngăn chặn chuỗi rỗng và báo lỗi trực quan nếu chứa ký tự ngoài các chữ số `0-9`.
2. **Hiển thị kết quả:**
   * Khung kết quả tổng thể làm nổi bật chuỗi số kết quả phép cộng.
3. **Bảng tiến trình từng bước (Step-by-step Progress Table):**
   * Hiển thị bảng mô phỏng chi tiết từng thao tác: Số thứ tự bước, mô tả phép tính (hai chữ số cộng lại, số nhớ được cộng thêm), kết quả tạm thời thu được, và giá trị biến nhớ chuyển sang hàng tiếp theo.

## 4.1. REST API

| Endpoint | Method | Request | Success response | Error response |
|---|---|---|---|---|
| `/api/calculations` | `POST` | JSON: `num1`, `num2`, `includeSteps` | `sum`, `steps` | RFC 7807 Problem Details |

Contract chính thức nằm tại `C:\my-big-number\docs\api-spec.md`.

### REST API target files

- `C:\my-big-number\my-big-number-web\src\main\java\com\bignumber\web\controller\BigNumberRestController.java`
- `C:\my-big-number\my-big-number-web\src\main\java\com\bignumber\web\dto\CalculationApiRequest.java`
- `C:\my-big-number\my-big-number-web\src\main\java\com\bignumber\web\dto\CalculationApiResponse.java`
- `C:\my-big-number\my-big-number-web\src\main\java\com\bignumber\web\exception\ApiExceptionHandler.java`
- `C:\my-big-number\my-big-number-web\src\test\java\com\bignumber\web\controller\BigNumberRestControllerTest.java`

---

## 5. Cách khởi chạy và truy cập

### Khởi chạy từ dòng lệnh:
Đứng tại thư mục gốc của toàn bộ dự án (`big-number-workspace`) và chạy:
```bash
mvn --projects my-big-number-web spring-boot:run
```

### Chạy từ file JAR đóng gói:
```bash
mvn clean package -DskipTests
java -jar my-big-number-web/target/my-big-number-web-0.0.1.jar
```

### Truy cập giao diện:
* URL: `http://localhost:8084`
* Cổng dịch vụ mặc định: `8084` (có thể điều chỉnh trong file `src/main/resources/application.properties`).