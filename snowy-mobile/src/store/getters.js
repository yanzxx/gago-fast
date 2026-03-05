const getters = {
	envKey: state =>  state.global.envKey,
	allEnv: state =>  state.global.allEnv,
	homeConfigs: state => state.global.homeConfigs,
	token: state => state.global.token,
	userMobileMenus: state => state.global.userMobileMenus,
	userInfo: state => state.global.userInfo,
	sysBaseConfig: state => state.global.sysBaseConfig,
	dictTypeTreeData: state => state.global.dictTypeTreeData,
}
export default getters
