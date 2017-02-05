package sorm.core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sorm.bean.ColumnInfo;
import sorm.bean.TableInfo;
import sorm.utils.JavaFileUtils;
import sorm.utils.StringUtils;

/**
 * 负责获取管理数据库所有表结构和类结构的关系，并可以根据表结构生成类结构。
 * @author hc
 *
 */
public class TableContext {

	/**
	 * 表名为key，表信息对象为value
	 */
	public static  Map<String,TableInfo>  tables = new HashMap<String,TableInfo>();
	
	/**
	 * 将po包下，类的class对象和表信息对象关联起来，便于重用！
	 */
	public static  Map<Class,TableInfo>  poClassTableMap = new HashMap<Class,TableInfo>();
	
	private TableContext(){}
	
	/*
	 * 我并不知道下面这段代码的具体逻辑。。。
	 * 如果想深究，可以去查一下DatabaseMetaData这个类
	 * 可以获取我们连接到的数据库的结构、存储等很多信息。如：

         1、数据库与用户，数据库标识符以及函数与存储过程。
         2、数据库限制。
         3、数据库支持不支持的功能。
         4、架构、编目、表、列和视图等。
	 */
	static {
		try {
			//初始化获得表的信息
			Connection con = DBManager.getConn();
			DatabaseMetaData dbmd = con.getMetaData(); 
			
			ResultSet tableRet = dbmd.getTables(null, "%","%",new String[]{"TABLE"}); 
			
			while(tableRet.next()){
				String tableName = (String) tableRet.getObject("TABLE_NAME");
				
				TableInfo ti = new TableInfo(tableName, new ArrayList<ColumnInfo>()
						,new HashMap<String, ColumnInfo>());
				/*
				 * 启动时就已经对tables这个map进行了填充
				 * 也就是说启动时就已经获得了数据库中的表信息，并存放进了map中
				 */
				tables.put(tableName, ti);
				
				ResultSet set = dbmd.getColumns(null, "%", tableName, "%");  //查询表中的所有字段
				while(set.next()){
					ColumnInfo ci = new ColumnInfo(set.getString("COLUMN_NAME"), 
							set.getString("TYPE_NAME"), 0);
					ti.getColumns().put(set.getString("COLUMN_NAME"), ci);
				}
				
				ResultSet set2 = dbmd.getPrimaryKeys(null, "%", tableName);  //查询t_user表中的主键
				while(set2.next()){
					ColumnInfo ci2 = (ColumnInfo) ti.getColumns().get(set2.getObject("COLUMN_NAME"));
					ci2.setKeyType(1);  //设置为主键类型
					ti.getPriKeys().add(ci2);
				}
				
				if(ti.getPriKeys().size()>0){  //取唯一主键。。方便使用。如果是联合主键。则为空！
					ti.setOnlyPriKey(ti.getPriKeys().get(0));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		/*
		 * 每次启动时
		 * 更新类结构
		 */
		updateJavaPOFile();
		
		/*
		 * 加载po包下所有的类，便于重用，提高了效率
		 * 而不用每次用的时候都去加载
		 */
		loadPOTables();
	}
	
	
	/**
	 * 根据表结构，更新配置的po包下面的java类
	 * 实现了从表结构转换到类结构
	 * 在项目启动时调用
	 */
	public static void updateJavaPOFile(){
		//通过TableContext可以拿到数据库中表的信息
		Map<String,TableInfo> map=TableContext.tables;
		for(TableInfo t:map.values()){
			JavaFileUtils.creatJavaPOFile(t, new MySqlTypeConvertor());
		}
	}
	
	/**
	 * 加载po包下的类
	 */
	public static void loadPOTables(){
		
		for(TableInfo tableInfo:tables.values()){
			
			try {
				
				Class c = Class.forName(DBManager.getConf().getPoPackage()+"."
				+StringUtils.firstCahr2UpperCase(tableInfo.getTname()));
				poClassTableMap.put(c, tableInfo);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static void main(String[] args) {
		 Map<String,TableInfo>  tables = TableContext.tables;
		 System.out.println(tables);
	}

}
