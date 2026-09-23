package com.bignumber.workorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.Instant;
import java.util.Objects;

/**
 * DRAFT GENERATION (Bản mã nháp kiểm chứng)
 * Task: POST /api/workorders handler
 * Context files: docs/coding-rules.md, docs/api-rules.md, docs/security-rules.md
 * Constraints:
 *  - Do not invent extra JSON fields not specified in requirements
 *  - Use standard validation (@Valid, @NotBlank, etc.)
 *  - Return 400 on error (Problem Details RFC 7807)
 *  - Constructor injection
 *  - No variables declared inside loops
 *  - No hardcoded secrets, no PII in logs
 */

// ============================================================================
// 1. DTOs: TUÂN THỦ NGHIÊM NGẶT SCHEMA (STRICT SCHEMA CONFORMANCE)
// ============================================================================

/**
 * Request tạo mới WorkOrder.
 * Chỉ gồm các trường tối thiểu trong đặc tả: title, description, priority.
 */
record CreateWorkOrderRequest(
        String title,
        String description,
        String priority
) {
    public CreateWorkOrderRequest {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Trường 'title' không được để trống.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Trường 'description' không được để trống.");
        }
        if (priority == null || priority.isBlank()) {
            throw new IllegalArgumentException("Trường 'priority' không được để trống.");
        }
    }
}

/**
 * Response trả về khi tạo thành công WorkOrder (201 Created).
 * Chỉ gồm: id, title, description, priority, status, createdAt.
 */
record WorkOrderResponse(
        String id,
        String title,
        String description,
        String priority,
        String status,
        Instant createdAt
) {}

/**
 * Đối tượng lỗi chuẩn RFC 7807 Problem Details.
 */
record ProblemDetails(
        URI type,
        String title,
        int status,
        String detail,
        URI instance
) {}

// ============================================================================
// 2. SERVICE LAYER (DEPENDENCY INJECTION QUA CONSTRUCTOR, KHÔNG BIẾN TRONG LOOP)
// ============================================================================

interface WorkOrderRepository {
    WorkOrder save(WorkOrder order);
}

class WorkOrder {
    private final String id;
    private final String title;
    private final String description;
    private final String priority;
    private final String status;
    private final Instant createdAt;

    public WorkOrder(String id, String title, String description, String priority, String status, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}

class WorkOrderService {
    private static final Logger log = LoggerFactory.getLogger(WorkOrderService.class);
    private final WorkOrderRepository repository;

    // Rule: Constructor Injection
    public WorkOrderService(WorkOrderRepository repository) {
        this.repository = Objects.requireNonNull(repository, "WorkOrderRepository không được là null");
    }

    public WorkOrderResponse createWorkOrder(CreateWorkOrderRequest request) {
        // Logging an toàn: KHÔNG log PII hoặc dữ liệu nhạy cảm
        log.info("Xử lý tạo mới WorkOrder với mức độ ưu tiên: {}", request.priority());

        String generatedId = "WO-" + System.currentTimeMillis();
        WorkOrder entity = new WorkOrder(
                generatedId,
                request.title(),
                request.description(),
                request.priority(),
                "OPEN",
                Instant.now()
        );

        WorkOrder saved = repository.save(entity);

        return new WorkOrderResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getPriority(),
                saved.getStatus(),
                saved.getCreatedAt()
        );
    }
}

// ============================================================================
// 3. HANDLER / CONTROLLER LAYER (POST /api/workorders, RETURN PROBLEM DETAILS)
// ============================================================================

public class ScratchHandler {

    private static final Logger log = LoggerFactory.getLogger(ScratchHandler.class);
    private final WorkOrderService workOrderService;

    // Rule: Constructor Injection
    public ScratchHandler(WorkOrderService workOrderService) {
        this.workOrderService = Objects.requireNonNull(workOrderService, "WorkOrderService không được là null");
    }

    /**
     * Endpoint: POST /api/workorders
     * Xử lý request, trả về 201 Created khi thành công hoặc 400 Problem Details khi lỗi.
     */
    public Object handleCreateWorkOrder(CreateWorkOrderRequest request, String requestPath) {
        // Fail-fast validation
        if (request == null) {
            log.warn("Cảnh báo: Request body là null tại đường dẫn {}", requestPath);
            return new ProblemDetails(
                    URI.create("https://example.com/probs/bad-request"),
                    "Bad Request",
                    400,
                    "Payload yêu cầu không được để trống.",
                    URI.create(requestPath)
            );
        }

        try {
            WorkOrderResponse response = workOrderService.createWorkOrder(request);
            log.info("Tạo WorkOrder thành công với ID: {}", response.id());
            return response;
        } catch (IllegalArgumentException ex) {
            log.warn("Dữ liệu đầu vào không hợp lệ: {}", ex.getMessage());
            return new ProblemDetails(
                    URI.create("https://example.com/probs/invalid-input"),
                    "Invalid Request Data",
                    400,
                    ex.getMessage(),
                    URI.create(requestPath)
            );
        } catch (Exception ex) {
            // Không để lộ internal stacktrace ra client (Security Rule 5)
            log.error("Lỗi hệ thống khi tạo WorkOrder", ex);
            return new ProblemDetails(
                    URI.create("https://example.com/probs/internal-error"),
                    "Internal Server Error",
                    500,
                    "Đã xảy ra lỗi trong quá trình xử lý, vui lòng thử lại sau.",
                    URI.create(requestPath)
            );
        }
    }
}
