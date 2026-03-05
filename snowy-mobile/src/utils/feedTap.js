/**
 * 适配App端震动反馈,当发生错误时可以进行震动反馈,提升用户体验,目前不能使用
 */
export default function onFeedTap() {
	let platform=uni.getSystemInfoSync().platform
	// #ifdef APP-PLUS
	if (platform == "ios") {
		let UIImpactFeedbackGenerator = plus.ios.importClass('UIImpactFeedbackGenerator');
		let impact = new UIImpactFeedbackGenerator();
		impact.prepare();
		impact.init(1);
		impact.impactOccurred();
	}
	if (platform == "android") {
		uni.vibrateShort();
	}
	// #endif
}