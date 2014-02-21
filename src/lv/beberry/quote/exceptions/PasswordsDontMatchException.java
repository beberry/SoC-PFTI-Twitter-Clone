package lv.beberry.quote.exceptions;

public class PasswordsDontMatchException extends Exception
{
    //Parameterless Constructor
    public PasswordsDontMatchException() {}

    //Constructor that accepts a message
    public PasswordsDontMatchException(String message)
    {
       super(message);
    }
}
