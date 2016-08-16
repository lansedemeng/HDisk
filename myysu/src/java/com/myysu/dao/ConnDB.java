package com.model;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnDB {
	private Connection ct = null;
	//public static int cc;
	public String url = "jdbc:mysql://localhost:3306/hadoop";
	//public String name = "com.mysql.jdbc.Driver";
	public String user = "root";
	public String password = "";
	
	public Connection getConn(){
		
	try {
	//加载驱动
	Class.forName("com.mysql.jdbc.Driver");
			
	//得到连接
	ct = DriverManager.getConnection(url,user,password);

	} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	}
	return ct;
	}

}
