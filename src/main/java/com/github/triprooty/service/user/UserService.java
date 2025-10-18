package com.github.triprooty.service.user;


import com.github.triprooty.domain.User;
import com.github.triprooty.dto.request.user.UserInfoRequest;
import com.github.triprooty.dto.response.user.UserInfoResponse;
import com.github.triprooty.global.exception.AppException;
import com.github.triprooty.global.exception.file.FileErrorCode;
import com.github.triprooty.global.exception.user.UserErrorCode;
import com.github.triprooty.global.exception.user.UserNotFoundException;
import com.github.triprooty.repository.UserRepository;
import com.github.triprooty.service.external.GcsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
    private final GcsService gcsService;

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
//    @Transactional
//    public void changeUserPassword(UUID userId, String newPassword){
//        User user=getUserOrThrow(userId);
//        if(passwordEncoder.matches(newPassword,user.getPassword())){
//            throw new AppException(UserErrorCode.SAME_AS_OLD_PASSWORD); //USER-015
//        }
//        user.updatePassword(passwordEncoder.encode(newPassword));
//    }

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

        if (request.getProfileImage()!=null) {
            final String newUrl = request.getProfileImage().trim();

            // 1) 빈 문자열이면 이미지 제거
            if (newUrl.isEmpty()) {
                if (user.getProfileImage()!=null && !user.getProfileImage().isBlank()) {
                    String oldObj = gcsService.toObjectName(user.getProfileImage());
                    if (oldObj.startsWith("profiles/")) {
                        gcsService.delete(oldObj); // 기존 최종 이미지 정리
                    }
                }
                user.updateProfileImageUrl(null);
                return;
            }

            // 2) temp 경로(내 userId)에서 올라온 경우 → 확정 저장으로 승격
            String obj = gcsService.toObjectName(newUrl);
            if (obj.startsWith("temp/profile/" + user.getId())) {
                confirmProfileImage(user, newUrl); // 내부에서 copy → temp 삭제 → user.profileImage 갱신
                return;
            }

            // 3) 이미 최종 URL이거나(GCS/profiles 또는 외부 URL) 직접 설정한 경우
            if (!newUrl.equals(user.getProfileImage())) {
                // 기존 최종 이미지가 GCS profiles/*면 정리
                if (user.getProfileImage()!=null && !user.getProfileImage().isBlank()) {
                    String oldObj = gcsService.toObjectName(user.getProfileImage());
                    if (oldObj.startsWith("profiles/")) {
                        gcsService.delete(oldObj);
                    }
                }
                user.updateProfileImageUrl(newUrl);
            }
        }

    }

    @Transactional
    public void confirmProfileImage(User user, String tempUrl) {
        String tempObj = gcsService.toObjectName(tempUrl);

        if (!tempObj.startsWith("temp/profile/" + user.getId())) {
            throw new AppException(FileErrorCode.PERMISSION_DENIED);
        }

        String finalObj = "profiles/" + user.getId() + "/" + UUID.randomUUID() + "_" + System.currentTimeMillis();
        gcsService.copy(tempObj, finalObj);
        gcsService.delete(tempObj);

        String finalUrl = gcsService.publicUrlOf(finalObj);

        // 기존 이미지가 있으면 정리(선택)
        if (user.getProfileImage() != null && !user.getProfileImage().isBlank()) {
            String oldObj = gcsService.toObjectName(user.getProfileImage());
            if (oldObj.startsWith("profiles/")) {
                gcsService.delete(oldObj);
            }
        }

        user.updateProfileImageUrl(finalUrl);
    }

    /**
     * 프로필 이미지 업로드
     */
    @Transactional
    public String uploadTempProfileImage(UUID userId, MultipartFile file) throws IOException {
        getUserOrThrow(userId);
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        // 간단 유효성(선택) : 이미지만 허용
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new AppException(FileErrorCode.INVALID_TYPE); // 커스텀 코드 추천
        }

        String objectName = "temp/profile/" + userId + "/" + UUID.randomUUID();
        return gcsService.uploadPublic(objectName, contentType, file.getBytes()); // 공개 URL 반환
    }

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
