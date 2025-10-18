package com.github.triprooty.dto.request.user;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserInfoRequest {
    @Size(max=50)
    private String name;
    private String profileImage;
    private Boolean locationTracing;
    private Boolean alarm;
}
