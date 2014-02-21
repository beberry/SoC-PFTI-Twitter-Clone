package lv.beberry.quote.exceptions;

public class UsernameTakenException extends Exception
{
    //Parameterless Constructor
    public UsernameTakenException() {}

    //Constructor that accepts a message
    public UsernameTakenException(String message)
    {
       super(message);
    }
}
