package com.coffeecoffeecoffeeclub.coffeeclub.web.editor;

import java.beans.PropertyEditorSupport;

import com.coffeecoffeecoffeeclub.coffeeclub.dao.CoffeeClubDbAccess;
import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedDrinkType;

public class MappedDrinkTypeEditor extends PropertyEditorSupport {
    private CoffeeClubDbAccess dbAccess;
    
    public MappedDrinkTypeEditor(CoffeeClubDbAccess dbAcccess) {
        this.dbAccess = dbAcccess;
    }
    
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(dbAccess.findMappedDrinkType(Integer.parseInt(text)));
    }
    
    @Override
    public String getAsText() {
        MappedDrinkType type = (MappedDrinkType)getValue();
        if (type == null) {
            return null;
        }
        
        return String.valueOf(type.getTypeId());
    }
}
