package sorm.core;

import java.util.List;

/**
 * �����ѯ�������ṩ����ĺ����ࣩ
 * @author hc
 *
 */
@SuppressWarnings("all")
public interface Query {
	
	
	/**
	 * ֱ��ִ��һ��DML���
	 * @param sql sql���
	 * @param params ����
	 * @return ����ִ��sql����Ӱ���¼������
	 */
	public int executeDML(String sql,Object[] params);
	
	
	/**
	 * ��һ������洢�����ݿ���
	 * �Ѷ����в�Ϊnull�����������ݿ��д洢,�������Ϊnull���0
	 * @param obj Ҫ�洢�Ķ���
	 */
	public void insert(Object obj);
	
	
	/**
	 * ɾ��clazz��ʾ���Ӧ�ı��м�¼��ָ������id�ļ�¼)
	 * @param clazz �����Ӧ�����Class����
	 * @param id ������ֵ
	 * ɾ��һ��¼���޷���ֵ
	 */
	public void delete(Class clazz,Object id);	//delete from User where id=?
	
	
	/**
	 * ɾ�����������ݿ��ж�Ӧ�ļ�¼���������ڵ����Ӧ�ı������������Ӧ����¼��
	 * @param obj
	 */
	public void delete(Object obj);
	
	
	/**
	 * ���¶����Ӧ�ļ�¼������ֻ����ָ�����ֶε�ֵ
	 * @param obj ��Ҫ���µĶ���
	 * @param fieldNames ��Ҫ���µ������б�
	 * @return ִ��sql����Ӱ���¼������
	 */
	public int update(Object obj,String[] fieldNames); //update user set uname=?,pwd=?
	
	
	/**
	 * ��ѯ���ض��м�¼������ÿ�м�¼��װ��clazzָ������Ķ�����
	 * @param sql ��ѯ��sql���
	 * @param clazz	��װ���ݵ�javabean���Class����
	 * @param params sql����
	 * @return ��ѯ���Ľ��
	 */
	
	public List queryRows(String sql,Class clazz,Object[] params);
	
	
	/**
	 * ��ѯ����һ�м�¼�������ü�¼��װ��clazzָ������Ķ�����
	 * @param sql ��ѯ��sql���
	 * @param clazz	��װ���ݵ�javabean���Class����
	 * @param params sql����
	 * @return ��ѯ���Ľ��
	 */
	public Object queryUniRow(String sql,Class clazz,Object[] params);

	
	/**
	 * ��ѯ����һ��ֵ��һ��һ�У���������ֵ����
	 * @param sql ��ѯ��sql���
	 * @param params sql����
	 * @return ��ѯ���Ľ��
	 */
	public Object queryValue(String sql,Object[] params);
	
	
	/**
	 * ��ѯ����һ�����֣�Number����������ֵ����
	 * @param sql ��ѯ��sql���
	 * @param params sql����
	 * @return ��ѯ���Ľ��
	 */
	public Number queryNumber(String sql,Object[] params);
	
	
}




