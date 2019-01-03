<!DOCTYPE html>
  <head>
    <meta charset="utf-8">
     <script src="js/jquery-2.1.1.js"></script>
     <script src="js/homepage.js"></script>
    <title>Weather Newsletter</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css"  href="css/homepage.css">
  </head>

<body>

<form action="/action_page.php" style="border:1px solid #ccc">
  <div class="container">
    <h1>Weather Newsletter</h1>
    <p>Please fill in this form to get an email about the current weather near you!</p>
    <hr>

    <label for="email"><b>Email</b></label>
    <input type="text" placeholder="Enter Email" id="email"  required>

    <label for="psw"><b>Location</b><br>
    Valid locations are limited to the top 100 cities in America</label>
    <input type="text" placeholder="Enter Location" list="cityList" id="location" required>
    <datalist id="cityList"></datalist>
    <div class="alert" id = "alert" style="display: none">
  <span class="closebtn" onclick="this.parentElement.style.display='none';">&times;</span> 
  <strong>Error:</strong> Email is invalid or email could not send. Please try again
  </div>
    <div class="alert" id = "alert2" style="display: none">
  <span class="closebtn" onclick="this.parentElement.style.display='none';">&times;</span> 
  <strong>Error:</strong> Location is invalid. Please enter a valid location.
  </div>
  <div class="alert" id = "alert3" style="display: none">
  <span class="closebtn" onclick="this.parentElement.style.display='none';">&times;</span> 
  <strong>Error:</strong> There is already an account associated with this email
</div>
  <div id = "success" style="display: none">
  <span class="closebtn" onclick="this.parentElement.style.display='none';">&times;</span> 
  <strong>Success!</strong> An email has been sent to your account 
  </div>
  <div id = "waiting" style="display: none">
  <span class="closebtn" onclick="this.parentElement.style.display='none';">&times;</span> 
  <strong>Processing...</strong> Please wait.
  </div>
   

    <div class="clearfix">
      <button type="submit" id="signupbtn" >Subscribe</button>
    </div>
  </div>
</form>

</body>
 </html>