package com.fckedu.exam_creation.user.controller;

import com.fckedu.exam_creation.user.dto.request.LoginUserRequestDTO;
import com.fckedu.exam_creation.user.dto.request.NewUserRequestDTO;
import com.fckedu.exam_creation.user.dto.response.AuthorizedResponseDTO;
import com.fckedu.exam_creation.user.usecase.UserUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<AuthorizedResponseDTO> login(@RequestBody LoginUserRequestDTO payload) {
        AuthorizedResponseDTO dto = userUsecase.login(payload);
        return ResponseEntity.ok(dto);
    }
}
