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
 * ��װ��Java�ļ���Դ���룩���õĲ���
 * @author hc
 *
 */
public class JavaFileUtils {
	
	/**
	 * �����ֶ���Ϣ����java������Ϣ���磺varchar username-->private String username;�Լ���Ӧ��set��get����Դ��
	 * @param column �ֶ���Ϣ
	 * @param convertor ����ת����
	 * @return java���Ժ�set/get����Դ��
	 */
	public static JavaFieldGetSet creatJavaFieldGetSetSRC(ColumnInfo column,TypeConvertor convertor){
		JavaFieldGetSet jfgs=new JavaFieldGetSet();
		
		String javaFieldType=convertor.databaseType2JavaType(column.getDataType());
		
		jfgs.setFieldInfo("\tprivate "+javaFieldType+" "+column.getName()+";\n");
		
		/*
		 * ����get����Դ�룬��ʽ����
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
		 * ����set����Դ��,��ʽ����
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
	 * ���ݱ���Ϣ����java���Դ����
	 * @param tableInfo ����Ϣ
	 * @param convertor ��������ת����
	 * @return java���Դ����
	 */
	public static String creatJavaSrc(TableInfo tableInfo,TypeConvertor convertor){
		
		/*
		 * Ŀǰֻ�����˻��������Ժ�set��get������
		 * ���ڸ��ֹ������������Ժ����ơ�����
		 */
		
		//ʹ��map�洢���ֶ���Ϣ����ȻҲ����ʹ��list���洢
		Map<String,ColumnInfo> columns=tableInfo.getColumns();
		
		//ʹ��list���java������Ϣ
		List<JavaFieldGetSet> javaFields=new ArrayList<JavaFieldGetSet>();
		
		for(ColumnInfo c:columns.values()){
			javaFields.add(creatJavaFieldGetSetSRC(c,convertor));
		}
		
		StringBuilder src=new StringBuilder();
		
		//����package���
		src.append("package "+DBManager.getConf().getPoPackage()+";\n\n");
		
		//����import���,ֱ�ӵ������е�
		src.append("import java.sql.*;\n");
		src.append("import java.util.*;\n\n");
		
		//�������������
		src.append("public class "+StringUtils.firstCahr2UpperCase(tableInfo.getTname())+" {\n\n");
		
		//���������б�
		for(JavaFieldGetSet f:javaFields){
			src.append(f.getFieldInfo());
		}
		
		//����get�����б�
		for(JavaFieldGetSet f:javaFields){
			src.append(f.getGetInfo());
		}
		
		//����set�����б�
		for(JavaFieldGetSet f:javaFields){
			src.append(f.getSetInfo());
		}
		
		//���������
		src.append("}\n");
		
////Դ����ԡ�����
//System.out.println(src);
		
		return src.toString();
	}
	
	
	
	public static void creatJavaPOFile(TableInfo tableInfo,TypeConvertor convertor){
		String src=creatJavaSrc(tableInfo,convertor);
		
		/*
		 * ��Դ�ļ��У�srcPathΪ��Ŀ��·����poPackageΪ��·��
		 */
		String srcPath=DBManager.getConf().getSrcPath()+"\\";
		/*
		 * �������еĵ�ת����\\
		 * ������\Ϊת���ַ���java��һ��\������\
		 * \\. �� \\\\  ����/
		 */
		String packagePath=DBManager.getConf().getPoPackage().replaceAll("\\.", "\\\\");

		File f=new File(srcPath+packagePath);
////�����ļ�·���Ƿ���ȷ
//System.out.println(f.getAbsolutePath()+"  *************");    
		/*
		 * ָ��Ŀ¼�����ڣ�������û���������
		 * ��Ȼ��Ҳ���Ա�������û�����ʾһ��
		 */
		if(!f.exists()){
			f.mkdirs();
		}
		
		BufferedWriter bw=null;
		try {
			/*
			 * ��ָ��·����дjavaԴ���룬ʹ�����������Դ�ļ���ָ����·��,��Configuration��������Դ�ļ�
			 * ��DBManager�Ǵ��ܹܣ�ʲô����
			 */
//System.out.println(f.getAbsoluteFile()+"\\\\"+StringUtils.firstCahr2UpperCase(tableInfo.getTname())+".java");
			bw=new BufferedWriter(new FileWriter(f.getAbsoluteFile()+"/"+StringUtils.firstCahr2UpperCase(tableInfo.getTname())+".java"));
			System.out.println("������"+tableInfo.getTname()+"��Ӧ��java��"+StringUtils.firstCahr2UpperCase(tableInfo.getTname())+".java");
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
	 * �������ɵ�Դ��
	 */
	public static void main(String[] args) {
		
//		ColumnInfo ci=new ColumnInfo("id","int",0);
//		JavaFieldGetSet j=creatJavaFieldGetSetSRC(ci,new MySqlTypeConvertor());
//		//��дJavaFieldGetSet��toString
//		System.out.println(j);
//		//System.out.println(j.getFieldInfo()+j.getGetInfo()+j.getSetInfo());
		
//		//ͨ��TableContext�����õ����ݿ��б����Ϣ
//		Map<String,TableInfo> map=TableContext.tables;
//		TableInfo t=map.get("emp");
//		creatJavaSrc(t, new MySqlTypeConvertor());
		
		
		//ͨ��TableContext�����õ����ݿ��б����Ϣ
		Map<String,TableInfo> map=TableContext.tables;
		for(TableInfo t:map.values()){
			creatJavaPOFile(t, new MySqlTypeConvertor());
		}
		
	}
	
}
