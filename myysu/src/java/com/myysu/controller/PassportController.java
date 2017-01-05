import java.awt.image.BufferedImage;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.jiuxian.commons.Constans;
import com.jiuxian.controller.BaseController;
import com.jiuxian.entity.admin.BiPower;
import com.jiuxian.entity.admin.BiUsers;
import com.jiuxian.pojo.session.UserLoginSession;
import com.jiuxian.service.admin.PowerService;
import com.jiuxian.service.admin.AdminUserService;
import com.jiuxian.tool.BiPowerTool;
import com.jiuxian.util.CheckCode;
import com.jiuxian.util.Helper;
import com.jiuxian.util.MD5Util;

/**
 * @Description:用户登录控制器
 * @author: chinachenyyx
 * @date: 2015-9-16 下午15:13:10
 */
@Controller
public class PassportController extends BaseController {
	/** 全局日志记录 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PassportController.class);
	/** 用户相关服务接口 */
	@Resource
	private AdminUserService userService;
	/** 用户权限服务 */
	@Resource
	private PowerService powerService;

	/**
	 * @Description: 进入登录页面
	 * @author: chinachenyyx
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/login.htm", method = RequestMethod.GET)
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			/** 获取用户session */
			UserLoginSession userLoginSession = getUserLoginSession(request, response);
			/** 用户已经登录，跳转至第一个导航的第一个子菜单页面 */
			if (userLoginSession != null) {

				BiPower firstPower = BiPowerTool.getFirstBiPower(BiPowerTool.getBiPowersByType(userLoginSession.getUserPowers(), BiPower.TYPE_NAV));
				String url = "";
				if (firstPower != null) {
					url = firstPower.getUrl();
				}

				if (StringUtils.isNotBlank(url)) {
					response.sendRedirect(properties.getProperty("domain_znbi") + url);
				}
				return null;
			}
			/** 未登录跳转至登录页面 */
			return new ModelAndView("login/login");
		} catch (Exception e) {
			LOGGER.error("请求登录页面失败", e);
			return null;
		}
	}

	/**
	 * @Description: 登录用户验证
	 * @author: chinachenyyx
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/newlogin.htm")
	public ModelAndView check(HttpServletRequest request, HttpServletResponse response) throws Exception {
		/** 用户名 */
		String uname = request.getParameter("username");
		/** 密码 */
		String pwd = request.getParameter("password");
		/** 验证码 */
		 String captchaVal = request.getParameter("bi_checkCode");
		 String sessionCaptcahVal =
		 String.valueOf(getSessionAttribute("bi_checkCode", request,
		 response));
		 if (StringUtils.isBlank(captchaVal) ||
		 !captchaVal.equalsIgnoreCase(sessionCaptcahVal)) {
		 addActionMessage("验证码错误或已过期,请重新输入!", request);
		 ajaxResponse(request, response, 3);
		 return null;
		 }

		/** 将传来的用户名和密码做非空判断，任意一个为空都返回“账户名或密码错误，请重新输入”的错误信息 */
		if (StringUtils.isBlank(uname) || StringUtils.isBlank(pwd)) {
			addActionMessage("登录失败，用户名或密码错误!", request);
			ajaxResponse(request, response, 2);
			return null;
		}

		/** 用户名首尾去空格 */
		uname = uname.trim();
		/** 密码进行MD5加密 */
		pwd = MD5Util.getMD5String(pwd);
		/** 把用户名的字母变为小写进查询 */
		String userName = uname.toLowerCase();

		/** 根据用户名、密码查询BI用户 */
		BiUsers user = null;
		try {
			user = userService.selectUserByNameAndPwd(userName, pwd);
		} catch (Exception e) {
			addActionMessage("登录失败，用户名或密码错误!", request);
			ajaxResponse(request, response, 2);
			return null;
		}

		/** 判断用户是否存在，存在为FALSE，不存在为TRUE */
		boolean result = true;
		if (user != null) {
			result = false;
		}
		/** 如果用户不存在则返回错误信息 */
		if (result) {
			addActionMessage("登录失败，用户名或密码错误!", request);
			ajaxResponse(request, response, 2);
			return null;
		} else {
			/** 保存用户的最后登录时间 */
			user.setLastLoginTime(new Date());
			try {
				userService.updateBiUsers(user);
			} catch (Exception e) {
				LOGGER.error("--------------------更新用户最后登录时间失败！", e);
			}

			/** 用户存在时创建session，存入共享的session缓存(session保存时间为半小时) */
			UserLoginSession userLoginSession = new UserLoginSession();
			/** 将User对象的值传入UserLoginSession对象 */
			setloginsession(user, userLoginSession);

			/** 查询用户的权限信息 */
			List<BiPower> userPowerList = powerService.selectUserPowers(user);

			/** 按sort排序 */
			if (userPowerList != null && userPowerList.size() > 0) {
				Collections.sort(userPowerList, new Comparator<BiPower>() {
					public int compare(BiPower o1, BiPower o2) {
						Integer sort1 = o1.getSort();
						Integer sort2 = o2.getSort();
						return sort1.compareTo(sort2);
					}
				});
				userLoginSession.setUserPowers(userPowerList);
			} else {
				addActionMessage("哎呦...,您还没有菜单权限，请联系管理员，给您分配菜单权限!", request);
				ajaxResponse(request, response, 2);
				return null;
			}

			/** 分离出导航菜单 start */
			List<BiPower> navList = new ArrayList<BiPower>();
			if (userPowerList != null && userPowerList.size() > 0) {
				for (BiPower power : userPowerList) {
					if (power.getType() == BiPower.TYPE_NAV) {
						navList.add(power);
					}
				}
			}
			if (navList != null && navList.size() > 0) {
				userLoginSession.setNavList(navList);
			}
			/** 分离出导航菜单 end */

			/** 默认密码提示用户修改密码，值提示一次 */
			if (MD5Util.getMD5String(Constans.ZNBI_DEFAULT_PASSWORD).equals(pwd)) {
				Helper.addDomainCookieAndAge("ZNBI_DEFAULT_PASSWORD_SIGN", 60 * 60 * 24 * 1000, "yes", response);
			}

			/** 获取排序最小的导航 */
			BiPower firstPower = BiPowerTool.getFirstBiPower(BiPowerTool.getBiPowersByType(userPowerList, BiPower.TYPE_NAV));
			if (firstPower == null) {
				addActionMessage("哎呦...,您还没有菜单权限，请联系管理员，给您分配菜单权限!", request);
				ajaxResponse(request, response, 2);
				return null;
			}
			/** 保存用户登录session */
			setUserLoginSession(userLoginSession, request, response);
			/** 登录成功后存储用户名Cookie信息 */
			Helper.addDomainCookieAndAge("znbi_user_name", 60 * 60 * 24 * 7, URLEncoder.encode(URLEncoder.encode(userName, "UTF-8"), "UTF-8"), response);
			/** 登录成功跳转用户第一导航下的第一菜单页面 */
			addActionMessage(firstPower.getUrl(), request);
			/** 返回成功信息 */
			return ajaxResponse(request, response, Constans.ACTION_STATUS_CODE_SUCCESS);
		}
	}

	/**
	 * @Description: 退出登录
	 * @author: chinachenyyx
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		/** 清除用户登录session */
		removeUserLoginSession(request, response);
		/** 清除用户名的Cookie */
		Helper.addDomainCookieAndAge("znbi_user_name", 0, null, response);
		/** 清除默认密码标示 的Cookie */
		Helper.addDomainCookieAndAge("ZNBI_DEFAULT_PASSWORD_SIGN", 0, null, response);
		/** 重定向到BI登录页 */
		response.sendRedirect("/login.htm");
	}

	/**
	 * 存入登录session
	 * 
	 * @author chinachenyyx
	 * @param user
	 * @param userLoginSession
	 * @return
	 */
	public UserLoginSession setloginsession(BiUsers user, UserLoginSession userLoginSession) {

		if (userLoginSession == null) {
			userLoginSession = new UserLoginSession();
		}

		if (user != null) {
			userLoginSession.setUser(user);
		}
		return userLoginSession;
	}

	/**
	 * 
	 * @Description: 生成验证码图形
	 * @author: chinachenyyx
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/getCheckCode.htm")
	public void getCheckCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			String sRand = "";
			Random random = new Random();

			for (int i = 0; i < 4; i++) {
				String rand = String.valueOf(random.nextInt(10));
				sRand += rand;
			}

			setSessionAttribute("bi_checkCode", sRand, 60 * 3, request, response);
			BufferedImage image = CheckCode.create(sRand);
			ImageIO.write(image, "JPEG", response.getOutputStream());

		} catch (Exception e) {
			LOGGER.error("生成图形验证码失败：" + e);
		}
	}

}
