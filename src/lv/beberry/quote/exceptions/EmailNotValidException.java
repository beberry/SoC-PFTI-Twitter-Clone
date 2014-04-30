package lv.beberry.quote.exceptions;

public class EmailNotValidException extends Exception
{
    //Parameterless Constructor
    public EmailNotValidException() {}

    //Constructor that accepts a message
    public EmailNotValidException(String message)
    {
       super(message);
    }
}
