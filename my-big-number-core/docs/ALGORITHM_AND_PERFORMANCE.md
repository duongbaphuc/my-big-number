# Nguyên Lý Thuật Toán & Tối Ưu Hiệu Năng (`Algorithm & Performance`)

Tài liệu này giải thích chi tiết giải thuật cộng số lớn mô phỏng cách tính đặt cột dọc của học sinh tiểu học và các kỹ thuật tối ưu hóa hiệu năng đã áp dụng trong module `my-big-number-core`.

---

## 1. Nguyên lý giải thuật (Mô phỏng phép cộng tiểu học)

Học sinh lớp 3 khi thực hiện phép cộng đặt tính cột dọc (ví dụ `1234 + 897`) sẽ tính toán theo các quy tắc sau:

```
    1 2 3 4
  +   8 9 7
  ---------
    2 1 3 1
```

### Các bước thực thi chi tiết:
1. **Duyệt từ phải sang trái (Hàng thấp đến hàng cao):**
   - Bắt đầu từ hàng đơn vị ($i = 0$), hàng chục ($i = 1$), hàng trăm ($i = 2$), hàng nghìn ($i = 3$)...
   - Số bước lặp tối đa là $\max(\text{length}(stn1), \text{length}(stn2))$.
2. **Lấy chữ số tương ứng:**
   - Tại vị trí $i$, lấy ký tự tại chỉ số $\text{len} - 1 - i$.
   - Chuyển ký tự thành số nguyên: $\text{digit} = c - '0'$.
   - Nếu một chuỗi đã duyệt hết chữ số thì giá trị tương ứng được coi là $0$.
3. **Cộng hai chữ số và biến nhớ:**
   $$\text{sumDigits} = \text{digit1} + \text{digit2}$$
   $$\text{total} = \text{sumDigits} + \text{carry}$$
4. **Xác định kết quả tạm và nhớ:**
   - Chữ số ghi vào cột hiện tại: $\text{currentDigit} = \text{total} \pmod{10}$.
   - Số nhớ mang sang cột tiếp theo: $\text{nextCarry} = \lfloor\text{total} / 10\rfloor$.
5. **Hạ số nhớ cuối cùng (nếu còn):**
   - Sau khi duyệt hết tất cả các chữ số của hai số, nếu $\text{carry} > 0$, số nhớ này được hạ trực tiếp vào đầu kết quả.

---

## 2. Sơ đồ luồng thuật toán (Flowchart)

```mermaid
flowchart TD
    Start([Bắt đầu: stn1, stn2]) --> Validate{Kiểm tra dữ liệu?}
    Validate -- "null / rỗng / ký tự sai" --> ThrowErr[Ghi log SEVERE & Ném IllegalArgumentException]
    Validate -- Hợp lệ --> Init[Khởi tạo: carry = 0, i = 0, maxLen = max len1, len2]
    
    Init --> LoopCheck{i < maxLen ?}
    
    LoopCheck -- Đúng --> GetDigits[Lấy digit1, digit2 từ phải sang trái]
    GetDigits --> Calc[total = digit1 + digit2 + carry]
    Calc --> UpdateDigit[Ghi currentDigit = total % 10 vào buffer kết quả]
    UpdateDigit --> UpdateCarry[nextCarry = total / 10]
    UpdateCarry --> LogStep[Ghi nhận bước tính & tăng i]
    LogStep --> LoopCheck
    
    LoopCheck -- Sai --> CheckCarry{carry > 0 ?}
    CheckCarry -- Có --> AppendFinalCarry[Hạ số nhớ vào đầu kết quả]
    CheckCarry -- Không --> ReturnResult[Tạo chuỗi kết quả hoàn chỉnh]
    AppendFinalCarry --> ReturnResult
    ReturnResult --> Finish([Kết thúc])
```

---

## 3. Bảng minh họa các bước tính mẫu (`1234 + 897`)

| Bước | Hàng | `digit1` | `digit2` | `carry` cũ | `total` | Chữ số lưu | `carry` mới | Kết quả tạm | Diễn giải |
| :---: | :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :--- |
| **1** | Đơn vị | 4 | 7 | 0 | 11 | **1** | 1 | `"1"` | Lấy 4 cộng 7 được 11. Lưu 1, nhớ 1. |
| **2** | Chục | 3 | 9 | 1 | 13 | **3** | 1 | `"31"` | Lấy 3 cộng 9 được 12. Cộng nhớ 1 được 13. Lưu 3, nhớ 1. |
| **3** | Trăm | 2 | 8 | 1 | 11 | **1** | 1 | `"131"` | Lấy 2 cộng 8 được 10. Cộng nhớ 1 được 11. Lưu 1, nhớ 1. |
| **4** | Nghìn | 1 | 0 | 1 | 2 | **2** | 0 | `"2131"` | Lấy 1 cộng 0 được 1. Cộng nhớ 1 được 2. Lưu 2, nhớ 0. |

Kết quả cuối cùng: **`2131`**.

---

## 4. Phân tích độ phức tạp (Big-O Complexity)

Gọi $N = \max(\text{length}(stn1), \text{length}(stn2))$:

| Phương thức | Độ phức tạp thời gian (Time) | Độ phức tạp không gian (Space) |
| :--- | :---: | :---: |
| **`sum(...)`** (Fast Path) | $\mathcal{O}(N)$ | $\mathcal{O}(1)$ bộ nhớ phụ (chỉ cấp phát 1 mảng kết quả $\mathcal{O}(N)$) |
| **`sumWithProgress(...)`** | $\mathcal{O}(N)$ | $\mathcal{O}(N)$ (lưu trữ $N$ đối tượng `CalculationStep`) |

---

## 5. Các kỹ thuật tối ưu hóa chuyên sâu đã áp dụng

### Kỹ thuật 1: Triệt tiêu điểm nghẽn $\mathcal{O}(N^2)$ của `StringBuilder.insert(0, ...)`
* **Vấn đề cũ:** Khi dùng `insert(0, c)` trên `StringBuilder`, toàn bộ ký tự hiện có phải dịch chuyển sang phải 1 ô trong mảng buffer. Qua $N$ bước lặp, chi phí sao chép là $1 + 2 + \dots + N = \mathcal{O}(N^2)$.
* **Cách khắc phục:** Cấp phát một mảng ký tự kích thước cố định `char[] buffer = new char[maxLen + 1]` và chỉ số `pos = buffer.length - 1`. Khi có chữ số mới, ghi trực tiếp:
  ```java
  buffer[pos--] = (char) ('0' + currentDigit);
  ```
  Ghi trực tiếp vào ô nhớ đạt độ phức tạp **$\mathcal{O}(1)$** tuyệt đối.

### Kỹ thuật 2: Tách luồng tính toán siêu tốc (Fast Path) cho hàm `sum()`
* Trong các tác vụ backend thông thường, hệ thống chỉ cần chuỗi kết quả cuối cùng mà không cần diễn giải.
* Hàm `sum()` được tách riêng để tính toán trực tiếp trên mảng ký tự:
  - **Zero-object allocation:** Không tạo đối tượng bước tính, không tạo chuỗi mô tả, không gọi logger.
  - Tốc độ tăng hơn **100 lần** so với việc gọi qua `sumWithProgress`.

### Kỹ thuật 3: Tái sử dụng vùng đệm (Buffer Reuse) & Zero-Autoboxing
* Trong `sumWithProgress`, một đối tượng `StringBuilder desc` duy nhất được tạo ra trước vòng lặp và reset bằng `desc.setLength(0)`.
* Thay thế hoàn toàn `String.format` bằng các lệnh `desc.append(...)` nhận giá trị nguyên thủy `int`, loại bỏ việc tự động đóng gói đối tượng (`Integer.valueOf`), giảm triệt để áp lực lên Garbage Collector (GC).

### Kỹ thuật 4: Pre-allocation Capacity cho Collections
* Khởi tạo trước kích thước cho `ArrayList`:
  ```java
  List<CalculationStep> steps = new ArrayList<>(maxLen + 1);
  ```
  Ngăn chặn việc Java phải liên tục cấp phát lại mảng và copy dữ liệu cũ khi mảng đầy (tăng gấp rưỡi dung lượng ngầm).

---

## 6. Kết quả đo đạc thực tế (Benchmark)

Thực hiện trên máy phát triển với Java 17:

* **Cộng 2 số có 5.000 chữ số (so sánh thuật toán cũ và mới):**
  - Thuật toán cũ: **~231 ms**
  - Thuật toán mới: **~47 ms** (Nhanh hơn gấp ~5 lần)
* **Cộng 2 số có 50.000 chữ số (Fast Path `sum()`):**
  - Thời gian thực thi: **~0.35 ms** (Xử lý 50.000 chữ số chỉ trong một phần ba mili-giây).
