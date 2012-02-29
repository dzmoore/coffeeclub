package com.coffeecoffeecoffeeclub.coffeeclub.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedDrink;
import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedDrinkType;
import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedInvoice;
import com.coffeecoffeecoffeeclub.coffeeclub.domain.MappedUser;
import com.coffeecoffeecoffeeclub.coffeeclub.service.DrinkService;
import com.coffeecoffeecoffeeclub.coffeeclub.util.Conca;
import com.coffeecoffeecoffeeclub.coffeeclub.util.Constants;
import com.coffeecoffeecoffeeclub.coffeeclub.web.editor.MappedDrinkTypeEditor;
import com.coffeecoffeecoffeeclub.coffeeclub.web.editor.MappedUserEditor;

@Controller
public class DrinkController {
    private Logger log4jLogger;
    private DrinkService drinkService;
    
    @ModelAttribute("allTypes")
    public List<MappedDrinkType> populateTypes() {
        return drinkService.findAllDrinkTypes();
    }
    
    @ModelAttribute("allUsers")
    public List<MappedUser> populateAllUsers() {
        return drinkService.findAllUsers();
    }
    
    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(MappedUser.class, new MappedUserEditor(drinkService.getDbAccess()));
        dataBinder.registerCustomEditor(MappedDrinkType.class, new MappedDrinkTypeEditor(drinkService.getDbAccess()));
    }
    
    @Autowired
    public DrinkController(DrinkService drinkService) {
        log4jLogger = Logger.getLogger(getClass());
        this.drinkService = drinkService;
    }
    
    @RequestMapping(value="/main")
    public String indexMain(HttpSession session) {
        final Object userObj = session.getAttribute(Constants.USER);
        if (userObj != null) {
            final MappedUser user = (MappedUser)userObj;
            final List<MappedDrink> drinks = drinkService.findAllDrinksForUser(user.getId());
            session.setAttribute(Constants.DRINKS, drinks);
            
        } else {
            return "redirect:login";
        }
        
        return "main";
    }
    
    @RequestMapping(value="/user/{username}")
    public String showDrinksForUser(@PathVariable("username") String username, HttpSession session) {
        final MappedUser user = drinkService.getDbAccess().findMappedUser(username);

        if (user != null && user.getId() != Constants.INVALID_ID) {
            final List<MappedInvoice> invoices = drinkService.findAllUnpaidInvoicesForUser(user.getId());
            session.setAttribute(Constants.INVOICES, invoices);
        }

        return "user";
    }
    
    @RequestMapping(value="/drink", method=RequestMethod.GET)
    public String drink(@RequestParam("id") String id, HttpSession session) {
        String to = "redirect:main";
        
        // check for user object
        final Object userObj = session.getAttribute(Constants.USER);
        if (userObj != null) {
            try {
                // parse the drink id
                final int intId = Integer.parseInt(id);
                // we will go to the drink view, set it at this point
                to = "drink";
            
                // see if the drink attr has already been set or whether we need
                // to retrieve it
                final Object drinkAttrObj = session.getAttribute(Constants.DRINK);
                if (drinkAttrObj == null || ((MappedDrink)drinkAttrObj).getDrinkId() != intId) {
                    session.setAttribute(Constants.DRINK, drinkService.findDrink(intId));
                }
                
            } catch (Exception e) {
                log4jLogger.error(Conca.t("Error parsing id [", id, "]"), e);
                to = "redirect:main";
            }
        } else {
            session.setAttribute(Constants.RETURN_PATH, Conca.t("redirect:drink?id=", id));
            to = "redirect:login";
        }
        
        return to;
    }
    
    @RequestMapping(value="/newdrink", method=RequestMethod.GET)
    public ModelAndView newDrink(HttpSession session) {
        final Object userObj = session.getAttribute(Constants.USER);
        if (userObj == null) {
            return new ModelAndView("redirect:login");
        }
        
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("drink", new MappedDrink());
        return new ModelAndView("newdrink", model);
    }
    
    @RequestMapping(value="/newdrink", method=RequestMethod.POST)
    public String newDrinkPOST(
            @ModelAttribute("drink") MappedDrink newDrink,
            BindingResult result,
            HttpSession session)
    {
        log4jLogger.trace(Conca.t("newdrink model attr={",newDrink,"}"));
        
        String to = "redirect:main";
        final Object userObj = session.getAttribute(Constants.USER);
        if (userObj != null) {
            try {
                final MappedDrink drink = drinkService.createNewDrink(newDrink.getType().getTypeId(), newDrink.getCost(), newDrink.getUser().getId());
                session.setAttribute(Constants.DRINK, drink);
                to = Conca.t("redirect:drink?id=", drink.getDrinkId());
                
            } catch (Exception e) {
                to = "redirect:main";
            }
            
        } else {
            session.setAttribute(Constants.RETURN_PATH, "redirect:newdrink");
            to = "redirect:login";
        }
        
        return to;
    }
    
    @RequestMapping(value="/newdrinktype", method=RequestMethod.GET)
    public ModelAndView newDrinkType(HttpSession session) {
        final Object userObj = session.getAttribute(Constants.USER);
        if (userObj == null) {
            session.setAttribute(Constants.RETURN_PATH, "redirect:newdrinktype");
            return new ModelAndView("redirect:login");
        }
        
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("drinktype", new MappedDrinkType());
        return new ModelAndView("newdrinktype", model);
    }
    
    @RequestMapping(value="/newdrinktype", method=RequestMethod.POST)
    public String newDrinkTypePOST(
            @ModelAttribute("drinktype") MappedDrinkType newDrink,
            BindingResult result,
            HttpSession session)
    {
        log4jLogger.trace(Conca.t("newdrinktype model attr={",newDrink,"}"));
        
        String to = "redirect:main";
        final Object userObj = session.getAttribute(Constants.USER);
        if (userObj != null) {
            try {
                final MappedDrinkType drink = drinkService.createNewDrinkType(
                        newDrink.getName(), newDrink.getDescription(), newDrink.getCostPerML(), ((MappedUser)userObj).getId());
                session.setAttribute(Constants.DRINK, drink);
                to = Conca.t("redirect:main");
                
            } catch (Exception e) {
                to = "redirect:main";
            }
            
        } else {
            to = "redirect:login";
        }
        
        return to;
    }
    
    @RequestMapping(value="/newinvoice", method=RequestMethod.GET)
    public ModelAndView newInvoice(HttpSession session) {
        final Object userObj = session.getAttribute(Constants.USER);
        if (userObj == null) {
            session.setAttribute(Constants.RETURN_PATH, "redirect:newinvoice");
            return new ModelAndView("redirect:login");
        }
        
        final List<MappedDrink> drinks = drinkService.findAllNonInvoicedDrinks(((MappedUser)userObj).getId());
        
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put(Constants.DRINKS, drinks);
        return new ModelAndView("newinvoice", model);
    } 
    
    @RequestMapping(value="/newinvoice", method=RequestMethod.POST)
    public ModelAndView newInvoicePOST(@RequestParam("drinkIds") int[] drinkIds, HttpSession session) {
        final Object userObj = session.getAttribute(Constants.USER);
        if (userObj == null) {
            return new ModelAndView("redirect:login");
        }
        
        final Object drinksObj = session.getAttribute(Constants.DRINKS);
        if (drinksObj != null) {
            @SuppressWarnings("unchecked")
            final List<MappedDrink> drinks = (List<MappedDrink>)drinksObj;
            final List<MappedDrink> selectedDrinks = new ArrayList<MappedDrink>();
            for (int eaId : drinkIds) {
                for (final MappedDrink eaDrink : drinks) {
                    if (eaDrink.getDrinkId() == eaId) {
                        selectedDrinks.add(eaDrink);
                        break;
                    }
                }
            }
            
            if (selectedDrinks.size() > 0) {
                final MappedInvoice invoice = drinkService.createNewInvoice(((MappedUser)userObj).getId(), selectedDrinks);
                
                log4jLogger.trace(Conca.t("invoice created: [", invoice, "]"));
            }
        }
        
        HashMap<String, Object> model = new HashMap<String, Object>();
        return new ModelAndView("newinvoice", model);
    } 
}
