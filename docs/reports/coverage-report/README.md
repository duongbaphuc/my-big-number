# Báo Cáo Độ Bao Phủ Kiểm Thử Toàn Diện (Multi-Module Test Coverage Report)

Tài liệu này tổng hợp toàn bộ các kết quả đo lường độ bao phủ mã nguồn (**Code Coverage**) được thực thi và xác minh tự động bởi **JaCoCo (Java Code Coverage Engine v0.8.11)** cho cả hai sub-modules:
1. `my-big-number-core` (Lõi tính toán số học lớn)
2. `my-big-number-web` (Giao diện Web Thymeleaf & REST API)

---

## 1. Bảng Tổng Hợp Chỉ Số Toàn Dự Án (ĐẠT 100% TUYỆT ĐỐI)

| Sub-module | Total Lines | Missed Lines | **Line Cov (%)** | Total Branches | Missed Branches | **Branch Cov (%)** | Total Methods | Missed Methods | **Status** |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **`my-big-number-core`** | **126** | **0** | **100.0%** | **66** | **0** | **100.0%** | **14** | **0** | 🏆 **100% Tuyệt đối** |
| **`my-big-number-web`** | **125** | **0** | **100.0%** | **26** | **0** | **100.0%** | **35** | **0** | 🏆 **100% Tuyệt đối** |
| **TỔNG TOÀN DỰ ÁN** | **251** | **0** | **100.0%** | **92** | **0** | **100.0%** | **49** | **0** | 🏆 **100% Tuyệt đối** |

---

## 2. Chi Tiết Độ Bao Phủ Từng Class Trong `my-big-number-core`

| Lớp mã nguồn (Class) | Lines Cov / Total | Branch Cov / Total | Instructions | Methods | Trạng thái |
| :--- | :---: | :---: | :---: | :---: | :---: |
| `com.bignumber.core.MyBigNumber` | **102 / 102** (100%) | **52 / 52** (100%) | **581 / 581** | **5 / 5** | ✅ 100% |
| `com.bignumber.core.CalculationStep` | **23 / 23** (100%) | **14 / 14** (100%) | **106 / 106** | **8 / 8** | ✅ 100% |
| `com.bignumber.core.CalculationResult` | **1 / 1** (100%) | **0 / 0** (n/a) | **9 / 9** | **1 / 1** | ✅ 100% |
| **Tổng Core** | **126 / 126** (100%) | **66 / 66** (100%) | **696 / 696** | **14 / 14** | 🏆 **100%** |

---

## 3. Chi Tiết Độ Bao Phủ Từng Class Trong `my-big-number-web`

| Lớp mã nguồn (Class) | Lines Cov / Total | Branch Cov / Total | Instructions | Methods | Trạng thái |
| :--- | :---: | :---: | :---: | :---: | :---: |
| `com.bignumber.web.controller.BigNumberRestController` | **18 / 18** (100%) | **2 / 2** (100%) | **67 / 67** | **3 / 3** | ✅ 100% |
| `com.bignumber.web.controller.BigNumberController` | **14 / 14** (100%) | **2 / 2** (100%) | **51 / 51** | **4 / 4** | ✅ 100% |
| `com.bignumber.web.exception.ApiExceptionHandler` | **61 / 61** (100%) | **20 / 20** (100%) | **184 / 184** | **9 / 9** | ✅ 100% |
| `com.bignumber.web.BigNumberWebApplication` | **4 / 4** (100%) | **0 / 0** (n/a) | **12 / 12** | **3 / 3** | ✅ 100% |
| `com.bignumber.web.dto.CalculationRequest` | **9 / 9** (100%) | **0 / 0** (n/a) | **23 / 23** | **5 / 5** | ✅ 100% |
| `com.bignumber.web.dto.CalculationApiRequest` | **16 / 16** (100%) | **2 / 2** (100%) | **50 / 50** | **8 / 8** | ✅ 100% |
| `com.bignumber.web.dto.CalculationApiResponse` | **1 / 1** (100%) | **0 / 0** (n/a) | **9 / 9** | **1 / 1** | ✅ 100% |
| `com.bignumber.web.dto.CalculationStepDto` | **1 / 1** (100%) | **0 / 0** (n/a) | **15 / 15** | **1 / 1** | ✅ 100% |
| `com.bignumber.web.dto.ProblemDetailResponse` | **1 / 1** (100%) | **0 / 0** (n/a) | **18 / 18** | **1 / 1** | ✅ 100% |
| **Tổng Web** | **125 / 125** (100%) | **26 / 26** (100%) | **429 / 429** | **35 / 35** | 🏆 **100%** |

---

## 4. Hướng Dẫn Tái Tạo Kết Quả (Reproducibility)

Để chạy kiểm thử và tự động sinh báo cáo JaCoCo HTML & CSV:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
.\mvnw.cmd clean test
```

Báo cáo chi tiết dạng HTML được tạo tại:
- Core: `my-big-number-core/target/site/jacoco/index.html`
- Web & API: `my-big-number-web/target/site/jacoco/index.html`
