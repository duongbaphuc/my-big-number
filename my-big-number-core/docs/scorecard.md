# Bảng Đánh Giá Tuân Thủ Quy Tắc (Scorecard Check)
**Dự án:** `my-big-number-core` | **Mô đun:** Context Engineering (Lab 2.1)  
**Mục tiêu kiểm chứng:** Đánh giá mã nguồn nháp [`scratch/ScratchHandler.java`](../scratch/ScratchHandler.java) sinh ra bởi AI/Copilot dựa trên bộ quy tắc nền tảng (**Rules Pack**).

---

## 1. Ngữ cảnh & Ràng buộc Prompt (Context Prompt)
```text
Role: Senior Engineer.
Task: Write a POST /api/workorders handler in Java.
Context files: docs/coding-rules.md, docs/api-rules.md, docs/security-rules.md.
Constraints: Do not invent extra JSON fields not specified in requirements, use standard validation, return 400 on error. Match repo style.
```

---

## 2. Bảng chấm điểm chi tiết (Scorecard Table - 12 Tiêu chí)

| STT | Tiêu chí đánh giá (Criteria) | Quy tắc đối chiếu | Kỳ vọng (Expectation) | Thực tế đạt được (Actual Code) | Trạng thái | Điểm |
| :---: | :--- | :--- | :--- | :--- | :---: | :---: |
| **1** | **REST Resource Naming** | `api-rules.md` (Rule 1) | Endpoint dùng danh từ số nhiều `/api/workorders`. | Handler định nghĩa đúng endpoint xử lý `/api/workorders`. | **PASS** | 10/10 |
| **2** | **HTTP Verb & Status Code** | `api-rules.md` (Rule 2) | Dùng `POST`, trả về 201 khi thành công, 400 khi lỗi đầu vào. | Trả về `WorkOrderResponse` (201) và `ProblemDetails` (400 Bad Request). | **PASS** | 10/10 |
| **3** | **Strict Schema Conformance** | `api-rules.md` (Rule 3) | **KHÔNG** tự ý sinh thêm trường dữ liệu ngoài đặc tả. | `CreateWorkOrderRequest` chỉ có đúng 3 trường: `title`, `description`, `priority`. Không có trường thừa (như `metadata`, `tags`). | **PASS** | 10/10 |
| **4** | **Chuẩn lỗi RFC 7807** | `api-rules.md` (Rule 4) | Phản hồi lỗi theo chuẩn Problem Details (`type`, `title`, `status`, `detail`, `instance`). | Class `ProblemDetails` định nghĩa đầy đủ 5 trường chuẩn RFC 7807. | **PASS** | 10/10 |
| **5** | **Xác thực dữ liệu đầu vào** | `api-rules.md` (Rule 5) | Xác thực các trường không được null hoặc blank, áp dụng Fail-Fast. | Kiểm tra `title`, `description`, `priority` không được rỗng ngay trong compact constructor của Record. | **PASS** | 10/10 |
| **6** | **Dependency Injection** | `coding-rules.md` (Rule 4) | Bắt buộc dùng Constructor Injection, cấm Field Injection `@Autowired`. | `ScratchHandler` và `WorkOrderService` đều dùng Constructor Injection cho các trường `final`. | **PASS** | 10/10 |
| **7** | **Variable Scope in Loops** | `coding-rules.md` (Rule 3) | Không khai báo biến bên trong vòng lặp. | Toàn bộ mã nguồn tuân thủ quản lý phạm vi biến chặt chẽ, không có khai báo biến thừa trong loop. | **PASS** | 10/10 |
| **8** | **Xử lý ngoại lệ chuẩn** | `coding-rules.md` (Rule 5) | Không ném `RuntimeException` chung chung, bắt lỗi cụ thể `IllegalArgumentException`. | Bắt riêng `IllegalArgumentException` trả về HTTP 400 rõ ràng. | **PASS** | 10/10 |
| **9** | **Logging không lộ PII** | `coding-rules.md` (Rule 6) | Dùng SLF4J, không log mật khẩu hay thông tin cá nhân PII. | Dùng `LoggerFactory.getLogger(...)`, chỉ log `priority`, `id`, không log thông tin nhạy cảm. | **PASS** | 10/10 |
| **10** | **Không Hardcode Secrets** | `security-rules.md` (Rule 1) | Tuyệt đối không hardcode mật khẩu, token hay API key. | Không có secret, credential hay token nào bị nhúng trong mã nguồn. | **PASS** | 10/10 |
| **11** | **Chống rò rỉ Stacktrace** | `security-rules.md` (Rule 5) | Lỗi hệ thống 500 không được trả stacktrace thô ra ngoài. | Khối catch `Exception` ghi log `error` nội bộ và trả về thông báo chung `Internal Server Error` an toàn. | **PASS** | 10/10 |
| **12** | **Tính Bất biến (Immutability)** | `coding-rules.md` (Rule 9) | Ưu tiên dùng Java `record` và trường `final`. | Dùng Java `record` cho `CreateWorkOrderRequest`, `WorkOrderResponse`, `ProblemDetails`. | **PASS** | 10/10 |

---

## 3. Tổng kết Đánh giá (Final Summary)

* **Tổng số tiêu chí kiểm tra:** 12 tiêu chí
* **Số tiêu chí đạt (Pass):** 12 / 12
* **Số tiêu chí vi phạm (Fail):** 0 / 12
* **Tổng điểm đạt được:** **120 / 120 (100% PASS)**

### Nhận xét & Kết luận:
Nhờ việc thiết lập bộ quy tắc ngữ cảnh chặt chẽ (**Context Engineering / Rules Pack** bao gồm `coding-rules.md`, `api-rules.md` và `security-rules.md`), mã nguồn sinh ra bởi AI hoàn toàn:
1. Tuân thủ 100% chuẩn REST và RFC 7807, không bị ảo giác (hallucination) sinh thêm các trường JSON lạ.
2. Đảm bảo an toàn bảo mật, không để lộ PII hoặc stacktrace.
3. Đáp ứng tiêu chuẩn kiến trúc hiện đại của Java 17+ (Constructor Injection, Record, Immutability).
