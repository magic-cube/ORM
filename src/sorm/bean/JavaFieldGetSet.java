package sorm.bean;

/**
 * ��װ��java���Ժ�get��set������Դ����
 * @author hc
 *
 */
public class JavaFieldGetSet {
	
	/**
	 * ���Ե�Դ����Ϣ
	 */
	private String fieldInfo;
	
	/**
	 * get������Դ����Ϣ public int getUserId(){}
	 */
	private String getInfo;
	
	/**
	 * set������Դ����Ϣ public void setUserId(){}
	 */
	private String setInfo;
	
	@Override
	public String toString(){
		System.out.println(fieldInfo);
		System.out.println(getInfo);
		System.out.println(setInfo);
		return super.toString();
	}
	
	public JavaFieldGetSet() {
		
	}
	
	public JavaFieldGetSet(String fieldInfo, String getInfo, String setInfo) {
		super();
		this.fieldInfo = fieldInfo;
		this.getInfo = getInfo;
		this.setInfo = setInfo;
	}

	public String getFieldInfo() {
		return fieldInfo;
	}

	public void setFieldInfo(String fieldInfo) {
		this.fieldInfo = fieldInfo;
	}

	public String getGetInfo() {
		return getInfo;
	}

	public void setGetInfo(String getInfo) {
		this.getInfo = getInfo;
	}

	public String getSetInfo() {
		return setInfo;
	}

	public void setSetInfo(String setInfo) {
		this.setInfo = setInfo;
	}
	
	
}
