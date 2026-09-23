# Project-Level Documentation

## Mục đích

Thư mục này chứa các tài liệu cấp workspace cho việc bổ sung REST API vào `my-big-number-web`. Đây là nguồn tham chiếu trước khi tạo hoặc sửa source code.

## Thứ tự đọc và sử dụng

1. [API Requirements](api-requirements.md): phạm vi, quy tắc nghiệp vụ và tiêu chí chấp nhận.
2. [API Specification](api-spec.md): endpoint, JSON schema, status code và error contract.
3. [API Test Matrix](api-test-matrix.md): các trường hợp kiểm thử bắt buộc.
4. [API Design](api-design.md): các file dự kiến, luồng xử lý và giới hạn implementation.

## Authority Order

Khi các tài liệu mâu thuẫn, áp dụng thứ tự ưu tiên:

1. `api-spec.md`: API contract bắt buộc.
2. `api-requirements.md`: nghiệp vụ và phạm vi.
3. `api-test-matrix.md`: hành vi phải kiểm thử.
4. `api-design.md`: hướng dẫn triển khai.
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