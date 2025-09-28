package com.winediary.entity;

public enum WineType {
    RED_WINE("Red Wine"),
    WHITE_WINE("White Wine"),
    ROSE_WINE("Rosé Wine"),
    SPARKLING_WINE("Sparkling Wine"),
    CHAMPAGNE("Champagne"),
    DESSERT_WINE("Dessert Wine"),
    FORTIFIED_WINE("Fortified Wine");

    private final String displayName;

    WineType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}