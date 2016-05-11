function next_click(containerID, loadFile, needMask, callback) {
	return function() {
		$("#" + needMask).mask(); // TODO 导致边框变形
		var testObj = $("#" + containerID);
		if (testObj.length <= 0) {
			testObj = $("<div id='" + containerID + "'></div>");
			testObj.load(loadFile + "?" + Math.random(), function() {
				testObj.appendTo("#home_container");
				$.scrollTo("#" + containerID, 300);
				if (callback) {
					callback.apply(arguments);
				}
			});
		} else {
			testObj.show();
			$.scrollTo("#" + containerID, 300);
			if (callback) {
				callback.apply(arguments);
			}
		}
	};
};

function next_click_transverse(containerID, loadFile, index, callbackfirst, callback) {

	return function() {
		if (callbackfirst) {
			callbackfirst.apply(arguments);
		}
		var testObj = $("#" + containerID);
		if (testObj.length <= 0) {
			testObj = $("<div id='" + containerID + "'></div>");
			$(testObj).height($(window).height()), $(testObj).width($(window).width() - 20)

			testObj.load(loadFile + "?" + Math.random(), function() {
				$.each($("#home_container>div"), function(i, div) {
					$(div).hide(700);
				});
				$(testObj).fadeIn("slow", function() {
					testObj.appendTo("#home_container");
				});
				if (callback) {
					callback.apply(arguments);
				}
			});
		} else {
			if (callback) {
				callback.apply(arguments);
			}
		}
	};
};

function last_click_transverse(lastContainerID, thisContainerID, callback) {
	return function() {
		// $("#"+thisContainerID).hide(300, "linear");
		var deldiv = $("#" + thisContainerID);
		// $("#"+thisContainerID).remove();
		deldiv.fadeOut("normal", function() {
			deldiv.remove();
			$("#" + lastContainerID).show(700);
		});
		
		if (callback) {
			callback.apply(arguments);
		}
	};
};

// ---------------------
function last_click(lastContainerID, thisContainerID, callback) {
	return function() {
		// $("#"+thisContainerID).hide(300, "linear");
		var deldiv = $("#" + thisContainerID);
		// $("#"+thisContainerID).remove();
		deldiv.fadeOut("normal", function() {
			deldiv.remove();
		});
		$.scrollTo("#" + lastContainerID, 300, function() {
			$("#" + lastContainerID).unmask();
		});
		if (callback) {
			callback.apply(arguments);
		}
	};
};

// function next_click(containerID, loadFile, needMask, callback) {
// return function() {
// console.log(containerID);
// $(needMask).mask(); // TODO 导致边框变形
// var testObj = $(containerID);
// if (testObj.is(":hidden")) {
// testObj.show();
// $.scrollTo(containerID, 300);
// if (callback) {
// callback.apply(arguments);
// }
// } else {
// testObj.load(loadFile, function() {
// $.scrollTo(containerID, 300);
// console.log(testObj.attr("id"));
// if (callback) {
// callback.apply(arguments);
// }
// });
// }
// };
// };
// // ---------------------
// function last_click(lastContainerID, thisContainerID, callback) {
// return function() {
// $(thisContainerID).hide(300, "linear");
// $.scrollTo(lastContainerID, 300, function() {
// $(lastContainerID).unmask();
// });
// if (callback) {
// callback.apply(arguments);
// }
// };
// };
// ---------------------

// var omap;

/**
 * @param divid
 * @param target{lng:lng,lat:lat,zoom:zoom,cityname:cityname}
 * @returns {BMap.Map}
 */
var bmap = {};
function showMap(divid, target) {
	bmap = {};
	var tempMap = bmap[divid];
	if (!tempMap) {
		var mapOptions = {
			enableMapClick : false
		};
		var map;
		if (divid) {
			map = new BMap.Map(divid, mapOptions);
		} else {
			map = new BMap.Map("allmap", mapOptions);
		}
		map.clearOverlays();
		map.setMapStyle({
			features : ["road"],
			style : "normal"
		});// ,"water","land","building"
		if (target) {
			map.centerAndZoom(new BMap.Point(target.lng, target.lat), target.zoom);
			map.setCurrentCity(target.cityname);
		} else {
			map.centerAndZoom(new BMap.Point(116.54356, 39.869255), 11);
			//map.setCurrentCity("上海"); // 设置地图显示的城市 此项是必须设置的
		}
		// omap.addControl(new BMap.MapTypeControl()); // 添加地图类型控件
		// omap.enableScrollWheelZoom(false); // 开启鼠标滚轮缩放 */
		map.disableScrollWheelZoom();

		// var top_left_control = new BMap.ScaleControl({anchor:
		// BMAP_ANCHOR_TOP_LEFT});// 左上角，添加比例尺
		// var top_left_navigation = new BMap.NavigationControl();
		// //左上角，添加默认缩放平移控件
		var top_left_navigation = new BMap.NavigationControl({
			anchor : BMAP_ANCHOR_TOP_LEFT,
			type : BMAP_NAVIGATION_CONTROL_SMALL
		});
		map.addControl(top_left_navigation);
		tempMap = map;
		bmap[divid] = tempMap;
	}

	return tempMap;
};

function getCheckedValue(key) {
	var viewer = "";
	$(key).each(function() {
		if ($(this).prop("checked") == true) {
			viewer += $(this).val();
		}
	});
	return viewer;
}

function util_shape_distance(lon1, lat1, lon2, lat2) {
	var D2R = 0.017453;
	var a2 = 6378137.0;
	var e2 = 0.006739496742337;
	if (lon1 == lon2 && lat1 == lat2) {
		return 0.0;
	} else {
		var fdLambda = (lon1 - lon2) * D2R;
		var fdPhi = (lat1 - lat2) * D2R;
		var fPhimean = ((lat1 + lat2) / 2.0) * D2R;
		var fTemp = 1 - e2 * (Math.pow(Math.sin(fPhimean), 2));
		var fRho = (a2 * (1 - e2)) / Math.pow(fTemp, 1.5);
		var fNu = a2 / (Math.sqrt(1 - e2 * (Math.sin(fPhimean) * Math.sin(fPhimean))));
		var fz = Math.sqrt(Math.pow(Math.sin(fdPhi / 2.0), 2) + Math.cos(lat2 * D2R) * Math.cos(lat1 * D2R) * Math.pow(Math.sin(fdLambda / 2.0), 2));
		fz = 2 * Math.asin(fz);
		var fAlpha = Math.cos(lat2 * D2R) * Math.sin(fdLambda) * 1 / Math.sin(fz);
		fAlpha = Math.asin(fAlpha);
		var fR = (fRho * fNu) / ((fRho * Math.pow(Math.sin(fAlpha), 2)) + (fNu * Math.pow(Math.cos(fAlpha), 2)));
		return fz * fR;
	}
};

function util_shapearray_distance_baidupoint(points) {
	var revalue = 0;
	if (points.length <= 1) {
		return 0;
	}
	var last = points[0];
	for (var i = 1; i < points.length; i++) {
		var thiz = points[i];
		revalue += util_shape_distance(last.lng, last.lat, thiz.lng, thiz.lat);
		last = thiz;
	}
	return revalue;
}

function util_rtt_loscolor(los) {
	var tlos = 101;
	try {
		tlos = parseInt(los);
	} catch (e) {
		tlos = 101;
	}

	var color = "black";
	if (tlos == 1) {
		color = "#008000";
	} else if (tlos == 3) {
		color = "#D8FE01";// FFB400
	} else if (tlos == 4) {
		color = "#C4A23B"; // C4A23B
	} else if (tlos == 5) {
		color = "#ff0000";
	} else if (tlos === 100) {
		color = "#396BF3";// tmc event
	}
	return color;
}

function clone(src) {
	function mixin(dest, source, copyFunc) {
		var name, s, i, empty = {};
		for (name in source) {
			// the (!(name in empty) || empty[name] !== s) condition avoids
			// copying properties in "source"
			// inherited from Object.prototype. For example, if dest has a
			// custom toString() method,
			// don't overwrite it with the toString() method that source
			// inherited from Object.prototype
			s = source[name];
			if (!(name in dest) || (dest[name] !== s && (!(name in empty) || empty[name] !== s))) {
				dest[name] = copyFunc ? copyFunc(s) : s;
			}
		}
		return dest;
	}

	if (!src || typeof src != "object" || Object.prototype.toString.call(src) === "[object Function]") {
		// null, undefined, any non-object, or function
		return src; // anything
	}
	if (src.nodeType && "cloneNode" in src) {
		// DOM Node
		return src.cloneNode(true); // Node
	}
	if (src instanceof Date) {
		// Date
		return new Date(src.getTime()); // Date
	}
	if (src instanceof RegExp) {
		// RegExp
		return new RegExp(src); // RegExp
	}
	var r, i, l;
	if (src instanceof Array) {
		// array
		r = [];
		for (i = 0, l = src.length; i < l; ++i) {
			if (i in src) {
				r.push(clone(src[i]));
			}
		}
		// we don't clone functions for performance reasons
		// }else if(d.isFunction(src)){
		// // function
		// r = function(){ return src.apply(this, arguments); };
	} else {
		// generic objects
		r = src.constructor ? new src.constructor() : {};
	}
	return mixin(r, src, clone);
}

/**
 * 为BTable 设置event
 * 
 * @param tableid
 * @param method
 */
function event_tableevent(tableid, method) {
	var table = $("#" + tableid);
	$.each(method, function(i, one) {
		table.on(one.event, one.func);
	});
}
