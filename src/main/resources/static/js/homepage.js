const error1 = "You've entered an incorrect location. Please enter a correct location"

window.onload=function(){
	const x = document.getElementById("alert2");
	const y = document.getElementById("alert");
	const y1 = document.getElementById("alert3");
	const z = document.getElementById("success");
	const z1 = document.getElementById("waiting")
	const dataList = document.getElementById("cityList")
	var cityInfo;
	//get the list of cities on the loading of the page
	$.post("/cityInfo", null, responseJSON => {
		const responseObject = JSON.parse(responseJSON);
		cityInfo = responseObject.list;
		//create the dropdown menu for the location input 
		for (i=0; i<cityInfo.length; i++) {
			var option = document.createElement('option');
        // Set the value using the item in the JSON array.
        	option.value = cityInfo[i];
        // Add the <option> element to the <datalist>.
        	dataList.appendChild(option);
		}
		
	});
	//when a user submits the form
	document.getElementById("signupbtn").addEventListener("click", function(event){
		z1.style.display = "block";
		x.style.display = "none";
	  	y.style.display = "none";
		z.style.display = "none";
		y1.style.display = "none";
		event.preventDefault();
	  	const email = document.getElementById("email").value;
	  	const location = document.getElementById("location").value
	  	const split = location.split(",")
	  	//check spliting the location results in an array of size two
	  	//if not, stop and show the corresponding error message
	  	if (split.length!=2) {
	
	  		x.style.display = "block";
	  		y.style.display = "none";
		  	z.style.display = "none";
		  	y1.style.display = "none";
		  	z1.style.display = "none";
  
	  	}
	  	//send to the backend the email, the city, and the state
	  	else {
		  	const postParameters = {
	      		email: email,
	      		city:split[0].trim(),
	      		state:split[1].trim()
	    	};
		  	$.post("/register",postParameters , responseJSON => {
		  		const responseObject = JSON.parse(responseJSON);
		  		//next three if/else if checks if signing up was unsuccessful and based on the error message shows the corresponding error message
		  		if (!responseObject.success && responseObject.errorMessage === "email failed to send") {
	  				y.style.display = "block";
	  				x.style.display = "none";
	  				z.style.display = "none";
	  				y1.style.display = "none";
	  				z1.style.display = "none"
		  		}
		  		else if (!responseObject.success && responseObject.errorMessage === "invalid city") {
		  			x.style.display = "block"
	  				y.style.display = "none";
		  			z.style.display = "none";
		  			y1.style.display = "none";
		  			z1.style.display = "none"
		  		}
		  		else if (!responseObject.success && responseObject.errorMessage === "user exists") {
		  			y1.style.display = "block";
	  				y.style.display = "none";
		  			z.style.display = "none";
		  			x.style.display = "none";
		  			z1.style.display = "none"
		  		}
		  		//if successful, show the success message and clear the form
		  		else {
		  			z.style.display = "block";
		  			$("input:text").val("");
		  			y.style.display = "none";
		  			x.style.display = "none";
		  			y1.style.display = "none";
		  			z1.style.display = "none"
		  		}
	 
	    	});
	    }
	    
	});
}
