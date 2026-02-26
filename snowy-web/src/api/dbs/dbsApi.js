
import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/dbs/${url}`, ...arg)
/**
 * 多租户
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// 获取数据源分页
	dbsPage(data) {
		return request('storage/page', data, 'get')
	},
	// 提交表单 edit为true时为编辑，默认为新增
	submitForm(data, edit = false) {
		return request(edit ? 'storage/add' : 'storage/edit', data)
	},
	// 删除数据源
	dbsDelete(data) {
		return request('storage/delete', data)
	},
	// 获取数据源详情
	dbsDetail(data) {
		return request('storage/detail', data, 'get')
	},
	// 获取数据库中所有表
	dbsTables(data) {
		return request('tables', data, 'get')
	},
	// 获取数据库表中所有字段
	dbsTableColumns(data) {
		return request('tableColumns', data, 'get')
	}
}
