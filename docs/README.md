# Project-Level Documentation

## Mục đích

Thư mục này chứa các tài liệu cấp workspace cho việc bổ sung REST API vào `my-big-number-web`. Đây là nguồn tham chiếu trước khi tạo hoặc sửa source code.

## Cấu trúc thư mục tài liệu

- **`docs/core/`**: Tài liệu kỹ thuật, thuật toán và quy chuẩn lập trình của module lõi.
  - [Requirement & Business Context](core/requirement.md)
  - [Thuật toán & Phân tích Kỹ thuật](core/algorithm.md)
  - [Quy chuẩn Lập trình (Coding Rules)](core/coding-rules.md)
  - [Mô hình Đe dọa & Phòng vệ (Security Rules)](core/security-rules.md)
  - [Đặc tả Java API chi tiết](core/core-api-reference.md)
  - [Phân tích Benchmark Thuật toán](core/benchmark-and-performance.md)
- **`docs/api/`**: Tài liệu đặc tả và thiết kế cho REST API Sub-module.
  - [API Requirements](api/api-requirements.md): phạm vi, quy tắc nghiệp vụ và tiêu chí chấp nhận.
  - [API Specification](api/api-spec.md): endpoint, JSON schema, status code và error contract.
  - [API Coding Rules](api/coding-rules.md): quy chuẩn lập trình Spring Boot, Controller, DTO & Validation.
  - [API Design](api/api-design.md): các file dự kiến, luồng xử lý và giới hạn implementation.
  - [API Rules](api/api-rules.md): quy chuẩn sinh mã và ràng buộc kiến trúc cho AI.
  - [API Test Matrix](api/api-test-matrix.md): các trường hợp kiểm thử bắt buộc.
- **`docs/reports/`**: Báo cáo thẩm định và độ bao phủ kiểm thử.
  - [Báo cáo Thẩm định Ngữ cảnh](reports/audit-report-2026-09-23.md)
  - [Compliance Scorecard](reports/scorecard.md)
  - [Báo cáo JaCoCo Coverage](reports/coverage-report/README.md)

## Authority Order

Khi các tài liệu mâu thuẫn, áp dụng thứ tự ưu tiên:

1. `docs/api/api-spec.md`: API contract bắt buộc.
2. `docs/api/api-requirements.md`: nghiệp vụ và phạm vi.
3. `docs/api/api-test-matrix.md`: hành vi phải kiểm thử.
4. `docs/api/api-design.md`: hướng dẫn triển khai.
5. Module README và tài liệu tham khảo: chỉ dùng để bổ sung ngữ cảnh.

Không được tự suy đoán khi các tài liệu cấp 1-3 mâu thuẫn. Dừng sinh code và báo cáo mâu thuẫn.

## Quy tắc sinh code

- Đọc toàn bộ bốn tài liệu trước khi sửa Java hoặc POM.
- Không tự bổ sung field, endpoint hoặc requirement chưa có trong tài liệu.
- Khi tài liệu mâu thuẫn với source code, ghi nhận mâu thuẫn và cập nhật spec trước khi triển khai.
- Sau mỗi thay đổi nhỏ, chạy test hẹp nhất phù hợp; trước bàn giao chạy `mvn clean verify`.

## Code Generation Checklist

- [ ] Đã đọc toàn bộ tài liệu normative.
- [ ] Đã xác định endpoint, request schema và response schema.
- [ ] Đã xác định mọi error status, error code và message.
- [ ] Đã xác định target files.
- [ ] Đã viết hoặc cập nhật test trước khi sửa production code.
- [ ] Đã chạy `mvn -pl my-big-number-web -am test`.
- [ ] Đã chạy `mvn clean verify`.