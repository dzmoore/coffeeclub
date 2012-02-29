package com.coffeecoffeecoffeeclub.coffeeclub.service;

import java.util.Date;
import java.util.List;

import com.coffeecoffeecoffeeclub.coffeeclub.dao.CoffeeClubDbAccess;
import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedDrink;
import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedDrinkType;
import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedInvoice;
import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedUser;

public class DrinkService {
    private CoffeeClubDbAccess dbAccess;

    public DrinkService(final CoffeeClubDbAccess dbAccess) {
        this.dbAccess = dbAccess;
    }
    
    public CoffeeClubDbAccess getDbAccess() {
        return dbAccess;
    }
    
    public MappedDrink createNewDrink(final int typeId, final float cost, final int userId) {
        return getDbAccess().createNewDrink(typeId, cost, userId);
    }
    
    public List<MappedUser> findAllUsers() {
        return getDbAccess().findAllUsers();
    }
    
    public List<MappedDrinkType> findAllDrinkTypes() {
        return getDbAccess().findAllDrinkTypes();
    }
    
    public MappedDrink findDrink(final int drinkId) {
        return getDbAccess().findDrinkById(drinkId);
    }
    
    public List<MappedDrink> findAllNonInvoicedDrinks(final int userId) {
        return getDbAccess().findAllDrinksNotAttachedToInvoice(userId);
    }
    
    public MappedDrinkType createNewDrinkType( 
            final String name, 
            final String description, 
            final double costPerML, 
            final int userId)
    {
        return getDbAccess().createNewDrinkType(name, description, costPerML, userId);
    }
    
    public List<MappedDrink> findAllDrinksForUser(final int userId) {
        final List<MappedDrink> drinks = getDbAccess().findAllDrinksForUser(userId);
        return drinks;
    }   
    
    public List<MappedInvoice> findAllUnpaidInvoicesForUser(final int userId) {
        return getDbAccess().findAllUnpaidInvoices(userId);
    }
    
    public MappedInvoice createNewInvoice(final int userId, final List<MappedDrink> drinks) {
        return getDbAccess().createNewInvoice(drinks, userId);
    }
}
