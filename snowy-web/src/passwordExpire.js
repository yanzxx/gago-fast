import { baseRequest } from '@/utils/request'
import tool from '@/utils/tool'
import { notification } from 'ant-design-vue'

/**
 * 检查用户密码是否过期
 * @returns {Promise<void>} 返回Promise
 */
export const checkPasswordExpire = async () => {
	try {
		if (tool.data.get('TOKEN')) {
			// 调用后端接口检查密码是否过期
			const result = await baseRequest('/sys/userCenter/checkPasswordExpire', {}, 'get')
			// 如果密码已过期，显示提醒通知
			if (tool.data.get('TOKEN') && result.expired) {
				notification['warning']({
					message: '密码过期提醒',
					description: result.message,
					duration: 0 // 通知不会自动关闭
				})
			}
		}
	} catch (error) {
		console.error('检查密码过期失败:', error)
	}
}

// 导出默认函数
export default checkPasswordExpire

// 登录后每次页面刷新时调用
checkPasswordExpire()