/**
 * jQuery based form validator made by Jekabs Stikans.
 * 
 * @param  None
 * @return None
 */
$.fn.formValidator = function (pbox1,pbox2,options)
{
	var defaults = 
        {
        	mainOb: this,
            pbox1: pbox1,
            pbox2: pbox2
        }

    var opts = $.extend(defaults, options);
	
	return this.each(function(){
		
		var passwordTimer;
		
		$(opts.pbox1).keyup(function(passwordTimer)
		{
			clearTimeout(this.passwordTimer);
			
			if($(opts.pbox2).val().length > 0)
			{
				this.passwordTimer = setTimeout(function() { checkInputs(opts.pbox1,opts.pbox2); },1600);
			}
		});


		$(opts.pbox2).keyup(function(passwordTimer)
		{
			clearTimeout(this.passwordTimer);
			
			if($(opts.pbox1).val().length > 0)
			{
				this.passwordTimer = setTimeout(function() { checkInputs(opts.pbox2,opts.pbox1);},1600);
			}
		});
		
		$(opts.mainOb).keyup(validateForm);
	});

	function checkInputs(selectedInput, otherInput)
	{
		// Check if anything is already in the second password box.
		if($(otherInput).val().length > 0 && $(selectedInput).val().length > 0)
		{
			// Check if the passwords match.

			if($(selectedInput).val() != $(otherInput).val())
			{
				if(!$(opts.mainOb).find(".formErrorList").is(':visible'))
				{
					$(opts.mainOb).find(".formErrorList").html("Passwords don't match!");
					$(opts.mainOb).find(".formErrorList").slideDown();
				}
			}
			else
			{
				if($(opts.mainOb).find(".formErrorList").is(':visible'))
				{
					$(opts.mainOb).find(".formErrorList").slideUp();
				}
			}

			$(selectedInput).addClass('.has-error');
			$(otherInput).addClass('.has-error');
		}
	}

	function validateForm()
	{
		if($(opts.pbox1).val().length > 0 && $(opts.pbox1).val() ==$(opts.pbox2).val() && isEmail($(opts.mainOb).find("input[type=email]").val()) && $(opts.mainOb).find("input[name=username]").val().length > 0)
		{
			$(opts.mainOb).find("input[type=submit]").removeAttr('disabled');
		}
		else
		{
		//	$(opts.mainOb).find("input[type=submit]").attr('disabled','disabled');
		}
		
	}
	
	function isEmail(email)
	{
		  var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		  return regex.test(email);
	}
}