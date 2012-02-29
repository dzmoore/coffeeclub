package com.coffeecoffeecoffeeclub.coffeeclub.dao.test;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.coffeecoffeecoffeeclub.coffeeclub.dao.CoffeeClubDbAccess;

public class TestCoffeeClubDbAccess {
	private CoffeeClubDbAccess dbAccess;

	@Before
	public void setup() {
		ApplicationContext ctx = new FileSystemXmlApplicationContext(new File("src\\main\\webapp\\WEB-INF\\dbaccess.xml").getAbsolutePath());
		dbAccess = (CoffeeClubDbAccess) ctx.getBean("dbAccess");
	}
	
	@Test
	public void testUserAuth() {
		final String username = "test";
		final String password = "test";
		final String hashPass = "";
		
	}
}
