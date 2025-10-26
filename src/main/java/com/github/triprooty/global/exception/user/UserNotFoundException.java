package com.github.triprooty.global.exception.user;

import com.github.triprooty.global.exception.AppException;
import com.github.triprooty.repository.UserRepository;

public class UserNotFoundException extends AppException {
    public UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND);
    }
}
