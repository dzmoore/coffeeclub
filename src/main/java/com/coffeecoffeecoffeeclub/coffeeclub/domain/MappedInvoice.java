package com.coffeecoffeecoffeeclub.coffeeclub.domain;

import java.util.ArrayList;
import java.util.List;

import com.coffeecoffeecoffeeclub.coffeeclub.util.Constants;

public class MappedInvoice {
    private int id;
    private MappedUser user;
    private float due;
    private float paid;
    private List<MappedDrink> drinks;
    
    public MappedInvoice() {
        id = Constants.INVALID_ID;
        user = new MappedUser();
        due = 0f;
        drinks = new ArrayList<MappedDrink>();
        paid = 0f;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MappedUser getUser() {
        return user;
    }

    public void setUser(MappedUser user) {
        this.user = user;
    }

    public float getDue() {
        return due;
    }

    public void setDue(float due) {
        this.due = due;
    }

    public List<MappedDrink> getDrinks() {
        return drinks;
    }

    public void setDrinks(List<MappedDrink> drinks) {
        this.drinks = drinks;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MappedInvoice [id=");
        builder.append(id);
        builder.append(", user=");
        builder.append(user);
        builder.append(", due=");
        builder.append(due);
        builder.append(", paid=");
        builder.append(getPaid());
        builder.append(", drinks=");
        builder.append(drinks);
        builder.append("]");
        return builder.toString();
    }

    public float getPaid() {
        return paid;
    }

    public void setPaid(float paid) {
        this.paid = paid;
    }
    
}
