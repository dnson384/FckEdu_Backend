package com.fckedu.exam_creation.user.controller;

import com.fckedu.exam_creation.user.dto.request.LoginUserRequestDTO;
import com.fckedu.exam_creation.user.dto.request.NewUserRequestDTO;
import com.fckedu.exam_creation.user.dto.response.AuthorizedResponseDTO;
import com.fckedu.exam_creation.user.dto.response.UserResponseDTO;
import com.fckedu.exam_creation.user.usecase.UserUsecase;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserUsecase userUsecase;

    public UserController(UserUsecase userUsecase) {
        this.userUsecase = userUsecase;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthorizedResponseDTO> register(@RequestBody NewUserRequestDTO newUser) {
        AuthorizedResponseDTO dto = userUsecase.register(newUser);
        return ResponseEntity.ok(dto);
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
                dto.getAccessToken()
        );
        refreshToken.setHttpOnly(true);
        refreshToken.setMaxAge(15 * 60);
        refreshToken.setPath("/");
        // refreshToken.setSecure(true);

        response.addCookie(refreshToken);

        return ResponseEntity.ok(dto.getUser());
    }

    @GetMapping("/me")
    public UserResponseDTO getMe(@RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.substring(7);
        return userUsecase.getMe(accessToken);
    }
}
