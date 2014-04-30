package lv.beberry.quote.exceptions;

public class WrongPasswordException extends Exception
{
    //Parameterless Constructor
    public WrongPasswordException() {}

    //Constructor that accepts a message
    public WrongPasswordException(String message)
    {
       super(message);
    }
}
