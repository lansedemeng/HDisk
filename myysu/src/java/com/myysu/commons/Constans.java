/**
 * 公共常量的定义
 * 
 * @author chinachenyyx
 * @date 2017-01-05
 * 
 */
public class Constans {
	/** BI系统自动登录key： */
	public static final String PASSPORT_REMEMBERME_CACHE_KEY = "P_REMEMBERME_TOKEN_ZNTG";

	public static final int DEFAULT_PAGE_SIZE = 10;

	public static final String COMMAND = "command";

	public static final String USER_SESSION_KEY = "USER_SESSION";

	public static final String SUBMIT_TOKEN_SESSION_KEY = "SUBMIT_TOKEN";

	public static final String ACTION_MSGS_ATTRIBUTE_KEY = "action_msgs";

	public static final String ACTION_STATUS_ATTRIBUTE_KEY = "action_status";

	public static final int ACTION_STATUS_CODE_FAILED = 0;

	public static final int ACTION_STATUS_CODE_SUCCESS = 1;

	public static final int ACTION_STATUS_CODE_UNAUTHORIZED = 2;

	public static final String DELETE_SECURITY_IPRULE_KEY = "delete.security.iprule";
	/** ajax返回未授权的状态 */
	public static final int ACTION_STATUS_CODE_JSRCHECKERROR = -999;

	/** SessionID key： */
	public static final String SESSION_ID_CACHE_KEY = "PTOKEN_ZNTG_BI";
	/** 缓存中存放验证码文本的key */
	public static final String CAPTCHA_TEXT_CACHE_KEY = "CAPTCHA";
	/** 中酿BI主域名 */
	public static final String ZHONGNIANG_SHARED_COOKIE_DOMAIN = "bi.zhongniang.com";

	public static final String SHARED_COOKIE_DOMAIN = "zhongniang.com";
	/** HTTP Client User-Agent **/
	public static final String HTTP_CLIENT_USER_AGENT = "JX-Http-Proxy";
	/** HTTP Client RealIp Head */
	public static final String HTTP_CLIENT_REALIP_HEAD = "HttpClient-Proxy-For";
	/** 缓存中存放验证码文本的key */
	public static final String TIMELEN_FREQUENCY_COMSULTATION = "consultation.frequency.timelen";
	/** BI系统默认密码 */
	public static final String ZNBI_DEFAULT_PASSWORD = "bi-zn12345678";
	/** 默认每页显示数量 */
	public static final int PAGE_SIZE = 10;
	/** 默认页码 */
	public static final int PAGE_NUM = 1;
	/** 转换日期时间格式*/
	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
}