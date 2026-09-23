# MyBigNumber API Compliance Scorecard

**Dự án:** `MyBigNumber`
**Mục tiêu:** Đánh giá source code REST API sinh bởi AI/Copilot theo project-level API contract.

---

## 1. Ngữ cảnh & Ràng buộc Prompt (Context Prompt)
```text
Role: Senior Engineer.
Task: Implement `POST /api/calculations` for MyBigNumber.
Context files: `C:\my-big-number\docs\api-requirements.md`, `C:\my-big-number\docs\api-spec.md`, `C:\my-big-number\docs\api-test-matrix.md`, `C:\my-big-number\docs\api-design.md`.
Constraints: Do not invent JSON fields, preserve the Thymeleaf routes, use the core public API, return Problem Details on error, and run the specified Maven tests.
```

---

## 2. Bảng chấm điểm chi tiết (Scorecard Table - 12 Tiêu chí)

| STT | Tiêu chí đánh giá (Criteria) | Quy tắc đối chiếu | Kỳ vọng (Expectation) | Thực tế đạt được (Actual Code) | Trạng thái | Điểm |
| :---: | :--- | :--- | :--- | :--- | :---: | :---: |
| **1** | **REST Endpoint** | `api-spec.md` | Endpoint là `POST /api/calculations`. | Chưa có implementation | **PENDING** | 0/10 |
| **2** | **HTTP Verb & Status Code** | `api-spec.md` | Trả `200` khi thành công, `400` khi input lỗi. | Chưa có implementation | **PENDING** | 0/10 |
| **3** | **Strict Schema Conformance** | `api-spec.md` | Chỉ dùng `num1`, `num2`, `includeSteps`, `sum`, `steps`. | Chưa có implementation | **PENDING** | 0/10 |
| **4** | **Chuẩn lỗi RFC 7807** | `api-rules.md` (Rule 7) | Phản hồi lỗi có `type`, `title`, `status`, `detail`, `code`. | Chưa có implementation | **PENDING** | 0/10 |
| **5** | **Xác thực dữ liệu đầu vào** | `api-requirements.md` | Validate numeric strings, length, null option, and unknown fields. | Chưa có implementation | **PENDING** | 0/10 |
| **6** | **Core reuse and injection** | `api-design.md` | Use constructor injection and the public `MyBigNumber` API. | Chưa có implementation | **PENDING** | 0/10 |
| **7** | **Architecture boundaries** | `api-design.md` | Controller không sao chép thuật toán hoặc render template. | Chưa có implementation | **PENDING** | 0/10 |
| **8** | **Xử lý ngoại lệ chuẩn** | `coding-rules.md` | Map lỗi cụ thể theo error matrix. | Chưa có implementation | **PENDING** | 0/10 |
| **9** | **Logging không lộ dữ liệu** | `security-rules.md` | Không log full numeric inputs hoặc stack trace. | Chưa có implementation | **PENDING** | 0/10 |
| **10** | **Không Hardcode Secrets** | `security-rules.md` (Rule 1) | Không hardcode mật khẩu, token hay API key. | Chưa có executable verification | **PENDING** | 0/10 |
| **11** | **Chống rò rỉ Stacktrace** | `security-rules.md` (Rule 5) | Lỗi HTTP 500 không trả stacktrace hoặc exception nội bộ. | Chưa có implementation | **PENDING** | 0/10 |
| **12** | **Contract tests** | `api-test-matrix.md` | Mỗi case trong test matrix có executable test. | Chưa có implementation | **PENDING** | 0/10 |

---

## 3. Tổng kết Đánh giá (Final Summary)

* **Tổng số tiêu chí kiểm tra:** 12 tiêu chí
* **Số tiêu chí đạt (Pass):** 0 / 12 before implementation
* **Số tiêu chí vi phạm (Fail):** 0 / 12
* **Tổng điểm đạt được:** **Pending implementation and executable verification**

### Nhận xét & Kết luận:
Không được đánh dấu `PASS` nếu chưa có source path, test path, executable command và output xác nhận. Scorecard này đánh giá implementation thực tế, không suy luận từ việc tài liệu tồn tại.
