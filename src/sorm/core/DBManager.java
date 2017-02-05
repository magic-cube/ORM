package sorm.core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import sorm.bean.Configuration;

/**
 * 根据配置信息，维持连接对象的管理（增加连接池功能）
 * @author hc
 *
 */
public class DBManager {
	
	/**
	 * 在首次加载DBManager时就会将资源文件中的值赋给Configeration这个javabean
	 */
	private static Configuration conf;
	
	static{//静态代码块
		Properties pros=new Properties();
		try {
			pros.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		conf=new Configuration();
		conf.setDriver(pros.getProperty("driver"));
		conf.setPoPackage(pros.getProperty("poPackage"));
		conf.setPwd(pros.getProperty("pwd"));
		conf.setSrcPath(pros.getProperty("srcPath"));
		conf.setUrl(pros.getProperty("url"));
		conf.setUser(pros.getProperty("user"));
		conf.setUsingDB(pros.getProperty("usingDB"));
	}
	public static Connection getConn(){
		/*
		 * 建立与数据库的连接，并返回这个连接
		 */
		Connection conn=null;
		try {
			/*
			 * 目前直接建立连接，后期增加连接池处理,提高效率
			 */
			Class.forName(conf.getDriver());
			conn=DriverManager.getConnection(conf.getUrl(),conf.getUser(),conf.getPwd());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
		return conn;
	}
	
	public static void close(ResultSet rs,Statement ps,Connection conn){
		/*
		 * 用于关闭常用的三个连接
		 */
		if(rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(ps!=null){
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	//重载，只关闭Statement和Connection
	public static void close(Statement ps,Connection conn){

		if(ps!=null){
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	//只关闭connection
	public static void close(Connection conn){
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 返回configuration对象
	 * @return
	 */
	public static Configuration getConf(){
		return conf;
	}
	
}
