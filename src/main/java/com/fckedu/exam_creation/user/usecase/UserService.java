package com.fckedu.exam_creation.user.usecase;

import com.fckedu.exam_creation.common.dto.user.response.CommonUserResponseDTO;
import com.fckedu.exam_creation.common.exception.NotFoundException;
import com.fckedu.exam_creation.user.dto.mapper.UserDTOMapper;
import com.fckedu.exam_creation.user.infrastructure.repository.UserRepositoryImpl;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepositoryImpl repo;
    private final UserDTOMapper mapper;

    public UserService(UserRepositoryImpl repo, UserDTOMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public CommonUserResponseDTO findByEmail(String email) {
        return repo.findByEmail(email)
                .map(mapper::toCommonDTO)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));
    }
}
