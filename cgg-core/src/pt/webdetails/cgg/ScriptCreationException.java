package pt.webdetails.cgg;

public class ScriptCreationException extends Exception
{
  public ScriptCreationException()
  {
  }

  public ScriptCreationException(final String message)
  {
    super(message);
  }

  public ScriptCreationException(final String message, final Throwable cause)
  {
    super(message, cause);
  }

  public ScriptCreationException(final Throwable cause)
  {
    super(cause);
  }
}
