package com.github.triprooty.service.user;


import com.github.triprooty.domain.User;
import com.github.triprooty.dto.request.user.UserInfoRequest;
import com.github.triprooty.dto.response.user.UserInfoResponse;
import com.github.triprooty.global.exception.AppException;
import com.github.triprooty.global.exception.user.UserErrorCode;
import com.github.triprooty.global.exception.user.UserNotFoundException;
import com.github.triprooty.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 내 정보 조회
     */
    public UserInfoResponse getUserInfo(UUID userId){
        User user=getUserOrThrow(userId);
        return UserInfoResponse.from(user);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changeUserPassword(UUID userId, String newPassword){
        User user=getUserOrThrow(userId);
        if(passwordEncoder.matches(newPassword,user.getPassword())){
            throw new AppException(UserErrorCode.SAME_AS_OLD_PASSWORD); //USER-015
        }
        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    /**
     * 내 정보 수정
     */
    @Transactional
    public void updateUserInfo(UUID userId, UserInfoRequest request){
        User user=getUserOrThrow(userId);
        if(request.getName()==null&&
        request.getLocationTracing()==null&&
        request.getProfileImage()==null&&
        request.getAlarm()==null){
            throw new AppException(UserErrorCode.INVALID_PROFILE_UPDATE); //USER-014
        }
        if(request.getName()!=null){
            if(!request.getName().equals(user.getName())&&
            userRepository.existsByName(request.getName())){
                throw new AppException(UserErrorCode.DUPLICATE_NICKNAME); // USER-007
            }
            user.setName(request.getName());
        }
        if(request.getLocationTracing()!=null){user.setLocationTracing(request.getLocationTracing());}
        if(request.getAlarm()!=null){user.setAlarm(request.getAlarm());}

        //GCS 한 뒤에 수정 예정
        if(request.getProfileImage()!=null){user.setProfileImage(request.getProfileImage());}

    }

    /**
     * 프로필 이미지 업로드
     */

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void deleteUser(UUID userId){
        User user=getUserOrThrow(userId);

        //토큰 무효화

        user.softDelete();
    }


    public User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new); //USER-001
    }

}
