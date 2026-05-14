package com.fckedu.exam_creation.common.dto.category;

import java.util.List;

public class NewLessonDataDTO {
    List<NewBankStatDTO> bankStats;
    private String name;

    public NewLessonDataDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NewBankStatDTO> getBankStats() {
        return bankStats;
    }

    public void setBankStats(List<NewBankStatDTO> bankStats) {
        this.bankStats = bankStats;
    }
}
