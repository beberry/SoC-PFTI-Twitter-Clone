/**
 * jQuery based data submit plugin made by Jekabs Stikans.
 * 
 * @param  None
 * @return None
 */
$.fn.ajaxPost = function (url,id,respOb,callbackF,reqType,clearform,options)
{
	var defaults = 
        {
        	mainOb: this,
            url: url,
            id: id,
            respOb: respOb,
            clearform: clearform,
            callbackF:callbackF,
            reqType:reqType
        };

	
    var opts = $.extend(defaults, options);
	
	return this.each(function(){
		
		opts.mainOb.find("form").submit(function() {
			
			var myForm = $(this);
			
			if(reqType == "DELETE")
			{
			    $.ajax({
			           type: opts.reqType+"",
			           url: url+myForm.find("input[name="+opts.id+"]").val(),
			           success: function(data)
			           {		        	   
			        	  callbackList(myForm);
			           }
			         });
			    
				
			    return false;
			}
			else
			{
			    $.ajax({
			           type: opts.reqType+"",
			           url: url+"?ajax",
			           data: $(myForm).serialize(),
			           success: function(data)
			           {		        	   
			        	  callbackList(myForm);
			           }
			         });
			    
			    return false;
			}
			
		});
	
		
		$(opts.respOb).on('touch click', function ()
		{
			$(opts.respOb).fadeOut();
		});
		
	});
	
	function callbackList(myForm)
	{
		if(opts.clearform)
	   {
		   myForm.find("input[type=text], textarea").val("");
	   }
	   
	   if(opts.callbackF == "deleteTweet")
	   {
		   deleteTweetCallback(myForm);
	   }
	   else if(opts.callbackF == "followers")
	   {
		   followCallback(myForm);
	   }
	   else if(opts.callbackF == "submitTweetCallback")
	   {
		   submitTweetCallback(myForm);
	   }		        	   
	   else if(opts.callbackF == "saveProfile")
	   {
		   saveProfile(myForm);
	   }
	}
	
	function deleteTweetCallback(myForm)
	{
		myForm.parent().parent().slideUp();
	}
	
	function submitTweetCallback(myForm)
	{
		showInf();
	}
	
	function saveProfile(myForm)
	{
		showInf();
	}
	
	
	function followCallback(myForm)
	{
		inputZ  = myForm.find("input");
		buttonZ = myForm.find("button");
			
		if(inputZ.attr("name") == "unFollowUsername")
		{
			inputZ.attr("name", "followUsername");
			buttonZ.removeClass("btn-danger");
			buttonZ.addClass("btn-primary");
			buttonZ.html("Follow");
		}
		else
		{
			inputZ.attr("name", "unFollowUsername");
			buttonZ.removeClass("btn-primary");
			buttonZ.addClass("btn-danger");
			buttonZ.html("Unfollow");
		}
	}
	
	function showInf()
	{
 	   $(opts.respOb).fadeOut(300, function()
       		{
       		   $(opts.respOb+ ' .info').html("<h2>Data sent to the server!</h2>");
       		   
       		   $(opts.respOb).fadeIn(1000,function()
       		   {
       			   setTimeout(function(){$(opts.respOb).fadeOut();},2100); 
       		   });
       		   
       		});
	}
	
};