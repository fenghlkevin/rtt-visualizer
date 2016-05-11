var targetZ={lng:116.54356,lat:39.869255,zoom:12};

function vali(){
	var url=window.location.href;
	var temp=url.split("#");
	function goerror(){
		  window.location.href="error.html"; 
	}
	if(temp.length<2){
		goerror();
	}else{
		if(temp.length==3){
			var t=temp[2].split("||");
			targetZ.lng=t[0];
			targetZ.lat=t[1];
		}
		
		if(temp[1]!==''){
			var templist=["daimler","mib2","renault","bmw"];
			var t=temp[1];
			var isvali=false;
			$.each(templist,function(i,id){
				var mli=$("#"+id);
				mli.hide();
				if(mli.attr("id")==='mib2'&&t==="abfddaed12566c945760c5e36bdd206d"){
					mli.show();
					isvali=true;
					// MIB2
				}else if(mli.attr("id")==='daimler'&&t==="9a4ea1db4d541164d18b62ffba3f1891"){
					// DAIMLER
					mli.show();
					isvali=true;
				}else if(mli.attr("id")==='renault'&&t==="a3c6ac924dd999674b48c8c10ce05391"){
					// DAIMLER
					mli.show();
					isvali=true;
				}else if(mli.attr("id")==='bmw'&&t==="bcb48dddff8c14b5f452ee573b4db770"){
					// DAIMLER
					mli.show();
					isvali=true;
				}else if(t==="test"){
					mli.show();
					isvali=true;
				}
			});
			if(!isvali){
				goerror();
			}
			var as=$("a");
			$.each(as,function(i,a){
				a.href="#"+t;
			})
		}
	}
	
}
