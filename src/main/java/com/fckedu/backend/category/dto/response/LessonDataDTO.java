package com.fckedu.backend.category.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonDataDTO {
    private String id;
    private String name;
    private List<BankStatDTO> bankStats;
}
