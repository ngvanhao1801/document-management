package com.example.demo.dto;

import lombok.Data;

@Data
public class ChartDTO {
    private String label;
    private int value;

    public ChartDTO(String label, int value) {
        this.label = label;
        this.value = value;
    }

}
