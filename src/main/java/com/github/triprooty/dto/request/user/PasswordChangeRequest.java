package com.github.triprooty.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class PasswordChangeRequest {
    @NotBlank(message = "기존 비밀번호는 필수입니다.")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-]).{8,20}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함한 8~20자여야 합니다."
    )    private String newPassword;
}
