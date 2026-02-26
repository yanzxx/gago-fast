import { message } from 'ant-design-vue'

// 根据自定义的此节点定义的，转换表单的隐藏、必填、禁用
export default {
	convFormFilid(formJson, fieldInfo) {
		// 递归遍历控件树
		const traverse = (array) => {
			array.forEach((element) => {
				if (element.type === 'grid' || element.type === 'tabs') {
					// 栅格布局 and 标签页
					element.columns.forEach((item) => {
						traverse(item.list)
					})
				} else if (element.type === 'card') {
					// 卡片布局 and  动态表格
					traverse(element.list)
				} else if (element.type === 'table') {
					// 表格布局
					element.trs.forEach((item) => {
						item.tds.forEach((val) => {
							traverse(val.list)
						})
					})
				} else {
					const type = element.type
					if ((type !== 'alert') & (type !== 'text') & (type !== 'divider') & (type !== 'html')) {
						const obj = fieldInfo.find((i) => i.key === element.model)
						if (obj) {
							element.options.hidden = obj.value === 'HIDE' // ? true : false
							element.options.disabled = obj.value === 'READ' //  ? true : false
						} else {
							message.warning('程序检测到功能字段配置发生了异常，依然能正常使用，请联系管理员进行流程重新配置部署！')
						}
					}
				}
			})
		}
		traverse(formJson.list)
		return formJson
	},
	// 掏出所有字段，返回列表
	getListField(data) {
		let result = []
		// 递归遍历控件树
		const traverse = (array) => {
			array.forEach((element) => {
				if (element.type === 'grid' || element.type === 'tabs') {
					// 栅格布局 and 标签页
					element.columns.forEach((item) => {
						traverse(item.list)
					})
				} else if (element.type === 'card') {
					// 卡片布局 and  动态表格
					traverse(element.list)
				} else if (element.type === 'table') {
					// 表格布局
					element.trs.forEach((item) => {
						item.tds.forEach((val) => {
							traverse(val.list)
						})
					})
				} else {
					const type = element.type
					// 排除一些
					if ((type !== 'alert') & (type !== 'text') & (type !== 'divider') & (type !== 'batch') & (type !== 'html')) {
						result.push(element)
					}
				}
			})
		}
		traverse(data)
		return result
	}
}
