package com.slestac.UC7.web;

import static spark.Spark.*;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Hello world!
 *
 * Docker start for mysql:
 * sudo docker run --name some-mysql3 -p 3306:3306 -e MYSQL_ROOT_PASSWORD=spark -e MYSQL_USER=demo 
 * 		-e MYSQL_PASSWORD=demo -e MYSQL_DATABASE=demo -d mysql
 */
public class App 
{
    public static void main( String[] args )
    {

    	if (args.length < 1) {
    		System.out.println("Please pass the hostname for the db server");
    		return;
    	}
    	
		String databaseUrl = "jdbc:mysql://" + args[0] +"/demo";
		
		try {
			
			System.out.println("connecting to: " + databaseUrl);
		        
			ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl);
			((JdbcConnectionSource)connectionSource).setUsername("demo");
			((JdbcConnectionSource)connectionSource).setPassword("demo");			
			
			Dao<Demo,String> demoDao = DaoManager.createDao(connectionSource, Demo.class);
			
			TableUtils.createTableIfNotExists(connectionSource, Demo.class);
			
	        get("/hello", (req, res) -> "Hello World");
	        
	        get("/demo/:id", (req, res) -> {
		        Demo demo = demoDao.queryForId(req.params(":id"));
		        if (demo != null) {
		        	StringBuilder returnStr = new StringBuilder();
		        	returnStr.append("Hey " + demo.getGroup() + "! ");
		        	returnStr.append("at " + demo.getVenue() + " ");
		        	returnStr.append("on " + demo.getDate());
		            return returnStr.toString(); // or JSON? :-)
		        } else {
		            res.status(404); // 404 Not found
		            return "User not found";
		        }
	        });
	        

	        post("/demo", (req, res) -> {
			        String venue = req.queryParams("venue");
			        String group = req.queryParams("group");
			        String date = req.queryParams("date");
			                
			        Demo demo = new Demo();
			        demo.setDate(date);
			        demo.setGroup(group);
			        demo.setVenue(venue);
		                
			        demoDao.create(demo);
			                           
			        res.status(201); // 201 Created
			        return("Success");
			});

			
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e);
		};
		
    }
}
