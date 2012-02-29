package com.coffeecoffeecoffeeclub.coffeeclub.web.editor;

import java.beans.PropertyEditorSupport;

import com.coffeecoffeecoffeeclub.coffeeclub.dao.CoffeeClubDbAccess;
import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedUser;
import com.coffeecoffeecoffeeclub.coffeeclub.service.LoginService;

public class MappedUserEditor extends PropertyEditorSupport {
    private CoffeeClubDbAccess dbAccess;
    
    public MappedUserEditor(CoffeeClubDbAccess dbAcccess) {
        this.dbAccess = dbAcccess;
    }
    
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(dbAccess.findMappedUser(Integer.parseInt(text)));
    }
    
    @Override
    public String getAsText() {
        MappedUser user = (MappedUser)getValue();
        if (user == null) {
            return null;
        }
        
        return String.valueOf(user.getId());
    }
}
