package com.fckedu.exam_creation.user.controller;

import com.fckedu.exam_creation.common.dto.user.response.CommonUserResponseDTO;
import com.fckedu.exam_creation.user.dto.request.LoginUserRequestDTO;
import com.fckedu.exam_creation.user.dto.request.NewUserRequestDTO;
import com.fckedu.exam_creation.user.dto.response.AuthorizedResponseDTO;
import com.fckedu.exam_creation.user.dto.response.UserResponseDTO;
import com.fckedu.exam_creation.user.usecase.UserService;
import com.fckedu.exam_creation.user.usecase.UserUsecase;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserUsecase userUsecase;
    private final UserService userService;

    public UserController(UserUsecase userUsecase, UserService userService) {
        this.userUsecase = userUsecase;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody NewUserRequestDTO newUser, HttpServletResponse response) {
        AuthorizedResponseDTO dto = userUsecase.register(newUser);

        Cookie accessToken = new Cookie(
                "accessToken",
                dto.getAccessToken()
        );
        accessToken.setHttpOnly(true);
        accessToken.setMaxAge(15 * 60);
        accessToken.setPath("/");
        // accessToken.setSecure(true);

        response.addCookie(accessToken);

        Cookie refreshToken = new Cookie(
                "refeshToken",
                dto.getRefreshToken()
        );
        refreshToken.setHttpOnly(true);
        refreshToken.setMaxAge(15 * 60);
        refreshToken.setPath("/");
        // refreshToken.setSecure(true);

        response.addCookie(refreshToken);

        return ResponseEntity.ok(dto.getUser());
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody LoginUserRequestDTO payload, HttpServletResponse response) {
        AuthorizedResponseDTO dto = userUsecase.login(payload);

        Cookie accessToken = new Cookie(
                "accessToken",
                dto.getAccessToken()
        );
        accessToken.setHttpOnly(true);
        accessToken.setMaxAge(15 * 60);
        accessToken.setPath("/");
        // accessToken.setSecure(true);

        response.addCookie(accessToken);

        Cookie refreshToken = new Cookie(
                "refeshToken",
                dto.getRefreshToken()
        );
        refreshToken.setHttpOnly(true);
        refreshToken.setMaxAge(15 * 60);
        refreshToken.setPath("/");
        // refreshToken.setSecure(true);

        response.addCookie(refreshToken);

        return ResponseEntity.ok(dto.getUser());
    }

    @PostMapping("/logout")
    public ResponseEntity<Boolean> logout(@RequestBody String refreshToken) {
        return ResponseEntity.ok(userUsecase.logout(refreshToken));
    }

    @GetMapping("/me")
    public CommonUserResponseDTO getMe(@RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.substring(7);
        return userService.getMe(accessToken);
    }
}
