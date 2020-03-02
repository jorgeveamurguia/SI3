<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator"
	prefix="decorator"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<decorator:usePage id="thePage" />


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta http-equiv="Content-Type" content="text/html;" />
<meta http-equiv="Content-Style-Type" content="text/css" />

<title><decorator:title default="UNED: Pr&aacute;ctica SI-3" />
</title>
<link rel="stylesheet" href="style.css" type="text/css" media="screen" />

<decorator:getProperty property="page.customStyles" default="" />
</head>
<body>

<div id="body_wrapper"><!-- Start of Page  -->
<div id="page_header"><!-- Start of Company Name -->
<div id="company_name">
<h1><a href="index.jsp"> Pr&aacute;ctica SI3</a></h1>
</div>
<!-- End of Company Name --> <!-- Start of Login Navigation -->
<div id="loginArea_1">
<div id="loginArea_2">
<div id="loginArea"><decorator:getProperty
	property="page.loginArea" default="" /></div>
</div>
</div>
<!-- End of Login Navigation -->


<div class="clearthis">&nbsp;</div>
</div>
<!-- End of Page  --> <!-- Start of  Navigation -->
<div id="header_nav"><decorator:getProperty
	property="page.headerMenu" default="" /></div>
<!-- End of  Navigation --> <!-- Start of Left Sidebar --> <!-- <div id="leftsidebar_1"> -->
<!-- 	<div id="leftsidebar_2"> --> <!-- 		<div id="leftsidebar_content"> -->
<!-- 		</div> --> <!-- 	</div> --> <!-- </div> --> <!-- End of Left Sidebar -->

<!-- Start of Main Content -->
<div id="maincontent_1">
<div id="maincontent_2">
<div id="maincontent_content">
<decorator:body />
</div>
</div>
</div>
<!-- End of Main Content -->

<div class="clearthis">&nbsp;</div>

<!-- Start of Page Footer -->
<div id="page_footer">

<div id="ringoffire">

<div class="box">Pr&aacute;ctica desarrollada por: <br />
<a href="mailto:gonzalez.karlos@gmail.com">Carlos Gonz&aacute;lez
Muñoz</a> <br />
<a href="mailto:jorge.veamurguia@gmail.com">Jorge Vea Murguia</a></div>

</div>

<!-- 			<div id="paypalverified">
				<a href="http://www.freewebsitetemplates.com/"><span></span> </a>
			</div>
 -->
<div id="copyright_info"><small>Webdesign by </small> <br />
<a href="mailto:gonzalez.karlos@gmail.com">Carlos Gonz&aacute;lez
Muñoz</a> &amp; <a href="mailto:jorge.veamurguia@gmail.com">Jorge Vea
Murguia</a> <small><b>&copy;</b> Students wishing to finish their
studies!</small></div>

<div class="clearthis">&nbsp;</div>
</div>

<!-- End of Page Footer --></div>

</body>
</html>