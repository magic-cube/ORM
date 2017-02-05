package sorm.core;
/**
 * ����java�������ͺ����ݿ��������͵��໥ת��
 * @author hc
 *
 */
public interface TypeConvertor {
	
	/**
	 * �����ݿ���������ת����java����������
	 * @param columnType ���ݿ��ֶε���������
	 * @return java����������
	 */
	public String databaseType2JavaType(String columnType);
	
	
	/**
	 * ��java��������ת�������ݿ���������
	 * @param javaDataType java��������
	 * @return ���ݿ���������
	 */
	public String javaType2DatabaseType(String javaDataType);
	
	
}
