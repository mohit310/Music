<%@page import="java.io.File" %>
<%@page import="java.util.List" %>
<%@page import="java.net.URLEncoder" %>
<%@page import="org.apache.commons.lang.StringEscapeUtils" %>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<script type="text/javascript" src="<%= request.getContextPath()%>/js/flowplayer-3.2.6.min.js"></script>

	<!-- some minimal styling, can be removed -->
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/style.css">

	<!-- page title -->
	<title>MK:Music Streamer</title>

</head>
<body>
	<div>
		<div id="player" style="display:block;width:480px;height:400px;"></div>
		<script>
			$f("player", "<%= request.getContextPath()%>/swf/flowplayer-3.2.7.swf", {

				plugins: {
					
					content: {
						url: '<%= request.getContextPath()%>/swf/flowplayer.content-3.2.0.swf',
						backgroundColor:'#002200',
						top:25, right: 25, width: 160, height: 100
					},
					
					controls: {
						fullscreen: false,
						height: 30,
						autoHide: false,
						playlist: true,
						loop: true
					}
				},
				
				clip: {
					onStart: function(song) {
			    		var meta = song.metaData;
			    		
			    		this.getPlugin("content").setHtml(
			    			"<p>Artist: <b>" + meta.TPE1 + "</b></p>" +
			    			"<p>Album:   <b>" + meta.TALB + "</b></p>" +
			    			"<p>Title:   <b>" + meta.TIT2 + "</b></p>"
			    		);					
			    	},
			    	
			    	coverImage: { url: '<%= request.getContextPath()%>/images/background.jpg', scaling: 'orig' }
					
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