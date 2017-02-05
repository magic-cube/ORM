package sorm.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * ��װ�˷��䳣�õĲ���
 * @author hc
 *
 */
public class ReflectUtils {
	
	/**
	 * ����obj�����get��������Ӧ����fieldName��get����
	 * @param fieldName 
	 * @param obj
	 * @return
	 */
	public static Object invokGet(String fieldName,Object obj){
		/*
		 * ͨ��������ƣ��������Զ�Ӧ��set��get����
		 */
		try {
			Class clazz=obj.getClass();
			Method m=clazz.getDeclaredMethod("get"+StringUtils.firstCahr2UpperCase(fieldName), null);
			//ͨ������ȥִ������get����()�ķ������᷵��һ��������ֵ
			return m.invoke(obj, null);//������ֵ
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	public static void invokSet(Object obj,String columnName,Object columnValue){
		//�˴��ڶ�������Ϊ����Ĳ������ͣ�Ҳ���ǻ�ȡ����volumnValue�����ͣ���������class���󼴿�
		Method m;
		try {
			m = obj.getClass().getDeclaredMethod("set"+StringUtils.firstCahr2UpperCase(columnName),columnValue.getClass());
			m.invoke(obj, columnValue);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
	}
	
}




