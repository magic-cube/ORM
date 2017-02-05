package sorm.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import sorm.bean.ColumnInfo;
import sorm.bean.TableInfo;
import sorm.po.Emp;
import sorm.utils.JDBCUtils;
import sorm.utils.ReflectUtils;
import sorm.utils.StringUtils;
import sorm.vo.EmpVo;

/**
 * 负责针对MySql数据库的查询
 * @author hc
 *
 */
public class MySqlQuery implements Query{

	/*
	 * 测试delete方法
	 * 实现了对象到sql语句的封装
	 */
	@Test
	public void testDelete(){
		Emp e=new Emp();
		e.setId(4);
		new MySqlQuery().delete(e);
	}
	
	
	/*
	 * 测试insert方法 
	 */
	@Test
	public void testInsert(){
		Emp e=new Emp();
		/* 
		 * 因为测试表中的id设置为自增
		 * 想来，目前Mysql测试无bug，Oracle自增的话待测试
		 * 此处id，不设自增，设置的话为设定值
		 * 
		 */
		e.setId(4);
		
		e.setEmpname("tom");
		e.setAge(18);
		e.setBirthday(new java.sql.Date(System.currentTimeMillis()));
		
		new MySqlQuery().insert(e); 
	}
	
	/*
	 * 测试update方法
	 */
	@Test
	public void testUpdate(){
		Emp e=new Emp();
		e.setId(1);
		
		e.setEmpname("lili");
		e.setAge(28);
		e.setBirthday(new java.sql.Date(System.currentTimeMillis()));
		e.setSalary(3000.8);
		new MySqlQuery().update(e, new String[]{"empname","age","salary"});
	}
	
	/*
	 * 测试查询多行多列
	 */
	@Test
	public  void testQueryRows(){
		List<Emp> list=new MySqlQuery().queryRows("select id,empname,age,salary from emp where age>? and salary<?", 
				Emp.class, new Object[]{10,5000});
		System.out.println(list);
		
		for(Emp e:list){
			System.out.println(e.getEmpname()+" "+e.getAge()+" "+e.getSalary());
		}
	}
	
	/*
	 * 测试联表查询(需要自己去新建一个javabean去专门封装一些信息)
	 * 多行多列 
	 */
	@Test
	public void testQueryRows2(){
		
		String sql="select e.id,e.empname,salary+bonus 'xinshui',age,d.dname 'deptName',d.address 'deptAddr' "
				+ "from emp e join dept d on e.deptId=d.id";
		List<EmpVo> list=new MySqlQuery().queryRows(sql, 
				EmpVo.class,null);
		System.out.println(list);
		
		for(EmpVo e:list){
			System.out.println(e.getEmpname()+"- "+e.getXinshui()+"- "+e.getDeptAddr());
		}
		
	}
	
	@Test
	public void testQueryValue(){
		Object obj=new MySqlQuery().queryValue("select age from emp where salary>?", new Object[]{1000});
		System.out.println(obj);
	}
	
	@Override
	public int executeDML(String sql, Object[] params) {
		Connection conn=DBManager.getConn();
		
		int count=0;
		PreparedStatement ps=null;
		try {
			ps=conn.prepareStatement(sql);
			
			//给sql设置参数
			JDBCUtils.handleParams(ps, params);
System.out.println(ps);
			count=ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DBManager.close(ps, conn);
		}
		
		return count;
	}
	
	
	@Override
	public void insert(Object obj) {
		/*
		 * obj-->表中 insert into 表名 (id,name,pwd) values(?,?,?)
		 */
		Class c=obj.getClass();
		
		List<Object> params=new ArrayList<Object>(); //存储sql的参数对象
		
		
		//获取表信息
		TableInfo tableInfo=TableContext.poClassTableMap.get(c);
		//目前，只能处理数据库来维护自增主键的方式
		
		StringBuilder sql=new StringBuilder("insert into "+tableInfo.getTname()+"(");
		int countNotNullField=0;//计算不为null的属性值,为了知道后面有几个问号？ 需要拼接
		
		//获取所有属性
		Field[] fs=c.getDeclaredFields();
		for(Field f:fs){
			//通过反射获取属性的名字
			String fieldName=f.getName();
			//通过反射获取该属性的get方法，进而获取属性的值
			Object fieldValue=ReflectUtils.invokGet(fieldName, obj);
			
			if(fieldValue!=null){
				countNotNullField++;//计数器
				sql.append(fieldName+",");//参数名
				params.add(fieldValue);//参数对象
			}
		}
		
		/*
		 * 将目前的sql语句最后的一个，换成）
		 * 即从insert into 表名 (id,name,pwd,  -->insert into 表名 (id,name,pwd)
		 */
		sql.setCharAt(sql.length()-1, ')');
		sql.append(" values (");
		for(int i=0;i<countNotNullField;i++){
			sql.append("?,");
		}
		//同样，将最后一个逗号变为)
		sql.setCharAt(sql.length()-1, ')');
		
		/*
		 * 此处的sql为StringBuilder，需要转成String
		 * 此处的params为List，需要转换成数组
		 */
		executeDML(sql.toString(), params.toArray());
	}

	@Override
	public void delete(Class clazz, Object id) {
		/*
		 * Emp.class,2 -->delete from emp where id=2
		 * 通过Class对象找TableInfo
		 */
		TableInfo tableInfo=TableContext.poClassTableMap.get(clazz);
		
		//获得主键
		ColumnInfo onlyPriKey=tableInfo.getOnlyPriKey();
		
		String sql="delete from "+tableInfo.getTname()+" where "+onlyPriKey.getName()+"=? ";
		
		executeDML(sql , new Object[]{id});
	}

	@Override
	public void delete(Object obj) {
		Class clazz=obj.getClass();
		
		TableInfo tableInfo=TableContext.poClassTableMap.get(clazz);
		
		//主键
		ColumnInfo onlyPriKey=tableInfo.getOnlyPriKey();
		
		/*
		 * 通过反射机制，调用属性对应的set和get方法
		 */
		
		Object priKeyValue=ReflectUtils.invokGet(onlyPriKey.getName(), obj);
		delete(clazz, priKeyValue); 
		
	}

	@Override
	public int update(Object obj, String[] fieldNames) {
		/*
		 * obj{"uname","pwd"},需要你传入需要修改的参数列表
		 * update 表名 set uname=?,pwd=? where id=?
		 */
		Class c=obj.getClass();
		
		List<Object> params=new ArrayList<Object>(); //存储sql的参数对象
		
		//获取表信息
		TableInfo tableInfo=TableContext.poClassTableMap.get(c);
		/*
		 * 获取主键
		 * 。。。还是那句话，目前只能处理主键为一个的情况
		 */
		ColumnInfo priKey=tableInfo.getOnlyPriKey();
		
		StringBuilder sql=new StringBuilder("update "+tableInfo.getTname()+" set ");
		for(String fname:fieldNames){
			//通过反射获取该属性的get方法，进而获取属性的值
			Object fvalue=ReflectUtils.invokGet(fname, obj);
			params.add(fvalue);
			sql.append(fname+"=?,");
		}
		//使用空格替换最后多出来的逗号
		sql.setCharAt(sql.length()-1, ' ');
		sql.append(" where ");
		sql.append(priKey.getName()+"=? ");
		
		//反射获取主键的值，（不是名字）向参数列表中添加主键的值
		params.add(ReflectUtils.invokGet(priKey.getName(), obj));
		
		return executeDML(sql.toString(),params.toArray());
	}

	@Override
	public List queryRows(String sql, Class clazz, Object[] params) {
		
		Connection conn=DBManager.getConn();
		
		List list = null;//存放查询结果的容器
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			ps=conn.prepareStatement(sql);
			
			//给sql设置参数
			JDBCUtils.handleParams(ps, params);
System.out.println(ps);
			rs=ps.executeQuery();
			//源信息，包含了结果集中的行和列信息
			ResultSetMetaData metaData=rs.getMetaData();
			
			//多行
			while(rs.next()){
				if(list==null){
					list=new ArrayList();
				}
				Object rowObject =clazz.newInstance();//调用javabean的无参构造器
				
				//sleect username,pwd,age from user where id=? and age?18
				//每次循环向Object中填值
				//多列
				for(int i=0;i<metaData.getColumnCount();i++){
					//获取列名,假设获取到的为username 
					String columnName=metaData.getColumnLabel(i+1);
					Object columnValue=rs.getObject(i+1);
					
					//调用rowObject对象的setUsername(String uname)方法，将columnValue的值设置进去
					ReflectUtils.invokSet(rowObject, columnName, columnValue);
				}
				
				//将存储了行信息的Object存到List中
				list.add(rowObject);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBManager.close(ps, conn);
		}
		
		return list;
	}

	@Override
	public Object queryUniRow(String sql, Class clazz, Object[] params) {
		List list=queryRows(sql, clazz, params);
		/*
		 * 拿出list中的第一个值即可,当然，查一个的时候也只有一个值
		 */
		return (list==null&&list.size()>0)?null:list.get(0);
	}

	@Override
	public Object queryValue(String sql, Object[] params) {
		Connection conn=DBManager.getConn();
		
		Object value = null;//存放查询结果的对象
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			ps=conn.prepareStatement(sql);
			
			//给sql设置参数
			JDBCUtils.handleParams(ps, params);
System.out.println(ps);
			rs=ps.executeQuery();
			//多行
			while(rs.next()){
				//select count(*) from user
				value=rs.getObject(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DBManager.close(ps, conn);
		}
		
		return value;
	}
	
	/*
	 * 使用sql查询，要求返回一个数字
	 */
	@Override
	public Number queryNumber(String sql, Object[] params) {
		return (Number)queryValue(sql, params);
	}

}
