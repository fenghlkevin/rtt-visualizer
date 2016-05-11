function show_intable(result, map, tableid, rtic) {
	var data = new Array();
	for (var i = 0; i < result.length; i++) {
		var msg = result[i];
		var istec = false;
		if (msg.type === 'TMC-TEC' || msg.type === 'OLR-TEC') {
			istec = true;
		}
		var overlay = create_overlay(msg.showResults, msg.message, map, istec);
		var tree = create_tree(msg.message, "message");
		var status = 'OK';
		if (msg.error) {
			status = msg.error.level;
		}
		data[i] = {
			messageid : msg.message.MMC.messageID,
			versionid : msg.message.MMC.versionID,
			message : msg.message,
			datatype : msg.type,
			shape : msg.showResults,
			error : msg.error,
			overlay : overlay,
			treeview : tree,
			status : status
		};
	}
	var table = $('#' + tableid);

	if (table) {
		table.bootstrapTable("destroy");
	}
	table.bootstrapTable({
		data : data
	});

	table.show();
	do_addevent_totable(map, tableid);
};

function create_overlay(shows, msg, map, istec) {

	// var shows = row.shape;
	var overlay = new Array();
	function linefunc(mshape, los) {
		var bmapline = new Array();
		var lastpoint = null;
		$.each(mshape, function(x, point) {
			if (point && point.x && point.y) {
				bmapline.push(new BMap.Point(point.x, point.y));
			} else {
				console.log("error point");
			}
		});
		var color = util_rtt_loscolor(los);

		// draw
		var polyline = new BMap.Polyline(bmapline, {
			strokeColor : color,
			strokeWeight : 2,
			strokeOpacity : 1
		});
		if (istec) {
			var p = polyline.getPath()[polyline.getPath().length - 1];
			if (p != undefined) {
				var url = "../icon/tpeg/default.gif";
				if (msg.tecData.causes.mainCause === "27") {
					url = "../icon/tpeg/jamfront.png";
				} else if (msg.tecData.causes.mainCause === "5") {
					url = "../icon/tpeg//closure.png";
				}

				var pt = new BMap.Point(p.lng, p.lat);
				var myIcon = new BMap.Icon(url, new BMap.Size(30, 30));
				var marker2 = new BMap.Marker(pt, {
					icon : myIcon
				});
				overlay.push(marker2);
			}
		}

		if (msg) {
			var opts = {
				width : 200, // 信息窗口宽度
				height : 100, // 信息窗口高度
				title : "Data Message Info", // 信息窗口标题
				enableMessage : false,// 设置允许信息窗发送短息
			}
			var infoWindow = new BMap.InfoWindow("Message ID is " + msg.MMC.messageID, opts); // 创建信息窗口对象
			polyline.addEventListener("click", function(type, target, point, pixel) {
				map.openInfoWindow(infoWindow, type.point); // 开启信息窗口
			});
		}
		overlay.push(polyline);
	};

	function markerfunc(markers) {
		$.each(markers, function(i, marker) {
			var pt = new BMap.Point(marker.x, marker.y);
			var mkoverlay = new BMap.Marker(pt); // 创建标注
			if (msg) {
				var opts = {
					width : 200, // 信息窗口宽度
					height : 100, // 信息窗口高度
					title : "Data Message Info", // 信息窗口标题
					enableMessage : false,// 设置允许信息窗发送短息
				}
				var infoWindow = new BMap.InfoWindow("Message ID is " + msg.MMC.messageID, opts); // 创建信息窗口对象
				mkoverlay.addEventListener("click", function(type, target, point, pixel) {
					map.openInfoWindow(infoWindow, type.point); // 开启信息窗口
				});
			}
			overlay.push(mkoverlay);
		});
	};

	$.each(shows, function(i, oneshow) {
		var mshape = oneshow.shape;
		var markers = oneshow.markers;
		linefunc(mshape, oneshow.los);
		markerfunc(markers);
	});

	// for (var i = 0; i < shows.length; i++) {
	// var oneshow = shows[i];
	// var mshape = oneshow.shape;
	// var markers=oneshow.markers;
	//		
	// for (var y = 0; y < mshape.length; y++) {
	// var point = mshape[y];
	// bmapline[y] = new BMap.Point(point.x, point.y)
	// }
	// var los = oneshow.los;
	// var color = "black";
	// if (los === 1) {
	// color = "#008000";
	// } else if (los === 3) {
	// color = "#D8FE01";// FFB400
	// } else if (los === 4) {
	// color = "#C4A23B"; // C4A23B
	// } else if (los === 5) {
	// color = "#ff0000";
	// } else if (los === 100) {
	// color = "#396BF3";// tmc event
	// }
	//
	// // draw
	// var polyline = new BMap.Polyline(bmapline, {
	// strokeColor : color,
	// strokeWeight : 3,
	// strokeOpacity : 1
	// });
	//
	// if (msgid) {
	// var opts = {
	// width : 200, // 信息窗口宽度
	// height : 100, // 信息窗口高度
	// title : "Data Message Info", // 信息窗口标题
	// enableMessage : false,// 设置允许信息窗发送短息
	// }
	// var infoWindow = new BMap.InfoWindow("Message ID is " + msgid, opts); //
	// 创建信息窗口对象
	// polyline.addEventListener("click", function(type, target, point, pixel) {
	// console.log(type.point);
	// map.openInfoWindow(infoWindow, type.point); // 开启信息窗口
	// });
	// }
	//
	// overlay[i] = polyline;
	//
	// }
	return overlay;
}

function create_tree(source, text) {
	var revalue = {
		text : text,
		children : new Array()
	};
	for (key in source) {
		var jk = key + ":" + source[key];
		var objk = key + ":";

		var temp = source[key];
		if (temp instanceof Array) {
			revalue.children.push(create_tree(source[key], objk + "array"));
		} else if (typeof (temp) === "object") {
			revalue.children.push(create_tree(source[key], objk + "object"));
		} else {
			var child = {
				text : jk
			};
			revalue.children.push(child);
		}
	}
	return revalue;
};

function rowStyle(row, index) {
	var classes = ['active', 'success', 'info', 'warning', 'danger'];

	if (row.error) {
		var revalue = {};
		if (row.error.level === 'SS') {
			revalue.classes = classes[4];
		} else if (row.error.level === 'S') {
			revalue.classes = classes[3];
		} else if (row.error.level === 'A') {
			revalue.classes = classes[3];
		} else if (row.error.level === 'B') {
			revalue.classes = classes[2];
		} else if (row.error.level === 'C') {
			revalue.classes = classes[2];
		} else if (row.error.level === 'D') {
			revalue.classes = classes[2];
		} else if (row.error.level === 'OK') {
			revalue.classes = classes[1];
		}
		return revalue;
	}
	return {};
}

function do_ouput(tableid,valueid) {
	return function() {
		var sections = $("#" + tableid).bootstrapTable('getAllSelections');
		var ids = "";
		$.each(sections, function(i, sec) {
			console.log(sec);
			ids += sec.messageid + "#";
		});
		var url = "../../parser/outputfile?callback=?";
		var data = $("#"+valueid).val();
		data = data.replace("data:;base64,", "");
		$.post(url, {
			ids : ids,
			data : data
		}, function(result) {
			window.location.href = result;
		}, "json");
	}
}

function do_addevent_totable(map, tableid) {

	var outputCheckFunc = function() {
		var sections = $("#" + tableid).bootstrapTable('getAllSelections');
		var output = $("#outp");
		if (sections.length <= 0) {
			output.attr("disabled", "disabled");
		} else {
			output.removeAttr("disabled");
		}
	}

	var checkFunc = function(e, row, changeView) {
		console.log(row);
		if (row.overlay) {
			$.each(row.overlay, function(i, val) {
				map.addOverlay(val);

			});

		}
	};
	var event_checkrow = {
		event : "check.bs.table",
		func : function(e, row) {
			checkFunc(e, row, true);
			outputCheckFunc();
			var allpoints = [];
			var overlays = map.getOverlays();
			$.each(overlays, function(i, o) {
				try {
					allpoints = allpoints.concat(o.getPath());
				} catch (e) {
					allpoints.push(o.getPosition());
				}
			});
			map.setViewport(allpoints);
		}
	};

	var event_uncheckrow = {
		event : "uncheck.bs.table",
		func : function(e, row) {
			outputCheckFunc();
			if (row.overlay) {
				$.each(row.overlay, function(i, val) {
					map.removeOverlay(val);
				});
			}
		}
	}

	var event_checkall = {
		event : "check-all.bs.table",
		func : function(e, rows) {
			outputCheckFunc();
			$.each(rows, function(i, row) {
				checkFunc(e, row, false)
			});
		}
	};
	var event_uncheckall = {
		event : "uncheck-all.bs.table",
		func : function(e, rows) {
			outputCheckFunc();
			map.clearOverlays();
		}
	}
	var event_clickrow = {
		event : "click-row.bs.table",
		func : function(e, row, $element) {
			var datastatus = 'Data Parse Status : ';
			var color = "black";
			if (row.error) {
				datastatus = 'Data Parse Status : ' + row.error.level + ',<br/> Message : ' + row.error.message + '<br/>';
				color = "red";
			} else {
				datastatus = 'Data Parse Status : OK';
			}
			$element.popover({
				placement : "left",// bottom
				html : "true",
				trigger : "click",
				content : function() {
					var rediv = $('<div class="container" style="overflow-y:scroll;overflow-x:scroll;width:250px; height:400px;"><span style="font-size:1px;color:"+color+";">'
							+ datastatus + '</span><hr><div id="tree"></div></div>');
					// var div = $('<div
					// style="overflow-y:scroll;overflow-x:scroll;width:250px;
					// height:380px;"></div>');
					var div = $(rediv).find("div[id='tree']");
					div.jstree({
						'core' : {
							'data' : [row.treeview]
						}
					});
					div.on('changed.jstree', function(e, data) {
						$.each(row.overlay, function(i, val) {
							map.removeOverlay(val);
						});
						for (var i = 0; i < data.selected.length; i++) {
							var selectedNode = data.instance.get_node(data.selected[i]);
							var parent = data.instance.get_node(selectedNode.parent);
							if (parent.text === "sections:array") {
								var temp = selectedNode.text;
								var i = temp.split(":")[0];
								var val = row.overlay[i];
								map.addOverlay(val);
							}
						}
					});
					return rediv;
				}
			});
		}
	};

	var events = [event_checkrow, event_uncheckrow, event_checkall, event_uncheckall, event_clickrow];

	event_tableevent(tableid, events);
}