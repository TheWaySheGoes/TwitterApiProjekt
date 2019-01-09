$( document ).ready(function() {
	
	var url = window.location.href;
	var member = url.substring(url.indexOf("ledamot=") + 8);
	
	var lastName = url.substring(url.indexOf("+") + 1);
	var firstName = member.replace("+"+lastName,"");
	
	var riksdagenUrl = "http://data.riksdagen.se/personlista/?fnamn=" + firstName + "&enamn=" + lastName + "&utformat=json";
	
	
	
	$.getJSON( riksdagenUrl, function( data ) {
		console.log(data);
		
		var personData = data["personlista"]["person"];
		
		console.log(personData);
		
		//$("body").append(data["personlista"]["person"]["tilltalsnamn"] + " " + data["personlista"]["person"]["efternamn"]);
		//$("body").append("<img src='" + data["personlista"]["person"]["bild_url_80"] + "'>");
		
		$("#memberName").html(personData["tilltalsnamn"] + " " + personData["efternamn"] + " (" + personData["parti"] + ")");
		$("#memberRole").html(personData["status"]);
		$("#memberPicture").attr("src",personData["bild_url_max"]);
	});
});