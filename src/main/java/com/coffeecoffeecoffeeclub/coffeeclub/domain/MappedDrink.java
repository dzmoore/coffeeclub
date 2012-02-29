package com.coffeecoffeecoffeeclub.coffeeclub.domain;

import java.io.Serializable;
import java.util.Date;

import com.coffeecoffeecoffeeclub.coffeeclub.util.Constants;

public class MappedDrink implements Serializable {
	private static final long serialVersionUID = -3229170050086695930L;

	private int drinkId;
	private MappedUser user;
	private MappedDrinkType type;
	private Date dateConsumed;
	private float cost;
	
	public MappedDrink() {
	    drinkId = Constants.INVALID_ID;
	    user = new MappedUser();
	    type = new MappedDrinkType();
	    dateConsumed = new Date();
	    cost = 0f;
    }

    public int getDrinkId() {
        return drinkId;
    }

    public void setDrinkId(int drinkId) {
        this.drinkId = drinkId;
    }

    public MappedUser getUser() {
        return user;
    }

    public void setUser(MappedUser user) {
        this.user = user;
    }

    public MappedDrinkType getType() {
        return type;
    }

    public void setType(MappedDrinkType type) {
        this.type = type;
    }

    public Date getDateConsumed() {
        return dateConsumed;
    }

    public void setDateConsumed(Date dateConsumed) {
        this.dateConsumed = dateConsumed;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MappedDrink [drinkId=");
        builder.append(drinkId);
        builder.append(", user=");
        builder.append(user);
        builder.append(", type=");
        builder.append(type);
        builder.append(", dateConsumed=");
        builder.append(dateConsumed);
        builder.append(", cost=");
        builder.append(cost);
        builder.append("]");
        return builder.toString();
    }
}
