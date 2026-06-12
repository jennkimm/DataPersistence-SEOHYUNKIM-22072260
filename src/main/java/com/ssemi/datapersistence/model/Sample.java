package com.ssemi.datapersistence.model;

public class Sample {
    private String id;
    private String name;
    private int averageProductionTime;  // 단위: 분
    private double yieldRate;           // 0.0 ~ 1.0
    private int inventory;

    public Sample() {}

    public Sample(String id, String name, int averageProductionTime,
                  double yieldRate, int inventory) {
        this.id = id;
        this.name = name;
        this.averageProductionTime = averageProductionTime;
        this.yieldRate = yieldRate;
        this.inventory = inventory;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAverageProductionTime() { return averageProductionTime; }
    public void setAverageProductionTime(int averageProductionTime) {
        this.averageProductionTime = averageProductionTime;
    }

    public double getYieldRate() { return yieldRate; }
    public void setYieldRate(double yieldRate) { this.yieldRate = yieldRate; }

    public int getInventory() { return inventory; }
    public void setInventory(int inventory) { this.inventory = inventory; }
}