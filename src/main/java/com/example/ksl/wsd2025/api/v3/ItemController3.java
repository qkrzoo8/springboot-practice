package com.example.ksl.wsd2025.api.v3;
import com.example.ksl.wsd2025.api.dto.ApiResponse;

import com.example.ksl.wsd2025.api.dto.ItemDto;
import com.example.ksl.wsd2025.api.request.ItemCreateRequest;
import com.example.ksl.wsd2025.api.request.ItemUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v3/items")
public class ItemController3 {

    // 메모리용 임시 DB (예제용)
    private final Map<Long, ItemDto> store = new HashMap<>();
    private long sequence = 1L;

    // 1) GET: 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getItems(
            @RequestParam(defaultValue = "none") String simulation
    ) {
        // 과제 테스트용: 서버 내부 오류 상황 재현
        if ("error".equals(simulation)) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("error",
                            "실습용: 서버 내부 오류가 발생했습니다."));
        }

        // 과제 테스트용: 서비스 일시 중단 상황 재현
        if ("maintenance".equals(simulation)) {
            return ResponseEntity.status(503)
                    .header("Retry-After", "60")
                    .body(new ApiResponse<>("error",
                            "실습용: 서비스 점검 중입니다."));
        }

        return ResponseEntity.ok(
                new ApiResponse<>("success", new ArrayList<>(store.values()))
        );
    }
    // 1) GET: 단건 조회 (PathVariable 사용)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getItem(@PathVariable Long id) {
        ItemDto item = store.get(id);

        if (item == null) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>("error", "해당 상품이 없습니다."));
        }

        return ResponseEntity.ok(
                new ApiResponse<>("success", item)
        );
    }

    // 1) POST: 생성
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createItem(
            @RequestBody ItemCreateRequest request
    ) {
        if (request.getName() == null || request.getName().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("error", "상품 이름을 입력해주세요."));
        }

        if (request.getPrice() == null || request.getPrice() < 0) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("error", "가격은 0 이상이어야 합니다."));
        }

        ItemDto item = new ItemDto();
        item.setId(sequence++);
        item.setName(request.getName());
        item.setPrice(request.getPrice());

        store.put(item.getId(), item);

        return ResponseEntity.status(201)
                .body(new ApiResponse<>("success", item));
    }

    // 1) PUT: 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateItem(
            @PathVariable Long id,
            @RequestBody ItemUpdateRequest request
    ) {
        ItemDto item = store.get(id);

        if (item == null) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>("error", "해당 상품이 없습니다."));
        }

        if (request.getName() == null || request.getName().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("error", "상품 이름을 입력해주세요."));
        }

        if (request.getPrice() == null || request.getPrice() < 0) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("error", "가격은 0 이상이어야 합니다."));
        }

        item.setName(request.getName());
        item.setPrice(request.getPrice());

        return ResponseEntity.ok(new ApiResponse<>("success", item));
    }

    // 2) PUT: 상품 가격만 수정
    @PutMapping("/{id}/price")
    public ResponseEntity<ApiResponse<?>> updateItemPrice(
            @PathVariable Long id,
            @RequestBody ItemUpdateRequest request
    ) {
        ItemDto item = store.get(id);

        // 상품이 없는 경우 → 404
        if (item == null) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>("error", "해당 상품이 없습니다."));
        }

        // 가격이 없거나 0 이하인 경우 → 400
        if (request.getPrice() == null || request.getPrice() < 0) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("error", "가격은 0 이상이어야 합니다."));
        }

        item.setPrice(request.getPrice());

        return ResponseEntity.ok(
                new ApiResponse<>("success", item)
        );
    }
    // 1) DELETE: 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteItem(@PathVariable Long id) {
        ItemDto removed = store.remove(id);

        if (removed == null) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>("error", "해당 상품이 없습니다."));
        }

        return ResponseEntity.ok(
                new ApiResponse<>("success", removed)
        );
    }

    // 2) DELETE: 전체 상품 삭제
    @DeleteMapping("/all")
    public ResponseEntity<ApiResponse<?>> deleteAllItems() {

        if (store.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>("error", "삭제할 상품이 없습니다."));
        }

        int count = store.size();
        store.clear();

        return ResponseEntity.ok(
                new ApiResponse<>("success", count + "개의 상품을 삭제했습니다.")
        );
    }

    @PostMapping("/{id}/copy")
    public ResponseEntity<ApiResponse<?>> copyItem(@PathVariable Long id) {
        ItemDto original = store.get(id);

        if (original == null) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>("error", "해당 상품이 없습니다."));
        }

        ItemDto copied = new ItemDto();
        copied.setId(sequence++);
        copied.setName(original.getName() + " (복사본)");
        copied.setPrice(original.getPrice());

        store.put(copied.getId(), copied);

        return ResponseEntity.status(201)
                .body(new ApiResponse<>("success", copied));
    }

}
