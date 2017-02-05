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
 * ����������Ϣ��ά�����Ӷ���Ĺ����������ӳع��ܣ�
 * @author hc
 *
 */
public class DBManager {
	
	/**
	 * ���״μ���DBManagerʱ�ͻὫ��Դ�ļ��е�ֵ����Configeration���javabean
	 */
	private static Configuration conf;
	
	static{//��̬�����
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
		 * ���������ݿ�����ӣ��������������
		 */
		Connection conn=null;
		try {
			/*
			 * Ŀǰֱ�ӽ������ӣ������������ӳش���,���Ч��
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
		 * ���ڹرճ��õ���������
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
	//���أ�ֻ�ر�Statement��Connection
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
	//ֻ�ر�connection
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
	 * ����configuration����
	 * @return
	 */
	public static Configuration getConf(){
		return conf;
	}
	
}
