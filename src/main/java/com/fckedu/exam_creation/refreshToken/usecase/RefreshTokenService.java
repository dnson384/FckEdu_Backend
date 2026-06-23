package com.fckedu.exam_creation.refreshToken.usecase;

import com.fckedu.exam_creation.common.dto.refreshToken.request.NewRTRequestDTO;
import com.fckedu.exam_creation.refreshToken.domain.entity.RefreshTokenEntity;
import com.fckedu.exam_creation.refreshToken.infrastructure.repository.RefreshTokenRepositoryImpl;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepositoryImpl repo;

    public RefreshTokenService(RefreshTokenRepositoryImpl repo) {
        this.repo = repo;
    }

    public boolean save(NewRTRequestDTO newRT) {
        RefreshTokenEntity entity = new RefreshTokenEntity(
                null,
                newRT.getJti(),
                newRT.getUserId(),
                newRT.getExpiresAt(),
                newRT.getIssuedAt()
        );

        return repo.save(entity);
    }
}
