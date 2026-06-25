package com.fckedu.exam_creation.user.usecase;

import com.fckedu.exam_creation.common.dto.user.response.CommonUserResponseAllDTO;
import com.fckedu.exam_creation.common.dto.user.response.CommonUserResponseDTO;
import com.fckedu.exam_creation.common.exception.NotFoundException;
import com.fckedu.exam_creation.security.service.SecurityService;
import com.fckedu.exam_creation.user.domain.entity.UserEntity;
import com.fckedu.exam_creation.user.dto.mapper.UserDTOMapper;
import com.fckedu.exam_creation.user.infrastructure.repository.UserRepositoryImpl;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepositoryImpl repo;
    private final UserDTOMapper mapper;
    private final SecurityService securityService;

    public UserService(UserRepositoryImpl repo, UserDTOMapper mapper, SecurityService securityService) {
        this.repo = repo;
        this.mapper = mapper;
        this.securityService = securityService;
    }

    public CommonUserResponseAllDTO findByEmail(String email) {
        return repo.findByEmail(email)
                .map(mapper::toCommonAllDTO)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));
    }

    public CommonUserResponseAllDTO findById(String userId) {
        return mapper.toCommonAllDTO(repo.findById(userId));
    }

    public CommonUserResponseDTO getMe(String accessToken) {
        String userId = securityService.getPayloadFromAccessToken(accessToken).getUserId();
        UserEntity user = repo.findById(userId);
        return mapper.toCommonDTO(user);
    }
}
