<%@ page contentType="text/css" %>

/*{{{ general playlist settings, light gray */
.playlist {

	position:relative;
	overflow:hidden;	 	
	height:500px !important;
}

.playlist .clips {	
	position:absolute;
	height:20000em;
}

.playlist, .clips {
	width:260px;	
}

.clips a {
	background:url(<%= request.getContextPath()%>/images/h80.png);
	display:block;
	background-color:#fefeff;
	padding:12px 15px;
	height:46px;
	width:195px;
	font-size:12px;
	border:1px outset #ccc;		
	text-decoration:none;
	letter-spacing:-1px;
	color:#000;
	cursor:pointer;
}

.clips a.first {
	border-top-width:1px;
}

.clips a.playing, .clips a.paused, .clips a.progress {
	background:url(<%= request.getContextPath()%>/images/light.png) no-repeat 0px -69px;
	width:225px;
	border:0;
}
	
.clips a.progress {
	opacity:0.6;		
}

.clips a.paused {
	background-position:0 0;	
}

.clips a span {
	display:block;		
	font-size:11px;
	color:#666;
}

.clips a em {
	font-style:normal;
	color:#f00;
}	

.clips a:hover {
	background-color:#f9f9fa;		
}

.clips a.playing:hover, .clips a.paused:hover, .clips a.progress:hover {
	background-color:transparent !important;		 
}
/*}}}*/


/*{{{ petrol colored */

.clips.petrol a {
	background-color:#193947;
	color:#fff;
	border:1px outset #193947;
}

.clips.petrol a.playing, .clips.petrol a.paused, .clips.petrol a.progress {
	background:url(<%= request.getContextPath()%>/images/dark.png) no-repeat 0px -69px;
	border:0;
}

.clips.petrol a.paused {
	background-position:0 0;	
}

.clips.petrol a span {
	color:#aaa;
}

.clips.petrol a em {
	color:#FCA29A;
	font-weight:bold;
}	

.clips.petrol a:hover {
	background-color:#274D58;		
} 

.clips.petrol a.playing:hover, .clips.petrol a.paused:hover, .clips.petrol a.progress:hover {
	background-color:transparent !important;		 
}
/*}}}*/


/*{{{ low version */

.clips.low a {	
	height:31px;
}

.clips.low a.playing, .clips.low a.paused, .clips.low a.progress {
	background-image:url(<%= request.getContextPath()%>/images/light_small.png);
	background-position:0 -55px;
}

.clips.low a.paused {
	background-position:0 0;	
}


/*}}}*/


/*{{{ go buttons */

a.go {
	display:block;
	width:18px;
	height:18px;
	background:url(<%= request.getContextPath()%>/images/up.png) no-repeat;
	margin:5px 0 5px 105px;
	cursor:pointer;
}

a.go:hover, a.go.down:hover {
	background-position:0px -18px;		
}

a.go.down {
	background-image:url(<%= request.getContextPath()%>/images/down.png);	
}

.petrol a.go {
	background-image:url(<%= request.getContextPath()%>/images/up_dark.png);		
}

.petrol a.go.down {
	background-image:url(<%= request.getContextPath()%>/images/down_dark.png);		
}

a.go.disabled {
	visibility:hidden;		
}

/*}}}*/
