<!DOCTYPE html>
<html>
  	<head>
		<title>Taskie</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>  
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
		<link rel="shortcut icon" href="../../favicon.ico" type="image/x-icon">
		<link rel="icon" href="../../favicon.ico" type="image/x-icon">
		
		<!-- CSS Files comes here -->
		<link href="../../css/bootstrap.css" rel="stylesheet" media="screen">
		<link href="../../css/style.css" rel="stylesheet" media="screen">
		<link href="../../css/animate.css" rel="stylesheet" media="screen">
		<link href="../../css/owl.carousel.css" rel="stylesheet" media="screen">
		<link href="../../css/owl.theme.css" rel="stylesheet" media="screen">
		<link href="../../css/nivo-lightbox.css" rel="stylesheet" media="screen">
		<link href="../../css/nivo_lightbox_themes/default/default.css" rel="stylesheet" media="screen">
		<link href="../../css/colors/red.css" rel="stylesheet" media="screen">
		<link href="../../css/responsive.css" rel="stylesheet" media="screen">
		
		<!-- Google fonts -->
		<link href="http://fonts.googleapis.com/css?family=Source+Sans+Pro:200,300,400,600,700,900,200italic,300italic,400italic,600italic,700italic,900italic" rel="stylesheet" type="text/css">
		
		<!-- Modernizer and IE specyfic files -->  
		<script src="../../js/modernizr.custom.js"></script>
 	</head>
  
  <body>
	
	<!--###############################-->
	<!--PRELOADER #####################-->
	<!--###############################-->
	
	<div id="preloader">
		<div id="status">
			<div class="spinner">
				<div class="bounce1"></div>
				<div class="bounce2"></div>
				<div class="bounce3"></div>
			</div>
		</div>
	</div>


	
	<!--###############################-->
	<!--ABOUT #########################-->
	<!--###############################-->
	
	<section id="about">
		<div class="container">
			<div class="row" id="about_intro">
				<div class="col-sm-12 col-md-12 col-lg-12" >
					<div id="logo"><a href="#home"><img src="../../images/logo.png" alt="logo"></a></div>
					<h2>Password Reset Process</h2>
					<p>{{ message }} <br></p>
					
				</div>
			</div>	
		</div>
	</section>
	
	
	
	<!--###############################-->
	<!--Newsletter and Footer #########-->
	<!--###############################-->
	

		<div class="container">		   
		   	<div class="row">
		   		<div class="col-sm-6 col-md-6 col-lg-6">
		   			<p align="left">Copyright &copy; All Rights Reserved - Taskie</p>
		   		</div>
		   		<div class="col-sm-6 col-md-6 col-lg-6">
		   			<p align="right">A product of Altersense India</p>
		   		</div>
		  	</div>
		</div>


	<!-- JavaScript plugins comes here -->
	<script src="../../js/jquery-2.0.3.min.js"></script>
	<script src="../../js/jquery.easing.min.js"></script>
	<script src="../../js/jquery.scrollTo.js"></script>
	<script src="../../js/jquery.form.js"></script>
	<script src="../../js/main.js"></script>
	<script src="../../js/retina.js"></script>
	<script src="../../js/waypoints.min.js"></script>
	<script src="../../js/owl.carousel.min.js"></script>
	<script src="../../js/nivo-lightbox.min.js"></script>
	<script type="text/javascript">
		$('document').ready(function(){
				$('#subscribe-form').ajaxForm( {
				target: '#preview',
				success: function() { 
					  $('#subscribe-form').slideUp('slow');
					  $('#preview').css({"opacity":"1"});
					}
				});
			});
	</script>
	<script>
		var url ='../../images/icons.svg';
		var c=new XMLHttpRequest(); c.open('GET.html', url, false); c.setRequestHeader('Content-Type', 'text/xml'); c.send();
		document.body.insertBefore(c.responseXML.firstChild, document.body.firstChild)
	</script>

	<!-- Demo Switcher JS -->
	<script type="text/javascript" src="../../js/fswit.js"></script>
		
  </body>
</html>