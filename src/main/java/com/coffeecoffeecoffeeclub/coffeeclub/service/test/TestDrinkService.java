package com.coffeecoffeecoffeeclub.coffeeclub.service.test;

import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedDrink;
import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedDrinkType;
import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedInvoice;
import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedUser;
import com.coffeecoffeecoffeeclub.coffeeclub.service.DrinkService;
import com.coffeecoffeecoffeeclub.coffeeclub.util.Conca;
import com.coffeecoffeecoffeeclub.coffeeclub.util.Constants;

public class TestDrinkService {
    private DrinkService drinkService;
    private Logger log4jLogger;
    
    @Before
    public void setup() {
        System.setProperty("log4j.configuration", "file:./src/main/resources/log4j.properties");
        
        log4jLogger = Logger.getLogger(getClass());
        log4jLogger.setLevel(Level.INFO);
        
        ApplicationContext ctx = new FileSystemXmlApplicationContext("src/main/webapp/WEB-INF/drink-service.xml");
        drinkService = (DrinkService) ctx.getBean("drinkService");
    }
    
    @Ignore
    @Test
    public void testCreateNewDrinkType() {
        final String drinkTypeName = Conca.t("type-name-", System.nanoTime());
        final String description = "here is a description for this test drink type";
        final double costPerML = 0.0021141649;
        final int userId = 1;
        
        log4jLogger.info(Conca.t("ensuring user with id [", userId, "] exists"));
        
        final MappedUser user = drinkService.getDbAccess().findMappedUser(userId);
        
        log4jLogger.info(Conca.t("user returned from db [", user, "]"));
        
        TestCase.assertNotNull(user);
        TestCase.assertTrue(user.getId() != Constants.INVALID_ID);
        
        log4jLogger.info(Conca.t("user appears to exist, attempting to create new drink type"));
        
        final MappedDrinkType type = drinkService.createNewDrinkType(drinkTypeName, description, costPerML, user.getId());
        
        log4jLogger.info(Conca.t("drink type returned from query [", type, "]"));
        
        TestCase.assertNotNull(type);
        TestCase.assertTrue(type.getTypeId() != Constants.INVALID_ID);
        
        log4jLogger.info(Conca.t("drink type creation appears successful"));
    }
    
    @Ignore
    @Test
    public void testCreateNewDrink() {
        final int userId = 6;
        final float cost = 1.5f;
        
        createNewDrink(userId, cost);
    }

    private void createNewDrink(final int userId, final float cost) {
        log4jLogger.info(Conca.t("finding drink type to use"));
        
        final List<MappedDrinkType> types = drinkService.findAllDrinkTypes();
        
        TestCase.assertNotNull(types);
        TestCase.assertTrue(types.size() > 0);
        
        final MappedDrinkType type = types.get(0);
        
        log4jLogger.info(Conca.t("using drink type [", type, "]"));
        
        log4jLogger.info(Conca.t("ensuring user with id [", userId, "] exists"));
        
        final MappedUser user = drinkService.getDbAccess().findMappedUser(userId);
        
        log4jLogger.info(Conca.t("user returned from db [", user, "]"));
        
        TestCase.assertNotNull(user);
        TestCase.assertTrue(user.getId() != Constants.INVALID_ID);
        
        log4jLogger.info(Conca.t("user appears to exist, attempting to create new drink"));
        
        final MappedDrink drink = drinkService.createNewDrink(type.getTypeId(), cost, user.getId());
        
        log4jLogger.info(Conca.t("drink returned from query [", drink, "]"));
        
        TestCase.assertNotNull(drink);
        TestCase.assertTrue(drink.getDrinkId() != Constants.INVALID_ID);
        
        log4jLogger.info(Conca.t("drink creation appears successful"));
        
        log4jLogger.info(Conca.t("finding all drinks for userid [", user.getId(), "]"));
    
        final List<MappedDrink> drinks = drinkService.findAllDrinksForUser(user.getId());
        
        log4jLogger.info(Conca.t("drinks found for userid [", user.getId(), "]: {", drinks, "}"));
        
        TestCase.assertNotNull(drinks);
        TestCase.assertTrue(drinks.size() > 0);
    }
    
    @Ignore
    @Test
    public void testFindAllNonInvoicedDrinks() {
        final int userId = 1;
        
        log4jLogger.info(Conca.t("ensuring user with id [", userId, "] exists"));
        
        final MappedUser user = drinkService.getDbAccess().findMappedUser(userId);
        
        log4jLogger.info(Conca.t("user returned from db [", user, "]"));
        
        TestCase.assertNotNull(user);
        TestCase.assertTrue(user.getId() != Constants.INVALID_ID);
        
        log4jLogger.info(Conca.t("user appears to exist, finding all non-invoiced drinks"));
        
        final List<MappedDrink> drinks = drinkService.findAllNonInvoicedDrinks(userId);
        
        log4jLogger.info(Conca.t("drinks not attached to invoice {", drinks, "}"));
        
        TestCase.assertNotNull(drinks);
        TestCase.assertTrue(drinks.size() > 0);
    }
    
    @Test
    public void testCreateInvoice() {
        final int userId = 1;
        final int drinkCount = 5;
        
        log4jLogger.info(Conca.t("ensuring user with id [", userId, "] exists"));
        
        final MappedUser user = drinkService.getDbAccess().findMappedUser(userId);
        
        log4jLogger.info(Conca.t("user returned from db [", user, "]"));
        
        TestCase.assertNotNull(user);
        TestCase.assertTrue(user.getId() != Constants.INVALID_ID);
        
        log4jLogger.info(Conca.t("creating ", drinkCount, " new drinks"));
        
        for (int i = 0; i < drinkCount; i++) {
            createNewDrink(userId, 1.1f);
        }
        
        log4jLogger.info(Conca.t("created the new drinks, finding all non-invoiced drinks"));
        
        final List<MappedDrink> drinks = drinkService.findAllNonInvoicedDrinks(userId);
        
        log4jLogger.info(Conca.t("drinks not attached to invoice {", drinks, "}"));
        
        TestCase.assertNotNull(drinks);
        TestCase.assertTrue(drinks.size() > 0);
        
        log4jLogger.info("Creating new invoice");
        
        final MappedInvoice invoice = drinkService.createNewInvoice(userId, drinks);
        
        log4jLogger.info(Conca.t("invoice returned: [", invoice, "]"));
        
        TestCase.assertNotNull(invoice);
        TestCase.assertTrue(invoice.getId() != Constants.INVALID_ID);
        TestCase.assertTrue(invoice.getUser().getId() == userId);
        
        float total = 0f;
        for (final MappedDrink ea : drinks) {
            total += ea.getCost();
        }
        TestCase.assertTrue(total == invoice.getDue());
        
    }
}
