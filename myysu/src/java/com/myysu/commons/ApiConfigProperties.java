import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * 接口配置文件属性
 */
public class ApiConfigProperties {

	private static ResourceBundle apiConfigProperties;

	static {
		apiConfigProperties = PropertyResourceBundle.getBundle("ApiConfig");
	}

	public static String get(String key) {
		return apiConfigProperties.getString(key);
	}

}
