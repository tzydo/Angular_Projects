package com.pl.skijumping.domain.entity;

public enum MaritalStatus {
    MARIED("maried"),
    SINGLE("single");

    private String text;

    private MaritalStatus(String text) {
        this.text = text;
    }

    public static MaritalStatus getStatus(String text) {
        MaritalStatus value = null;
        for (MaritalStatus maritalStatus : values()) {
            if (maritalStatus.text.equalsIgnoreCase(text)) {
                value = maritalStatus;
            }
        }
        return value;
    }
}
