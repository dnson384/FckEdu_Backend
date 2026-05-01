package com.fckedu.backend.common.dto;

import java.util.List;

public class NewLessonDataDTO {
    List<BankStatDTO> bankStats;
    private String name;

    public NewLessonDataDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BankStatDTO> getBankStats() {
        return bankStats;
    }

    public void setBankStats(List<BankStatDTO> bankStats) {
        this.bankStats = bankStats;
    }
}
