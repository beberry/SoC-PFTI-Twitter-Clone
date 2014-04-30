</head>
<body>
    <div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">Le Quote</a>
        </div>
        <div class="collapse navbar-collapse">
          <ul class="nav navbar-nav">
            <li class="${pageContext.request.requestURI eq '/leQuote/RenderTweets.jsp' ? 'active' : ''}${pageContext.request.requestURI eq '/leQuote/Login.jsp' ? 'active' : ''}${pageContext.request.requestURI eq '/leQuote/Register.jsp' ? 'active' : ''}"><a href="/leQuote/message">Home</a></li>
            <li class="${pageContext.request.requestURI eq '/leQuote/Friend.jsp' ? 'active' : ''}"><a href="/leQuote/friend">Friends</a></li>
            <%  if ((session.getAttribute("userid") != null) && (session.getAttribute("userid") != "")) { %>
            <li class="${pageContext.request.requestURI eq '/leQuote/Profile.jsp' ? 'active' : ''}"><a href="/leQuote/profile">Profile</a></li>
            <li><a href="/leQuote/logout">Log out</a></li>
            <% } %>
          </ul>
        </div><!--/.nav-collapse -->
      </div>
    </div>
	<!-- Start of main container. -->
	<div id="container">