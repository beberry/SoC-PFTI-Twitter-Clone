package lv.beberry.quote.exceptions;

public class PasswordCantBeEmptyExceotion extends Exception
{
    //Parameterless Constructor
    public PasswordCantBeEmptyExceotion() {}

    //Constructor that accepts a message
    public PasswordCantBeEmptyExceotion(String message)
    {
       super(message);
    }
}
