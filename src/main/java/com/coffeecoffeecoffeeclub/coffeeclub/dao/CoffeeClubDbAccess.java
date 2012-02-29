package com.coffeecoffeecoffeeclub.coffeeclub.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedDrink;
import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedDrinkType;
import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedInvoice;
import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedUser;
import com.coffeecoffeecoffeeclub.coffeeclub.util.Conca;

public class CoffeeClubDbAccess extends DbExecutor {
    private static final String SELECT_ALL_USER = "select id, username, active from user "
            + "where lower(username) = ?";
    private static final String SELECT_ALL_USER_AND_HASHPASS = SELECT_ALL_USER
            + " and password = ?";
    
    private ReentrantLock insertLock;
    private Logger log4jLogger;

    public CoffeeClubDbAccess(String dbName, String username, String password) {
        super(dbName, username, password);
        
        log4jLogger = Logger.getLogger(getClass());
        
        insertLock = new ReentrantLock();
    }

    public boolean saltDoesNotExist(final String salt) {
        final String query = Conca.t(
                "select salt ",
                "from user ",
                "where salt = ?"
        );
        
        final List<Object> values = new ArrayList<Object>();
        final List<Class<?>> types = new ArrayList<Class<?>>();

        values.add(salt);
        types.add(String.class);
        
        try {
            ResultSet rs = execute(query, values, types);

            return !rs.next();
        } catch (Exception e) {
            log4jLogger.error("error occurred while attempting to find salt", e);
        }
        
        return false;
    }
    
    public List<MappedUser> findAllUsers() {
        List<MappedUser> users = new ArrayList<MappedUser>();
        final String query = "select id, username, active from user ";
        
        try {
            ResultSet rs = execute(query, Collections.emptyList(), new ArrayList<Class<?>>());
            while (rs.next()) {
                final MappedUser user = new MappedUser();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setActive(rs.getInt("active") == 1);
                
                users.add(user);
            }
            
        } catch (Exception e) {
            log4jLogger.error(Conca.t("error occurred while attempting to find all users"), e);
        }
        
        return users;
    }
    
    public String findSalt(final String username) {
        String salt = null;
        final String query = Conca.t(
                "select salt ",
                "from user ",
                "where lower(username) = ?"
        );
        
        final List<Object> values = new ArrayList<Object>();
        final List<Class<?>> types = new ArrayList<Class<?>>();

        values.add(StringUtils.lowerCase(username));
        types.add(String.class);
        
        try {
            ResultSet rs = execute(query, values, types);
            if (rs.next()) {
                salt = rs.getString("salt");
            }
        } catch (Exception e) {
            log4jLogger.error(Conca.t("error occurred while attempting to find salt for username [", username, "]"), e);
        }
        
        return salt;
    }

    public MappedUser userAndPasswordMatch(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null
                || password.trim().isEmpty()) {
            // return immediately under various conditions
            return null;
        }

        final List<Object> values = new ArrayList<Object>();
        final List<Class<?>> types = new ArrayList<Class<?>>();

        values.add(StringUtils.lowerCase(username));
        types.add(String.class);

        values.add(password);
        types.add(String.class);

        ResultSet rs = execute(SELECT_ALL_USER_AND_HASHPASS, values, types);

        MappedUser user = null;
        try {

            if (rs.next()) {
                user = new MappedUser();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setActive(rs.getInt("active") == 1);
            }
        } catch (SQLException e) {
            log4jLogger.error(Conca.t(
                    "SQLException occurred while attempting to auth user:{",
                    "username=[", username, "]"), e);
            closeAndReconnect();
            return null;
        }
        return user;
    }
    
    public MappedUser findMappedUser(final String username) {
        final String query = SELECT_ALL_USER;
        
        final List<Object> values = new ArrayList<Object>();
        final List<Class<?>> types = new ArrayList<Class<?>>();

        values.add(StringUtils.lowerCase(username));
        types.add(String.class);
        
        MappedUser user = new MappedUser();
        try {
            ResultSet rs = execute(query, values, types);
            if (rs.next()) {
                user = MappedUser.createMappedUser(rs);
            }
        } catch (Exception e) {
            log4jLogger.error(Conca.t("error occurred while attempting to find user for username [", username, "]"), e);
        }
        
        return user;
    }
    
    public MappedUser findMappedUser(final int userId) {
        final String query = "select id, username, active from user where id = ?";
        
        final List<Object> values = new ArrayList<Object>();
        final List<Class<?>> types = new ArrayList<Class<?>>();

        values.add(userId);
        types.add(Integer.class);
        
        MappedUser user = new MappedUser();
        try {
            ResultSet rs = execute(query, values, types);
            if (rs.next()) {
                user = MappedUser.createMappedUser(rs);
            }
        } catch (Exception e) {
            log4jLogger.error(Conca.t("error occurred while attempting to find user for userId [", userId, "]"), e);
        }
        
        return user;
    }
    
    public MappedUser createUser(final String username, final String salt, final String password) {
        final String query = Conca.t(
            "insert into user ",
            "(username, active, salt, password) ",
            "VALUES ",
            "(?, ?, ?, ?)"
        );
        
        final List<Object> values = new ArrayList<Object>();
        final List<Class<?>> types = new ArrayList<Class<?>>();

        values.add(StringUtils.lowerCase(username));
        types.add(String.class);
        
        values.add(1);
        types.add(Integer.class);
        
        values.add(salt);
        types.add(String.class);
        
        values.add(password);
        types.add(String.class);
        
        MappedUser user = new MappedUser();
        try {
            int rows = executeUpdate(query, values, types);
            if (rows == 1) {
                user = findMappedUser(username);
            }
        } catch (Exception e) {
            log4jLogger.error(Conca.t("error occurred while attempting to create user for username [", username, "]"), e);
        }
        
        return user;
    }
    
    public boolean updateUserPassword(final int userId, final String newHashPassword) {
        boolean success = false;
        final String query = Conca.t(
            "update user ",
            "set password = ? ",
            "where id = ?"
        );
        
        final List<Object> values = new ArrayList<Object>();
        final List<Class<?>> types = new ArrayList<Class<?>>();

        values.add(newHashPassword);
        types.add(String.class);
        
        values.add(userId);
        types.add(Integer.class);
        
        try {
            int rows = executeUpdate(query, values, types);
            success = (rows == 1);
        
        } catch (Exception e) {
            log4jLogger.error(Conca.t("error occurred while attempting to change password for userid [", userId, "]"), e);
        }
        
        return success;
    }
    
    public MappedDrink createNewDrink(final int typeId, final float cost, final int userId) {
        MappedDrink result = new MappedDrink();
     
        try {
            insertLock.lock();
            final String query = Conca.t(
                    "insert into drink ",
                    "(date_consumed, cost, drinkTypeFk, userFk) ",
                    "VALUES ",
                    "(?, ?, ?, ?)"
            );
            
            final List<Object> values = new ArrayList<Object>();
            final List<Class<?>> types = new ArrayList<Class<?>>();
    
            values.add(new Date());
            types.add(Date.class);
            
            values.add(cost);
            types.add(Float.class);
            
            values.add(typeId);
            types.add(Integer.class);
            
            values.add(userId);
            types.add(Integer.class);
            
            try {
                int rows = executeUpdate(query, values, types);
                final boolean success = (rows == 1);
                
                if (success) {
                    final String lastInserttedIdQuery = "select last_insert_id()";
                    final ResultSet rs = execute(lastInserttedIdQuery, Collections.emptyList(), new ArrayList<Class<?>>());
                    if (rs.next()) {
                        result = findDrinkById(rs.getInt(1));
                    }
                }
            
            } catch (Exception e) {
                log4jLogger.error(
                    Conca.t(
                        "error occurred while attempting to insert new drink. ",
                        "userid [", userId, "] typeId [", typeId, "] cost [", cost, "]"
                    ), 
                    e
                );
            }
        } finally {
            insertLock.unlock();
        }
        
        return result;
    }
    
    public MappedDrinkType createNewDrinkType(
            final String name, 
            final String description, 
            final double costPerML, 
            final int userId) 
    {
        final MappedDrinkType returnObj = new MappedDrinkType();
        try {
            insertLock.lock();
            
            final String query = Conca.t(
                    "insert into drink_type ",
                    "(name, created_by_userFk, descript, cost_per_ml) ",
                    "VALUES ",
                    "(?, ?, ?, ?)"
                    );
    
            final List<Object> values = new ArrayList<Object>();
            final List<Class<?>> types = new ArrayList<Class<?>>();
    
            values.add(name);
            types.add(String.class);
    
            values.add(userId);
            types.add(Integer.class);
    
            values.add(description);
            types.add(String.class);
    
            values.add(costPerML);
            types.add(Double.class);
    
    
            try {
                int rows = executeUpdate(query, values, types);
                final boolean success = (rows == 1);
    
                if (success) {
                    // get the last inserted id (this is connection specific)
                    // and this method is synchronized, so as long as nothing
                    // else inserts into this table (on this connection, without
                    // synchronizing), retrieving the last insert id this way 
                    // should not have any concurrency problems.
                    final String lastInserttedIdQuery = "select last_insert_id()";
                    final ResultSet rs = execute(lastInserttedIdQuery, Collections.emptyList(), new ArrayList<Class<?>>());
                    if (rs.next()) {
                        returnObj.setTypeId(rs.getInt(1));
                    }
    
                    returnObj.setName(name);
                    returnObj.setCreatedBy(findMappedUser(userId));
                    returnObj.setCostPerML(costPerML);
                    returnObj.setDescription(description);
                }
    
            } catch (Exception e) {
                log4jLogger.error(
                    Conca.t(
                        "error occurred while attempting to insert new drink type. ",
                        "userid [", userId, "] name [", name, "] costPerML [", costPerML, "] ",
                        " description [", description, "]"
                    ), 
                    e
                );
            }
        } finally {
            insertLock.unlock();
        }

        return returnObj;
    }
    
    public MappedDrink findDrinkById(final int drinkId) {
        final MappedDrink result = new MappedDrink();
        
        final String query = Conca.t(
                "select ",
                "   d.id                            as drinkId, ",
                "   d.date_consumed                 as dateConsumed, ",
                "   d.cost                          as cost, ",
                "   dt.id                           as typeId, ",
                "   dt.name                         as name, ",
                "   dt.descript                     as description, ",
                "   dt.cost_per_ml                  as costPerML, ",
                "   u.id                            as userId, ",
                "   u.username                      as username, ",
                "   u.active                        as active, ",
                "   u2.id                           as c_userId, ",
                "   u2.username                     as c_username, ",
                "   u2.active                       as c_active ",
                "from ",
                "   drink d join drink_type dt on (d.drinkTypeFk = dt.id) ",
                "   join user u on (d.userFk = u.id) ",
                "   join user u2 on (dt.created_by_userFk = u2.id) ",
                "where d.id = ? "
            );
            
            final List<Object> values = new ArrayList<Object>();
            final List<Class<?>> types = new ArrayList<Class<?>>();

            values.add(drinkId);
            types.add(Integer.class);
            
            try {
                final ResultSet rs = execute(query, values, types);
                
                if (rs.next()) {
                    result.setDrinkId(rs.getInt("drinkId"));
                    result.setDateConsumed(rs.getDate("dateConsumed"));
                    result.setCost(rs.getFloat("cost"));
                  
                    final MappedDrinkType type = result.getType();
                    type.setTypeId(rs.getInt("typeId"));
                    type.setName(rs.getString("name"));
                    type.setDescription(rs.getString("description"));
                    type.setCostPerML(rs.getDouble("costPerML"));
                    type.getCreatedBy().setId(rs.getInt("c_userId"));
                    type.getCreatedBy().setUsername(rs.getString("c_username"));
                    type.getCreatedBy().setActive(rs.getInt("c_active") == 1);
                    
                    result.getUser().setId(rs.getInt("userId"));
                    result.getUser().setUsername(rs.getString("username"));
                    result.getUser().setActive(rs.getInt("active") == 1);
                }
                
            } catch (Exception e) {
                log4jLogger.error(Conca.t("error occurred while attempting to find drink for id [", drinkId, "]"), e);
            }
        
        return result;
    }
    
    public List<MappedDrink> findAllDrinksForUser(final int userId) {
        List<MappedDrink> drinks = new ArrayList<MappedDrink>();
        
        final String query = Conca.t(
            "select ",
            "   d.id                            as drinkId, ",
            "   d.date_consumed                 as dateConsumed, ",
            "   d.cost                          as cost, ",
            "   dt.id                           as typeId, ",
            "   dt.name                         as name, ",
            "   dt.descript                     as description, ",
            "   dt.cost_per_ml                  as costPerML, ",
            "   u.id                            as userId, ",
            "   u.username                      as username, ",
            "   u.active                        as active, ",
            "   u2.id                           as c_userId, ",
            "   u2.username                     as c_username, ",
            "   u2.active                       as c_active ",
            "from ",
            "   drink d join drink_type dt on (d.drinkTypeFk = dt.id) ",
            "   join user u on (d.userFk = u.id) ",
            "   join user u2 on (dt.created_by_userFk = u2.id) ",
            "where u.id = ? ",
            "order by dateConsumed desc"
        );
            
        final List<Object> values = new ArrayList<Object>();
        final List<Class<?>> types = new ArrayList<Class<?>>();

        values.add(userId);
        types.add(Integer.class);
        
        try {
            final ResultSet rs = execute(query, values, types);
            
            while (rs.next()) {
                final MappedDrink eaDrink = new MappedDrink();
                eaDrink.setDrinkId(rs.getInt("drinkId"));
                eaDrink.setDateConsumed(rs.getDate("dateConsumed"));
                eaDrink.setCost(rs.getFloat("cost"));
                
                final MappedDrinkType type = eaDrink.getType();
                type.setTypeId(rs.getInt("typeId"));
                type.setName(rs.getString("name"));
                type.setDescription(rs.getString("description"));
                type.setCostPerML(rs.getDouble("costPerML"));
                type.getCreatedBy().setId(rs.getInt("c_userId"));
                type.getCreatedBy().setUsername(rs.getString("c_username"));
                type.getCreatedBy().setActive(rs.getInt("c_active") == 1);
                
                eaDrink.getUser().setId(rs.getInt("userId"));
                eaDrink.getUser().setUsername(rs.getString("username"));
                eaDrink.getUser().setActive(rs.getInt("active") == 1);
                
                drinks.add(eaDrink);
            }
            
        } catch (Exception e) {
            log4jLogger.error(Conca.t("error occurred while attempting to find all drink for userid [", userId, "]"), e);
        }
        
        return drinks;
    }
    
    public List<MappedDrinkType> findAllDrinkTypes() {
        List<MappedDrinkType> drinkTypes = new ArrayList<MappedDrinkType>();
        
        final String query = Conca.t(
            "select ",
            "   dt.id                           as typeId, ",
            "   dt.name                         as name, ",
            "   dt.descript                     as description, ",
            "   dt.cost_per_ml                  as costPerML, ",
            "   u.id                            as userId, ",
            "   u.username                      as username, ",
            "   u.active                        as active ",
            "from ",
            "   drink_type dt join user u on (dt.created_by_userFk = u.id) ",
            "order by name desc"
        );
        
        try {
            final ResultSet rs = execute(query, Collections.emptyList(), new ArrayList<Class<?>>());
            
            while (rs.next()) {
                final MappedDrinkType eaType = new MappedDrinkType();

                eaType.setTypeId(rs.getInt("typeId"));
                eaType.setName(rs.getString("name"));
                eaType.setDescription(rs.getString("description"));
                eaType.setCostPerML(rs.getDouble("costPerML"));
                eaType.getCreatedBy().setId(rs.getInt("userId"));
                eaType.getCreatedBy().setUsername(rs.getString("username"));
                eaType.getCreatedBy().setActive(rs.getInt("active") == 1);

                drinkTypes.add(eaType);
            }
            
        } catch (Exception e) {
            log4jLogger.error("error occurred while attempting to find all drink types", e);
        }
        
        return drinkTypes;
    }
    
    public MappedDrinkType findMappedDrinkType(final int id) {
        MappedDrinkType drinkType = new MappedDrinkType();
        
        final String query = Conca.t(
            "select ",
            "   dt.id                           as typeId, ",
            "   dt.name                         as name, ",
            "   dt.descript                     as description, ",
            "   dt.cost_per_ml                  as costPerML, ",
            "   u.id                            as userId, ",
            "   u.username                      as username, ",
            "   u.active                        as active ",
            "from ",
            "   drink_type dt join user u on (dt.created_by_userFk = u.id) ",
            "where dt.id = ?"
        );
        
        try {
            List<Object> params = new ArrayList<Object>();
            params.add(id);
            
            List<Class<?>> types = new ArrayList<Class<?>>();
            types.add(Integer.class);
            
            final ResultSet rs = execute(query, params, types);
            
            if (rs.next()) {
                drinkType.setTypeId(rs.getInt("typeId"));
                drinkType.setName(rs.getString("name"));
                drinkType.setDescription(rs.getString("description"));
                drinkType.setCostPerML(rs.getDouble("costPerML"));
                drinkType.getCreatedBy().setId(rs.getInt("userId"));
                drinkType.getCreatedBy().setUsername(rs.getString("username"));
                drinkType.getCreatedBy().setActive(rs.getInt("active") == 1);
            }
            
        } catch (Exception e) {
            log4jLogger.error("error occurred while attempting to find drink type", e);
        }
        
        return drinkType;
    }
    
    public List<MappedDrink> findAllDrinkForInvoice(final int invoiceId) {
        final List<MappedDrink> drinks = new ArrayList<MappedDrink>();
        final String query = Conca.t(
                "select ",
                "   d.id                            as drinkId, ",
                "   d.date_consumed                 as dateConsumed, ",
                "   d.cost                          as cost, ",
                "   dt.id                           as typeId, ",
                "   dt.name                         as name, ",
                "   dt.descript                     as description, ",
                "   dt.cost_per_ml                  as costPerML, ",
                "   u.id                            as userId, ",
                "   u.username                      as username, ",
                "   u.active                        as active, ",
                "   u2.id                           as c_userId, ",
                "   u2.username                     as c_username, ",
                "   u2.active                       as c_active ",
                "from ",
                "   drink d join drink_type dt on (d.drinkTypeFk = dt.id) ",
                "   join user u on (d.userFk = u.id) ",
                "   join user u2 on (dt.created_by_userFk = u2.id) ",
                "   join invoice i on (d.invoiceFk = i.id) ",
                "where i.id = ? ",
                "   order by dateConsumed desc"
        );  

        final List<Object> values = new ArrayList<Object>();
        final List<Class<?>> types = new ArrayList<Class<?>>();

        values.add(invoiceId);
        types.add(Integer.class);

        try {
            final ResultSet rs = execute(query, values, types);

            while (rs.next()) {
                final MappedDrink eaDrink = new MappedDrink();
                eaDrink.setDrinkId(rs.getInt("drinkId"));
                eaDrink.setDateConsumed(rs.getDate("dateConsumed"));
                eaDrink.setCost(rs.getFloat("cost"));

                final MappedDrinkType type = eaDrink.getType();
                type.setTypeId(rs.getInt("typeId"));
                type.setName(rs.getString("name"));
                type.setDescription(rs.getString("description"));
                type.setCostPerML(rs.getDouble("costPerML"));
                type.getCreatedBy().setId(rs.getInt("c_userId"));
                type.getCreatedBy().setUsername(rs.getString("c_username"));
                type.getCreatedBy().setActive(rs.getInt("c_active") == 1);

                eaDrink.getUser().setId(rs.getInt("userId"));
                eaDrink.getUser().setUsername(rs.getString("username"));
                eaDrink.getUser().setActive(rs.getInt("active") == 1);

                drinks.add(eaDrink);
            }

        } catch (Exception e) {
            log4jLogger.error(Conca.t("error occurred while attempting to find all drink for invoiceId [", invoiceId, "]"), e);
        }

        return drinks;
    }
    
    public List<MappedInvoice> findAllUnpaidInvoices(final int userId) {
        final List<MappedInvoice> invoices = new ArrayList<MappedInvoice>();
        
        final String query = Conca.t(
            "select ",
            "   i.id                            as invoiceId, ",
            "   i.due                           as due, ",
            "   i.paid                          as paid, ",
            "   u.id                            as userId, ",
            "   u.username                      as username, ",
            "   u.active                        as active ",
            "from ",
            "   invoice i join user u on (i.userFk = u.id) ",
            "where ",
            "   paid = 0 and due != 0 and ",
            "   u.id = ?"
        );
        
        try {
            List<Object> params = new ArrayList<Object>();
            params.add(userId);
            
            List<Class<?>> types = new ArrayList<Class<?>>();
            types.add(Integer.class);
            
            final ResultSet rs = execute(query, params, types);
            
            while (rs.next()) {
                final MappedInvoice invoice = new MappedInvoice();
                invoice.setId(rs.getInt("invoiceId"));
                invoice.setDue(rs.getFloat("due"));
                invoice.setPaid(rs.getFloat("paid"));
                invoice.getUser().setId(rs.getInt("userId"));
                invoice.getUser().setUsername(rs.getString("username"));
                invoice.getUser().setActive(rs.getInt("active") == 1);
                
                invoice.getDrinks().addAll(findAllDrinkForInvoice(invoice.getId()));
                
                invoices.add(invoice);
            }
            
        } catch (Exception e) {
            log4jLogger.error(Conca.t("error occurred while attempting to find invoices for user with id ", userId), e);
        }
        
        return invoices;
    }
    
    public List<MappedDrink> findAllDrinksNotAttachedToInvoice(final int userId) {
        final List<MappedDrink> drinks = new ArrayList<MappedDrink>();
        final String query = Conca.t(
                "select ",
                "   d.id                            as drinkId, ",
                "   d.date_consumed                 as dateConsumed, ",
                "   d.cost                          as cost, ",
                "   dt.id                           as typeId, ",
                "   dt.name                         as name, ",
                "   dt.descript                     as description, ",
                "   dt.cost_per_ml                  as costPerML, ",
                "   u.id                            as userId, ",
                "   u.username                      as username, ",
                "   u.active                        as active, ",
                "   u2.id                           as c_userId, ",
                "   u2.username                     as c_username, ",
                "   u2.active                       as c_active ",
                "from ",
                "   drink d join drink_type dt on (d.drinkTypeFk = dt.id) ",
                "   join user u on (d.userFk = u.id) ",
                "   join user u2 on (dt.created_by_userFk = u2.id) ",
                "where ",
                "   d.invoiceFk is null and ",
                "   u.id = ? ",
                "   order by dateConsumed desc"
        );  

        final List<Object> values = new ArrayList<Object>();
        final List<Class<?>> types = new ArrayList<Class<?>>();

        values.add(userId);
        types.add(Integer.class);

        try {
            final ResultSet rs = execute(query, values, types);

            while (rs.next()) {
                final MappedDrink eaDrink = new MappedDrink();
                eaDrink.setDrinkId(rs.getInt("drinkId"));
                eaDrink.setDateConsumed(rs.getDate("dateConsumed"));
                eaDrink.setCost(rs.getFloat("cost"));

                final MappedDrinkType type = eaDrink.getType();
                type.setTypeId(rs.getInt("typeId"));
                type.setName(rs.getString("name"));
                type.setDescription(rs.getString("description"));
                type.setCostPerML(rs.getDouble("costPerML"));
                type.getCreatedBy().setId(rs.getInt("c_userId"));
                type.getCreatedBy().setUsername(rs.getString("c_username"));
                type.getCreatedBy().setActive(rs.getInt("c_active") == 1);

                eaDrink.getUser().setId(rs.getInt("userId"));
                eaDrink.getUser().setUsername(rs.getString("username"));
                eaDrink.getUser().setActive(rs.getInt("active") == 1);

                drinks.add(eaDrink);
            }

        } catch (Exception e) {
            log4jLogger.error(Conca.t("error occurred while attempting to find all non-invoiced drinks for userId [", userId, "]"), e);
        }

        return drinks;
    }
    
    private float calculateDue(final List<MappedDrink> drinks) {
        float total = 0f;
        for (final MappedDrink ea : drinks) {
            total += ea.getCost();
        }
        
        return total;
    }

    public MappedInvoice createNewInvoice(final List<MappedDrink> drinks, final int userId) {
        final MappedInvoice invoice = new MappedInvoice();
        try {
            insertLock.lock();
            
            final float due = calculateDue(drinks);

            String query = Conca.t(
                "insert into invoice ",
                "   (userFk, due) VALUES ",
                "   (?, ?)"
            );

            final List<Object> params = new ArrayList<Object>();
            final List<Class<?>> types = new ArrayList<Class<?>>();

            params.add(userId);
            types.add(Integer.class);

            params.add(due);
            types.add(Float.class);

            final int result = executeUpdate(query, params, types);

            if (result == 1) {
                final String lastInserttedIdQuery = "select last_insert_id()";
                final ResultSet rs = execute(lastInserttedIdQuery, Collections.emptyList(), new ArrayList<Class<?>>());
              
                if (rs.next()) {
                    invoice.setId(rs.getInt(1));
                    
                    if (drinks.size() > 0) {
                        params.clear();
                        types.clear();
                        
                        query = Conca.t(
                            "update drink ",
                            "   set invoiceFk = ? ",
                            "   where id in ",
                            "   ("
                        );
                        
                        params.add(invoice.getId());
                        types.add(Integer.class);
                        
                        final StringBuilder sb = new StringBuilder(query);
                        
                        boolean notFirst = false;
                        
                        for (final MappedDrink ea : drinks) {
                            if (notFirst) {
                                sb.append(", ");
                            }
                            
                            sb.append("?");
                            
                            params.add(ea.getDrinkId());
                            types.add(Integer.class);
                            
                            notFirst = true;
                        }
                        
                        sb.append(")");
                        
                        executeUpdate(sb.toString(), params, types);
                    }
                }

                invoice.setUser(findMappedUser(userId));
                invoice.setDrinks(drinks);
                invoice.setDue(due);
            }

        } catch (Exception e) {
            log4jLogger.error(Conca.t("error occurred while attempting to create invoice for user with id ", userId), e);
            
        } finally {
            insertLock.unlock();
        }

        return invoice;
    }

}
