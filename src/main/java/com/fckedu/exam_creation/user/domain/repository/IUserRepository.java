package com.fckedu.exam_creation.user.domain.repository;

import com.fckedu.exam_creation.user.domain.entity.UserEntity;

import java.util.Optional;

public interface IUserRepository {
    Optional<UserEntity> findByEmail(String email);

    UserEntity save(UserEntity newUser);

    UserEntity findById(String userId);

    String updateAvatar(String userId, String s3Key);
}
