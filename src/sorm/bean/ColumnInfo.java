package sorm.bean;

/**
 * ��װ����һ���ֶε���Ϣ
 * @author hc
 * @version 0.1 �汾��
 *
 */
public class ColumnInfo {
	
	/**
	 * �ֶ�����
	 */
	private String name;
	
	/**
	 * �ֶε���������
	 */
	private String dataType;
	
	/**
	 * �ֶεļ����ͣ�0����ͨ����1�������
	 */
	private int keyType;

	public ColumnInfo() {
		
	}
	
	public ColumnInfo(String name, String dataType, int keyType) {
		super();
		this.name = name;
		this.dataType = dataType;
		this.keyType = keyType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public int getKeyType() {
		return keyType;
	}

	public void setKeyType(int keyType) {
		this.keyType = keyType;
	}
	
	
} 
