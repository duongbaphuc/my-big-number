# `security-rules.md` — Threat Model & Defenses (`my-big-number-core`)

Tài liệu phân tích mô hình mối đe dọa an toàn thông tin (Threat Model), các lỗ hổng tiềm ẩn khi xử lý dữ liệu số lớn và quy định các biện pháp phòng vệ (Security Defenses) cho module `my-big-number-core`.

---

## 1. Mô hình Đe dọa (Threat Modeling - STRIDE)

Dù là thư viện toán học thuần, `my-big-number-core` trực tiếp xử lý dữ liệu từ người dùng truyền vào qua các tầng ứng dụng bên ngoài. Các rủi ro an ninh chính được xác định bao gồm:

```text
  [Untrusted External Input]
            │
            ▼
┌───────────────────────────────┐      Threat: Cạn kiệt Heap/CPU (DoS)
│ Web / API Boundary Sanitizer  │ ──►  Threat: Ký tự rác / Injection
└───────────────┬───────────────┘
                │ (Sanitized Strings)
                ▼
┌───────────────────────────────┐      Threat: Lộ dữ liệu nhạy cảm qua Logs
│   my-big-number-core          │ ──►  Threat: Tràn số biến đếm (Integer Overflow)
└───────────────────────────────┘
```

| Mối đe dọa (Threat) | Loại hình (STRIDE) | Nguy cơ cụ thể đối với Core | Mức độ nghiêm trọng |
| :--- | :--- | :--- | :---: |
| **Denial of Service (DoS)** | Denial of Service | Chuỗi số có kích thước hàng chục MB gây tràn bộ nhớ heap (`OutOfMemoryError`) hoặc chiếm dụng CPU trong thời gian dài. | **HIGH (P1)** |
| **Character Injection** | Tampering / Injection | Ký tự lạ, số âm, dấu chấm thập phân, ký tự điều khiển Unicode (`\0`, `\n`) lọt vào thuật toán tính toán. | **HIGH (P1)** |
| **Information Disclosure** | Information Disclosure | Lộ dữ liệu số nhạy cảm (số thẻ ngân hàng, số căn cước, số dư tài khoản) vào file log hệ thống hoặc rò rỉ stack trace ra client. | **MEDIUM (P2)** |
| **Integer Index Overflow** | Elevation / Crash | Độ dài chuỗi vượt ngưỡng `Integer.MAX_VALUE - 1` dẫn đến `maxLen + 1` bị tràn thành số âm trong cấp phát mảng. | **LOW (P3)** |

---

## 2. Phân tích Chi tiết & Cơ chế Phòng ngự (Defenses)

### 2.1. Phòng chống Tấn công Từ chối Dịch vụ (Resource Exhaustion / DoS Defense)
* **Nguy cơ:** Việc gọi `sumWithProgress` với 2 chuỗi có độ dài $1.000.000$ chữ số sẽ sinh ra $1.000.000$ đối tượng `CalculationStep`. Mỗi đối tượng chứa 2 chuỗi (`description` và `intermediateResult`), có thể tiêu tốn hơn **500MB RAM** chỉ cho một phép tính đơn lẻ, dẫn đến Crash JVM do `OutOfMemoryError`.
* **Cơ chế phòng vệ:**
  1. **Tách biệt Fast Path:** Luồng `sum(stn1, stn2)` chỉ cấp phát một mảng `char[]` duy nhất có kích thước $maxLen + 1$. Chi phí bộ nhớ cho chuỗi $100.000$ ký tự chỉ tốn $\approx 200\text{ KB}$, hoàn toàn an toàn.
  2. **Ràng buộc giới hạn tải (Payload Limit):** Tầng Web/REST Controller phải áp đặt giới hạn kiểm tra độ dài cứng trước khi gọi vào Core:
     ```java
     // Ràng buộc tại Controller Boundary (docs/api/api-rules.md)
     @Size(max = 100000, message = "num1 exceeds the maximum length")
     ```

### 2.2. Kiểm soát Tính hợp lệ Tuyệt đối của Ký tự (Strict Whitelisting)
* **Nguy cơ:** Kẻ tấn công cố tình truyền số âm (`"-123"`), số thực (`"12.3"`), hoặc ký tự điều khiển Unicode để làm sai lệch logic cộng dồn mã ASCII.
* **Cơ chế phòng vệ:**
  * Thuật toán áp dụng cơ chế **Character Whitelisting** nghiêm ngặt tại từng vị trí:
    ```java
    // Trích xuất từ MyBigNumber.java
    if (c1 < '0' || c1 > '9') {
        LOGGER.severe("LỖI: Chuỗi 1 chứa ký tự không hợp lệ '" + c1 + "' tại vị trí " + index1 + ".");
        throw new IllegalArgumentException(String.format("Chuỗi 1 chứa ký tự không hợp lệ '%c' tại vị trí %d.", c1, index1));
    }
    ```
  * **Miễn nhiễm hoàn toàn với ReDoS (Regular Expression DoS):** Core không dùng biểu thức chính quy (Regex) trong vòng lặp tính toán mà so sánh trực tiếp mã ký tự ASCII (`'0'` đến `'9'`) với chi phí $\mathcal{O}(1)$ thời gian thực.

### 2.3. Phòng chống Rò rỉ Dữ liệu Nhạy cảm (Sensitive Data Exposure in Logging)
* **Nguy cơ:** Tại các dòng log:
  ```java
  LOGGER.log(Level.INFO, "Bắt đầu phép tính cộng: {0} + {1}", new Object[]{stn1, stn2});
  ```
  Nếu hệ thống được sử dụng để xử lý chuỗi số nhận dạng cá nhân (PII) như mã số định danh, số thẻ tín dụng hoặc mã giao dịch tài chính, việc ghi log toàn bộ chuỗi số sẽ vi phạm các quy chuẩn an toàn dữ liệu như **PCI-DSS** và **GDPR**.
* **Quy tắc bảo mật Logging:**
  1. Trong môi trường Production, cấu hình `logging.properties` ở mức `WARNING` hoặc `SEVERE` để triệt tiêu các bản ghi `Level.INFO`.
  2. Khi ghi log lỗi tại tầng Controller / Service, **tuyệt đối không log toàn bộ nội dung chuỗi số**; chỉ ghi nhận độ dài ký tự và mã định danh request (`Request ID / Trace ID`):
     ```java
     // [GOOD]: Log an toàn trong môi trường Production
     LOGGER.info("Bắt đầu xử lý tính toán. Length1: " + stn1.length() + ", Length2: " + stn2.length());
     ```

### 2.4. Tránh rò rỉ Stacktrace & Cấu trúc Nội bộ (Error Disclosure)
* **Nguyên tắc:** Core ném `IllegalArgumentException` chứa thông báo vị trí lỗi cụ thể (ví dụ: `"Chuỗi 1 chứa ký tự không hợp lệ 'a' tại vị trí 2."`).
* Tầng Web / ExceptionHandler **tuyệt đối không được trả nguyên vẹn exception stack trace, package name hay class internals** cho client. Mọi ngoại lệ từ Core phải được đóng gói thành chuẩn lỗi **RFC 7807 Problem Details** (chỉ trả về `type`, `title`, `status`, `detail`, `code`).

---

## 3. Ma trận Đối chiếu với OWASP Top 10 (2021)

| Hạng mục OWASP | Rủi ro liên quan | Trạng thái phòng vệ trong Core & Web |
| :--- | :--- | :--- |
| **A03:2021 - Injection** | Ký tự độc hại lọt vào chuỗi số | **ĐÃ PHÒNG VỆ:** Whitelist ký tự cứng `[0-9]` trên từng chỉ số, ném lỗi ngay khi gặp ký tự ngoài khoảng ASCII. |
| **A04:2021 - Insecure Design** | Cạn kiệt tài nguyên máy chủ do chuỗi số vô hạn | **ĐÃ PHÒNG VỆ:** Giới hạn tải tối đa $100.000$ ký tự tại tầng ngoài; thiết kế luồng Fast Path $\mathcal{O}(1)$ bộ nhớ phụ. |
| **A05:2021 - Security Misconfiguration** | Để lộ stacktrace nội bộ ra ngoài HTTP response | **ĐÃ PHÒNG VỆ:** `@RestControllerAdvice` trong module Web bắt và chuyển hóa mọi ngoại lệ sang RFC 7807 Problem Details. |
| **A09:2021 - Security Logging Failures** | Ghi log lộ thông tin nhạy cảm hoặc không ghi nhận lỗi | **ĐÃ PHÒNG VỆ:** Mọi lỗi nhập liệu được ghi log `SEVERE` kèm vị trí; log `INFO` được bảo vệ bởi `LOGGER.isLoggable`. |

---

## 4. Checklist Rà soát An toàn trước khi Release (Security Checklist)

- [x] Không hardcode bất kỳ secret, API key hay thông tin nhạy cảm nào trong mã nguồn Core.
- [x] Không sử dụng kiểu số nguyên thủy (`int`, `long`, `BigInteger`) cho đầu vào để chống tràn số toán học.
- [x] Input đã được validate `null`, chuỗi rỗng `""` và phạm vi ký tự `[0-9]`.
- [x] Thuật toán duyệt từ phải sang trái không sử dụng `StringBuilder.insert(0)` gây treo CPU.
- [x] Có cơ chế bắt `IllegalArgumentException` ở tầng gọi ngoài để trả về lỗi 400 thân thiện.
- [x] Mức log Production được cấu hình phù hợp để tránh làm đầy ổ cứng bằng chuỗi số lớn.
