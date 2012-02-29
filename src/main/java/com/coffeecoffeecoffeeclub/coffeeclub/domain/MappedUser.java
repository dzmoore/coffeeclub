package com.coffeecoffeecoffeeclub.coffeeclub.domain;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MappedUser implements Serializable {
	private static final long serialVersionUID = -2121990998905286418L;
	private int id;
	private String username;
	private boolean active;
	
	public MappedUser() {
		setUsername("");
		setActive(false);
		setId(-1);
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public static MappedUser createMappedUser(ResultSet rs) throws SQLException {
		final MappedUser user = new MappedUser();

		user.setId(rs.getInt("id"));
		user.setUsername(rs.getString("username"));
		user.setActive(rs.getInt("active") == 1);
		return user;
	}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MappedUser [id=");
        builder.append(id);
        builder.append(", username=");
        builder.append(username);
        builder.append(", active=");
        builder.append(active);
        builder.append("]");
        return builder.toString();
    }
}
