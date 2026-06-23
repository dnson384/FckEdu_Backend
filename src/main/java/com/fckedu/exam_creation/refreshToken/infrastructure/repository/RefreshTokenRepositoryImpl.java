package com.fckedu.exam_creation.refreshToken.infrastructure.repository;

import com.fckedu.exam_creation.common.exception.NotFoundException;
import com.fckedu.exam_creation.refreshToken.domain.entity.RefreshTokenEntity;
import com.fckedu.exam_creation.refreshToken.infrastructure.document.RefreshTokenDocument;
import com.fckedu.exam_creation.refreshToken.infrastructure.mapper.RefreshTokenMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokenRepositoryImpl {
    private final MongoTemplate mongoTemplate;
    private final RefreshTokenMapper mapper;

    public RefreshTokenRepositoryImpl(MongoTemplate mongoTemplate, RefreshTokenMapper mapper) {
        this.mongoTemplate = mongoTemplate;
        this.mapper = mapper;
    }

    public boolean save(RefreshTokenEntity newEntity) {
        RefreshTokenDocument document = mapper.toDocument(newEntity);
        RefreshTokenDocument savedRefreshToken = mongoTemplate.save(document);
        return savedRefreshToken.getId() != null;
    }

    public RefreshTokenEntity getRefreshTokenByJti(String jti, String userId) {
        Criteria criteria = new Criteria();
        criteria.andOperator(
                Criteria.where("jti").is(jti),
                Criteria.where("userId").is(userId)
        );
        
        Query query = new Query(criteria);

        RefreshTokenDocument doc = mongoTemplate.findOne(query, RefreshTokenDocument.class);

        if (doc == null) {
            throw new NotFoundException("Không tìm thấy RT");
        }
        return mapper.toEntity(doc);
    }
}
