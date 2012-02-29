package com.coffeecoffeecoffeeclub.coffeeclub.domain;

import com.coffeecoffeecoffeeclub.coffeeclub.util.Constants;

public class MappedDrinkType {
    private int typeId;
    private String name;
    private MappedUser createdBy;
    private String description;
    private double costPerML;
    
    public MappedDrinkType() {
        typeId = Constants.INVALID_ID;
        createdBy = new MappedUser();
        description = "";
        costPerML = 0;
        name = "";
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MappedUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(MappedUser createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCostPerML() {
        return costPerML;
    }

    public void setCostPerML(double costPerML) {
        this.costPerML = costPerML;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MappedDrinkType [typeId=");
        builder.append(typeId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", createdBy=");
        builder.append(createdBy);
        builder.append(", description=");
        builder.append(description);
        builder.append(", costPerML=");
        builder.append(costPerML);
        builder.append("]");
        return builder.toString();
    }
    
    
}
