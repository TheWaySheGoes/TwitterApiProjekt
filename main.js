$( document ).ready(function() {
	$.getJSON( "http://data.riksdagen.se/personlista/?iid=0695090885212&fnamn=&enamn=&f_ar=&kn=&parti=&valkrets=&rdlstatus=&org=&utformat=json&termlista=", function( data ) {
		console.log(data);
		
		var personData = data;
		
		$("body").append(data["personlista"]["person"]["tilltalsnamn"] + " " + data["personlista"]["person"]["efternamn"]);
		$("body").append("<img src='" + data["personlista"]["person"]["bild_url_80"] + "'>");
		/*var items = [];
		$.each( data, function( key, val ) {
		items.push( "<li id='" + key + "'>" + val + "</li>" );*/
	});
});