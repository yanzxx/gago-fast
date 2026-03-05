import TileLayer from 'ol/layer/Tile';
import XYZSOURCE from 'ol/source/XYZ';
import * as Lerc from 'lerc';
import { getTopLeft, getWidth} from 'ol/extent';
import WMTSTileGrid from 'ol/tilegrid/WMTS';
import TileState from 'ol/TileState';

// lerc通用函数图层创建图层
export const  colorLercHandle=(stops, url, maxZoom, type, lercClassType, max, min,projection,proName)=> {
	const projectionExtent = projection.getExtent();
	const size = getWidth(projectionExtent) / 256; // size就是一个像素代表的经纬度
	const matrixIds = [];
	function getResolutions() {
		const resolutions = [];
		for (let z = 0; z < 20; ++z) {
			resolutions[z] = size / Math.pow(2, z);
			matrixIds[z] = z;
		}
		return resolutions;
	}
	const canvas = document.createElement('canvas');
	const ctx = canvas.getContext('2d');
	const stop = [];
	const colors = [];
	stops.forEach(item => {
		stop.push(item[0]);
		colors.push(item[1]);
	});
	const colorValArray = [];
	stop.forEach((item, index) => {
		if (index < stop.length - 1) {
			if (type === 0) {
				colorValArray.push({
					val: [stop[index], stop[index + 1]],
					range: [colorTran(colors[index]), colorTran(colors[index + 1])],
				});
			} else if (type === 1) {
				colorValArray.push({
					val: [stop[index], stop[index + 1]],
					range: [colors[index], colors[index + 1]],
				});
			}
		}
	});
	if (lercClassType === 'exponential' || lercClassType === 'interval') {
		if (type === 0) {
			colorValArray.unshift({
				val: [min, stop[0]],
				range: [colorTran(colors[0]), colorTran(colors[0])],
			});
			colorValArray.push({
				val: [stop[stop.length - 1], max],
				range: [colorTran(colors[colors.length - 1]), colorTran(colors[colors.length - 1])],
			});
		} else if (type === 1) {
			colorValArray.unshift({
				val: [min, stop[0]],
				range: [colors[0], colors[0]],
			});
			colorValArray.push({
				val: [stop[stop.length - 1], max],
				range: [colors[colors.length - 1], colors[colors.length - 1]],
			});
		}
	}
	let vsss = null;
	let vName = 0;
	const lercLayer = new TileLayer({
		// opacity: 0.9,
		minZoom: 2,
		maxZoom: 19,
		source: new XYZSOURCE({
			url,
			tileLoadFunction(tile, src) {
				fetch(src)
					.then((response) => {
						if (response.status === 200) {
							return response.arrayBuffer();
						}
						return new ArrayBuffer(0);
					})
					.then((body) => {
						if (body.byteLength > 0) {
							const image = Lerc.decode(body); // lerc endecode 解码
							canvas.width = image.width;
							canvas.height = image.height;
							const imgData = ctx.createImageData(image.width, image.width); // 按照给的切片大小，To create a new, blank ImageData object
							const pixels = image.pixels[0]; // 取到像素值，Uint16Array 数组 [0~255],RGBA 像素值
							if (lercClassType !== 'exponential') {
								let j = 0;
								for (const itemss of  pixels) {
									const pixel = itemss;
									let rgba = [0, 0, 0, 0];
									const items = colorValArray.find((item) => pixel >= item.val[0] && pixel < item.val[1]);
									if (items) {
										rgba = items.range[0];
									}
									imgData.data[j] = rgba[0]; // r
									imgData.data[j + 1] = rgba[1]; // g
									imgData.data[j + 2] = rgba[2]; // b
									imgData.data[j + 3] =
										pixel === 0 ? 0 : rgba[3] * 255; // a
									j += 4;
								}
							} else {
								let j = 0;
								for (const itemss of  pixels) {
									const pixel = itemss;
									const rgba = [0, 0, 0, 0];
									const items = colorValArray.find((item) => pixel >= item.val[0] && pixel < item.val[1]);
									if (items) {
										const factor = (pixel - items.val[0]) / (items.val[1] - items.val[0]); // 计算该值在两边界值中间的比例
										const colors1 = items.range[0];
										const colors2 = items.range[1];
										rgba[0] = colors2[0] + Math.round((colors1[0] - colors2[0]) * factor); // 通过比例计算中间值颜色
										rgba[1] = colors2[1] + Math.round((colors1[1] - colors2[1]) * factor);
										rgba[2] = colors2[2] + Math.round((colors1[2] - colors2[2]) * factor);
										rgba[3] = Math.abs(colors2[3] + colors1[3]) / 2;
									}
									imgData.data[j] = rgba[0]; // r
									imgData.data[j + 1] = rgba[1]; // g
									imgData.data[j + 2] = rgba[2]; // b
									imgData.data[j + 3] =
										pixel === 0 ? rgba[3] * 255 : rgba[3] * 255; // a
									j += 4;
								}
							}
							vsss = imgData;
							vName = tile.tileCoord[0];
							ctx.putImageData(imgData, 0, 0);
							// @ts-ignore
							tile.getImage().src = canvas.toDataURL();
						} else {
							// @ts-ignore
							tile.getImage().src = '';
						}
					})
					.catch((error) => {
						// 如果不catch 必须都进行赋值像素值
						tile.setState(TileState.ERROR);
						const imgData = ctx.createImageData(256, 256);
						console.log(vName,maxZoom,"vName111")
						if (vName === Number(maxZoom)) {
							ctx.putImageData(vsss, 0, 0);
						} else {
							ctx.putImageData(imgData, 0, 0);
							// @ts-ignore
							tile.getImage().src = canvas.toDataURL();
						}
						
					});
			},
			projection: proName,
			tileGrid: new WMTSTileGrid({
				origin: getTopLeft(projectionExtent),
				resolutions: getResolutions(),
				matrixIds,
			}),
		}),
		preload: Infinity,
		// zIndex: 7,
	});
	return lercLayer
	// map.addLayer(lercLayer);
}
// 十六进制转二进制颜色
const colorTran=(sColor)=> {
	sColor = sColor.toLowerCase();
	const reg = /^#([0-9a-fA-f]{3}|[0-9a-fA-f]{6})$/;
	if (sColor && reg.test(sColor)) {
		if (sColor.length === 4) {
			let sColorNew = '#';
			for (let i = 1; i < 4; i += 1) {
				sColorNew += sColor.slice(i, i + 1).concat(sColor.slice(i, i + 1));
			}
			sColor = sColorNew;
		}
		const sColorChange = [];
		for (let i = 1; i < 7; i += 2) {
			sColorChange.push(parseInt('0x' + sColor.slice(i, i + 2), 16));
		}
		sColorChange.push(255);
		return sColorChange;
	}
	return sColor;
}
// lerc更改url
export const editLercHandle = (stops, url, maxZoom, type, lercClassType, max, min,projection,proName,zoom)=>{
	const projectionExtent = projection.getExtent();
	const size = getWidth(projectionExtent) / 256; // size就是一个像素代表的经纬度
	const matrixIds = [];
	function getResolutions() {
		const resolutions = [];
		for (let z = 0; z < 20; ++z) {
			resolutions[z] = size / Math.pow(2, z);
			matrixIds[z] = z;
		}
		return resolutions;
	}
	const canvas = document.createElement('canvas');
	const ctx = canvas.getContext('2d');
	const stop = [];
	const colors = [];
	stops.forEach(item => {
		stop.push(item[0]);
		colors.push(item[1]);
	});
	const colorValArray = [];
	stop.forEach((item, index) => {
		if (index < stop.length - 1) {
			if (type === 0) {
				colorValArray.push({
					val: [stop[index], stop[index + 1]],
					range: [colorTran(colors[index]), colorTran(colors[index + 1])],
				});
			} else if (type === 1) {
				colorValArray.push({
					val: [stop[index], stop[index + 1]],
					range: [colors[index], colors[index + 1]],
				});
			}
		}
	});
	if (lercClassType === 'exponential' || lercClassType === 'interval') {
		if (type === 0) {
			colorValArray.unshift({
				val: [min, stop[0]],
				range: [colorTran(colors[0]), colorTran(colors[0])],
			});
			colorValArray.push({
				val: [stop[stop.length - 1], max],
				range: [colorTran(colors[colors.length - 1]), colorTran(colors[colors.length - 1])],
			});
		} else if (type === 1) {
			colorValArray.unshift({
				val: [min, stop[0]],
				range: [colors[0], colors[0]],
			});
			colorValArray.push({
				val: [stop[stop.length - 1], max],
				range: [colors[colors.length - 1], colors[colors.length - 1]],
			});
		}
	}
	let vsss = null;
	let vName = 0;
	console.log(colorValArray,'colorValArray')
	const lercSource = new XYZSOURCE({
		url,
		tileLoadFunction(tile, src) {
			fetch(src)
				.then((response) => {
					if (response.status === 200) {
						return response.arrayBuffer();
					}
					return new ArrayBuffer(0);
				})
				.then((body) => {
					if (body.byteLength > 0) {
						const image = Lerc.decode(body); // lerc endecode 解码
						canvas.width = image.width;
						canvas.height = image.height;
						const imgData = ctx.createImageData(image.width, image.width); // 按照给的切片大小，To create a new, blank ImageData object
						const pixels = image.pixels[0]; // 取到像素值，Uint16Array 数组 [0~255],RGBA 像素值
						if (lercClassType !== 'exponential') {
							let j = 0;
							for (const itemss of  pixels) {
								const pixel = itemss;
								let rgba = [0, 0, 0, 0];
								const items = colorValArray.find((item) => pixel >= item.val[0] && pixel < item.val[1]);
								if (items) {
									rgba = items.range[0];
								}
								imgData.data[j] = rgba[0]; // r
								imgData.data[j + 1] = rgba[1]; // g
								imgData.data[j + 2] = rgba[2]; // b
								imgData.data[j + 3] =
									pixel === 0 ? 0 : rgba[3] * 255; // a
								j += 4;
							}
						} else {
							let j = 0;
							for (const itemss of  pixels) {
								const pixel = itemss;
								const rgba = [0, 0, 0, 0];
								const items = colorValArray.find((item) => pixel >= item.val[0] && pixel < item.val[1]);
								if (items) {
									const factor = (pixel - items.val[0]) / (items.val[1] - items.val[0]); // 计算该值在两边界值中间的比例
									const colors1 = items.range[0];
									const colors2 = items.range[1];
									rgba[0] = colors2[0] + Math.round((colors1[0] - colors2[0]) * factor); // 通过比例计算中间值颜色
									rgba[1] = colors2[1] + Math.round((colors1[1] - colors2[1]) * factor);
									rgba[2] = colors2[2] + Math.round((colors1[2] - colors2[2]) * factor);
									rgba[3] = Math.abs(colors2[3] + colors1[3]) / 2;
								}
								imgData.data[j] = rgba[0]; // r
								imgData.data[j + 1] = rgba[1]; // g
								imgData.data[j + 2] = rgba[2]; // b
								imgData.data[j + 3] =
									pixel === 0 ? rgba[3] * 255 : rgba[3] * 255; // a
								j += 4;
							}
						}
						vsss = imgData;
						vName = tile.tileCoord[0];
						ctx.putImageData(imgData, 0, 0);
						// @ts-ignore
						tile.getImage().src = canvas.toDataURL();
					} else {
						// @ts-ignore
						tile.getImage().src = '';
					}
				})
				.catch((error) => {
					// 如果不catch 必须都进行赋值像素值
					tile.setState(TileState.ERROR);
					const imgData = ctx.createImageData(256, 256);
					console.log(tile.tileCoord[0],'err222')
					if (tile.tileCoord[0] >= Number(maxZoom)) {
						ctx.putImageData(vsss, 0, 0);
					} else {
						ctx.putImageData(imgData, 0, 0);
						// @ts-ignore
						tile.getImage().src = canvas.toDataURL();
					}
					
				});
		},
		projection: proName,
		tileGrid: new WMTSTileGrid({
			origin: getTopLeft(projectionExtent),
			resolutions: getResolutions(),
			matrixIds,
		}),
	})
	return lercSource
}
/**
 * 两个浮点数相加
 * @author yxk
 * @param num1
 * @param num2
 * @return {number}
 */
export function accAdd(num1, num2) {
	// eslint-disable-next-line one-var
	let r1 = 0,
		r2 = 0
	try {
		r1 = (num1.toString().split('.')[1] || []).length
	} catch (e) {
		return num1 + num2
	}
	try {
		r2 = (num2.toString().split('.')[1] || []).length
	} catch (e) {
		return num1 + num2
	}
	const m = Math.pow(10, Math.max(r1, r2))
	return Math.round(accMul(num1, m) + accMul(num2, m)) / m
}

/**
 * 两个浮点数相减
 * @author yxk
 * @param num1
 * @param num2
 * @return {number}
 */
export function accSub(num1, num2) {
	let r1, r2
	try {
		r1 = (num1.toString().split('.')[1] || []).length
	} catch (e) {
		return num1 - num2
	}
	try {
		r2 = (num2.toString().split('.')[1] || []).length
	} catch (e) {
		return num1 - num2
	}
	const m = Math.pow(10, Math.max(r1, r2))
	return Math.round(accMul(num1, m) - accMul(num2, m)) / m
}

/**
 * 两个浮点数相乘
 * @author yxk
 * @param num1
 * @param num2
 * @return {number}
 */
export function accMul(num1, num2) {
	num1 = num1 + ''
	num2 = num2 + ''
	let m = 0
	const s1 = num1.toString()
	const s2 = num2.toString()
	try {
		m += (s1.split('.')[1] || []).length
	} catch (e) {
		return num1 * num2
	}
	try {
		m += (s2.split('.')[1] || []).length
	} catch (e) {
		return num1 * num2
	}
	return (
		(Number(s1.replace('.', '')) * Number(s2.replace('.', ''))) /
		Math.pow(10, m)
	)
}

/**
 * 两个浮点数相除
 * @author yxk
 * @param num1
 * @param num2
 * @return {number}
 */
export function accDiv(num1, num2) {
	let m = 0
	const s1 = num1.toString()
	const s2 = num2.toString()
	try {
		m += (s1.split('.')[1] || []).length
	} catch (e) {
		return num1 / num2
	}
	try {
		m += (s2.split('.')[1] || []).length
	} catch (e) {
		return num1 / num2
	}
	const n1 = accMul(num1, Math.pow(10, m))
	const n2 = accMul(num2, Math.pow(10, m))
	return Number(n1) / Number(n2)
}
