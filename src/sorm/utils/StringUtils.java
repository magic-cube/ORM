package sorm.utils;

/**
 * ��װ���ַ������õĲ���
 * @author hc
 *
 */
public class StringUtils {
	
	/**
	 * ��Ŀ���ַ�������ĸ��Ϊ��д
	 * @param str Ŀ���ַ���
	 * @return ����ĸ��Ϊ��д���ַ���
	 */
	public static String firstCahr2UpperCase(String str){
		/*
		 * Ŀ�꣺abcd-->Abcd
		 * ʵ�ֹ��̣�abcd-->ABCD-->Abcd
		 */
		return str.toUpperCase().substring(0,1)+str.substring(1);
	}
}
