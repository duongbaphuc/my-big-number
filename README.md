# MyBigNumber Workspace - Dự Án Cộng Hai Số Nguyên Lớn

Hệ thống quản lý và triển khai thuật toán cộng hai số nguyên lớn không giới hạn độ dài, mô phỏng phương pháp tính toán cột dọc của học sinh tiểu học. Dự án được thiết kế theo kiến trúc **Maven Multi-Module (Monorepo)**, tích hợp sẵn module thư viện lõi và module ứng dụng web giao diện người dùng.

---

## 1. Mục lục
1. [Giới thiệu tổng quan](#2-giới-thiệu-tổng-quan)
2. [Cấu trúc Multi-Module](#3-cấu-trúc-multi-module)
3. [Yêu cầu môi trường](#4-yêu-cầu-môi-trường)
4. [Hướng dẫn cài đặt và thực thi](#5-hướng-dẫn-cài-đặt-và-thực-thi)
5. [Quy ước Clone và thẩm định độc lập](#6-quy-ước-clone-và-thẩm-định-độc-lập)
6. [Thông tin phát hành (Release & Tag)](#7-thông-tin-phát-hành-release--tag)

---

## 2. Giới thiệu tổng quan

Dự án được phân rã thành hai nhiệm vụ chính:
* **Task 1 (`my-big-number-core`):** Thư viện tính toán lõi cung cấp thuật toán cộng hai số dạng chuỗi, áp dụng cơ chế ghi nhận nhật ký (`java.util.logging.Logger`), kiểm soát dữ liệu đầu vào nghiêm ngặt và ném ngoại lệ `IllegalArgumentException` khi phát hiện ký tự không hợp lệ. Module đi kèm bộ kiểm thử tự động toàn diện với **JUnit 5**.
* **Task 2 (`my-big-number-web`):** Ứng dụng web trực quan xây dựng trên nền tảng **Spring Boot 3**, **Thymeleaf** và **Bootstrap 5**, tái sử dụng thư viện lõi từ Task 1 và trực quan hóa từng bước thực hiện phép toán theo bảng tiến trình thời gian thực.

---

## 3. Cấu trúc Multi-Module

```text
big-number-workspace/
├── pom.xml                                  # Root POM: quản lý phiên bản, plugin và dependency chung
├── README.md                                # Tài liệu tổng quan toàn dự án (file này)
├── docs/                                    # Đặc tả và thiết kế cấp workspace cho REST API
│   ├── README.md                            # Mục lục tài liệu và thứ tự sử dụng
│   ├── api-requirements.md                  # Yêu cầu nghiệp vụ và tiêu chí chấp nhận
│   ├── api-spec.md                          # Contract REST API và schema JSON
│   ├── api-test-matrix.md                   # Ma trận kiểm thử API
│   └── api-design.md                        # Thiết kế triển khai và giới hạn thay đổi
├── .gitignore                               # Cấu hình loại trừ file rác, target, .idea
│
├── my-big-number-core/                      # Sub-module Task 1: Thư viện tính toán lõi
│   ├── pom.xml
│   ├── README.md                            # Tài liệu kỹ thuật chi tiết của Core Module
│   └── src/
│       ├── main/java/com/bignumber/core/
│       │   ├── CalculationStep.java         # Model chi tiết từng bước tính toán
│       │   ├── CalculationResult.java       # Record đóng gói kết quả tổng thể
│       │   └── MyBigNumber.java             # Thuật toán cộng, logging và ném ngoại lệ
│       └── test/java/com/bignumber/core/
│           └── MyBigNumberTest.java         # Kiểm thử tự động (JUnit 5 Parameterized Tests)
│
└── my-big-number-web/                       # Sub-module Task 2: Ứng dụng Web giao diện
    ├── pom.xml
    ├── README.md                            # Hướng dẫn chạy và tương tác với Web App
    └── src/
        ├── main/
        │   ├── java/com/bignumber/web/
        │   │   ├── BigNumberWebApplication.java  # Lớp khởi chạy Spring Boot & cấu hình Bean
        │   │   ├── controller/
        │   │   │   └── BigNumberController.java  # Controller tiếp nhận và render dữ liệu
        │   │   └── dto/
        │   │       └── CalculationRequest.java   # DTO validation dữ liệu người dùng nhập
        │   └── resources/
        │       ├── application.properties        # Cấu hình Spring Boot và web server
        │       └── templates/
        │           └── index.html                # Giao diện Thymeleaf + Bootstrap 5
        └── test/
```

---

## 4. Yêu cầu môi trường

* **Java Development Kit (JDK):** Phiên bản 21 trở lên (LTS).
* **Apache Maven:** Phiên bản 3.8.x trở lên.
* **Trình duyệt web:** Google Chrome, Mozilla Firefox, Microsoft Edge hoặc Safari.

---

## 5. Hướng dẫn cài đặt và thực thi

### Bước 1: Build và cài đặt gói lõi vào kho nội bộ
Trước khi khởi động ứng dụng Web, Maven cần biên dịch và đưa artifact `my-big-number-core` vào Local Repository (`.m2`). Tại thư mục gốc của dự án, chạy lệnh:

```bash
mvn clean install
```

> **Ghi chú khắc phục lỗi kết nối:** Nếu gặp lỗi `Could not resolve dependencies` hoặc `Cannot access central in offline mode`:
> 1. Mở IDE (IntelliJ IDEA) $\rightarrow$ **File** $\rightarrow$ **Settings** $\rightarrow$ **Build, Execution, Deployment** $\rightarrow$ **Build Tools** $\rightarrow$ **Maven**.
> 2. Bỏ tích chọn ô **Work offline**.
> 3. Chạy lại `mvn clean install` trên Terminal.

### Bước 2: Chạy bộ kiểm thử tự động (Unit Tests)
Để chạy toàn bộ các test case hợp lệ và test case ném ngoại lệ của thư viện lõi:

```bash
mvn test
```

### Bước 3: Khởi chạy ứng dụng Web
Có thể khởi động ứng dụng Web bằng một trong hai cách:

* **Cách 1: Dùng Maven plugin (khuyên dùng cho môi trường phát triển):**
  ```bash
  mvn --projects my-big-number-web spring-boot:run
  ```

* **Cách 2: Chạy trực tiếp từ file JAR đã đóng gói:**
  ```bash
  java -jar my-big-number-web/target/my-big-number-web-0.0.1.jar
  ```

Mở trình duyệt và truy cập vào địa chỉ: `http://localhost:8084`.

---

## 6. Quy ước Clone và thẩm định độc lập

Nhằm mô phỏng quá trình đánh giá và nghiệm thu dự án từ người khác, toàn bộ mã nguồn phải được clone về máy cục bộ theo cấu trúc thư mục quy ước:

### Hệ điều hành Windows:
```cmd
mkdir D:\Projects\github.com\<youraccount>
cd /d D:\Projects\github.com\<youraccount>
git clone https://github.com/<youraccount>/<projectname>.git
cd <projectname>
mvn clean install
mvn --projects my-big-number-web spring-boot:run
```

### Hệ điều hành macOS / Linux:
```bash
mkdir -p ~/Projects/github.com/<youraccount>
cd ~/Projects/github.com/<youraccount>
git clone https://github.com/<youraccount>/<projectname>.git
cd <projectname>
mvn clean install
mvn --projects my-big-number-web spring-boot:run
```

---

## 7. Thông tin phát hành (Release & Tag)

Phiên bản bàn giao chuẩn được gắn tag chính thức:
* **Release Version:** `0.0.1`
* **Lệnh tạo tag:**
  ```bash
  git tag -a 0.0.1 -m "Release version 0.0.1: Complete Task 1 and Task 2"
  git push origin 0.0.1
  ```