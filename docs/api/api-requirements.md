# REST API Requirements

## 1. Mục tiêu

Bổ sung REST API cho dự án MyBigNumber để client khác có thể gửi hai số nguyên không âm có độ dài tùy ý và nhận kết quả cộng. API phải tái sử dụng thuật toán trong `my-big-number-core` và không làm thay đổi hành vi giao diện Thymeleaf hiện tại.

## 2. Phạm vi

### Trong phạm vi

- Nhận hai giá trị số dưới dạng chuỗi: `num1` và `num2`.
- Hỗ trợ số nguyên không âm chỉ gồm các ký tự `0-9`.
- Không giới hạn độ dài theo kiểu dữ liệu nguyên thủy Java.
- Trả về tổng dưới dạng chuỗi.
- Cho phép client yêu cầu danh sách các bước tính.
- Trả lỗi validation theo RFC 7807 Problem Details.
- Viết unit test và integration/contract test cho API.
- Giữ nguyên các route HTML hiện có: `GET /` và `POST /calculate`.

### Ngoài phạm vi

- Số âm, số thập phân hoặc biểu thức toán học.
- Lưu lịch sử phép tính.
- Xác thực người dùng, phân quyền hoặc database.
- Thay thế giao diện Thymeleaf bằng frontend riêng.
- Thay đổi thuật toán trong `my-big-number-core`.

## 3. Quy tắc nghiệp vụ

1. `num1` và `num2` là bắt buộc.
2. Hai giá trị phải khác rỗng và chỉ chứa `0-9`.
3. Không chuyển giá trị sang `int`, `long`, `BigInteger` hoặc kiểu số có giới hạn trước khi tính.
4. Kết quả phải giống `MyBigNumber.sum(num1, num2)`.
5. Khi client yêu cầu progress, các bước phải lấy từ `MyBigNumber.sumWithProgress(num1, num2)`.
6. Không trả stack trace hoặc thông tin nội bộ cho client.

## 3.1. Request Data Contract

| Tên trường | Kiểu dữ liệu | Ràng buộc bắt buộc/độ dài | Mô tả |
|---|---|---|---|
| `num1` | `string` | Bắt buộc; 1-100000 ký tự; regex `^[0-9]+$` | Số nguyên không âm thứ nhất |
| `num2` | `string` | Bắt buộc; 1-100000 ký tự; regex `^[0-9]+$` | Số nguyên không âm thứ hai |
| `includeSteps` | `boolean` | Không bắt buộc; mặc định `false`; `null` bị từ chối | Quyết định có trả danh sách bước hay không |

Field JSON ngoài bảng trên phải bị từ chối bằng HTTP `400`.

## 3.2. Pseudo-code xử lý

1. Nhận JSON body của request.
2. Deserialize body vào `CalculationApiRequest`.
3. Kiểm tra `num1` không null, không blank và khớp `^[0-9]+$`.
4. Kiểm tra `num2` không null, không blank và khớp `^[0-9]+$`.
5. Kiểm tra độ dài mỗi input không vượt quá 100000 ký tự.
6. Nếu `includeSteps=false`, gọi `MyBigNumber.sum(num1, num2)`.
7. Nếu `includeSteps=true`, gọi `MyBigNumber.sumWithProgress(num1, num2)`.
8. Map kết quả core sang response DTO.
9. Trả HTTP `200` với `sum` và `steps`.
10. Nếu validation thất bại, trả Problem Details HTTP `400` theo error matrix.
11. Nếu lỗi không dự kiến, ghi log nội bộ và trả Problem Details HTTP `500`.

## 3.3. Error Matrix

| Điều kiện vi phạm | HTTP status | Error code | Message trả về |
|---|---:|---|---|
| Thiếu `num1` | `400` | `INVALID_NUM1` | `num1 is required` |
| `num1` rỗng | `400` | `INVALID_NUM1` | `num1 must not be blank` |
| `num1` chứa ký tự ngoài `0-9` | `400` | `INVALID_NUM1` | `num1 must contain digits only` |
| `num1` vượt 100000 ký tự | `400` | `NUM1_TOO_LONG` | `num1 exceeds the maximum length` |
| Thiếu `num2` | `400` | `INVALID_NUM2` | `num2 is required` |
| `num2` rỗng | `400` | `INVALID_NUM2` | `num2 must not be blank` |
| `num2` chứa ký tự ngoài `0-9` | `400` | `INVALID_NUM2` | `num2 must contain digits only` |
| `includeSteps` là `null` hoặc sai kiểu | `400` | `INVALID_INCLUDE_STEPS` | `includeSteps must be a boolean` |
| JSON malformed hoặc có field lạ | `400` | `MALFORMED_REQUEST` | `Request body is invalid` |
| Lỗi không dự kiến | `500` | `INTERNAL_ERROR` | `An unexpected error occurred` |

## 3.4. Normalization

- Không loại bỏ leading zero ở input trước khi validation.
- Input như `000123` được phép và được tính như giá trị 123.
- Kết quả dùng dạng decimal canonical, không có leading zero, ngoại trừ kết quả `0`.

## 4. Tiêu chí chấp nhận

- Request hợp lệ trả HTTP `200` và kết quả chính xác.
- Số có hàng trăm chữ số được xử lý mà không overflow.
- Request thiếu field, field rỗng hoặc chứa ký tự ngoài `0-9` trả HTTP `400`.
- Error response có đúng các field RFC 7807: `type`, `title`, `status`, `detail`.
- Không có field JSON ngoài API contract.
- Các route Thymeleaf hiện tại tiếp tục hoạt động.
- `mvn clean verify` chạy thành công.

## 5. Quyết định thiết kế

API sử dụng `includeSteps` tùy chọn trong request. Giá trị mặc định là `false` để endpoint kết quả nhanh không phải tạo danh sách bước khi client không cần.

## 6. Rủi ro và giới hạn

- Request cực lớn có thể tiêu tốn CPU và bộ nhớ; cần giới hạn kích thước payload ở tầng HTTP hoặc cấu hình ứng dụng khi đưa lên production.
- CORS, authentication và rate limiting chưa thuộc phiên bản đầu tiên.

## 7. Điều còn mở

- Giá trị tối đa của độ dài `num1` và `num2` cho production cần được thống nhất theo hạ tầng triển khai.
- Chính sách rate limiting sẽ được bổ sung khi API được công khai ra Internet.