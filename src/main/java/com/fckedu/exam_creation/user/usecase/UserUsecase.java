package com.fckedu.exam_creation.user.usecase;

import com.fckedu.exam_creation.common.dto.refreshToken.request.NewRTRequestDTO;
import com.fckedu.exam_creation.common.dto.token.ATPayload;
import com.fckedu.exam_creation.common.dto.token.RTPayload;
import com.fckedu.exam_creation.common.exception.InternalServerException;
import com.fckedu.exam_creation.common.exception.NotFoundException;
import com.fckedu.exam_creation.common.exception.UnAuthorizedException;
import com.fckedu.exam_creation.refreshToken.usecase.RefreshTokenService;
import com.fckedu.exam_creation.security.service.SecurityService;
import com.fckedu.exam_creation.user.domain.entity.UserEntity;
import com.fckedu.exam_creation.user.dto.mapper.UserDTOMapper;
import com.fckedu.exam_creation.user.dto.request.LoginUserRequestDTO;
import com.fckedu.exam_creation.user.dto.request.NewUserRequestDTO;
import com.fckedu.exam_creation.user.dto.response.AuthorizedResponseDTO;
import com.fckedu.exam_creation.user.dto.response.UserResponseDTO;
import com.fckedu.exam_creation.user.infrastructure.repository.UserRepositoryImpl;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserUsecase {
    private final UserRepositoryImpl repo;
    private final UserDTOMapper mapperDTO;
    private final SecurityService securityService;
    private final RefreshTokenService refreshTokenService;

    public UserUsecase(UserRepositoryImpl repo, UserDTOMapper mapperDTO, SecurityService securityService, RefreshTokenService refreshTokenService) {
        this.repo = repo;
        this.mapperDTO = mapperDTO;
        this.securityService = securityService;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthorizedResponseDTO register(NewUserRequestDTO newUser) {
        String hashedPassword = securityService.hashPassword(newUser.getPlainPassword());
        UserEntity newUserEntity = new UserEntity(
                null,
                newUser.getEmail(),
                hashedPassword,
                newUser.getUsername(),
                "ROLE_TEACHER",
                newUser.getLoginMethod(),
                "",
                true
        );

        UserEntity createdUser = repo.save(newUserEntity);

        UserResponseDTO user = mapperDTO.toUserResponseDTO(createdUser);

        // AT
        ATPayload accessTokenPayload = new ATPayload(
                user.getId(), user.getEmail(), user.getRole()
        );
        String accessToken = securityService.generateAccessToken(accessTokenPayload);

        // RT
        String jti = UUID.randomUUID().toString();
        RTPayload refreshTokenPayload = new RTPayload(
                jti, user.getId(), user.getEmail(), user.getRole()
        );
        String refreshToken = securityService.generateRefreshToken(refreshTokenPayload);


        // Luu RT
        NewRTRequestDTO newRTRequestDTO = securityService.parseNewRefreshToken(refreshToken);
        boolean saveNewRT = refreshTokenService.save(newRTRequestDTO);

        if (!saveNewRT) {
            throw new InternalServerException("Lỗi trong quá trình lưu RT!");
        }

        return new AuthorizedResponseDTO(
                user, accessToken, refreshToken
        );
    }

    public AuthorizedResponseDTO login(LoginUserRequestDTO payload) {
        UserEntity user = repo.findByEmail(payload.getEmail())
                .orElseThrow(() -> new NotFoundException("Tài khoản chưa tồn tại"));

        if (user.getLoginMethod().equals("GOOGLE")) {
            throw new UnAuthorizedException("Sai phương thức đăng nhập");
        }

        // Validate password
        if (!securityService.validatePassword(payload.getPlainPassword(), user.getHashedPassword())) {
            throw new UnAuthorizedException("Mật khẩu không chính xác");
        }

        UserResponseDTO userDto = mapperDTO.toUserResponseDTO(user);

        // AT
        ATPayload accessTokenPayload = new ATPayload(
                user.getId(), user.getEmail(), user.getRole()
        );
        String accessToken = securityService.generateAccessToken(accessTokenPayload);

        // RT
        String jti = UUID.randomUUID().toString();
        RTPayload refreshTokenPayload = new RTPayload(
                jti, user.getId(), user.getEmail(), user.getRole()
        );
        String refreshToken = securityService.generateRefreshToken(refreshTokenPayload);

        // Luu RT
        NewRTRequestDTO newRTRequestDTO = securityService.parseNewRefreshToken(refreshToken);
        boolean saveNewRT = refreshTokenService.save(newRTRequestDTO);

        if (!saveNewRT) {
            throw new InternalServerException("Lỗi trong quá trình lưu RT!");
        }

        return new AuthorizedResponseDTO(
                userDto, accessToken, refreshToken
        );
    }

    public UserResponseDTO getMe(String accessToken) {
        String userId = securityService.getPayloadFromAccessToken(accessToken).getUserId();
        UserEntity user = repo.findById(userId);
        return mapperDTO.toUserResponseDTO(user);
    }
}
