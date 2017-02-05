package sorm.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sorm.bean.ColumnInfo;
import sorm.bean.JavaFieldGetSet;
import sorm.bean.TableInfo;
import sorm.core.DBManager;
import sorm.core.MySqlTypeConvertor;
import sorm.core.TableContext;
import sorm.core.TypeConvertor;

/**
 * 封装了Java文件（源代码）常用的操作
 * @author hc
 *
 */
public class JavaFileUtils {
	
	/**
	 * 根据字段信息生成java属性信息，如：varchar username-->private String username;以及相应的set和get方法源码
	 * @param column 字段信息
	 * @param convertor 类型转化器
	 * @return java属性和set/get方法源码
	 */
	public static JavaFieldGetSet creatJavaFieldGetSetSRC(ColumnInfo column,TypeConvertor convertor){
		JavaFieldGetSet jfgs=new JavaFieldGetSet();
		
		String javaFieldType=convertor.databaseType2JavaType(column.getDataType());
		
		jfgs.setFieldInfo("\tprivate "+javaFieldType+" "+column.getName()+";\n");
		
		/*
		 * 生成get方法源码，格式如下
		 * public String getUsername(){
		 * 		return username;
		 * }
		 */
		StringBuilder getSrc=new StringBuilder();
		getSrc.append("\tpublic "+javaFieldType+" get"+StringUtils.firstCahr2UpperCase(column.getName())+"(){\n");
		getSrc.append("\t\treturn "+column.getName()+" ;\n");
		getSrc.append("\t}\n");
		jfgs.setGetInfo(getSrc.toString());
		
		/*
		 * 生成set方法源码,格式如下
		 * public void setUsername(String username){
		 * 		this.username=username;
		 * }
		 */
		StringBuilder setSrc=new StringBuilder();
		setSrc.append("\tpublic void set"+StringUtils.firstCahr2UpperCase(column.getName())+"(");
		setSrc.append(javaFieldType+" "+column.getName()+"){\n");
		setSrc.append("\t\tthis."+column.getName()+"="+column.getName()+";\n");
		setSrc.append("\t}\n");
		jfgs.setSetInfo(setSrc.toString());
		
		return jfgs;
	}
	
	/**
	 * 根据表信息生成java类的源代码
	 * @param tableInfo 表信息
	 * @param convertor 数据类型转化器
	 * @return java类的源代码
	 */
	public static String creatJavaSrc(TableInfo tableInfo,TypeConvertor convertor){
		
		/*
		 * 目前只生成了基本的属性和set、get方法，
		 * 至于各种构造器，留待以后完善。。。
		 */
		
		//使用map存储的字段信息，当然也可以使用list来存储
		Map<String,ColumnInfo> columns=tableInfo.getColumns();
		
		//使用list存放java属性信息
		List<JavaFieldGetSet> javaFields=new ArrayList<JavaFieldGetSet>();
		
		for(ColumnInfo c:columns.values()){
			javaFields.add(creatJavaFieldGetSetSRC(c,convertor));
		}
		
		StringBuilder src=new StringBuilder();
		
		//生成package语句
		src.append("package "+DBManager.getConf().getPoPackage()+";\n\n");
		
		//生成import语句,直接导入所有的
		src.append("import java.sql.*;\n");
		src.append("import java.util.*;\n\n");
		
		//生成类声明语句
		src.append("public class "+StringUtils.firstCahr2UpperCase(tableInfo.getTname())+" {\n\n");
		
		//生成属性列表
		for(JavaFieldGetSet f:javaFields){
			src.append(f.getFieldInfo());
		}
		
		//生成get方法列表
		for(JavaFieldGetSet f:javaFields){
			src.append(f.getGetInfo());
		}
		
		//生成set方法列表
		for(JavaFieldGetSet f:javaFields){
			src.append(f.getSetInfo());
		}
		
		//生成类结束
		src.append("}\n");
		
////源码测试。。。
//System.out.println(src);
		
		return src.toString();
	}
	
	
	
	public static void creatJavaPOFile(TableInfo tableInfo,TypeConvertor convertor){
		String src=creatJavaSrc(tableInfo,convertor);
		
		/*
		 * 资源文件中，srcPath为项目的路径，poPackage为包路径
		 */
		String srcPath=DBManager.getConf().getSrcPath()+"\\";
		/*
		 * 将包名中的点转换成\\
		 * 正则中\为转义字符，java中一个\变两个\
		 * \\. 和 \\\\  或者/
		 */
		String packagePath=DBManager.getConf().getPoPackage().replaceAll("\\.", "\\\\");

		File f=new File(srcPath+packagePath);
////测试文件路径是否正确
//System.out.println(f.getAbsolutePath()+"  *************");    
		/*
		 * 指定目录不存在，则帮助用户建立起来
		 * 当然，也可以报个错给用户，提示一下
		 */
		if(!f.exists()){
			f.mkdirs();
		}
		
		BufferedWriter bw=null;
		try {
			/*
			 * 向指定路径下写java源代码，使用流输出到资源文件中指定的路径,由Configuration管理着资源文件
			 * 而DBManager是大总管，什么都管
			 */
//System.out.println(f.getAbsoluteFile()+"\\\\"+StringUtils.firstCahr2UpperCase(tableInfo.getTname())+".java");
			bw=new BufferedWriter(new FileWriter(f.getAbsoluteFile()+"/"+StringUtils.firstCahr2UpperCase(tableInfo.getTname())+".java"));
			System.out.println("建立表"+tableInfo.getTname()+"对应的java类"+StringUtils.firstCahr2UpperCase(tableInfo.getTname())+".java");
			bw.write(src);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(bw!=null){
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	/*
	 * 测试生成的源码
	 */
	public static void main(String[] args) {
		
//		ColumnInfo ci=new ColumnInfo("id","int",0);
//		JavaFieldGetSet j=creatJavaFieldGetSetSRC(ci,new MySqlTypeConvertor());
//		//重写JavaFieldGetSet的toString
//		System.out.println(j);
//		//System.out.println(j.getFieldInfo()+j.getGetInfo()+j.getSetInfo());
		
//		//通过TableContext可以拿到数据库中表的信息
//		Map<String,TableInfo> map=TableContext.tables;
//		TableInfo t=map.get("emp");
//		creatJavaSrc(t, new MySqlTypeConvertor());
		
		
		//通过TableContext可以拿到数据库中表的信息
		Map<String,TableInfo> map=TableContext.tables;
		for(TableInfo t:map.values()){
			creatJavaPOFile(t, new MySqlTypeConvertor());
		}
		
	}
	
}
