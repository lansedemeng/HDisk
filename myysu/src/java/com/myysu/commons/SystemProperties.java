import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * 系统属性
 * 
 * @author weiwei
 */
public class SystemProperties {

	private static ResourceBundle systempProperties;

	static {
		systempProperties = PropertyResourceBundle.getBundle("system");
	}

	public static String get(String key) {
		return systempProperties.getString(key);
	}

}
