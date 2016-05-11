var mib2_map;
var daimler_map;
var renault_map;
var bmw_map;
function function_to_step2_simulate_mib2() {
	map = showMap("mib2_map",targetZ);
	mib2_map = map;
	initMap(map);
}

function function_to_step2_simulate_daimler() {
	map = showMap("daimler_map",targetZ);
	daimler_map = map;
	initMap(map);
}

function function_to_step2_simulate_renault() {
	map = showMap("renault_map",targetZ);
	renault_map = map;
	initMap(map);
}

function function_to_step2_simulate_bmw() {
	map = showMap("bmw_map",targetZ);
	bmw_map = map;
	initMap(map);
}

function initMap(omap) {
	omap.addEventListener("rightclick", function(e) {
		rightclickPoint = {
			lng : e.point.lng,
			lat : e.point.lat
		};
	});

	var menu = new BMap.ContextMenu();
	var txtMenuItem = [{
		text : 'Select Point',
		callback : function() {
			var lon = rightclickPoint['lng'];
			var lat = rightclickPoint['lat'];
			$('#lon').val(lon);
			$('#lat').val(lat);

			omap.clearOverlays();
			var pt = new BMap.Point(lon, lat);
			var marker = new BMap.Marker(pt, {}); // 创建标注
			omap.addOverlay(marker);
		}
	}];
	for (var i = 0; i < txtMenuItem.length; i++) {
		menu.addItem(new BMap.MenuItem(txtMenuItem[i].text, txtMenuItem[i].callback, 100));
	}
	omap.addContextMenu(menu);

}

function initSession() {
	var lpinfoVersion = $("#lpinfo_version").find("option:selected").val();
	var mapVersion = $("#map_version").find("option:selected").val();
	var tmcPlr = $("#tmcPlr").find("option:selected").val();
	var tmcFreeFlow = $("#tmcFreeFlow").find("option:selected").val();
	var jamfrontWarning = $("#jamfrontWarning").find("option:selected").val();
	var openlr = $("#openlr").find("option:selected").val();
	var innerRadius = $("#innerRadius").val();
	var outerRadius = $("#outerRadius").val();
	var serviceUrl = $("#serviceUrl").val();
	$('#next').attr("disabled", true);

	var url = "../../simulate/initsession?" + "&callback=?";
	$.post(url, {
		lpinfoVersion : lpinfoVersion,
		mapVersion : mapVersion,
		tmcPlr : tmcPlr,
		tmcFreeFlow : tmcFreeFlow,
		jamfrontWarning : jamfrontWarning,
		openlr : openlr,
		innerRadius : innerRadius,
		outerRadius : outerRadius,
		serviceUrl : serviceUrl
	}, function(result) {
		if (result == null) {
			alert("INIT SESSION FAILED!");
		} else {
			result = eval('(' + result + ')');
			$('#sessionId').val(result['sessionID']);
			$('#getMessageUrl').val(result['getMessagesURL']);
			$('#next').attr("disabled", false);
		}
	}, "json");
}

function initSession_mib2() {
	var citys = [32, 1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16, 18, 20, 23, 24, 25, 26, 27, 28, 29, 30, 31];
	var lpinfoVersion = $("#lpinfo_version").find("option:selected").val();
	// var mapVersion = $("#map_version").find("option:selected").val();

	var version = lpinfoVersion.substring(0, 2)+".0";
	var minRadius = $("#minRadius").val();
	var midRadius = $("#midRadius").val();
	var maxRadius = $("#maxRadius").val();
	var serviceUrl = $("#serviceUrl").val();

	var postXml = "<trafficSessionRequest xmlns='http://www.tisa.org/THTTP' version=\"01.00\">"
			+ "<tpeg><format>TPEGBinary</format><messagesize unit=\"bytes\">50000</messagesize>"
			+ "<maxExtent>31</maxExtent><navVersion>123</navVersion><locRef><id>TMC-Loc</id><version>" + version  + "</version><preciseTMCInfo>true</preciseTMCInfo></locRef>"
			+ "<locRef><id>ETL-Loc</id><version>1.0</version><preciseTMCInfo>true</preciseTMCInfo></locRef>"
			+ "<app><id>TEC</id><version>3.0</version><sessionsize unit=\"messages\">500</sessionsize></app>"
			+ "<app><id>TFP</id><version>1.0</version><sessionsize unit=\"messages\">3000</sessionsize><tfpSpatialResolution unit=\"tfp004\">4</tfpSpatialResolution></app>"
			+ "<configuration><sessionExpiration unit=\"s\">3600</sessionExpiration><radius1 unit=\"m\">" + maxRadius + "</radius1><radius2 unit=\"m\">" + midRadius
			+ "</radius2><radius3 unit=\"m\">" + minRadius + "</radius3>"
			+ "<timeout unit=\"s\">1800</timeout><frequency unit=\"s\">300</frequency><frequencyShort unit=\"s\">180</frequencyShort>"
			+ "<fcd>true</fcd><encryption>false</encryption></configuration>" + "<tmcTables>";
	for ( var p in citys) {
		postXml += "<tmcTable><id>CN_" + citys[p] + "_" + version + "</id><cc>CN</cc><ltn>" + citys[p] + "</ltn><version>" + version + "</version></tmcTable>";
	}

	postXml += "</tmcTables></tpeg><routing><currentCountry unit=\"iso3166-2\">CN</currentCountry><destCountry unit=\"iso3166-2\">CN</destCountry></routing></trafficSessionRequest>";

	var url = "../../simulate/initsession_mib2?" + "&callback=?";
	$.post(url, {
		serviceUrl : serviceUrl,
		postXml : postXml
	}, function(result) {
		if (result == null) {
			alert("INIT SESSION FAILED!");
		} else {
			result = eval('(' + result + ')');
			$('#sessionId').val(result['sessionId']);
			$('#getMessageUrl').val(result['url']);
			$('#next').attr("disabled", false);
		}
	}, "json");
}

function initSession_renault() {
	var citys = [32, 1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16, 18, 20, 23, 24, 25, 26, 27, 28, 29, 30, 31];

	var deviceID = $("#deviceID").val();
	var lpinfoVersion = $("#lpinfo_version").find("option:selected").val();
	var tmcPlr = $("#tmcPlr").find("option:selected").val();
	var tmcFreeFlow = $("#tmcFreeFlow").find("option:selected").val();
	var openlr = $("#openlr").find("option:selected").val();
	var innerRadius = $("#innerRadius").val();
	var outerRadius = $("#outerRadius").val();
	var maxmessagestmc = $("#maxmessagestmc").val();
	var maxmessagesolr = $("#maxmessagesolr").val();
	var serviceUrl = $("#serviceUrl").val();
	$('#next').attr("disabled", true);

	var postreq = {
			deviceID : deviceID,
			innerRadius : innerRadius,
			outerRadius : outerRadius,
			maxMessagesTMC : maxmessagestmc,
			maxMessagesOLR : maxmessagesolr,
			freeFlow:tmcFreeFlow,
			tmcPlr:tmcPlr,
			openLR:openlr,
			locationTables:[]
		};
	var version = lpinfoVersion.substring(0, 2);
	for ( var p in citys) {
		postreq .locationTables.push( "C_" + citys[p] + "_" + version + ".0");
	}
	
	
	var postjson=JSON.stringify(postreq);

	var url = "../../simulate/initsession_renault?" + "&callback=?";
	$.post(url, {
		serviceUrl : serviceUrl,
		postXml : postjson
	}, function(result) {
		if (result == null) {
			alert("INIT SESSION FAILED!");
		} else {
			result = eval('(' + result + ')');
			$('#sessionId').val(result['sessionId']);
			$('#getMessageUrl').val(result['url']);
			$('#next').attr("disabled", false);
		}
	}, "json");
}

function getMessageBmw() {
	var lon = parseInt(Number($("#lon").val())* 4294967296 / 360);
	var lat = parseInt(Number($("#lat").val())* 4294967296 / 360);
	var vers=$("#vers").val();
	var driveid=$("#driveid").val();
	var tp_apps = $("#tp_apps").find("option:selected").val();
	var serviceUrl = $('#serviceUrl').val();
	var type = $('#serviceType').val();
	var bmwurl=serviceUrl+"?DriveID="+driveid+"&Clat="+lat+"&Clon="+lon+"&TP_Apps="+tp_apps+"&Vers="+vers+"&Bearing=&TReq=&Velocity=0&Guidance=ND&OP=gtm&Dlat=0&Dlon=0&Decoding_Feat="
	
	var url = "../../simulate/getMessage?" + "callback=?";
	var table = $('#getmessage_btable');
	if (table) {
		table.bootstrapTable("destroy");
	}
	$.post(url, {
		lon : lon,
		lat : lat,
		type : type,
		sessionId : "aaa",
		getMessageUrl : bmwurl
	}, function(result) {
		if (result === '') {
			alert("GET MESSAGE FAILED!");
		} else {
			$("#base64Result").val(result);
			// var map = showMap("daimler_map");
			// initMap(map);
			decodeTpeg(daimler_map);
		}
	}, "json");
}

function getMessageRenault() {
	var lon = $("#lon").val();
	var lat = $("#lat").val();
	var sessionId = $('#sessionId').val();
	var getMessageUrl = $('#getMessageUrl').val();
	var type = $('#serviceType').val();
	var url = "../../simulate/getMessage?" + "callback=?";
	var table = $('#getmessage_btable');
	if (table) {
		table.bootstrapTable("destroy");
	}
	$.post(url, {
		lon : lon,
		lat : lat,
		type : type,
		sessionId : sessionId,
		getMessageUrl : getMessageUrl+"?sessionID="+sessionId+"&tid=0"
	}, function(result) {
		if (result === '') {
			alert("GET MESSAGE FAILED!");
		} else {
			$("#base64Result").val(result);
			// var map = showMap("daimler_map");
			// initMap(map);
			decodeTpeg(daimler_map);
		}
	}, "json");
}

function getMessageDailmer() {
	var lon = $("#lon").val();
	var lat = $("#lat").val();
	var sessionId = $('#sessionId').val();
	var getMessageUrl = $('#getMessageUrl').val();
	var type = $('#serviceType').val();
	var url = "../../simulate/getMessage?" + "callback=?";
	var table = $('#getmessage_btable');
	if (table) {
		table.bootstrapTable("destroy");
	}
	$.post(url, {
		lon : lon,
		lat : lat,
		type : type,
		sessionId : sessionId,
		getMessageUrl : getMessageUrl
	}, function(result) {
		if (result === '') {
			alert("GET MESSAGE FAILED!");
		} else {
			$("#base64Result").val(result);
			// var map = showMap("daimler_map");
			// initMap(map);
			decodeTpeg(daimler_map);
		}
	}, "json");
}

var tid = 0;
function getMessageMib2() {
	tid += 1;
	var lon = $("#lon").val();
	var lat = $("#lat").val();
	var sessionId = $('#sessionId').val();
	var getMessageUrl = $('#getMessageUrl').val() + "?tid=" + tid;
	var type = $('#serviceType').val();
	var url = "../../simulate/getMessage?" + "callback=?";
	var table = $('#getmessage_btable');
	if (table) {
		table.bootstrapTable("destroy");
	}
	$.post(url, {
		lon : lon,
		lat : lat,
		type : type,
		sessionId : sessionId,
		getMessageUrl : getMessageUrl
	}, function(result) {

		if (result === '') {
			alert("GET MESSAGE FAILED!");
		} else {
			// alert(result);
			$("#base64Result").val(result);
			// var map=showMap("mib2_map");
			// initMap(map);
			// console.log(map);
			decodeTpeg(mib2_map);
		}
	}, "json");
}

function decodeTpeg(map) {
	var lpinfo_version = $("#lpinfo_version").find("option:selected").val();
	var tmc_map_version = $("#tmc_map_version").find("option:selected").val();
	var olr_map_version = $("#olr_map_version").find("option:selected").val();
	var debug = $("#parse_method").find("option:selected").val();

	var data = $("#base64Result").val();
	url = "../../parser/tpeg?callback=?";
	$.post(url, {
		lpinfo_version : lpinfo_version,
		tmc_map_version : tmc_map_version,
		olr_map_version : olr_map_version,
		debug : debug,
		data : data
	}, function(result) {
		if (result == null) {
			alert("TPEG DECODE FAILED!");
		} else {
			// show_intable(result);
			show_intable(result, map, "getmessage_btable");
		}
	}, "json");
}

// function show_intable(result) {
// var data = new Array();
// for (var i = 0; i < result.length; i++) {
// var msg = result[i];
// var overlay = create_overlay(msg.showResults);
// var tree = create_tree(msg.message, "message");
// var status = 'OK';
// if (msg.error) {
// status = msg.error.level;
// }
// data[i] = {
// messageid : msg.message.MMC.messageID,
// versionid : msg.message.MMC.versionID,
// message : msg.message,
// datatype : msg.type,
// shape : msg.showResults,
// error : msg.error,
// overlay : overlay,
// treeview : tree,
// status : status
// };
// }
// var table = $('#message_btable');
// if (table) {
// table.bootstrapTable("destroy");
// }
//
// table.bootstrapTable({
// data : data
// });
// $("#message_btable").show();
// loadtableevent("#message_btable");
// };

// function create_overlay(shows) {
// //var shows = row.shape;
// var overlay = new Array();
// for (var i = 0; i < shows.length; i++) {
// var oneshow = shows[i];
// var mshape = oneshow.shape;
// var bmapline = new Array();
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
// overlay[i] = polyline;
// }
//
// return overlay;
// }

// function create_tree(source, text) {
// var revalue = {
// text : text,
// children : new Array()
// };
// for (key in source) {
// var jk = key + ":" + source[key];
// var objk = key + ":";
//
// var temp = source[key];
// if (temp instanceof Array) {
// revalue.children.push(create_tree(source[key], objk + "array"));
// } else if (typeof (temp) === "object") {
// revalue.children.push(create_tree(source[key], objk + "object"));
// } else {
// var child = {
// text : jk
// };
// revalue.children.push(child);
// }
// }
// return revalue;
// };

// function loadtableevent(tableid) {
// $(tableid).on('all.bs.table', function(e, name, args) {
// // console.log('Event:', name, ', data:', args);
// }).on(
// 'click-row.bs.table',
// function(e, row, $element) {
// // $result.text('Event: click-row.bs.table');
// var datastatus = 'Data Parse Status : ';
// var color="black";
// if (row.error) {
// datastatus = 'Data Parse Status : ' + row.error.level + ',<br/> Message : ' +
// row.error.message+'<br/>';
// color="red";
// } else {
// datastatus = 'Data Parse Status : OK';
// }
// $element.popover({
// placement : "left",// bottom
// html : "true",
// trigger : "click",
// content : function() {
// var rediv = $('<div class="container"
// style="overflow-y:scroll;overflow-x:scroll;width:250px; height:400px;"><span
// style="font-size:1px;color:"+color+";">' + datastatus
// + '</span><hr><div id="tree"></div></div>');
// var div = $(rediv).find("div[id='tree']");
// div.jstree({
// 'core' : {
// 'data' : [row.treeview]
// }
// });
// div.on('changed.jstree', function(e, data) {
// $.each(row.overlay, function(i, val) {
// omap.removeOverlay(val);
// });
// for (var i = 0; i < data.selected.length; i++) {
// var selectedNode = data.instance.get_node(data.selected[i]);
// var parent = data.instance.get_node(selectedNode.parent);
//
// if (parent.text === "sections:array") {
// var temp = selectedNode.text;
// var i = temp.split(":")[0];
// var val = row.overlay[i];
// omap.addOverlay(val);
// }
// }
// })
// return rediv;
// }
// });
// }).on('dbl-click-row.bs.table', function(e, row, $element) {
// // $result.text('Event: dbl-click-row.bs.table');
// }).on('sort.bs.table', function(e, name, order) {
// // $result.text('Event: sort.bs.table');
// }).on('check.bs.table', function(e, row) {
// if (row.overlay) {
// $.each(row.overlay, function(i, val) {
// omap.addOverlay(val);
// });
// omap.panTo(new
// BMap.Point(row.shape[0]['shape'][0]['x'],row.shape[0]['shape'][0]['y']));
// }
// }).on('uncheck.bs.table', function(e, row) {
// if (row.overlay) {
// $.each(row.overlay, function(i, val) {
// omap.removeOverlay(val);
// });
// }
// }).on('check-all.bs.table', function(e) {
// // $result.text('Event: check-all.bs.table');
// }).on('uncheck-all.bs.table', function(e, rows) {
// omap.clearOverlays();
// }).on('load-success.bs.table', function(e, data) {
// // $result.text('Event: load-success.bs.table');
// }).on('load-error.bs.table', function(e, status) {
// // $result.text('Event: load-error.bs.table');
// }).on('column-switch.bs.table', function(e, field, checked) {
// // $result.text('Event: column-switch.bs.table');
// }).on('page-change.bs.table', function(e, size, number) {
// // $result.text('Event: page-change.bs.table');
// }).on('search.bs.table', function(e, text) {
// // $result.text('Event: search.bs.table');
// });
//
// };

