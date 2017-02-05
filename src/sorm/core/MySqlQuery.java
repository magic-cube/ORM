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
 * �������MySql���ݿ�Ĳ�ѯ
 * @author hc
 *
 */
public class MySqlQuery implements Query{

	/*
	 * ����delete����
	 * ʵ���˶���sql���ķ�װ
	 */
	@Test
	public void testDelete(){
		Emp e=new Emp();
		e.setId(4);
		new MySqlQuery().delete(e);
	}
	
	
	/*
	 * ����insert���� 
	 */
	@Test
	public void testInsert(){
		Emp e=new Emp();
		/* 
		 * ��Ϊ���Ա��е�id����Ϊ����
		 * ������ĿǰMysql������bug��Oracle�����Ļ�������
		 * �˴�id���������������õĻ�Ϊ�趨ֵ
		 * 
		 */
		e.setId(4);
		
		e.setEmpname("tom");
		e.setAge(18);
		e.setBirthday(new java.sql.Date(System.currentTimeMillis()));
		
		new MySqlQuery().insert(e); 
	}
	
	/*
	 * ����update����
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
	 * ���Բ�ѯ���ж���
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
	 * ���������ѯ(��Ҫ�Լ�ȥ�½�һ��javabeanȥר�ŷ�װһЩ��Ϣ)
	 * ���ж��� 
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
			
			//��sql���ò���
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
		 * obj-->���� insert into ���� (id,name,pwd) values(?,?,?)
		 */
		Class c=obj.getClass();
		
		List<Object> params=new ArrayList<Object>(); //�洢sql�Ĳ�������
		
		
		//��ȡ����Ϣ
		TableInfo tableInfo=TableContext.poClassTableMap.get(c);
		//Ŀǰ��ֻ�ܴ������ݿ���ά�����������ķ�ʽ
		
		StringBuilder sql=new StringBuilder("insert into "+tableInfo.getTname()+"(");
		int countNotNullField=0;//���㲻Ϊnull������ֵ,Ϊ��֪�������м����ʺţ� ��Ҫƴ��
		
		//��ȡ��������
		Field[] fs=c.getDeclaredFields();
		for(Field f:fs){
			//ͨ�������ȡ���Ե�����
			String fieldName=f.getName();
			//ͨ�������ȡ�����Ե�get������������ȡ���Ե�ֵ
			Object fieldValue=ReflectUtils.invokGet(fieldName, obj);
			
			if(fieldValue!=null){
				countNotNullField++;//������
				sql.append(fieldName+",");//������
				params.add(fieldValue);//��������
			}
		}
		
		/*
		 * ��Ŀǰ��sql�������һ�������ɣ�
		 * ����insert into ���� (id,name,pwd,  -->insert into ���� (id,name,pwd)
		 */
		sql.setCharAt(sql.length()-1, ')');
		sql.append(" values (");
		for(int i=0;i<countNotNullField;i++){
			sql.append("?,");
		}
		//ͬ���������һ�����ű�Ϊ)
		sql.setCharAt(sql.length()-1, ')');
		
		/*
		 * �˴���sqlΪStringBuilder����Ҫת��String
		 * �˴���paramsΪList����Ҫת��������
		 */
		executeDML(sql.toString(), params.toArray());
	}

	@Override
	public void delete(Class clazz, Object id) {
		/*
		 * Emp.class,2 -->delete from emp where id=2
		 * ͨ��Class������TableInfo
		 */
		TableInfo tableInfo=TableContext.poClassTableMap.get(clazz);
		
		//�������
		ColumnInfo onlyPriKey=tableInfo.getOnlyPriKey();
		
		String sql="delete from "+tableInfo.getTname()+" where "+onlyPriKey.getName()+"=? ";
		
		executeDML(sql , new Object[]{id});
	}

	@Override
	public void delete(Object obj) {
		Class clazz=obj.getClass();
		
		TableInfo tableInfo=TableContext.poClassTableMap.get(clazz);
		
		//����
		ColumnInfo onlyPriKey=tableInfo.getOnlyPriKey();
		
		/*
		 * ͨ��������ƣ��������Զ�Ӧ��set��get����
		 */
		
		Object priKeyValue=ReflectUtils.invokGet(onlyPriKey.getName(), obj);
		delete(clazz, priKeyValue); 
		
	}

	@Override
	public int update(Object obj, String[] fieldNames) {
		/*
		 * obj{"uname","pwd"},��Ҫ�㴫����Ҫ�޸ĵĲ����б�
		 * update ���� set uname=?,pwd=? where id=?
		 */
		Class c=obj.getClass();
		
		List<Object> params=new ArrayList<Object>(); //�洢sql�Ĳ�������
		
		//��ȡ����Ϣ
		TableInfo tableInfo=TableContext.poClassTableMap.get(c);
		/*
		 * ��ȡ����
		 * �����������Ǿ仰��Ŀǰֻ�ܴ�������Ϊһ�������
		 */
		ColumnInfo priKey=tableInfo.getOnlyPriKey();
		
		StringBuilder sql=new StringBuilder("update "+tableInfo.getTname()+" set ");
		for(String fname:fieldNames){
			//ͨ�������ȡ�����Ե�get������������ȡ���Ե�ֵ
			Object fvalue=ReflectUtils.invokGet(fname, obj);
			params.add(fvalue);
			sql.append(fname+"=?,");
		}
		//ʹ�ÿո��滻��������Ķ���
		sql.setCharAt(sql.length()-1, ' ');
		sql.append(" where ");
		sql.append(priKey.getName()+"=? ");
		
		//�����ȡ������ֵ�����������֣�������б������������ֵ
		params.add(ReflectUtils.invokGet(priKey.getName(), obj));
		
		return executeDML(sql.toString(),params.toArray());
	}

	@Override
	public List queryRows(String sql, Class clazz, Object[] params) {
		
		Connection conn=DBManager.getConn();
		
		List list = null;//��Ų�ѯ���������
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			ps=conn.prepareStatement(sql);
			
			//��sql���ò���
			JDBCUtils.handleParams(ps, params);
System.out.println(ps);
			rs=ps.executeQuery();
			//Դ��Ϣ�������˽�����е��к�����Ϣ
			ResultSetMetaData metaData=rs.getMetaData();
			
			//����
			while(rs.next()){
				if(list==null){
					list=new ArrayList();
				}
				Object rowObject =clazz.newInstance();//����javabean���޲ι�����
				
				//sleect username,pwd,age from user where id=? and age?18
				//ÿ��ѭ����Object����ֵ
				//����
				for(int i=0;i<metaData.getColumnCount();i++){
					//��ȡ����,�����ȡ����Ϊusername 
					String columnName=metaData.getColumnLabel(i+1);
					Object columnValue=rs.getObject(i+1);
					
					//����rowObject�����setUsername(String uname)��������columnValue��ֵ���ý�ȥ
					ReflectUtils.invokSet(rowObject, columnName, columnValue);
				}
				
				//���洢������Ϣ��Object�浽List��
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
		 * �ó�list�еĵ�һ��ֵ����,��Ȼ����һ����ʱ��Ҳֻ��һ��ֵ
		 */
		return (list==null&&list.size()>0)?null:list.get(0);
	}

	@Override
	public Object queryValue(String sql, Object[] params) {
		Connection conn=DBManager.getConn();
		
		Object value = null;//��Ų�ѯ����Ķ���
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			ps=conn.prepareStatement(sql);
			
			//��sql���ò���
			JDBCUtils.handleParams(ps, params);
System.out.println(ps);
			rs=ps.executeQuery();
			//����
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
	 * ʹ��sql��ѯ��Ҫ�󷵻�һ������
	 */
	@Override
	public Number queryNumber(String sql, Object[] params) {
		return (Number)queryValue(sql, params);
	}

}
