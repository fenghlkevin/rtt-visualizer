var mmc = {
	required : true,
	fieldtype : 'object',
	fieldenum : undefined,
	fieldlength : undefined,
	fieldMaxvalue : undefined,
	children : {
		messageid : {
			required : true,
			fieldtype : 'number',
			fieldenum : undefined,
			fieldlength : 10,
			fieldMaxvalue : undefined
		},
		versionid : {
			required : true,
			fieldtype : 'number',
			fieldenum : undefined,
			fieldlength : 3,
			fieldMaxvalue : 255
		},
		messageexpirytime : {
			required : true,
			fieldtype : 'number',
			fieldenum : undefined,
			fieldlength : 10,
			fieldMaxvalue : undefined
		},
		messagegenerationtime : {
			required : false,
			fieldtype : 'number',
			fieldenum : undefined,
			fieldlength : 10,
			fieldMaxvalue : undefined
		},
		selector_0_cancel : {
			required : false,
			fieldtype : 'number',
			fieldenum : [0, 1],
			fieldlength : undefined,
			fieldMaxvalue : undefined
		}
	}
};
var lrc = {
	required : true,
	fieldtype : 'object',
	fieldenum : undefined,
	fieldlength : undefined,
	fieldMaxvalue : undefined,
	children : {
		locationid : {
			required : true,
			fieldtype : 'number',
			fieldenum : undefined,
			fieldlength : 10,
			fieldMaxvalue : undefined
		},
		countrycode : {
			required : true,
			fieldtype : 'number',
			fieldenum : undefined,
			fieldlength : 3,
			fieldMaxvalue : 255
		},
		locationtablenumber : {
			required : true,
			fieldtype : 'number',
			fieldenum : undefined,
			fieldlength : 2,
			fieldMaxvalue : 99
		},
		extent : {
			required : false,
			fieldtype : 'number',
			fieldenum : undefined,
			fieldlength : 3,
			fieldMaxvalue : 255
		},
		extendedcountrycode : {
			required : false,
			fieldtype : 'number',
			fieldenum : undefined,
			fieldlength : 3,
			fieldMaxvalue : 255
		},
		locationtableversion : {
			required : false,
			fieldtype : 'number',
			fieldenum : undefined,
			fieldlength : undefined,
			fieldMaxvalue : undefined
		},
		selector_0_direction : {
			required : false,
			fieldtype : 'number',
			fieldenum : [0, 1],
			fieldlength : undefined,
			fieldMaxvalue : undefined
		}
	}
};

var tfpdata = {
	required : true,
	fieldtype : 'object',
	fieldenum : undefined,
	fieldlength : undefined,
	fieldMaxvalue : undefined,
	children : {
		starttime : {
			required : true,
			fieldtype : 'number',
			fieldenum : undefined,
			fieldlength : 10,
			fieldMaxvalue : undefined
		},
		duration : {
			required : false,
			fieldtype : 'number',
			fieldenum : undefined,
			fieldlength : undefined,
			fieldMaxvalue : undefined
		},
		spatialresolution : {
			required : true,
			fieldtype : 'number',
			fieldenum : undefined,
			fieldlength : 1,
			fieldMaxvalue : 9
		},
		flowvectors : {
			required : true,
			fieldtype : 'array',
			fieldenum : undefined,
			fieldlength : 1,
			fieldMaxvalue : 9,
			children : {
				timeoffset : {
					required : true,
					fieldtype : 'number',
					fieldenum : undefined,
					fieldlength : undefined,
					fieldMaxvalue : undefined
				},
				flowvectorsections : {
					required : true,
					fieldtype : 'array',
					fieldenum : undefined,
					fieldlength : undefined,
					fieldMaxvalue : undefined,
					children : {
						spatialoffset : {
							required : true,
							fieldtype : 'number',
							fieldenum : undefined,
							fieldlength : undefined,
							fieldMaxvalue : undefined
						},
						status : {
							required : true,
							fieldtype : 'object',
							fieldenum : undefined,
							fieldlength : undefined,
							fieldMaxvalue : undefined,
							children : {
								los : {
									required : false,
									fieldtype : 'number',
									fieldenum : undefined,
									fieldlength : 1,
									fieldMaxvalue : 9
								},
								averagespeed : {
									required : false,
									fieldtype : 'number',
									fieldenum : undefined,
									fieldlength : 1,
									fieldMaxvalue : 255
								},
								delay : {
									required : false,
									fieldtype : 'number',
									fieldenum : undefined,
									fieldlength : undefined,
									fieldMaxvalue : undefined
								}
							}
						},
						spatialresolution : {
							required : false,
							fieldtype : 'number',
							fieldenum : undefined,
							fieldlength : 1,
							fieldMaxvalue : 9
						}
					}
				}
			}
		}
	}
};

var freeTexts = {
	required : false,
	fieldtype : 'array',
	fieldenum : undefined,
	fieldlength : undefined,
	fieldMaxvalue : undefined,
	children : {
		languagecode : {
			required : true,
			fieldtype : 'number',
			fieldenum : undefined,
			fieldlength : undefined,
			fieldMaxvalue : undefined
		},
		freetext : {
			required : true,
			fieldtype : 'string',
			fieldenum : undefined,
			fieldlength : undefined,
			fieldMaxvalue : undefined
		}
	}
}

var vehicleRestrictions = {
	required : false,
	fieldtype : 'array',
	fieldenum : undefined,
	fieldlength : undefined,
	fieldMaxvalue : undefined,
	children : {
		vehicletype : {
			required : true,
			fieldtype : 'number',
			fieldenum : undefined,
			fieldlength : undefined,
			fieldMaxvalue : undefined
		}
	}
}

var tecdata = {
	required : true,
	fieldtype : 'object',
	fieldenum : undefined,
	fieldlength : undefined,
	fieldMaxvalue : undefined,
	children : {
		starttime : {
			required : false,
			fieldtype : 'number',
			fieldenum : undefined,
			fieldlength : 10,
			fieldMaxvalue : undefined
		},
		stoptime : {
			required : false,
			fieldtype : 'number',
			fieldenum : undefined,
			fieldlength : 10,
			fieldMaxvalue : undefined
		},
		lengthaffected : {
			required : false,
			fieldtype : 'number',
			fieldenum : undefined,
			fieldlength : 10,
			fieldMaxvalue : undefined
		},
		averagespeedabsolute : {
			required : false,
			fieldtype : 'number',
			fieldenum : undefined,
			fieldlength : 3,
			fieldMaxvalue : 255
		},
		effectcode : {
			required : true,
			fieldtype : 'number',
			fieldenum : undefined,
			fieldlength : 3,
			fieldMaxvalue : 255
		},
		causes : {
			required : true,
			fieldtype : 'array',
			fieldenum : undefined,
			fieldlength : undefined,
			fieldMaxvalue : undefined,
			children : {
				maincause : {
					required : true,
					fieldtype : 'number',
					fieldenum : undefined,
					fieldlength : 3,
					fieldMaxvalue : 255
				},
				warninglevel : {
					required : true,
					fieldtype : 'number',
					fieldenum : undefined,
					fieldlength : 3,
					fieldMaxvalue : 255
				},
				subcause : {
					required : true,
					fieldtype : 'number',
					fieldenum : undefined,
					fieldlength : 3,
					fieldMaxvalue : 255
				},
				lengthaffected : {
					required : false,
					fieldtype : 'number',
					fieldenum : undefined,
					fieldlength : undefined,
					fieldMaxvalue : undefined
				},
				lanerestrictiontype : {
					required : true,
					fieldtype : 'number',
					fieldenum : undefined,
					fieldlength : 3,
					fieldMaxvalue : 255
				},
				numberoflanes : {
					required : true,
					fieldtype : 'number',
					fieldenum : undefined,
					fieldlength : 3,
					fieldMaxvalue : 255
				},
				freetexts : freeTexts,
			}
		},
		advices : {
			required : true,
			fieldtype : 'array',
			fieldenum : undefined,
			fieldlength : undefined,
			fieldMaxvalue : undefined,
			children : {
				advicecode : {
					required : true,
					fieldtype : 'number',
					fieldenum : undefined,
					fieldlength : 3,
					fieldMaxvalue : 255
				},
				subadvicecode : {
					required : true,
					fieldtype : 'number',
					fieldenum : undefined,
					fieldlength : 3,
					fieldMaxvalue : 255
				},
				freetexts : freeTexts,
				vehiclerestrictions : vehicleRestrictions
			}
		},
		vehiclerestrictions : vehicleRestrictions
	}
}

var validate_template_tfp = {
	type : {
		required : true,
		fieldtype : 'string',
		fieldenum : ['tfp'],
		fieldlength : undefined,
		fieldMaxvalue : undefined
	},
	value : {
		required : true,
		fieldtype : 'object',
		fieldenum : undefined,
		fieldlength : undefined,
		fieldMaxvalue : undefined,
		children : {
			mmc : mmc,
			lrc : lrc,
			tfpdata : tfpdata
		}
	},
};

var validate_template_tec = {
	type : {
		required : true,
		fieldtype : 'string',
		fieldenum : ['tec'],
		fieldlength : undefined,
		fieldMaxvalue : undefined
	},
	value : {
		required : true,
		fieldtype : 'object',
		fieldenum : undefined,
		fieldlength : undefined,
		fieldMaxvalue : undefined,
		children : {
			mmc : mmc,
			lrc : lrc,
			tecdata : tecdata
		}
	},
};

var filestrarray = {};
function do_jsonfiles_init(e) {
	filestrarray = {};
	e = e || window.event;
	var files = e.target.files; // FileList Objects
	$.each(files, function(i, f) {
		var reader = new FileReader();
		reader.onload = (function(file) {
			return function(e) {
				filestrarray[file.name] = this.result;
			};
		})(f);
		reader.onerror = (function(file) {
			return function(e) {
				console.log(e);
			};
		})(f);
		reader.readAsText(f);// readAsText
	});
};

function do_create_tpegfile() {
	$('#dotime').show();
	$('#welldiv').show();
	var rdo = $('#pecreal');
	var rshow = $('#wellreal');
	if (filestrarray.length <= 0) {
		do_change_error(rdo, rshow, '没有合适的数据内容');
		return false;
	}
	var res = [];
	do_change_pec(rdo, rshow, 40, '检查数据正确性');
	var vali = true;
	$.each(filestrarray, function(i, str) {
		var item = JSON.parse(str);
		$.each(item, function(i, one) {
			res.push(one);
		});

		var revalue = validate_msg(i, item);
		if (!revalue.result) {
			do_change_error(rdo, rshow, revalue.error);
			vali = false;
			return false;
		}
	});
	if (vali) {
		var jsonvalue = JSON.stringify(res);
		setTimeout(function() {
			do_change_pec(rdo, rshow, 60, '编写二进制文件');
			var url = "../../createfile/json?callback=?";
			$.post(url, {
				data : jsonvalue
			}, function(result) {
				do_change_pec(rdo, rshow, 80, '生成二进制文件');
				setTimeout(function() {
					do_change_success(rdo, rshow, '下载TPEG文件');
					window.location.href = result;
				}, 1000);
			}, "json");

			// window.location.href = result;

		}, 2000);
	}

}
function do_change_error(obj, objshow, msg) {
	do_change_pec(obj, objshow, 100, msg);
	obj.attr("class", 'progress-bar progress-bar-danger progress-bar-striped');
}
function do_change_success(obj, objshow, msg) {
	do_change_pec(obj, objshow, 100, msg);
	obj.attr("class", 'progress-bar progress-bar-success progress-bar-striped');
}
function do_change_pec(obj, objshow, val, msg) {
	obj.attr("class", 'progress-bar  progress-bar-striped active');
	var tmsg = val + "%";
	obj.attr("style", "width: " + tmsg);
	obj.html(tmsg);
	var smsg = '';
	if (msg) {
		smsg = msg;
	}
	objshow.html(smsg);
}

function validate_msg(filename, source) {
	var revalue = {
		result : true,
		error : ''
	};
	if (!(source instanceof Array)) {
		revalue.result = false;
		revalue.error = filename + "is not validate. Json value must be array";
		return revalue;
	}
	for (var i = 0; i < source.length; i++) {
		var item = source[i];
		revalue = validate_item(item, validate_template_tfp.required, "message", validate_template_tfp.fieldtype, validate_template_tfp.fieldenum,
				validate_template_tfp.fieldlength, validate_template_tfp.fieldMaxvalue);
		if (!revalue.result) {
			revalue = validate_item(item, validate_template_tec.required, "message", validate_template_tec.fieldtype, validate_template_tec.fieldenum,
					validate_template_tec.fieldlength, validate_template_tec.fieldMaxvalue);
			if (!revalue.result) {
				revalue.error = filename + " : " + revalue.error;
				break;
			}
		}

		if (item.type === 'tfp') {
			revalue = validate_auto(item, "tfpmessage[" + i + "]", false, validate_template_tfp);
		} else {
			revalue = validate_auto(item, "tecmessage[" + i + "]", false, validate_template_tec);
		}
		if (!revalue.result) {
			break;
		}
	}
	return revalue;
}

function validate_auto(item, itemkey, isarray, template) {
	var revalue = {
		result : true,
		error : ''
	};
	for (key in item) {
		var tempvalue = item[key];
		var template_item = template[key];
		if (isarray) {
			template_item = template;
		}
		if (template_item === undefined) {
			revalue.result = false;
			revalue.error = itemkey + "-" + key + " is not setup, please check you json file.";
			break;
		}
		revalue = validate_item(item, template_item.required, key, template_item.fieldtype, template_item.fieldenum, template_item.fieldlength, template_item.fieldMaxvalue);
		if (!revalue.result) {
			break;
		}
		if (template_item.fieldtype === 'array') {
			for (var i = 0; i < tempvalue.length; i++) {
				var tv = tempvalue[i];
				revalue = validate_auto(tv, key, false, template_item.children);
			}

		} else if (template_item.fieldtype === 'object') {
			revalue = validate_auto(tempvalue, key, false, template_item.children);
		}
		if (!revalue.result) {
			break;
		}
	}
	return revalue;

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

/**
 * 
 * @param parentSource
 * @param required
 * @param fieldname
 * @param fieldtype:
 *            string,number,array,object[object 认为是{}]
 * @param fieldlength
 * @param fieldMaxvalue
 * @returns
 */
function validate_item(parentSource, required, fieldname, fieldtype, fieldenum, fieldlength, fieldMaxvalue) {
	var fieldvalue = parentSource[fieldname];
	var revalue = {
		result : true,
		error : ''
	};

	if (fieldvalue === undefined) {
		var temp = required ? false : true;
		revalue.result = temp;
		if (!temp) {
			revalue.error = fieldname + " is required, now value is null ";
			return revalue;
		}
	}

	if (fieldtype) {
		var st = typeof fieldvalue;
		if (st === 'object') {
			if (fieldvalue instanceof Array) {
				st = 'array';
			}
		}
		if (fieldtype != st) {
			revalue.result = false;
			revalue.error = fieldname + "'s type not validate. The target fieldtype is [" + fieldtype + "]";
			return revalue;
		}
	}

	if (fieldenum) {
		var tempv = false;
		var tval = "";
		for (var i = 0; i < fieldenum.length; i++) {
			var e = fieldenum[i];
			if (fieldvalue === e) {
				tempv = true;
				tval = tval + "," + e;
			}
		}
		if (!tempv) {
			revalue.result = false;
			revalue.error = fieldname + " value not validate, The target value in [" + tval + "]";
			return revalue;
		}
	}

	if (fieldlength) {
		if (fieldvalue.length > fieldlength) {
			revalue.result = false;
			revalue.error = fieldname + " value length more than fieldlength[" + fieldlength + "]";
			return revalue;
		}
	}
	if (fieldMaxvalue) {
		if (fieldvalue > fieldMaxvalue) {
			revalue.result = false;
			revalue.error = fieldname + "'s value  out of maxvalue[" + fieldMaxvalue + "]";
			return revalue;
		}
	}
	return revalue;
}
