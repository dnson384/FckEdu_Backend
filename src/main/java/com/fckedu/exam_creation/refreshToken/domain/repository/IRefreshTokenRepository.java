package com.fckedu.exam_creation.refreshToken.domain.repository;

import com.fckedu.exam_creation.refreshToken.domain.entity.RefreshTokenEntity;

public interface IRefreshTokenRepository {
    boolean save(RefreshTokenEntity newEntity);
}
