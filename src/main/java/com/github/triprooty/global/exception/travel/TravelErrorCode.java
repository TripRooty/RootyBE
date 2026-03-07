package com.github.triprooty.global.exception.travel;

import com.github.triprooty.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TravelErrorCode implements ErrorCode {
    TRAVEL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 여행입니다.", "TRAVEL-001"),
    TRAVEL_DAY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 여행 일자입니다.", "TRAVEL-002"),
    ITINERARY_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 itinerary item입니다.", "TRAVEL-003"),
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 장소입니다.", "TRAVEL-004"),

    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "startDate는 endDate보다 늦을 수 없습니다.", "TRAVEL-005"),
    INVALID_DAY_REORDER_SIZE(HttpStatus.BAD_REQUEST, "dayIds의 개수가 올바르지 않습니다.", "TRAVEL-006"),
    INVALID_DAY_REORDER_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 dayId가 포함되어 있습니다.", "TRAVEL-007"),
    READ_ACCESS_DENIED(HttpStatus.BAD_REQUEST, "조회 권한이 없습니다.", "TRAVEL-008"),
    EDIT_ACCESS_DENIED(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.", "TRAVEL-009"),

    INVALID_ITEM_REORDER_SIZE(HttpStatus.BAD_REQUEST, "itemIds의 개수가 올바르지 않습니다.", "TRAVEL-010"),
    INVALID_ITEM_REORDER_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 itemId가 포함되어 있습니다.", "TRAVEL-011"),
    PLACE_ITEM_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "PLACE 타입 item만 수정할 수 있습니다.", "TRAVEL-012"),
    MOVE_ITEM_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "MOVE 타입 item만 수정할 수 있습니다.", "TRAVEL-013"),
    INVALID_ORDER_NO(HttpStatus.BAD_REQUEST, "orderNo는 1 이상이어야 합니다.", "TRAVEL-014"),
    ORDER_NO_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "orderNo가 현재 범위를 벗어났습니다.", "TRAVEL-015"),
    INVALID_DELETED_ORDER_NO(HttpStatus.BAD_REQUEST, "삭제된 orderNo가 유효하지 않습니다.", "TRAVEL-016"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.", "TRAVEL-017");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
