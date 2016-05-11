/**
 * 第二部的回调函数
 */
function function_to_step2_createfile() {
	// return function() {
	var map = showMap("createfile_map");
	// do_add_mapcontrol(map);
	do_add_mapmenu(map);
	do_addevent_totable_createfile(map);
}

var createfile_tableid = "createfile_btable";
function function_to_step3_createfile() {
	var table = $('#' + createfile_tableid);
	var sdata = table.bootstrapTable("getSelections");
	var data = [];
	$.each(sdata, function(i, row) {
		var message = {
			messageid : row.messageid,
			messagetype : row.message_type,
			messagelrc : row.lrc_type,
			roaditems : [],
			event : null,
			mmc : null,
		};
		// message.event=row.message[0].event;
		data.push(message);
		$.each(row.message, function(j, roaditem) {
			message.mmc = roaditem.mmc;
			var lrc = null;
			if (message.messagetype === "tfp" && message.messagelrc === "olr") {
				lrc = {
					type : "LinearLocationReference",
					value : roaditem.itemids
				};
			} else if (message.messagetype === "tec" && message.messagelrc === "olr") {
				lrc = {
					type : "PointAlongLineLocationReference",
					value : roaditem.shape[0]
				}
			}
			var ri = {
				areacode : roaditem.areacode,
				direction : roaditem.direction,
				groupid : roaditem.groupid,
				id : roaditem.id,
				lrc : lrc,
				sections : []
			};
			message.event = roaditem.event;
			message.roaditems.push(ri);
			$.each(roaditem.losshape, function(x, section) {
				if (section.length >= 0) {
					var section = {
						length : section.length,
						los : section.los,
						speed:section.speed,
					// 也许需要增加shape
					}
					ri.sections.push(section);
				}
			});
		});
	});
	var jsonvalue = JSON.stringify(data);
	var url = "../../createfile/create?callback=?";
	$.post(url, {
		data : jsonvalue
	}, function(result) {
		window.location.href = result;
	}, "json");

}

function do_add_mapmenu(map) {
	var drawingManager = new BMapLib.DrawingManager(map);
	drawingManager.setDrawingMode(BMAP_DRAWING_CIRCLE);

	var menu = new BMap.ContextMenu();

	var points = [];

	var txtMenuItem = [{
		text : 'CREATE TMC-TFP',
		callback : function() {
			map.clearOverlays();
			drawingManager.setDrawingMode(BMAP_DRAWING_CIRCLE);
			drawingManager.open();
			do_message_type("tfp", "tmc", true);
		}
	}, {
		text : 'CREATE TMC-TEC',
		callback : function() {
			map.clearOverlays();
			drawingManager.setDrawingMode(BMAP_DRAWING_CIRCLE);
			drawingManager.open();
			do_message_type("tec", "tmc", true);
		}
	}, {
		text : 'CREATE OLR-TFP',
		callback : function() {
			map.clearOverlays();
			drawingManager.setDrawingMode(BMAP_DRAWING_MARKER)
			drawingManager.open();
			points = [];
			do_message_type("tfp", "olr", true);
		}
	}, {
		text : 'CREATE OLR-TEC',
		callback : function() {
			map.clearOverlays();
			drawingManager.setDrawingMode(BMAP_DRAWING_MARKER)
			drawingManager.open();
			points = [];
			do_message_type("tec", "olr", true);
		}
	}];
	for (var i = 0; i < txtMenuItem.length; i++) {
		var item = new BMap.MenuItem(txtMenuItem[i].text, txtMenuItem[i].callback, {
			width : 150
		});
		menu.addItem(item);
		menu.addSeparator();
	}
	map.addContextMenu(menu);

	function do_message_type(messagetype, lrctype, randomID) {
		$("#messagetype").val(messagetype);
		$("#lrctype").val(lrctype);
		$('#temp_messageid_input').val("");
		if (randomID) {
			var timestamp = new Date().getTime();
			$('#temp_messageid_input').val(timestamp);
			$("#messageid_input").val(timestamp);
			$("#messageid_input").removeAttr("disabled");
		}
	}

	drawingManager.addEventListener('circlecomplete', function(circle) {
		drawingManager.close();
		event_queryroad_circle(circle.getCenter(), circle.getRadius(), map);
		 $('[data-toggle="popover"]').popover();
	});

	drawingManager.addEventListener('markercomplete', function(e, overlay) {
		var form = do_get_formvalue();
		points.push(e.getPosition());
		if (form.lrctype === "olr" && form.messagetype === "tfp") {
			if (points.length === 2) {
				drawingManager.close();
				event_queryroad_route(points[0], points[1], map);
			}
		} else {
			// olr-tec
			drawingManager.close();
			overlay.addEventListener("click", function(type, target) {
				var thisroaditem = {
					areacode : 0,
					direction : -1,
					groupid : -1,
					id : 100001,
					inOverlay : false,
					length : length,
					prevItemId : 0,
					nextItemId : 0,
					overlay : [],
					staticoverlay : overlay,
					shape : util_bpoint_2_shapepoint([e.getPosition()]),
					type : "OLR"
				};
				event_overlay_click(map, thisroaditem);
			});
		}

	});
}

function util_bpoint_2_shapepoint(bshape) {
	var shape = [];
	$.each(bshape, function(i, bp) {
		var sp = {
			x : bp.lng,
			y : bp.lat
		};
		shape.push(sp);
	});
	return shape;
}

/**
 * 增加地图图层用的工具条
 * 
 * @param omap
 */
// function do_add_mapcontrol(omap) {
// var checkedOverlays = [];
// var overlaycomplete = function(e) {
// checkedOverlays.push(e.overlay);
// };
// //
// // BMAP_DRAWING_POLYLINE
// var drawingTypes = [BMAP_DRAWING_CIRCLE];
// // 实例化鼠标绘制工具
// var drawingManager = new BMapLib.DrawingManager(omap, {
// isOpen : false, // 是否开启绘制模式
// enableDrawingTool : true, // 是否显示工具栏
// drawingToolOptions : {
// anchor : BMAP_ANCHOR_TOP_RIGHT,
// offset : new BMap.Size(5, 5),
// scale : 0.7,
// drawingModes : drawingTypes
// },
// });
//
// // drawingManager.addEventListener('overlaycomplete', overlaycomplete);
// drawingManager.addEventListener('circlecomplete', function(circle) {
// drawingManager.close();
// event_queryroad_circle(circle.getCenter(), circle.getRadius(), omap);
// });
// }
/**
 * 创建折线对象
 * 
 * @param mshape
 * @returns {BMap.Polyline}
 */
function do_create_overlay(mshape, los) {
	var bmapline = new Array();
	for (var y = 0; y < mshape.length; y++) {
		var point = mshape[y];
		bmapline.push(new BMap.Point(point.x, point.y));
	}

	var color = "#271DE7";

	if (los) {
		color = util_rtt_loscolor(los);
	}
	// draw
	var polyline = new BMap.Polyline(bmapline, {
		strokeColor : color,
		strokeWeight : 5,
		strokeOpacity : 1
	});

	return polyline;
}

function do_get_formvalue() {
	var lpinfo_version = $("#lpinfo_version").find("option:selected").val();
	var tmc_map_version = $("#tmc_map_version").find("option:selected").val();
	var olr_map_version = $("#olr_map_version").find("option:selected").val();
	var messagetype = $("#messagetype").val();
	var lrctype = $("#lrctype").val();
	var messageid = $('#temp_messageid_input').val()
	// var messagetype = getCheckedValue("input[name='messagetype[]']");
	// var lrctype = getCheckedValue("input[name='lrctype[]']");
	return {
		lpinfo_version : lpinfo_version,
		messagetype : messagetype,
		lrctype : lrctype,
		messageid : messageid,
		tmc_map_version : tmc_map_version,
		olr_map_version : olr_map_version
	}
}

/**
 * 根据中心点、半径，搜索出附近的路段内容，并对路段内容设置pop窗口以及窗口内方法
 * 
 * @param center
 * @param radius
 * @param map
 */
function event_queryroad_circle(center, radius, map) {

	var formvalue = do_get_formvalue();
	var url = "../../createfile/querybycircle?callback=?";
	$.post(url, {
		radius : radius,
		center_lng : center.lng,
		center_lat : center.lat,
		lpinfo_version : formvalue.lpinfo_version,
		tmc_map_version : formvalue.tmc_map_version,
		olr_map_version : formvalue.olr_map_version,
		messagetype : formvalue.messagetype,
		lrctype : formvalue.lrctype
	}, function(result) {
		console.log(result);
		do_after_queryroad(result, map)
	}, "json");
}

/**
 * 根据中心点、半径，搜索出附近的路段内容，并对路段内容设置pop窗口以及窗口内方法
 * 
 * @param center
 * @param radius
 * @param map
 */
function event_queryroad_route(spoint, epoint, map) {

	var formvalue = do_get_formvalue();
	var url = "../../createfile/querybyroute?callback=?";
	$.post(url, {
		spoint : spoint.lng + " " + spoint.lat,
		epoint : epoint.lng + " " + epoint.lat,
		lpinfo_version : formvalue.lpinfo_version,
		tmc_map_version : formvalue.tmc_map_version,
		olr_map_version : formvalue.olr_map_version,
		messagetype : formvalue.messagetype,
		lrctype : formvalue.lrctype
	}, function(result) {
		do_after_queryroad(result, map)
	}, "json");
}

function do_after_queryroad(result, map) {
	map.clearOverlays();
	var allpoints = [];
	$.each(result, function(i, road) {
		if (i !== 0) {
			$.each(road.roaditems, function(i, thisroaditem) {
				var overlay = do_create_overlay(thisroaditem.shape);
				thisroaditem.overlay = overlay;
				var color = overlay.getStrokeColor();
				overlay.addEventListener("mouseover", function(type, target, point, pixel) {
					this.setStrokeColor("red");
				});

				overlay.addEventListener("mouseout", function(type, target, point, pixel) {
					this.setStrokeColor(color);
				});

				overlay.addEventListener("click", function(type, target, point, pixel) {
					event_overlay_click(map, thisroaditem);
				});
				allpoints = allpoints.concat(overlay.getPath());
				map.addOverlay(overlay);
			});
		}
	});
	map.setViewport(allpoints);
}
function util_create_pop_divid(ri, extent) {
	var msgtype = do_get_formvalue();
	var messageid = null;
	var id = null;
	if (msgtype.messagetype === "tec") {
		messageid = util_create_messageid();
		id = "id";
	} else {
		messageid = util_create_messageid(ri.id, ri.groupid, ri.areacode, ri.direction);
		id = ri.id;
	}
	// var id = !ri ? "id" : ri.id;
	return messageid + "_" + id + "_" + extent;
}
/**
 * 当折线被点击时
 * 
 * @param type
 * @param map
 * @param ri
 */
function event_overlay_click(map, ri) {
	// 对div进行添加或者隐藏显示

	function create_mmc() {
		var mmcpopid = util_create_messageid() + "_mmc";
		var mmclist = $("div[id='mmcpop']");
		var existDiv = false;
		$.each(mmclist.find("div[name='mmc[]']"), function(i, div) {
			// $(this).hide();
			if (div.id === mmcpopid) {
				existDiv = true;
				// $(this).show();
			}
		});
		if (!existDiv) {
			var page_url = 'createfile/createfile_mmc_modal_page.html';
			var rediv = $("<div id='" + mmcpopid + "' name='mmc[]'></div>");
			var mtime = util_getNowTime();
			var ntime = util_getNowTime(30)
			rediv.load(page_url + '?' + Math.random(), function() {
				rediv.appendTo("#mmcpop");
				rediv.find("div[id='mmcModal']").attr('id', mmcpopid);

				rediv.find("input[id='versionid']").val("0");

				var alldp = rediv.find(".input-append");
				alldp.datetimepicker({
					weekStart : 0,
					todayBtn : true,
					autoclose : true,
					todayHighlight : 1,
					startView : 2,
					showMeridian : true,
					initialDate : mtime,
				});

				var expdp = rediv.find("#dp1").datetimepicker({
					weekStart : 0,
					todayBtn : true,
					autoclose : true,
					todayHighlight : 1,
					startView : 2,
					showMeridian : true,
					initialDate : ntime,
				});

				$.each(alldp, function(i, datep) {
					var inp = $(datep).find("input[type='text']");
					if (datep.id === "dp1") {
						inp.val(ntime);
					} else {
						inp.val(mtime);
					}
				});
			});
		} else {

		}
		return mmcpopid
	}

	function create_msgpage(mmcpopid) {
		var msgtype = do_get_formvalue();
		var pop = $("div[id='pop']");
		var divid = util_create_pop_divid(ri, msgtype.messagetype + "_" + msgtype.lrctype);
		var existDiv = false;
		$.each(pop.find("div[name='pop[]']"), function(i, div) {
			$(this).hide();
			if (div.id === divid) {
				existDiv = true;
				$(this).show();
				load_infowindow_event($(this), ri, null, map, "#" + formid);
			}
		});
		// 用于表单内加行使用
		var formid = divid + "_form"
		var modalid = divid + "_modal"
		if (!existDiv) {
			var page_url = null;
			if (msgtype.messagetype === 'tfp') {
				var page_url = 'createfile/createfile_tfp_pop_page.html';
			} else if (msgtype.messagetype === 'tec') {
				var page_url = 'createfile/createfile_tec_pop_page.html';
			}
			var rediv = $("<div id='" + divid + "' name='pop[]'></div>");
			rediv.load(page_url + '?' + Math.random(), function() {
				rediv.appendTo("#pop");
				exprowhtml=rediv.find("#row").html();
				event_tfp_lospeed(rediv.find("#row"));
				rediv.find("div[id='form']").attr('id', formid);
				rediv.find("div[id='causeModal']").attr('id', modalid);
				rediv.find("input[id='mmcbtn']").click(function() {
					var mo = $("#mmcpop").find("div[id='" + mmcpopid + "']");
					mo.modal({
						backdrop : 'static',
						keyboard : false,
						show : true
					});

					mo.modal().css({
						"margin-top" : function() {
							return +100;
						}
					});
				});

				load_infowindow_event(rediv, ri, null, map, "#" + formid);
				return divid;
			});
		}
	}

	var mmcid = create_mmc();
	create_msgpage(mmcid);

	// set map line
	// 设置当前div的overlay 折线，如果overaly不存在，需要创建一个
	if (!ri.overlay) {
		var overlay = do_create_overlay(ri.shape);
		ri.overlay = overlay;
	}
	var overlays = map.getOverlays();
	var show = false;
	$.each(map.getOverlays(), function(i, overlay) {
		if (overlay == ri.overlay) {
			show = true;
		}
	});
	if (!show) {
		map.addOverlay(ri.overlay);
	}

}

var exprowhtml="";

function util_getNowTime(min) {
	if (min === undefined) {
		min = 0;
	}
	var temp = new Date();
	var now = new Date(temp.getTime() + min * 60 * 1000);
	var month = now.getMonth() + 1;
	if (month < 10) {
		month = "0" + month;
	}
	var date = now.getDate();
	if (date < 10) {
		date = "0" + date;
	}
	var hours = now.getHours();
	if (hours < 10) {
		hours = "0" + hours;
	}
	var min = now.getMinutes();
	if (min < 10) {
		min = "0" + min;
	}
	var mtime = month + "-" + date + "-" + now.getFullYear() + " " + hours + ":" + min;
	return mtime;
}

function util_create_roaditemkey(id, roaditem) {
	var id = id + "" + roaditem.areacode + "" + roaditem.direction;
	return id;
}
function event_tfp_lospeed(row) {
	var select = row.find('select[id="ts"]');
	var losele = row.find("input[name='los[]']");
	var speedele = row.find("input[name='speed[]']");

	var los = losele.val();
	var speed = speedele.val();
	select.multiselect({
		enableFiltering : true,
		maxHeight : 300,
		buttonClass:"btn btn-default btn-sm",
		onChange : function(element, checked) {
			// 获得所有的selected数据
			var key = element.val();
			var temp = key.split(".")
			var type = temp[0];
			var value = temp[1];
			var selectedopts = select.find("option:selected");
			var hasvaluebefore=false;
			$.each(selectedopts, function(i, op) {
				var tk=$(op).val();
				if(tk.split(".")[0]===type&&tk!=key){
					$(op).removeAttr("selected");
					hasvaluebefore=true;
				}
			});
			if(type==="los"){
				losele.val(key);
			}else{
				speedele.val(key);
			}
			if(!hasvaluebefore){
				$(element).attr("selected","selected");
			}
			select.multiselect('refresh')
		}
	});
	select.multiselect('refresh')
}

/**
 * pop窗口内的事件内容
 * 
 * @param rediv
 * @param ri
 * @param infoWindow
 */
function load_infowindow_event(rediv, ri, infoWindow, map, formid) {
	// load窗口后设置内容
	// 1.设置长度内容
	rediv.find("span[id='length']").text(ri.length);

	var leftlengthdiv = rediv.find("span[id='leftlength']")
	leftlengthdiv.text(0);

	var lengthinput = rediv.find("input[id='setlength']");
	lengthinput.val(ri.length);
	lengthinput.on('input', change_length_input(ri, rediv, leftlengthdiv));

	// 设置按钮事件
	// submit button
	event_pop_button_sub(rediv, "button[id='submit']", ri, map);
	// prev button
	event_pop_button_nextANDprev(rediv, "button[id='prev_item']", ri.prevItemId, ri, map);
	// next button
	event_pop_button_nextANDprev(rediv, "button[id='next_item']", ri.nextItemId, ri, map)

	// 1.设置增加行按钮事件
	rediv.find("button[id='addrow']").click(function() {
		var leftlengthdiv = rediv.find("span[id='leftlength']")
		if (parseInt(leftlengthdiv.text()) > 0) {
			//var row = rediv.find("div[id='row']");
			var newrow = $("<div></div>").html(exprowhtml);
			var newrow_input = newrow.find("input[id='setlength']");
			newrow_input.val(leftlengthdiv.text());
			leftlengthdiv.text(0);
			var func = change_length_input(ri, rediv, leftlengthdiv);
			newrow_input.on('input', func);
			newrow.find("button[id='remove']").click(function() {
				var row = $(this).parent();
				row.remove();
				func.apply();
			});
			event_tfp_lospeed(newrow);
			newrow.appendTo(formid);
		}
	});

}

function event_pop_button_nextANDprev(rediv, find, pnRiID, thisroaditem, map, callback) {
	var btn = rediv.find(find);
	if (!pnRiID || pnRiID === '0') {
		btn.attr("disabled", "disabled");
	} else {
		btn.removeAttr("disabled");
		btn.unbind("click");
		btn.click(function() {
			// get roaditem from table
			var table = $('#' + createfile_tableid);
			var tdata = table.bootstrapTable("getData");
			var firstrow = !tdata.selector ? false : true;
			// var hasRoadItem = false;
			var tempri;
			if (!firstrow) {
				var messageid = util_create_messageid(pnRiID, thisroaditem.groupid, thisroaditem.areacode, thisroaditem.direction);
				$.each(tdata, function(i, rowdata) {
					if (rowdata.messageid === messageid) {
						tempri = rowdata.message[pnRiID];
					}
				});
			}
			if (tempri) {
				do_pop_submit(rediv, thisroaditem, map);// submit this pop page
				event_overlay_click(map, tempri);// load next pop page;

			} else {
				var formvalue = do_get_formvalue();
				var url = "../../createfile/queryroaditem?callback=?";
				$.post(url, {
					lpinfo_version : formvalue.lpinfo_version,
					tmc_map_version : formvalue.tmc_map_version,
					olr_map_version : formvalue.olr_map_version,
					messagetype : formvalue.messagetype,
					lrctype : formvalue.lrctype,
					roaditemid : pnRiID,
					roaditem_areacode : thisroaditem.areacode,
					direction : thisroaditem.direction
				}, function(result) {
					do_pop_submit(rediv, thisroaditem, map);
					event_overlay_click(map, result);
				}, "json");
			}
		});
	}
}

function do_pop_submit(rediv, thisroaditem, map, callback) {

	var msgtype = do_get_formvalue();
	var sub = false;
	if (msgtype.messagetype === "tfp") {
		sub = do_submit_formdata_tfp_tmc(rediv, thisroaditem);
	} else if (msgtype.messagetype === "tec") {
		sub = do_submit_formdata_tec_tmc(rediv, thisroaditem);
	}
	thisroaditem.mmc = do_getMMC_value();
	if (sub) {
		var table = $('#' + createfile_tableid);
		table.show();
		var tdata = table.bootstrapTable("getData");
		$("#messageid_input").attr("disabled","disabled");
		var messageid =$("#messageid_input").val(); //util_create_messageid(thisroaditem.id, thisroaditem.groupid, thisroaditem.areacode, thisroaditem.direction);
		var firstrow = !tdata.selector ? false : true;

		var arr = {};
		arr[thisroaditem.id] = thisroaditem;
		var data = {
			messageid : messageid,
			message_type : msgtype.messagetype,
			lrc_type : msgtype.lrctype,
			message : arr
		};
		var usemessage = arr;
		if (firstrow) {
			table.bootstrapTable({
				data : [data]
			});
		} else {
			var existRow = null;
			$.each(tdata, function(i, row) {
				if (row.messageid === messageid) {
					existRow = row;
				}
			});
			if (existRow) {
				existRow.message[thisroaditem.id] = thisroaditem;
				var usemessage = existRow.message;
			} else {
				table.bootstrapTable("append", [data]);
			}
		}
		map.clearOverlays();
		do_draw_onmap(usemessage, map);

		var divid = util_create_pop_divid(thisroaditem, msgtype.messagetype + "_" + msgtype.lrctype);
		$("div[id='" + divid + "']").hide();
	}
}

function do_submit_formdata_tec_tmc(rediv, thisroaditem) {
	var cloneshape = jQuery.extend(true, [], thisroaditem.shape);
	var event = rediv.find("textarea[id='event']").val();
	if (event === "") {
		return false;
	}
	thisroaditem.losshape = [];
	var losshape = cloneshape;

	var losoverlay = null;
	if (thisroaditem.staticoverlay) {
		losoverlay = thisroaditem.staticoverlay;
	} else {
		losoverlay = do_create_overlay(losshape);
	}

	thisroaditem.losshape.push({
		length : -1,
		los : -1,
		losshape : losshape,
		losoverlay : losoverlay,
	});
	thisroaditem.event = event;
	return true;
}
function do_getMMC_value() {
	var mmcid = util_create_messageid() + "_mmc";
	var mmcdiv = $("div[id='" + mmcid + "']")[0];
	console.log(mmcdiv);
	var mmc = {
		versionid : $(mmcdiv).find("input[id='versionid']").val(),
		messageExpiryTime : $(mmcdiv).find("input[id='messageExpiryTime']").val(),
		cancelflag : $(mmcdiv).find("select[id='cancelflag']").find("option:selected").val(),
		messageGenerationTime : $(mmcdiv).find("input[id='messageGenerationTime']").val(),
		starttime : $(mmcdiv).find("input[id='starttime']").val(),
		stoptime : $(mmcdiv).find("input[id='stoptime']").val(),
	};
	return mmc;
}
/**
 * 提交tfp_tmc数据到btable的row中，存储到roaditem的losshape字段中. 数据结构
 * thisroaditem.losshape.push({ length : length,//影响长度 los : los,//los losshape :
 * losshape,//影响长度的经纬度序列 losoverlay : do_create_overlay(losshape, los)
 * //页面展示使用的overlay });
 * 
 * @param rediv
 * @param thisroaditem
 */
function do_submit_formdata_tfp_tmc(rediv, thisroaditem) {

	// submit 当前div中的数据内容，并存储到row中
	// 把当前roaditem设置的路况，取出来。并放如对象中
	// 把该row中设置到message中的 路段都取出来，并把roaditem.losshape 画到地图上
//	var losarray = rediv.find("select[name='los[]']").find("option:selected");
	var losarray = rediv.find("input[name='los[]']");
	var sparray = rediv.find("input[name='speed[]']");
	var lengthinput = rediv.find("input[name='length[]']");
	var leftlengthdiv = rediv.find("span[id='leftlength']")
	var vali = true;
	if (parseInt(leftlengthdiv.text()) != 0) {
		vali = false;
	} else {
		$.each(lengthinput, function(i, input) {
			try {
				var t = parseInt(input.value);
				if (t <= 0) {
					vali = false;
				}
			} catch (e) {
				vali = false;
			}
		});
	}
	if (!vali) {
		return false;
	}

	var cloneshape = jQuery.extend(true, [], thisroaditem.shape);
	thisroaditem.losshape = [];
	var lastPoint = cloneshape.shift();
	$.each(lengthinput, function(i, input) {
		var length = parseInt(input.value);
		if (length >= 0) {
			var distance = length;
			var los = $(losarray[i]).val().split(".")[1];
			var speed=$(sparray[i]).val().split(".")[1];
			var losshape = [];
			losshape.push(lastPoint);
			if (i === lengthinput.length - 1) {
				losshape = losshape.concat(cloneshape);
			} else {
				do {
					var nowPoint = cloneshape.shift();
					var len = util_shape_distance(lastPoint.x, lastPoint.y, nowPoint.x, nowPoint.y);
					if (distance - len >= 0) {
						losshape.push(nowPoint);
						distance = distance - len;
						lastPoint = nowPoint;
					} else {
						var t = (len - distance) / len;// 得到占比
						var tx = lastPoint.x + (nowPoint.x - lastPoint.x) * t;
						var ty = lastPoint.y + (nowPoint.y - lastPoint.y) * t;
						var sp = {
							x : tx,
							y : ty
						};
						losshape.push(sp);
						lastPoint = sp;
						break;
					}
				} while (cloneshape.length > 0);
			}

			thisroaditem.losshape.push({
				length : length,
				los : los,
				speed:speed,
				losshape : losshape,
				losoverlay : do_create_overlay(losshape, los)
			});
		}
	});
	return true;
}

function do_draw_onmap(usemessage, map) {
	$.each(usemessage, function(i, tempri) {
		$.each(tempri.losshape, function(j, onelos_shape) {
			map.addOverlay(onelos_shape.losoverlay);
		})
	});
}

function event_pop_button_sub(rediv, find, ri, map, callback) {
	var btn_submit = rediv.find(find);
	btn_submit.unbind("click");
	btn_submit.click(function() {
		do_pop_submit(rediv, ri, map, callback);
	});

}

function util_create_messageid(id, groupid, areacode, direction) {
	var formvalue = do_get_formvalue();
	if (!formvalue.messageid) {
		return (groupid * 100 + areacode) * 10 + direction;
	}
	return formvalue.messageid;

}

function change_length_input(ri, div, leftlengthdiv) {
	return function() {
		var uselength = 0;
		var inputs = div.find("input[name='length[]']");
		var lastinp = null;
		$.each(inputs, function(i, input) {
			uselength += parseInt(input.value ? input.value : 0);
		});

		var v = ri.length - uselength;

		if (v < 0) {
			uselength = 0;
			$.each(inputs, function(i, input) {
				if (i != inputs.length - 1) {
					uselength += parseInt(input.value ? input.value : 0);
				} else {
					v = ri.length - uselength;
					this.value = v;
					v = 0;
				}
			});
		}
		leftlengthdiv.text(v);
	}
};

function do_addevent_totable_createfile(map) {

	var checkFunc = function(e, row, changeView) {
		$.each(row.message, function(i, tempri) {
			$.each(tempri.losshape, function(j, one_losshape) {
				map.addOverlay(one_losshape.losoverlay);
			});
		});

		var overlays = map.getOverlays();
		var allpoints = [];
		$.each(overlays, function(i, o) {
			try {
				allpoints = allpoints.concat(o.getPath());
			} catch (e) {
				allpoints.push(o.getPosition());
			}
		});
		if (changeView) {
			map.setViewport(allpoints);
		}
		return allpoints;
	}
	event_tableevent(createfile_tableid, [{
		event : "check.bs.table",
		func : function(e, row) {
			checkFunc(e, row, true);
		}
	}, {
		event : "uncheck.bs.table",
		func : function(e, row) {
			$.each(row.message, function(i, tempri) {
				$.each(tempri.losshape, function(j, one_losshape) {
					map.removeOverlay(one_losshape.losoverlay);
				});
			});
		}
	}, {
		event : "check-all.bs.table",
		func : function(e, rows) {
			var allpoints = [];
			$.each(rows, function(i, row) {
				allpoints = allpoints.concat(checkFunc(e, row, false));
			});
			map.setViewport(allpoints);
		}
	}, {
		event : "uncheck-all.bs.table",
		func : function(e, rows) {
			map.clearOverlays();
		}
	}]);
}

// function getClickPointInfo(point) {
// // 根据 参考点类型，数据版本，，获取点附近的TMC数据或LINK数据
// console.log(point.point.lng + ", " + point.point.lat);
// }

