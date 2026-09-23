# REST API Specification

## 1. Endpoint

| Method | Path | Mục đích |
|---|---|---|
| `POST` | `/api/calculations` | Cộng hai số nguyên không âm |

API này độc lập với các route HTML hiện tại.

## 2. Request

`Content-Type: application/json`

```json
{
  "num1": "1234",
  "num2": "897",
  "includeSteps": false
}
```

### Request fields

| Tên trường | Kiểu dữ liệu | Ràng buộc bắt buộc/độ dài | Mô tả |
|---|---|---:|---|
| `num1` | `string` | Bắt buộc; 1-100000 ký tự; `^[0-9]+$` | Số nguyên không âm thứ nhất |
| `num2` | `string` | Bắt buộc; 1-100000 ký tự; `^[0-9]+$` | Số nguyên không âm thứ hai |
| `includeSteps` | `boolean` | Không bắt buộc; mặc định `false`; `null` bị từ chối | Include calculation steps |

Không được chuyển `num1` hoặc `num2` thành JSON number vì có thể mất độ chính xác với số rất dài.

## 3. Success response

HTTP `200 OK`:

```json
{
  "sum": "2131",
  "steps": []
}
```

Khi `includeSteps` là `true`:

```json
{
  "sum": "2131",
  "steps": [
    {
      "stepNumber": 1,
      "description": "7 + 4 + 0 = 11, ghi 1, nhớ 1",
      "intermediateResult": "1",
      "carry": 1
    }
  ]
}
```

### Response fields

| Tên trường | Kiểu dữ liệu | Ràng buộc bắt buộc/độ dài | Mô tả |
|---|---|---|---|
| `sum` | `string` | Bắt buộc; chỉ gồm chữ số; canonical decimal | Tổng cuối cùng |
| `steps` | `array<object>` | Bắt buộc; rỗng khi `includeSteps=false` | Danh sách bước có thứ tự |
| `steps[].stepNumber` | `integer` | Bắt buộc; tối thiểu `1` | Số thứ tự bước |
| `steps[].description` | `string` | Bắt buộc; không rỗng | Mô tả phép tính |
| `steps[].intermediateResult` | `string` | Bắt buộc; chỉ gồm chữ số | Kết quả tạm |
| `steps[].carry` | `integer` | Bắt buộc; chỉ `0` hoặc `1` | Số nhớ chuyển sang bước tiếp theo |

## 4. Error response

Mọi lỗi API phải dùng `application/problem+json` và RFC 7807:

```json
{
  "type": "https://my-big-number.example/problems/invalid-request",
  "title": "Invalid request",
  "status": 400,
  "detail": "num1 must contain digits only",
  "code": "INVALID_NUM1"
}
```

### Error fields (RFC 7807)

| Tên trường | Kiểu dữ liệu | Ràng buộc bắt buộc/độ dài | Mô tả |
|---|---|---|---|
| `type` | `string` | Bắt buộc; URI format | URI xác định loại lỗi cụ thể |
| `title` | `string` | Bắt buộc; không rỗng | Tiêu đề lỗi ngắn gọn chuẩn HTTP |
| `status` | `integer` | Bắt buộc; khớp HTTP status | Mã trạng thái HTTP trả về |
| `detail` | `string` | Bắt buộc; không rỗng | Giải thích chi tiết nguyên nhân vi phạm (không lộ stack trace) |
| `code` | `string` | Bắt buộc; UPPER_SNAKE_CASE | Mã lỗi định danh nghiệp vụ cho client |

Chỉ được trả năm field trên trong phiên bản đầu tiên.

| Tình huống | Status | Error code | Detail |
|---|---:|---|---|
| JSON hợp lệ và dữ liệu hợp lệ | `200` | Không áp dụng | Không áp dụng |
| Thiếu hoặc rỗng `num1` | `400` | `INVALID_NUM1` | `num1 is required` / `num1 must not be blank` |
| `num1` chứa ký tự ngoài `0-9` | `400` | `INVALID_NUM1` | `num1 must contain digits only` |
| `num1` vượt 100,000 ký tự | `400` | `NUM1_TOO_LONG` | `num1 exceeds the maximum length` |
| Thiếu hoặc rỗng `num2` | `400` | `INVALID_NUM2` | `num2 is required` / `num2 must not be blank` |
| `num2` chứa ký tự ngoài `0-9` | `400` | `INVALID_NUM2` | `num2 must contain digits only` |
| `num2` vượt 100,000 ký tự | `400` | `NUM2_TOO_LONG` | `num2 exceeds the maximum length` |
| `includeSteps` là `null` hoặc sai kiểu | `400` | `INVALID_INCLUDE_STEPS` | `includeSteps must be a boolean` |
| JSON malformed hoặc có field lạ | `400` | `MALFORMED_REQUEST` | `Request body is invalid` |
| HTTP method không hỗ trợ | `405` | `METHOD_NOT_ALLOWED` | `HTTP method is not supported` |
| Lỗi không dự kiến | `500` | `INTERNAL_ERROR` | `An unexpected error occurred` |

Field JSON ngoài schema phải bị từ chối. `Content-Type` thành công là `application/json`; lỗi là `application/problem+json`.

## 5. Compatibility

Các route sau không được thay đổi bởi REST API:

- `GET /` trả trang `index`.
- `POST /calculate` xử lý form Thymeleaf.