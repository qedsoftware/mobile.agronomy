package com.afsis.yieldestimator.crops;


public class Maize extends Crop {

    private int cobsPerUnitArea;
    private int rowsPerCob;
    private int kernelsPerRow;
    private MaizeGrowthStage growthStage;

    public int getCobsPerUnitArea() {
        return cobsPerUnitArea;
    }

    public void setCobsPerUnitArea(int cobsPerUnitArea) {
        this.cobsPerUnitArea = cobsPerUnitArea;
    }

    public int getRowsPerCob() {
        return rowsPerCob;
    }

    public void setRowsPerCob(int rowsPerCob) {
        this.rowsPerCob = rowsPerCob;
    }

    public MaizeGrowthStage getGrowthStage() {
        return growthStage;
    }

    public void setGrowthStage(MaizeGrowthStage growthStage) {
        this.growthStage = growthStage;
    }

    public void setKernelsPerRow(int kernelsPerRow) {
        this.kernelsPerRow = kernelsPerRow;
    }

    public int getKernelsPerRow() {
        return kernelsPerRow;
    }

    @Override
    public double estimateYield() {
        return cobsPerUnitArea * rowsPerCob * kernelsPerRow * growthStage.getAdjustmentFactor();
    }

}
