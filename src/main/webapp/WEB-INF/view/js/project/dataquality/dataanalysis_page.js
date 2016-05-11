function event_dataanalysis_radio_chooselink(rediv) {
	return function() {
		var value = $(this).val();
		if (value === "yes") {
			rediv.find("#yesdiv").show();
			rediv.find("#nodiv").hide();
		} else {
			rediv.find("#yesdiv").hide();
			rediv.find("#nodiv").show();
		}
	}
}

function do_dataanalysis_pageload(thispage) {

	var checkedlist = {};
	thispage.find("#yesdiv").hide();
	thispage.find("#nodiv").hide();
	var map = showMap("dataanalysis_map");
	do_dataanalysis_add_mapmenu(map, checkedlist);

	$.each(thispage.find("input[name='know[]']"), function(i, inp) {
		$(inp).change(event_dataanalysis_radio_chooselink(thispage));
	});
}

function do_dataanalysis_add_mapmenu(map, checkedlist) {
	var drawingManager = new BMapLib.DrawingManager(map);
	// drawingManager.setDrawingMode(BMAP_DRAWING_CIRCLE);
	drawingManager.setDrawingMode(BMAP_DRAWING_RECTANGLE);

	var menu = new BMap.ContextMenu();

	var points = [];

	var txtMenuItem = [{
		text : 'CIRCLE SEARCH LINKS',
		callback : function() {
			do_map_clearOverlays(map, checkedlist);
			drawingManager.setDrawingMode(BMAP_DRAWING_RECTANGLE);
			drawingManager.open();
			$("#linkids").val("");
		}
	}];
	for (var i = 0; i < txtMenuItem.length; i++) {
		var item = new BMap.MenuItem(txtMenuItem[i].text, txtMenuItem[i].callback, {
			width : 200
		});
		menu.addItem(item);
		menu.addSeparator();
	}
	map.addContextMenu(menu);

	drawingManager.addEventListener('rectanglecomplete', function(circle) {
		drawingManager.close();
		do_dataanalysis_query_rectanglelinks(circle.getBounds(), map, checkedlist);
		// $('[data-toggle="popover"]').popover();
	});
}

function do_dataanalysis_query_rectanglelinks(bounds, map, checkedlist) {
	var formvalue = tpeg_do_get_formvalue();
//	var url = "/rtt-visualizer/common/querylink/rectangle?callback=?";
	var url = "/rtt-visualizer/common/querylpinfo/rectangle?callback=?";
	$.post(url, {
		min_lng : bounds.getSouthWest().lng,
		min_lat : bounds.getSouthWest().lat,
		max_lng : bounds.getNorthEast().lng,
		max_lat : bounds.getNorthEast().lat,
		mapversion : formvalue.map_version,
	}, function(result) {
		console.log(result);
		do_map_clearOverlays(map, checkedlist)
		var overlays = do_createlink_overlay(result, checkedlist);
		$.each(overlays, function(i, overlay) {
			if (!checkedlist[overlay.link.linkid]) {
				map.addOverlay(overlay);
			}
		});
	}, "json");
}

function do_map_clearOverlays(map, checkedlist) {
	var overlays = map.getOverlays();
	$.each(overlays, function(i, overlay) {
		if(overlay.link){
			if (!checkedlist[overlay.link.linkid]) {
				map.removeOverlay(overlay);
			}
		}else{
			map.removeOverlay(overlay);
		}
		
	});
}

function do_createlink_overlay(links, checkedList) {
	var overlay = new Array();
	function linefunc(link) {
		var bmapline = new Array();
		$.each(link.shape, function(x, point) {
			if (point && point.x && point.y) {
				bmapline.push(new BMap.Point(point.x, point.y));
			} else {
				console.log("error point");
			}
		});
		var color = util_rtt_loscolor(100);

		// draw
		var polyline = new BMap.Polyline(bmapline, {
			strokeColor : color,
			strokeWeight : 3,
			strokeOpacity : 1
		});
		polyline.link = link;

		polyline.addEventListener("mouseover", function(type, target, point, pixel) {
			this.setStrokeColor("red");
		});

		polyline.addEventListener("mouseout", function(type, target, point, pixel) {
			if (checkedList[this.link.linkid]) {
				this.setStrokeColor("green");
			} else {
				this.setStrokeColor("blue");
			}
		});

		polyline.addEventListener("click", function(type, target, point, pixel) {
			if (checkedList[this.link.linkid]) {
				delete checkedList[this.link.linkid];
				this.setStrokeColor("red");
			} else {
				checkedList[this.link.linkid] = this.link;
				this.setStrokeColor("green");
			}

			var linkids = "";
			$.each(checkedList, function(i, _link) {
				linkids += _link.linkid + ",";
			});
			$("#linkids").val(linkids);
		});
		overlay.push(polyline);
	};

	$.each(links, function(i, link) {
		linefunc(link);
	});
	return overlay;
}

function tpeg_do_get_formvalue() {
	var map_version = $("#olr_map_version").find("option:selected").val();
	var timestamp = $("#timelimit_start").val();
	var lockeys = $("#linkids").val();
	return {
		map_version : map_version,
		timestamp : timestamp,
		linkids : linkids
	}
}

function function_to_step3(){
	
	var formvalue = tpeg_do_get_formvalue();
	
	var url = "/rtt-visualizer/tpeg/query/compare?callback=?";
	$.post(url, {
		lockeys : formvalue.lockeys,
		timestamp : formvalue.timestamp,
		mapversion : formvalue.map_version,
	}, function(result) {
		
		
		
		var table = $('#' + btable);

		if (table) {
			table.bootstrapTable("destroy");
		}
		table.bootstrapTable({
			data : data
		});

		table.show();
		
		
		console.log(result);
	}, "json");
	
}