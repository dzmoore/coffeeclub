package com.coffeecoffeecoffeeclub.coffeeclub.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.coffeecoffeecoffeeclub.coffeeclub.util.Conca;

public class DbExecutor {
	private static final int DB_EXECUTE_RETRY_ATTEMPTS = 5;
	
	private Logger log4jLogger;
	protected Connection dbConnection;
	protected String username;
	protected String password;
	protected String dbName;
	
	public DbExecutor(String dbName, String username, String password) {
		log4jLogger = Logger.getLogger(DbExecutor.class);
		this.username = username;
		this.password = password;
		this.dbName = dbName;
	}

	public void setDbConnection(Connection dbConnection) {
		this.dbConnection = dbConnection;
	}

	public Connection getDbConnection() {
		if (dbConnection == null) {
			// load the MySQL driver
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				log4jLogger.error(e.getMessage());
				e.printStackTrace();
			}
			
			// Setup the connection with the DB
			try {
				dbConnection = DriverManager.getConnection(
					Conca.t("jdbc:mysql://localhost/", dbName, "?user=", username, "&password=", password, "&autoReconnect=true")
				);
				
			} catch (SQLException e) {
				log4jLogger.error("Error establishing database connection", e);
			}
		}
		return dbConnection;
	}
	
	protected ResultSet execute(String query, List<Object> params, List<Class<?>> paramTypes) {
		ResultSet results = null;
		int retryAttempts = DB_EXECUTE_RETRY_ATTEMPTS;
		while (retryAttempts-- > 0) {
			
			if (log4jLogger.isTraceEnabled()) {
				log4jLogger.trace(Conca.t("execute: Query=[", query, "] params=[", params.toString(), "] paramTypes=[", paramTypes.toString(), "]"));
			}
			
			PreparedStatement ps = null;
			
			try {
				ps = getDbConnection().prepareStatement(query);
				
			} catch (Exception e) {
				// attempt to handle "broken pipe" and the like errors
				
				log4jLogger.error(Conca.t(
						"execute(String query, List<Object> params, List<Class<?>> paramTypes) errored: [", e.getMessage(), "]"), 
						e
				);
				
				if (e instanceof SQLException) {
					break;
				}

				if (StringUtils.containsIgnoreCase(e.getMessage(), "broken pipe")) {
					log4jLogger.error("Detected broken pipe error. Will attempt DB connection reset.");
					closeAndReconnect();
					continue;
				}
			}
			
			for (int i = 0; i < params.size(); i++) {
				Object ea = params.get(i);
				Class<?> eaType = paramTypes.get(i);

				if (ea == null || eaType == null) {
					log4jLogger.error("execute(String query, List<Object> params, List<Class<?>> paramTypes): parameter is null!");
					return null;
				}
				
				try {
					int parameterIndex = i+1;
					setParamByType(ps, ea, eaType, parameterIndex);
					
				} catch (Exception e) {
					log4jLogger.error(
							Conca.t("execute(String query, List<Object> params, List<Class<?>> paramTypes): [", e.getMessage(), "]"), 
							e
					);
					return null;
				}
			}

			try {			
				ps.execute();
				results = ps.getResultSet();
				break;
				
			} catch (Exception e) {
				log4jLogger.error(
						Conca.t("execute(String query, List<Object> params, List<Class<?>> paramTypes): [", e.getMessage(), "]"), 
						e
				);

				if (StringUtils.containsIgnoreCase(e.getMessage(), "broken pipe")) {
					log4jLogger.error("Detected broken pipe error. Will attempt DB connection reset.");
					closeAndReconnect();
				}
			}
		}
		return results;
	}
	
	public int executeUpdate(
            String query, 
            List<Object> params, 
            List<Class<?>> paramTypes) 
    {
        int rowsUpdated = -1;
        int retryAttempts = DB_EXECUTE_RETRY_ATTEMPTS;
        while (retryAttempts-- > 0) {
            if (log4jLogger.isTraceEnabled()) {
                log4jLogger.trace(Conca.t("execute: Query=[", query, "] params=[", params.toString(), "] paramTypes=[", paramTypes.toString(), "]"));
            }
            
            PreparedStatement ps = null;
            
            try {
                ps = getDbConnection().prepareStatement(query);
            } catch (Exception e) {
                log4jLogger.error("Error while trying to prepare statement for query: [: " + query + "]", e);
                if (e instanceof SQLException) {
                    break;
                }

                // SocketException?
                if (e.getMessage().toLowerCase().contains("broken pipe")) {
                    log4jLogger.error("Detected broken pipe error. Will attempt DB connection reset.");
                    closeAndReconnect();
                    continue;
                }
            }
            
            for (int i = 0; i < params.size(); i++) {
                Object ea = params.get(i);
                Class<?> eaType = paramTypes.get(i);

                if (ea == null || eaType == null) {
                    log4jLogger.error("parameter(s) is null! params=[" + params.toString() + "], paramTypes=[" + paramTypes.toString() + "]");
                    return rowsUpdated;
                }
                
                try {
                    // parameters are one-based indexes...
                    int parameterIndex = i+1;
                    
                    setParamByType(ps, ea, eaType, parameterIndex);
                } catch (SQLException e) {
                    log4jLogger.error("SQLException while attempting to set parameter: [" + ea.toString() + "]", e);
                    return rowsUpdated;
                }
            }

            try {           
                rowsUpdated = ps.executeUpdate();
                break;
            } catch (Exception e) {
                log4jLogger.error("Error when calling executeUpdate. preparedStatement=[" + (ps == null ? "NULL" : ps.toString()) + "]", e);

                if (e.getMessage().toLowerCase().contains("broken pipe")) {
                    log4jLogger.error("Detected broken pipe error. Will attempt DB connection reset.");
                    closeAndReconnect();
                }
            }
        }
        return rowsUpdated;
    }
	
	private void setParamByType(PreparedStatement ps, Object ea,
			Class<?> eaType, int parameterIndex) throws SQLException {
		if (eaType == String.class) {
			ps.setString(parameterIndex, ea.toString());
			
		} else if (eaType == Integer.class) {
			ps.setInt(parameterIndex, (Integer)ea);
		
		} else if (eaType == Float.class) {
			ps.setFloat(parameterIndex, (Float)ea);
		
		} else if (eaType == Date.class) {
		    java.sql.Date sqlDate = new java.sql.Date(((Date)ea).getTime());
		    ps.setDate(parameterIndex, sqlDate);
		
		} else if (eaType == Double.class) {
		    ps.setDouble(parameterIndex, (Double)ea);
		}
	}
	
	public void closeAndReconnect() {
		synchronized (dbConnection) {

			Level origLvl = log4jLogger.getLevel();
			log4jLogger.setLevel(Level.INFO);
			if (dbConnection != null) {
				log4jLogger.info("Attempting to close existing DB connection");
				try {
					dbConnection.close();
				} catch (SQLException e) {
					log4jLogger.error(e.getMessage());
					e.printStackTrace();
				}
			}
			log4jLogger.info("Nulling existing DB connection reference.");
			dbConnection = null;

			log4jLogger.info("Reconnecting.");
			getDbConnection();

			log4jLogger.setLevel(origLvl);
		}
	}
	
}
