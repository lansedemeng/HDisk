import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;
import com.jiuxian.commons.Constans;
import com.jiuxian.core.SerializationUtils;
import com.jiuxian.pojo.session.UserLoginSession;
import com.jiuxian.service.session.SessionService;
import com.jiuxian.util.ExceptionUtil;
import com.jiuxian.util.Helper;
/**
 * 基础类
 * @author wangmengjie
 *
 */
public class BaseController {
	private static final Log loger = LogFactory.getLog(BaseController.class);
	@Resource
	private SessionService sessionService;

	@Value("#{properties}")
	protected Properties properties;

	public boolean needPreview(HttpServletRequest request) {
		return StringUtils.equalsIgnoreCase(request.getMethod(), "get");
	}

	// 获取请求参数
	public Map<String, Object> getSimpleRequestParamMap(HttpServletRequest request) {
		Map<String, String[]> srcParamMap = request.getParameterMap();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		for (String key : srcParamMap.keySet()) {
			if (srcParamMap.get(key).length == 1) {
				String value = srcParamMap.get(key)[0];
				paramMap.put(key, StringUtils.trimToEmpty(value));
			} else {
				paramMap.put(key, srcParamMap.get(key));
			}
		}

		return paramMap;

	}

	/**
	 * 获取第三方调用接口的请求参数
	 * 
	 * @param request
	 * @return
	 */
	public Map<String, String> getApiParamsMap(HttpServletRequest request) {
		Map<String, String[]> srcParamMap = request.getParameterMap();
		Map<String, String> paramMap = new HashMap<String, String>();

		for (String key : srcParamMap.keySet()) {
			if (srcParamMap.get(key).length == 1) {
				String value = srcParamMap.get(key)[0];
				paramMap.put(key, StringUtils.trimToEmpty(value));
			} else {
				paramMap.put(key, srcParamMap.get(key).toString());
			}
		}

		return paramMap;

	}

	// 重定向
	public void redirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
		String servletContext = request.getSession().getServletContext().getContextPath();
		response.sendRedirect(servletContext + url);
	}

	// 添加信息
	@SuppressWarnings("unchecked")
	public void addActionMessage(String msg, HttpServletRequest request) throws IOException {
		if (request.getAttribute(Constans.ACTION_MSGS_ATTRIBUTE_KEY) == null) {
			request.setAttribute(Constans.ACTION_MSGS_ATTRIBUTE_KEY, new ArrayList<String>());
		}
		List<String> msgList = (List<String>) request.getAttribute(Constans.ACTION_MSGS_ATTRIBUTE_KEY);
		msgList.add(msg);
	}

	// 添加Controller信息
	@SuppressWarnings("unchecked")
	public List<String> getActionMessages(HttpServletRequest request) throws IOException {
		return (List<String>) request.getAttribute(Constans.ACTION_MSGS_ATTRIBUTE_KEY);
	}

	// ajax响应
	public ModelAndView ajaxResponse(HttpServletRequest request, HttpServletResponse response, int status) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(Constans.ACTION_MSGS_ATTRIBUTE_KEY, getActionMessages(request));
		data.put(Constans.ACTION_STATUS_ATTRIBUTE_KEY, status);
		response.getWriter().write(JSONObject.fromObject(data).toString());
		return null;
	}

	// ajax响应
	public ModelAndView ajaxResponse(HttpServletRequest request, HttpServletResponse response, int status, String msg) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(Constans.ACTION_MSGS_ATTRIBUTE_KEY, msg);
		data.put(Constans.ACTION_STATUS_ATTRIBUTE_KEY, status);
		response.getWriter().write(JSONObject.fromObject(data).toString());
		return null;
	}

	public ModelAndView ajaxResponseOneValid(HttpServletRequest request, HttpServletResponse response, String info, String status) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("info", info);
		data.put("status", status);
		response.getWriter().write(JSONObject.fromObject(data).toString());
		return null;
	}

	public ModelAndView ajaxResponse(HttpServletRequest request, HttpServletResponse response, int status, Map<String, Object> data) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		if (data == null) {
			data = new HashMap<String, Object>();
		}
		data.put(Constans.ACTION_MSGS_ATTRIBUTE_KEY, getActionMessages(request));
		data.put(Constans.ACTION_STATUS_ATTRIBUTE_KEY, status);
		response.getWriter().write(JSONObject.fromObject(data).toString());
		return null;
	}

	public String ajaxResponseWithReturn(HttpServletRequest request, HttpServletResponse response, int status, Map<String, Object> data) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		if (data == null) {
			data = new HashMap<String, Object>();
		}
		data.put(Constans.ACTION_MSGS_ATTRIBUTE_KEY, getActionMessages(request));
		data.put(Constans.ACTION_STATUS_ATTRIBUTE_KEY, status);
		String result = JSONObject.fromObject(data).toString();
		response.getWriter().write(JSONObject.fromObject(data).toString());
		return result;
	}

	// 返回json格式的数据
	public static void responseJson(HttpServletResponse response, Map<String, Object> data) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.getWriter().write(JSONObject.fromObject(data).toString());
	}

	// 获取用户session
	public UserLoginSession getUserLoginSession(HttpServletRequest request, HttpServletResponse response) {
		String sessionId = Helper.getSessionId(request, response);
		UserLoginSession loginSession = (UserLoginSession) SerializationUtils.deserialize(sessionService.getLoginSession(SerializationUtils.serialize(sessionId)));
		if (loginSession != null) {
			// 刷新过期时间
			sessionService.expire(SerializationUtils.serialize(sessionId), 60 * 30);
			sessionService.expire(SerializationUtils.serialize("BISESSIONID_" + loginSession.getUser().getID()), 60 * 30);
		}
		return loginSession;
	}

	// 保存用户登录session
	public void setUserLoginSession(UserLoginSession userLoginSession, HttpServletRequest request, HttpServletResponse response) {
		String sessionId = Helper.getSessionId(request, response);
		sessionService.setWithExpire(SerializationUtils.serialize(sessionId), SerializationUtils.serialize(userLoginSession), 60 * 30);
		String PCSessionId = "BISESSIONID_" + userLoginSession.getUser().getID();
		sessionService.setWithExpire(SerializationUtils.serialize(PCSessionId), SerializationUtils.serialize(sessionId), 60 * 30);
	}

	// 清除用户登录session
	public void removeUserLoginSession(HttpServletRequest request, HttpServletResponse response) {
		String sessionId = Helper.getSessionId(request, response);
		sessionService.remove(SerializationUtils.serialize(sessionId));
	}

	// 获取sessionAttribute
	public Object getSessionAttribute(String key, HttpServletRequest request, HttpServletResponse response) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}
		String sessionId = Helper.getSessionId(request, response);
		key = sessionId + "_" + key;
		return SerializationUtils.deserialize(sessionService.get(SerializationUtils.serialize(key)));
	}

	// 保存sessionAttribute
	public void setSessionAttribute(String key, Object value, HttpServletRequest request, HttpServletResponse response) {
		if (StringUtils.isNotEmpty(key)) {
			String sessionId = Helper.getSessionId(request, response);
			key = sessionId + "_" + key;
			sessionService.set(SerializationUtils.serialize(key), SerializationUtils.serialize(value));
		}
	}

	// 保存sessionAttribute并设置过期时间（单位：秒）
	public void setSessionAttribute(String key, Object value, int expire, HttpServletRequest request, HttpServletResponse response) {
		if (StringUtils.isNotEmpty(key)) {
			String sessionId = Helper.getSessionId(request, response);
			key = sessionId + "_" + key;
			sessionService.setWithExpire(SerializationUtils.serialize(key), SerializationUtils.serialize(value), expire);
		}
	}

	// 清除sessionAttribute
	public void removeSessionAttribute(String key, HttpServletRequest request, HttpServletResponse response) {
		if (StringUtils.isNotEmpty(key)) {
			String sessionId = Helper.getSessionId(request, response);
			key = sessionId + "_" + key;
			sessionService.remove(SerializationUtils.serialize(key));
		}
	}

	/**
	 * 跨域请求代理
	 * 
	 * @param requestUrl
	 * @return
	 */
	public String getDomainProxyRequest(String requestUrl) {
		String phpSessionSerializeData = "";
		try {
			HttpGet get = new HttpGet(requestUrl);
			HttpParams httpParams = get.getParams();
			// 设置User-Agent
			httpParams.setParameter(CoreProtocolPNames.USER_AGENT, Constans.HTTP_CLIENT_USER_AGENT);
			HttpClient client = new DefaultHttpClient();
			HttpResponse res;
			res = client.execute(get);

			phpSessionSerializeData = EntityUtils.toString(res.getEntity());
		} catch (Exception e) {
			ExceptionUtil.logerErrorMes(loger, e);
		}
		if (StringUtils.isNotEmpty(phpSessionSerializeData)) {
			return JSONObject.fromObject(phpSessionSerializeData).toString();
		} else {
			return phpSessionSerializeData;
		}
	}

	/**
	 * 跨域请求代理
	 * 
	 * @param requestUrl
	 * @return
	 */
	public String getDomainProxyPostRequest(String url, List<NameValuePair> nameValuePairs) {
		String phpSessionSerializeData = "";
		try {
			HttpPost post = new HttpPost(url);
			HttpClient client = new DefaultHttpClient();

			HttpParams httpParams = post.getParams();
			httpParams.setParameter(CoreProtocolPNames.USER_AGENT, Constans.HTTP_CLIENT_USER_AGENT);
			post.setHeader("X-Requested-With", "XMLHttpRequest");

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse Response = client.execute(post);
			HttpEntity entity = Response.getEntity();
			String data = EntityUtils.toString(entity);

			return JSONObject.fromObject(data).toString();
		} catch (Exception e) {
			ExceptionUtil.logerErrorMes(loger, e);
		}
		return phpSessionSerializeData;
	}

	/**
	 * 
	 * @Description: 获取cookie区域
	 * @author: chinachenyyx
	 * @date: 2017-01-05
	 * @param request
	 * @return
	 */
	protected Integer getCookieAreaId(HttpServletRequest request) {
		try {
			String[] user_area_info = StringUtils.split(Helper.getCookieValue(request, "user_province"), "_");
			return NumberUtils.toInt(user_area_info[user_area_info.length - 1]);
		} catch (Exception e) {
			loger.warn(e);
			return 2;// 默认地区：北京
		}
	}
}
