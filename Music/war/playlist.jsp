<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<%@page import="java.io.File" %>
<%@page import="java.util.List" %>
<%@page import="java.net.URLEncoder" %>
<%@page import="org.apache.commons.lang.StringEscapeUtils" %>
<html>
<head>
	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
	<script src="<%= request.getContextPath()%>/js/flowplayer-3.2.6.min.js"></script>
	<script src="<%= request.getContextPath()%>/js/flowplayer.playlist-3.0.8.js"></script>	
	
	
	<!-- some minimal styling, can be removed -->
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/playlist.jsp"/>
	<!-- page title -->
	<title>MK:Music Streamer</title>

	<style>
	/* container has a background image */

	a.player {	
		display:block;
		width:500px;
		height:340px;
		text-align:center;
		color:#fff;
		text-decoration:none;
		cursor:pointer;
		background:#000 url(<%= request.getContextPath()%>/images/h500.png) repeat-x 0 0;
		background:-moz-linear-gradient(top, rgba(55, 102, 152, 0.9), rgba(6, 6, 6, 0.9));
		-moz-box-shadow:0 0 40px rgba(100, 118, 173, 0.5);
	}
	
	a.player:hover {
		background:-moz-linear-gradient(center top, rgba(73, 122, 173, 0.898), rgba(6, 6, 6, 0.898));	
	}
	
	/* splash image */
	a.player img {
		margin-top:125px;
		border:0;	
	}
	
	a.player {
		margin-top:0px;
		width:475px;		
	}
	</style>
	
	<script>
	
	<%
		String url = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/playService?file=";
	%>

	//wait for the DOM to load using jQuery
	$(function() {		
		// setup player normally
		$f("player", "<%= request.getContextPath()%>/swf/flowplayer-3.2.7.swf", {
			plugins: {
				audio: {
					url: '<%= request.getContextPath() %>/swf/flowplayer.audio-3.2.2.swf'
				}
			},
			
			clip: {
		    	coverImage: { url: '<%= request.getContextPath()%>/images/background.jpg', scaling: 'orig' }
			},
			
		}).playlist("div.petrol", {loop:true});
		
		$("#myplaylist a:first").click();
	});
	</script>
</head>
<body>

<!-- configure entries inside playlist using standard HTML -->
<div class="clips petrol" style="float:left" id="myplaylist">
	<% 
   		List files = (List)request.getAttribute("playlist");
	   	StringBuilder writer = new StringBuilder();		
	   	for(int i=0; i< files.size(); i++) {
   			File audioFile = (File)files.get(i);
			String fileName = audioFile.getName();
			fileName = fileName.replaceAll("\\[", "");
			fileName = fileName.replaceAll("\\]", "");
			fileName = fileName.replaceAll(".mp3", "");
   			String file = audioFile.getPath();			
   			if(file.contains(".mp3")){
	   			try {
		   			file = URLEncoder.encode(file, "UTF-8");
		   			file = file.replaceAll("\\+", "%20");
		   			file = file.replaceAll("'","&#39;");
	   			}catch(Exception e){
	   				file = StringEscapeUtils.escapeHtml(file);
	   			}
	   			writer.append("<a href=\"" + url + file  +"\"");
	   			if(i==0){
	   				writer.append(" class=\"first\"");
	   			}
	   			writer.append(">");
	   			writer.append(fileName);
	   			writer.append("</a>\n");
	   		}
	   	}
	   	out.println(writer.toString());
    %>


</div>
<!-- player container and a splash image (play button) -->
<a class="player plain" id="player" style="float:left">
	<img src="<%= request.getContextPath()%>/images/play_text_large.png" />
</a>

<!-- let rest of the page float normally -->
<br clear="all"/>

</body>
</html>