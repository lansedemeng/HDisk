package com.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
 

public class UserBeanCl {
	private Statement sm = null;
	private Connection ct = null;
	private ResultSet rs = null;
	
	
	public void close(){
		try {
			
			
		if(sm != null){	
			sm.close();
			sm = null;
		}
		
		if(ct != null){
			ct.close();
			ct = null;
		}
		
		
		if(rs != null){
			rs.close();
			rs = null;
		}
		
		
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	//检查登录用户是否合法
		public boolean checkUser(String user, String password){
			boolean b = false;		
			try {
				
				//获得连接
				ct = new ConnDB().getConn();
				//创建statement
				sm = ct.createStatement();
				
				rs = sm.executeQuery("select * from user_info where username='"+user+"'");
				
				
				if(rs.next()){
					//说明用户存在
					String pwd = rs.getString(4);
					if(password.equals(pwd)){
						//说明密码正确
						b = true;
					}else{
						b = false;
						System.out.println("密码错误！");
					}
					
				}else{
					b = false;
				}	
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				this.close();
			}
			
			return b;
		}


		
}
