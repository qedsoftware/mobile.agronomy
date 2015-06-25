package com.afsis.yieldestimator.crops;

import com.afsis.yieldestimator.crops.GrowthStage;

public enum MaizeGrowthStage implements GrowthStage {

    V2("V2", 1),
    V4("V4", 1),
    V6("V6", 1),
    V8("V8", 1),
    V12("V12", 1),
    V16("V16", 1),
    V18("V18", 1),
    VT("VT", 1),
    R1("R1", 1),
    R2("R2", 1),
    R4("R4", 1),
    R6("R6", 1),;

    private String growthStage;
    private double adjustmentFactor;

    MaizeGrowthStage(String growthStage, double adjustmentFactor) {
        this.growthStage = growthStage;
        this.adjustmentFactor = adjustmentFactor;
    }

    public String getGrowthStage() {
        return growthStage;
    }

    public double getAdjustmentFactor() {
        return adjustmentFactor;
    }

    @Override
    public String toString() {
        return growthStage;
    }
}
