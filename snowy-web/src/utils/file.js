/**
 * @param {base64url}
 * @returns {string} // 获取文件得大小
 */
export function calSize(base64url) {
	let str = base64url.replace('data:image/png;base64,', '')
	const equalIndex = str.indexOf('=')
	if (str.indexOf('=') > 0) {
		str = str.substring(0, equalIndex)
	}
	const strLength = str.length
	const fileLength = strLength - (strLength / 8) * 2
	// 返回单位为KB的大小
	return (fileLength / 1024).toFixed(2)
}

/**
 * @param {path,w,callback}
 * @returns {callback} // 通过canvas压缩base64图片 并压缩
 */
export function dealImage(path, w = 1000, callback) {
	const newImage = new Image()
	let size = calSize(path) //获取mb大小
	let quality = 0.52
	if (size <= 1) {
		//1 mb
		quality = 0.9
	}
	if (size > 1) {
		quality = 0.8
	}

	newImage.src = path
	newImage.setAttribute('crossOrigin', 'Anonymous') // url为外域时需要
	let imgWidth
	let imgHeight
	newImage.onload = function () {
		imgWidth = this.width
		imgHeight = this.height
		const canvas = document.createElement('canvas')
		const ctx = canvas.getContext('2d')
		if (Math.max(imgWidth, imgHeight) > w) {
			if (imgWidth > imgHeight) {
				canvas.width = w
				canvas.height = (w * imgHeight) / imgWidth
			} else {
				canvas.height = w
				canvas.width = (w * imgWidth) / imgHeight
			}
		} else {
			canvas.width = imgWidth
			canvas.height = imgHeight
		}
		ctx.clearRect(0, 0, canvas.width, canvas.height)
		ctx.drawImage(this, 0, 0, canvas.width, canvas.height)
		const newBase64 = canvas.toDataURL('image/jpeg', quality)
		callback(newBase64)
	}
}
