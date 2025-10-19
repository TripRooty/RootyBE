package com.github.triprooty.controller;

import com.github.triprooty.dto.request.user.PasswordChangeRequest;
import com.github.triprooty.dto.request.user.UserInfoRequest;
import com.github.triprooty.dto.response.user.UserInfoResponse;
import com.github.triprooty.controller.docs.UserSwaggerSpec;
import com.github.triprooty.global.dto.DataResponse;
import com.github.triprooty.global.security.UserPrincipal;
import com.github.triprooty.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserSwaggerSpec {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<DataResponse<UserInfoResponse>> getMyInfo(
            @AuthenticationPrincipal UserPrincipal userDetails){
        UserInfoResponse response=userService.getUserInfo(userDetails.getId());
        return ResponseEntity.ok(DataResponse.from(response));
    }

    @PatchMapping("/me")
    public ResponseEntity<DataResponse<Void>> updateUserInfo(
            @AuthenticationPrincipal UserPrincipal userDetails,
            @Valid @RequestBody UserInfoRequest request){
        userService.updateUserInfo(userDetails.getId(), request);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @PatchMapping("/password")
    public ResponseEntity<DataResponse<Void>> updatePassword(
            @AuthenticationPrincipal UserPrincipal userDetails,
            @Valid @RequestBody PasswordChangeRequest request){
        userService.changeUserPassword(userDetails.getId(),request);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<String>> uploadProfileImage(
            @AuthenticationPrincipal UserPrincipal userDetails
            , @RequestParam("file") MultipartFile file) throws IOException {
        String imageUrl = userService.uploadTempProfileImage(userDetails.getId(), file);
        return ResponseEntity.ok(DataResponse.from(imageUrl));
    }

    @DeleteMapping("/me")
    public ResponseEntity<DataResponse<Void>> deleteUser(
            @AuthenticationPrincipal UserPrincipal userDetails){
        userService.deleteUser(userDetails.getId());
        return ResponseEntity.ok(DataResponse.ok());
    }



}
