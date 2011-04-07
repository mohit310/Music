<%@page import="java.io.File" %>
<%@page import="java.util.List" %>
<%@page import="java.net.URLEncoder" %>
<%@page import="org.apache.commons.lang.StringEscapeUtils" %>
<html>
<head>
<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<META HTTP-EQUIV="EXPIRES" CONTENT="0">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="<%= request.getContextPath()%>/js/flowplayer-3.2.6.min.js"></script>

	<!-- some minimal styling, can be removed -->
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/style.css">

	<!-- page title -->
	<title>MK:Music Streamer</title>

</head>
<body>
	<div>
		<div id="player" style="display:block;width:480px;height:30px;"></div>
		<script>
			$f("player", "<%= request.getContextPath()%>/swf/flowplayer-3.2.7.swf", {

				plugins: {
					controls: {
						fullscreen: false,
						height: 30,
						autoHide: false,
						playlist: true,
						loop: true
					}
				},

				playlist: [
				   <% 
				   		List files = (List)request.getAttribute("playlist");
					   	StringBuilder writer = new StringBuilder();		
					   	for(int i=0; i< files.size(); i++) {
				   			File audioFile = (File)files.get(i);
				   			String file = audioFile.getPath();
				   			if(file.contains(".mp3")){
					   			try {
						   			file = URLEncoder.encode(file, "UTF-8");
						   			file = file.replaceAll("\\+", "%20");
					   			}catch(Exception e){
					   				file = StringEscapeUtils.escapeHtml(file);
					   			}
					   			String url = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/playService?file=" + file;
					   			writer.append("{url: '" + url + "' },\n");
				   			}
					   	}
					   	int commaIndex = writer.lastIndexOf(",");
					   	if(commaIndex!=-1){
					   		writer.replace(commaIndex,commaIndex+1,"");
					   	}
					   	out.println(writer.toString());
				   %>
				]

			});
		</script>
	</div>
</body>
</html>