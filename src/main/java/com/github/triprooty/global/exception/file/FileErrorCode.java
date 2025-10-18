package com.github.triprooty.global.exception.file;

import com.github.triprooty.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode {
    UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.", "FILE-001"),
    DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다.", "FILE-002"),
    COPY_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 복사에 실패했습니다.", "FILE-003"),
    DOWNLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드에 실패했습니다.", "FILE-004"),
    INVALID_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다. (허용: jpg, jpeg, png)", "FILE-005"),
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "이 작업을 수행할 권한이 없습니다.", "FILE-006");


    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
