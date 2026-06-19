package com.fckedu.exam_creation.common.dto.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RTPayload {
    private String jti;
    private String id;
    private String email;
    private String role;
}
