# Dự Án Cộng Hai Số Lớn (MyBigNumber Workspace) - Phiên bản 0.0.1

Dự án cài đặt thuật toán cộng hai số lớn dưới dạng chuỗi mô phỏng phương pháp "đặt tính rồi tính" của học sinh tiểu học, được tổ chức dưới dạng Maven Multi-Module gồm phần thuật toán lõi (Core) và ứng dụng Web tương tác (Spring Boot + Thymeleaf + Bootstrap).

---

## 1. Giới thiệu tổng quan (Introduction)

Dự án được chia làm 2 giai đoạn (Tasks):
1. **Task 1 (Core Engine):** Cài đặt lớp lõi `MyBigNumber` với phương thức `sum(String stn1, String stn2)`. Thuật toán duyệt từ phải sang trái, cộng từng cặp ký số kèm biến nhớ và ghi nhận nhật ký (Logging) chi tiết từng bước. Nếu dữ liệu đầu vào chứa chữ cái hoặc ký tự không hợp lệ, hệ thống sẽ chủ động ném ngoại lệ `IllegalArgumentException`. Kèm theo bộ Unit Test sử dụng JUnit 5 kiểm thử cả các trường hợp hợp lệ và dữ liệu chứa ký tự lạ.
2. **Task 2 (Web Application):** Phát triển ứng dụng Web giao diện người dùng bằng Spring Boot, Thymeleaf và Bootstrap 5. Module Web tái sử dụng module Core như một sub-module thư viện và hiển thị trực quan bảng tiến trình từng bước tính toán.

---

## 2. Yêu cầu hệ thống (Prerequisites)

- **Java Development Kit (JDK):** Phiên bản 21 trở lên (LTS).
- **Apache Maven:** Phiên bản 3.8.x trở lên.
- **Trình duyệt web:** Chrome, Firefox, Edge, Safari,...

---

## 3. Cấu trúc thư mục dự án (Project Architecture)

Dự án áp dụng mô hình **Maven Multi-Module (Monorepo)**:

```text
big-number-workspace/
├── pom.xml                                   # Root POM quản lý phiên bản và build
├── README.md                                 # Tài liệu dự án
│
├── my-big-number-core/                       # Sub-module Task 1: Thuật toán lõi
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/bignumber/core/
│       │   ├── CalculationResult.java        # Record lưu kết quả và danh sách các bước
│       │   ├── CalculationStep.java          # Model mô tả chi tiết từng bước cộng
│       │   └── MyBigNumber.java              # Lớp lõi xử lý phép toán và logging
│       └── test/java/com/bignumber/core/
│           └── MyBigNumberTest.java          # Unit Test (JUnit 5 + Parameterized Test)
│
└── my-big-number-web/                        # Sub-module Task 2: Ứng dụng Web
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/com/bignumber/web/
        │   │   ├── BigNumberWebApplication.java   # Spring Boot Application Launcher
        │   │   ├── controller/
        │   │   │   └── BigNumberController.java   # Xử lý Request & trả View
        │   │   └── dto/
        │   │       └── CalculationRequest.java    # DTO nhận form & Bean Validation
        │   └── resources/
        │       ├── application.properties         # Cấu hình Spring Boot
        │       └── templates/
        │           └── index.html                 # Giao diện Thymeleaf + Bootstrap 5
        └── test/
```

---

## 4. Hướng dẫn biên dịch và khởi chạy (Getting Started)

### Bước 1: Build và cài đặt module Core vào kho cục bộ (Bắt buộc)
Trước khi chạy ứng dụng Web, cần thực hiện lệnh `install` tại thư mục gốc để Maven biên dịch và đưa gói `my-big-number-core` vào Local Repository (`.m2`):

```bash
# Đứng tại thư mục gốc của dự án
mvn clean install
```

> **Lưu ý khắc phục lỗi dependency/offline:**
> Nếu gặp lỗi `The POM for com.bignumber:my-big-number-core:jar:0.0.1 is missing` hoặc lỗi chế độ offline trong IntelliJ IDEA:
> 1. Vào **File** -> **Settings** -> **Build, Execution, Deployment** -> **Build Tools** -> **Maven**.
> 2. Bỏ chọn ô **Work offline** (nếu đang bật).
> 3. Chạy lại lệnh `mvn clean install` trên Terminal.

### Bước 2: Thực thi Unit Test (Task 1)
Để kiểm tra các ca kiểm thử thuật toán và xác nhận việc ném ngoại lệ khi gặp ký tự sai:

```bash
mvn test
```
*Hoặc chỉ chạy test cho riêng module Core:*
```bash
mvn --projects my-big-number-core test
```

### Bước 3: Khởi chạy ứng dụng Web (Task 2)
Có 2 cách khởi chạy:

- **Cách 1: Dùng Maven plugin từ Terminal (khuyên dùng):**
  ```bash
  mvn --projects my-big-number-web spring-boot:run
  ```

- **Cách 2: Chạy trực tiếp từ file JAR đã đóng gói:**
  ```bash
  mvn clean package -DskipTests
  java -jar my-big-number-web/target/my-big-number-web-0.0.1.jar
  ```

Sau khi ứng dụng khởi động xong (cổng mặc định `8084`), hãy mở trình duyệt và truy cập:
```text
http://localhost:8084
```

---

## 5. Quy ước Clone mã nguồn từ Git (Submission Guidelines)

Sau khi đưa mã nguồn lên Git Server (GitHub/GitLab), người khác hoặc giảng viên có thể clone và kiểm thử dự án về máy theo đúng đường dẫn quy ước:

### Đối với người dùng Windows:
```cmd
mkdir D:\Projects\github.com\<youraccount>
cd /d D:\Projects\github.com\<youraccount>
git clone https://github.com/<youraccount>/<projectname>.git
cd <projectname>
mvn clean install
mvn --projects my-big-number-web spring-boot:run
```

### Đối với người dùng macOS / Linux:
```bash
mkdir -p ~/Projects/github.com/<youraccount>
cd ~/Projects/github.com/<youraccount>
git clone https://github.com/<youraccount>/<projectname>.git
cd <projectname>
mvn clean install
mvn --projects my-big-number-web spring-boot:run
```

---

## 6. Gắn Tag phát hành phiên bản (Git Release Tag)

Dự án được đánh dấu phiên bản đánh giá là `0.0.1`:

```bash
git tag -a 0.0.1 -m "Release version 0.0.1: Complete Task 1 and Task 2"
git push origin --tags
```