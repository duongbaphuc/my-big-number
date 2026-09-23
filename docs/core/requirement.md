# `requirement.md` — Business & System Context (`my-big-number-core`)

## 1. Giới thiệu & Bối cảnh Nghiệp vụ (Business Context)

### 1.1. Mục đích Module
`my-big-number-core` là thư viện Java thuần (Plain Java 21 Library), đóng vai trò là **Domain Layer** độc lập trong kiến trúc Monorepo. Thư viện chịu trách nhiệm thực thi thuật toán cộng hai số nguyên không âm có độ dài bất kỳ (hàng nghìn đến hàng trăm nghìn chữ số) mà không bị giới hạn bởi độ chính xác của các kiểu dữ liệu nguyên thủy trong Java (`int`, `long`, `double`).

### 1.2. Giá trị nghiệp vụ & Trường hợp sử dụng (Use Cases)
1. **High-throughput Calculation (Fast Path):** Phục vụ các hệ thống backend, xử lý giao dịch hoặc tính toán số học lớn chỉ yêu cầu chuỗi tổng kết quả với hiệu năng tối đa và chi phí cấp phát bộ nhớ tối thiểu.
2. **Pedagogical / Visualization Engine (Full Path):** Mô phỏng chi tiết phương pháp đặt tính cộng theo cột dọc của học sinh tiểu học, cung cấp diễn giải từng bước bằng ngôn ngữ tự nhiên (tiếng Việt) để phục vụ hiển thị lên giao diện Web hoặc ứng dụng giáo dục.

---

## 2. Phạm vi của Module (Module Scope)

### 2.1. Trong phạm vi (In-Scope)
* Thuật toán cộng 2 chuỗi số nguyên dương/không âm với độ phức tạp thời gian tuyến tính $\mathcal{O}(N)$.
* Hỗ trợ hai phương thức tính toán riêng biệt:
  - `sum(String stn1, String stn2)`: Luồng tính toán nhanh, không sinh đối tượng trung gian.
  - `sumWithProgress(String stn1, String stn2)`: Luồng ghi nhận chi tiết trạng thái từng cột, số nhớ và câu giải thích.
* Kiểm tra tính hợp lệ dữ liệu đầu vào (Input Validation) nghiêm ngặt tại mức độ ký tự.
* Cơ chế ghi nhật ký hệ thống độc lập sử dụng `java.util.logging.Logger`.
* Độ bao phủ kiểm thử tự động đạt 100% dòng lệnh (Line Coverage) với JUnit 5.

### 2.2. Ngoài phạm vi (Out-of-Scope)
* Không xử lý số âm, số thập phân (dấu chấm/phẩy), số mũ hoặc biểu thức toán học phức tạp.
* Không chứa bất kỳ logic nào liên quan đến Web, HTTP protocol, REST controller, JSON serialization hay HTML rendering.
* Không phụ thuộc vào Spring Framework hay các bên thứ ba (Third-party libraries) trong production code.

---

## 3. Yêu cầu Chức năng (Functional Requirements - FR)

| Mã FR | Tên yêu cầu | Mô tả chi tiết hành vi | Mã nguồn đối chiếu |
| :--- | :--- | :--- | :--- |
| **FR-01** | **Tính toán Fast Path** | Nhận 2 chuỗi số và trả về chuỗi kết quả phép cộng. Chỉ cấp phát duy nhất một mảng ký tự `char[]`, không tạo chuỗi phụ hay ghi log từng bước để đạt tốc độ tối đa. | `MyBigNumber.java:sum` |
| **FR-02** | **Tính toán kèm Tiến trình** | Nhận 2 chuỗi số và trả về đối tượng `CalculationResult` gồm kết quả cuối cùng và danh sách có thứ tự các bước tính toán (`List<CalculationStep>`). | `MyBigNumber.java:sumWithProgress` |
| **FR-03** | **Diễn giải bước tính chuẩn tiểu học** | Tại mỗi hàng cộng (từ đơn vị đến hàng cao nhất), tự động tạo câu mô tả chi tiết: *"Lấy X cộng với Y được Z. [Cộng tiếp với nhớ K được T.] Lưu D vào kết quả tạm \"...\". Ghi nhớ M."* | `MyBigNumber.java:sumWithProgress` |
| **FR-04** | **Hạ số nhớ cuối cùng** | Sau khi duyệt hết tất cả các chữ số, nếu biến nhớ vẫn còn ($carry > 0$), phải tạo thêm một bước hạ số nhớ vào đầu kết quả thu được. | `MyBigNumber.java:sumWithProgress` |
| **FR-05** | **Xử lý lệch độ dài (Length Discrepancy)** | Tự động coi các chữ số còn thiếu ở chuỗi ngắn hơn là `0` khi chuỗi dài hơn vẫn còn ký tự cần duyệt. | `MyBigNumber.java:sum` & `sumWithProgress` |
| **FR-06** | **Kiểm soát tính hợp lệ đầu vào** | Ném `IllegalArgumentException` và ghi log `SEVERE` nếu tham số là `null`, chuỗi rỗng `""`, hoặc chứa bất kỳ ký tự nào ngoài `'0'` đến `'9'`. Thông báo lỗi phải chỉ rõ ký tự sai và chỉ số vị trí (0-indexed). | `MyBigNumber.java:validateInputs` |

---

## 4. Yêu cầu Phi Chức năng (Non-Functional Requirements - NFR)

### 4.1. Hiệu năng & Tối ưu Bộ nhớ (Performance & Efficiency)
* **Độ phức tạp thời gian (Time Complexity):** Bắt buộc đạt $\mathcal{O}(N)$ với $N = \max(\text{len1}, \text{len2})$. Tuyệt đối cấm sử dụng `StringBuilder.insert(0, ...)` vì sẽ gây suy thoái thành $\mathcal{O}(N^2)$.
* **Bộ nhớ phụ (Space Complexity):**
  * `sum(...)`: Đạt $\mathcal{O}(1)$ phụ (ngoài mảng ký tự chứa kết quả trả về `char[maxLen + 1]`).
  * `sumWithProgress(...)`: Pre-allocate kích thước cho `ArrayList` với dung lượng `maxLen + 1` để triệt tiêu chi phí mảng tự động co giãn.
* **Tối ưu Garbage Collection (GC):** Tái sử dụng đối tượng `StringBuilder desc` cho các bước tính bằng `desc.setLength(0)`, sử dụng các phương thức `append(int)` nguyên thủy để ngăn chặn boxing `Integer`.

### 4.2. Tính Bất biến & Thread Safety (Concurrency)
* Class `MyBigNumber` là **Stateless Service** (không lưu giữ trạng thái nội tại giữa các lần gọi). Một instance duy nhất có thể được chia sẻ an toàn giữa nhiều thread đồng thời.
* Mô hình dữ liệu kết quả:
  - `CalculationResult`: Sử dụng Java `record` bất biến (Immutable Data Carrier).
  - `CalculationStep`: Các trường dữ liệu là `final`, chỉ cung cấp getter, không có setter.

### 4.3. Tiêu chuẩn Ghi log (Logging)
* Sử dụng chuẩn thư viện máy ảo Java `java.util.logging.Logger`.
* Toàn bộ các câu lệnh ghi log mức độ thông tin (`Level.INFO`) phải được bảo vệ bởi điều kiện kiểm tra `if (LOGGER.isLoggable(Level.INFO))` để không gây tổn thất hiệu năng tạo chuỗi khi log bị tắt ở môi trường production.

---

## 5. Đặc tả Dữ liệu Vào / Ra (I/O Contracts)

### 5.1. Dữ liệu Đầu vào (Inputs)
Cả hai phương thức `sum` và `sumWithProgress` nhận cùng bộ tham số:

| Tham số | Kiểu dữ liệu | Ràng buộc bắt buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `stn1` | `java.lang.String` | Not null; Length $\ge 1$; Regex `^[0-9]+$` | Chuỗi biểu diễn số nguyên không âm thứ nhất |
| `stn2` | `java.lang.String` | Not null; Length $\ge 1$; Regex `^[0-9]+$` | Chuỗi biểu diễn số nguyên không âm thứ hai |

### 5.2. Dữ liệu Đầu ra (Outputs)

#### Đối với `sum(stn1, stn2)`:
* **Kiểu trả về:** `String`
* **Ràng buộc:** Chuỗi chữ số chuẩn decimal, độ dài trong khoảng $[\max(\text{len1}, \text{len2}), \max(\text{len1}, \text{len2}) + 1]$.

#### Đối với `sumWithProgress(stn1, stn2)`:
* **Kiểu trả về:** `CalculationResult`

**Cấu trúc dữ liệu của `CalculationResult`:**
| Tên trường | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `sum` | `String` | Bắt buộc; Chuỗi số canonical | Kết quả tổng cuối cùng của phép toán |
| `steps` | `List<CalculationStep>` | Bắt buộc; Danh sách có thứ tự | Danh sách các bước thực hiện đặt tính cộng |

**Cấu trúc dữ liệu của `CalculationStep`:**
| Tên trường | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `stepNumber` | `int` | Bắt buộc; Bắt đầu từ 1 | Thứ tự của bước tính toán |
| `description` | `String` | Bắt buộc; Tiếng Việt có dấu | Lời giải thích thao tác cộng và ghi nhớ |
| `intermediateResult` | `String` | Bắt buộc; Ký tự số | Chuỗi kết quả tích lũy tính đến bước hiện tại |
| `carry` | `int` | Bắt buộc; Giá trị $0$ hoặc $1$ | Số nhớ chuyển giao sang bước tiếp theo |

---

## 6. Phụ thuộc Hệ thống (Dependencies)

```text
my-big-number-core/
├── Production Dependencies:
│   └── JDK 21 (java.base: java.util.*, java.util.logging.*)
│
└── Test Dependencies:
    └── org.junit.jupiter:junit-jupiter (v5.10.2)
```
