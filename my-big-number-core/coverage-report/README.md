# Báo Cáo Độ Bao Phủ Kiểm Thử (Test Coverage Report)

Thư mục này chứa toàn bộ các báo cáo độ bao phủ mã nguồn (**Code Coverage**) được đo lường tự động bởi **JaCoCo (Java Code Coverage Engine v0.8.11)** cho module lõi `my-big-number-core`.

---

## 1. Cấu trúc thư mục báo cáo

```
coverage-report/
├── README.md                 # Tài liệu tổng hợp kết quả và phân tích chi tiết (File này)
├── coverage.csv              # Dữ liệu thống kê coverage dạng bảng CSV chuẩn
├── jacoco.exec               # File nhị phân lưu trữ toàn bộ dữ liệu thực thi (JaCoCo execution data)
└── html/                     # Báo cáo giao diện web tương tác (Interactive Web Report)
    ├── index.html            # Trang chủ báo cáo coverage (mở bằng trình duyệt bất kỳ)
    ├── com.bignumber.core/   # Chi tiết coverage từng class và từng dòng code mã nguồn
    └── jacoco-resources/     # Stylesheet, icon và script phục vụ giao diện HTML
```

---

## 2. Bảng tổng hợp chỉ số Coverage (ĐẠT 100% TUYỆT ĐỐI)

Được thực hiện với toàn bộ các ca kiểm thử trong [`MyBigNumberTest.java`](../src/test/java/com/bignumber/core/MyBigNumberTest.java):

| Hạng mục đo lường (Metric) | Đã bao phủ (Covered) | Chưa bao phủ (Missed) | Tổng cộng (Total) | **Tỷ lệ đạt được (%)** | Đánh giá chất lượng |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **Line Coverage** (Độ phủ dòng lệnh) | **112** | **0** | **112** | **100.0%** | 🏆 **Tuyệt đối (100%)** |
| **Branch Coverage** (Nhánh điều kiện) | **52** | **0** | **52** | **100.0%** | 🏆 **Tuyệt đối (100%)** |
| **Instruction Coverage** (Bytecode instructions) | **615** | **0** | **615** | **100.0%** | 🏆 **Tuyệt đối (100%)** |
| **Method Coverage** (Phương thức) | **11** | **0** | **11** | **100.0%** | 🏆 **Tuyệt đối (100%)** |
| **Class Coverage** (Lớp mã nguồn) | **3** | **0** | **3** | **100.0%** | 🏆 **Tuyệt đối (100%)** |
| **Complexity Coverage** (Độ phức tạp Cyclomatic) | **37** | **0** | **37** | **100.0%** | 🏆 **Tuyệt đối (100%)** |

---

## 3. Chi tiết độ bao phủ theo từng Class

### 3.1. Class `com.bignumber.core.MyBigNumber`
* **Line Coverage:** `101 / 101` (**100%**)
* **Instruction Coverage:** `579 / 579` (**100%**)
* **Branch Coverage:** `52 / 52` (**100%**)
* **Method Coverage:** `5 / 5` (**100%**)
* **Các ca kiểm thử đã bao phủ hoàn hảo:**
  - Luồng Fast Path tính toán trực tiếp `sum(stn1, stn2)` khi có nhớ, không nhớ, độ dài lệch nhau, số 0, số siêu lớn.
  - Luồng `sumWithProgress(stn1, stn2)` khi có nhớ, không nhớ, có hạ số nhớ cuối cùng và không hạ số nhớ.
  - Toàn bộ các nhánh kiểm tra ký tự không hợp lệ (`c < '0'` và `c > '9'`) cho cả chuỗi 1 và chuỗi 2 trên cả 2 hàm.
  - Toàn bộ các nhánh rẽ kiểm tra dữ liệu đầu vào: `stn1 == null`, `stn2 == null`, `stn1.isEmpty()`, `stn2.isEmpty()`.
  - Bao phủ cả 2 trạng thái bật/tắt của điều kiện `LOGGER.isLoggable(Level.INFO)`.

### 3.2. Class `com.bignumber.core.CalculationStep`
* **Line Coverage:** `10 / 10` (**100%**)
* **Instruction Coverage:** `27 / 27` (**100%**)
* **Method Coverage:** `5 / 5` (**100%**)
* **Các ca kiểm thử đã bao phủ:** Constructor và tất cả các getter (`getStepNumber()`, `getDescription()`, `getIntermediateResult()`, `getCarry()`).

### 3.3. Record `com.bignumber.core.CalculationResult`
* **Line Coverage:** `1 / 1` (**100%**)
* **Instruction Coverage:** `9 / 9` (**100%**)
* **Method Coverage:** `1 / 1` (**100%**)
* **Các ca kiểm thử đã bao phủ:** Constructor, getter `sum()`, getter `steps()`, `equals()`, `hashCode()`, `toString()`.

---

## 4. Hướng dẫn mở xem báo cáo HTML

Bạn có thể mở trực tiếp file HTML bằng bất kỳ trình duyệt web nào (Chrome, Edge, Firefox):

👉 **Đường dẫn file HTML:**
[`coverage-report/html/index.html`](html/index.html)
