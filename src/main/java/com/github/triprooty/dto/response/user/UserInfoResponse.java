package com.github.triprooty.dto.response.user;

import com.github.triprooty.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponse {
    private String email;
    private String name;
    private String profileImage;
    private Boolean locationTracing;
    private Boolean alarm;

    public static UserInfoResponse from(User user) {
        return UserInfoResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .locationTracing(user.getLocationTracing())
                .alarm(user.getAlarm())
                .build();
    }
}
