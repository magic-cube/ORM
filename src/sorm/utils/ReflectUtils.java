package sorm.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 封装了反射常用的操作
 * @author hc
 *
 */
public class ReflectUtils {
	
	/**
	 * 调用obj对象的get方法，对应属性fieldName的get方法
	 * @param fieldName 
	 * @param obj
	 * @return
	 */
	public static Object invokGet(String fieldName,Object obj){
		/*
		 * 通过反射机制，调用属性对应的set和get方法
		 */
		try {
			Class clazz=obj.getClass();
			Method m=clazz.getDeclaredMethod("get"+StringUtils.firstCahr2UpperCase(fieldName), null);
			//通过反射去执行了如get主键()的方法，会返回一个主键的值
			return m.invoke(obj, null);//主键的值
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	public static void invokSet(Object obj,String columnName,Object columnValue){
		//此处第二个参数为传入的参数类型，也就是获取到的volumnValue的类型，传入它的class对象即可
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




