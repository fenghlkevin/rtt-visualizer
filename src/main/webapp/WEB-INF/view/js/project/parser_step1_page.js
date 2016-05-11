function function_2_step2_decode_tpeg(){
	function_to_step2_decode("tpeg");
}

function function_2_step2_decode_rtic(){
	function_to_step2_decode("rtic");
}

function function_to_step2_decode(datatype) {
		var lpinfo_version = $("#lpinfo_version").find("option:selected").val();
		var tmc_map_version = $("#tmc_map_version").find("option:selected").val();
		var olr_map_version = $("#olr_map_version").find("option:selected").val();
		var debug = $("#parse_method").find("option:selected").val();

		var data = $("#file_value").val();
		var url="";
		if(!data||data===""){
			return;
		}
		var d;
		if(datatype==="tpeg"){
			data = data.replace("data:;base64,", "");
			url = "../../parser/tpeg?callback=?";
		}
		else if(datatype==="rtic"){
			d="rtic";
			data = data.replace("data:;base64,", "");
			url = "../../parser/rtic?callback=?";
		}else{
			
		}
		
		$.post(url, {
			lpinfo_version : lpinfo_version,
			tmc_map_version : tmc_map_version,
			olr_map_version : olr_map_version,
			debug:debug,
			data : data,
			filetype:filetype
		}, function(result) {
			var map = showMap("decode_map");
			show_intable(result, map,"parsedata_btable",data,d);
		}, "json");
}

filetype="";
function fileSelect(e) {
	filetype="";
	e = e || window.event;
	var files = e.target.files; // FileList Objects
	for (var i = 0, f; f = files[i]; i++) {
		var reader = new FileReader();
		reader.onload = (function(file) {
			var t=file.name.split(".");
			filetype=t[t.length-1];
			return function(e) {
				$("#file_value").val(this.result);
			};
		})(f);
		reader.readAsDataURL(f);
	}
};

var filetype="";
function fileselect_txt(e) {
	filetype="";
	e = e || window.event;
	var files = e.target.files; // FileList Objects
	for (var i = 0, f; f = files[i]; i++) {
		var reader = new FileReader();
		reader.onload = (function(file) {
			console.log(file);
			return function(e) {
				$("#file_value").val(this.result);
			};
		})(f);
		reader.readAsText(f);
	}
};

function event_click_datatype(){
	var datatype= getCheckedValue("input[name='datatype[]']");
	console.log(datatype);
	if(datatype==='rtic'){
		$("#olrmapversion_div").hide();
		$("#debugshow_div").hide();
	}else{
		$("#olrmapversion_div").show();
		$("#debugshow_div").show();
	}
}
// function loadtableevent(tableid,omap) {
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
// console.log(datastatus);
// $element.popover({
// placement : "left",// bottom
// html : "true",
// trigger : "click",
// content : function() {
// var rediv = $('<div class="container"
// style="overflow-y:scroll;overflow-x:scroll;width:250px; height:400px;"><span
// style="font-size:1px;color:"+color+";">' + datastatus
// + '</span><hr><div id="tree"></div></div>');
// // var div = $('<div
// // style="overflow-y:scroll;overflow-x:scroll;width:250px;
// // height:380px;"></div>');
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
// console.log(rows);
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
