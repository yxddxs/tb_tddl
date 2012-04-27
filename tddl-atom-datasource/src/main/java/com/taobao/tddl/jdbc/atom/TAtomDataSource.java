/*(C) 2007-2012 Alibaba Group Holding Limited.	

import java.sql.SQLException;
/**
 * ��̬����Դ��֧������Դ������̬�޸�
 * 
 * @author qihao
 * 
 */
public class TAtomDataSource extends AbstractTAtomDataSource {

	protected static Log logger = LogFactory.getLog(TAtomDataSource.class);

	private static Map<String, TAtomDsConfHandle> cacheConfHandleMap = new HashMap<String, TAtomDsConfHandle>();

	private volatile TAtomDsConfHandle dsConfHandle = new TAtomDsConfHandle();

	public void init() throws Exception {
		String dbName = TAtomConstants.getDbNameStr(this.getAppName(), this.getDbKey());
		synchronized (cacheConfHandleMap) {
			TAtomDsConfHandle cacheConfHandle = cacheConfHandleMap.get(dbName);
			if (null == cacheConfHandle) {
				//��ʼ��config�Ĺ�����
				this.dsConfHandle.init();
				cacheConfHandleMap.put(dbName, dsConfHandle);
				logger.info("create new TAtomDsConfHandle dbName : " + dbName);
			} else {
				dsConfHandle = cacheConfHandle;
				logger.info("use the cache TAtomDsConfHandle dbName : " + dbName);
			}
		}
	}

	/**
	 * �������������Դ
	 */
	public static void cleanAllDataSource() {
		synchronized (cacheConfHandleMap) {
			for (TAtomDsConfHandle handles : cacheConfHandleMap.values()) {
				try {
					handles.destroyDataSource();
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
			cacheConfHandleMap.clear();
		}
	}

	/**
	 * ˢ������Դ
	 */
	public void flushDataSource() {
		this.dsConfHandle.flushDataSource();
	}

	/**��������Դ������
	 * @throws Exception 
	 */
	public void destroyDataSource() throws Exception {
		String dbName = TAtomConstants.getDbNameStr(this.getAppName(), this.getDbKey());
		synchronized (cacheConfHandleMap) {
			this.dsConfHandle.destroyDataSource();
			cacheConfHandleMap.remove(dbName);
		}
	}

	public String getAppName() {
		return this.dsConfHandle.getAppName();
	}

	public String getDbKey() {
		return this.dsConfHandle.getDbKey();
	}

	public void setAppName(String appName) throws AtomAlreadyInitException {
		this.dsConfHandle.setAppName(TStringUtil.trim(appName));
	}

	public void setDbKey(String dbKey) throws AtomAlreadyInitException {
		this.dsConfHandle.setDbKey(TStringUtil.trim(dbKey));
	}

	public AtomDbStatusEnum getDbStatus() {
		return this.dsConfHandle.getStatus();
	}

	public void setDbStatusListeners(List<TAtomDbStatusListener> dbStatusListeners) {
		this.dsConfHandle.setDbStatusListeners(dbStatusListeners);
	}

	public void setSingleInGroup(boolean isSingleInGroup) {
		this.dsConfHandle.setSingleInGroup(isSingleInGroup);
	}

	/**=======���������ñ������ȵ��������ԣ���������˻�������͵����ö�ʹ�ñ��ص�����=======*/
	public void setPasswd(String passwd) throws AtomAlreadyInitException {
		this.dsConfHandle.setLocalPasswd(passwd);
	}

	public void setDriverClass(String driverClass) throws AtomAlreadyInitException {
		this.dsConfHandle.setLocalDriverClass(driverClass);
	}

	public AtomDbTypeEnum getDbType() {
		return this.dsConfHandle.getDbType();
	}

	public void setSorterClass(String sorterClass) throws AtomAlreadyInitException {
		this.dsConfHandle.setLocalSorterClass(sorterClass);
	}

	public void setConnectionProperties(Map<String, String> map) throws AtomAlreadyInitException {
		this.dsConfHandle.setLocalConnectionProperties(map);
	}

	protected DataSource getDataSource() throws SQLException {
		return this.dsConfHandle.getDataSource();
	}
}