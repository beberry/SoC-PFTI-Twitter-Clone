/**
 * jQuery based suggestion plugin made by Jekabs Stikans.
 * 
 * @param  None
 * @return None
 */
$.fn.suggestions = function (url,inputId,suggestionId,redUrl,options)
{
	var defaults = 
        {
        	mainOb: this,
            url: url,
            inputId: inputId,
            suggestionId: suggestionId,
            redUrl: redUrl
        }

    var opts = $.extend(defaults, options);
	
	return this.each(function(){
		
		
		$(opts.inputId).keyup(function()
		{
			// Get the new string.
			lookupKey = $(opts.inputId).val();
			
			if(lookupKey.length > 0)
			{
				// Make an ajax request	
				getSuggestions(lookupKey);
			}
			else
			{
				// Remove suggestion list, if one exists..
				$(opts.suggestionId).fadeOut();
			}
			
		});
		
		$(opts.inputId).focusout(function ()
		{
			if($(opts.suggestionId).is(':visible'))
			{
				$(opts.suggestionId).fadeOut();
			}
		});
	});

	
	function getSuggestions(suggestion)
	{
		$.getJSON(opts.url+'json/', { q: ''+suggestion }, function(data) {
			
			if(data != null)
			{
		    	htmlResp = "";
		    	
			    $.each(data, function(index, element) {
			    	   	

			    	firstPart = element.substring(0,suggestion.length);
			    	secondPart = element.substring(suggestion.length,element.length);
			    	

			    	
			    	htmlResp = htmlResp+'<li class="active" data-value="'+element+'"><a href="'+opts.redUrl+element+'"><strong>'+firstPart+'</strong>'+secondPart+'</a></li>';
			    });
	
				$(opts.suggestionId).html(htmlResp);
			}
			else
			{
				// Tell that nothing was found.
				$(opts.suggestionId).html('<li class="active" data-value="-"><strong>Such user was not found!</strong></li>');
			}
			
			if(!$(opts.suggestionId).is(':visible'))
			{
				$(opts.suggestionId).fadeIn();
			}
			
		});
	}
}