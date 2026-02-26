
package vip.xiaonuo.pay.core.wx;

import cn.hutool.core.util.StrUtil;
import com.github.binarywang.wxpay.config.WxPayConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 微信支付配置缓存类
 *
 * @author xuyuxiang
 * @date 2023/3/31 16:15
 **/
public class WxPayApiConfigKit {
	private static final ThreadLocal<String> TL = new ThreadLocal<>();

	private static final Map<String, WxPayConfig> CFG_MAP = new ConcurrentHashMap<>();
	private static final String DEFAULT_CFG_KEY = "_default_key_";

	/**
	 * <p>向缓存中设置 WxPayConfig </p>
	 * <p>每个 appId 只需添加一次，相同 appId 将被覆盖</p>
	 *
	 * @param wxPayConfig 微信支付配置
	 * @return {@link WxPayConfig}
	 */
	public static WxPayConfig putApiConfig(WxPayConfig wxPayConfig) {
		if (CFG_MAP.size() == 0) {
			CFG_MAP.put(DEFAULT_CFG_KEY, wxPayConfig);
		}
		return CFG_MAP.put(wxPayConfig.getAppId(), wxPayConfig);
	}

	/**
	 * 向当前线程中设置 {@link WxPayConfig}
	 *
	 * @param wxPayConfig {@link WxPayConfig} 微信配置对象
	 * @return {@link WxPayConfig}
	 */
	public static WxPayConfig setThreadLocalWxPayConfig(WxPayConfig wxPayConfig) {
		if (StrUtil.isNotEmpty(wxPayConfig.getAppId())) {
			setThreadLocalAppId(wxPayConfig.getAppId());
		}
		return putApiConfig(wxPayConfig);
	}

	/**
	 * 通过 WxPayConfig 移除支付配置
	 *
	 * @param wxPayConfig {@link WxPayConfig} 微信配置对象
	 * @return {@link WxPayConfig}
	 */
	public static WxPayConfig removeApiConfig(WxPayConfig wxPayConfig) {
		return removeApiConfig(wxPayConfig.getAppId());
	}

	/**
	 * 通过 appId 移除支付配置
	 *
	 * @param appId 微信应用编号
	 * @return {@link WxPayConfig}
	 */
	public static WxPayConfig removeApiConfig(String appId) {
		return CFG_MAP.remove(appId);
	}

	/**
	 * 向当前线程中设置 appId
	 *
	 * @param appId 微信应用编号
	 */
	public static void setThreadLocalAppId(String appId) {
		if (StrUtil.isEmpty(appId)) {
			appId = CFG_MAP.get(DEFAULT_CFG_KEY).getAppId();
		}
		TL.set(appId);
	}

	/**
	 * 移除当前线程中的 appId
	 */
	public static void removeThreadLocalAppId() {
		TL.remove();
	}

	/**
	 * 获取当前线程中的  appId
	 *
	 * @return 微信应用编号 appId
	 */
	public static String getAppId() {
		String appId = TL.get();
		if (StrUtil.isEmpty(appId)) {
			appId = CFG_MAP.get(DEFAULT_CFG_KEY).getAppId();
		}
		return appId;
	}

	/**
	 * 获取当前线程中的 WxPayConfig
	 *
	 * @return {@link WxPayConfig}
	 */
	public static WxPayConfig getWxPayConfig() {
		String appId = getAppId();
		return getApiConfig(appId);
	}

	/**
	 * 通过 appId 获取 WxPayConfig
	 *
	 * @param appId 微信应用编号
	 * @return {@link WxPayConfig}
	 */
	public static WxPayConfig getApiConfig(String appId) {
		WxPayConfig cfg = CFG_MAP.get(appId);
		if (cfg == null) {
			throw new IllegalStateException("需事先调用 WxPayApiConfigKit.putApiConfig(wxPayConfig) 将 appId对应的 wxPayConfig 对象存入，才可以使用 WxPayApiConfigKit.getWxPayConfig() 的系列方法");
		}
		return cfg;
	}
}
