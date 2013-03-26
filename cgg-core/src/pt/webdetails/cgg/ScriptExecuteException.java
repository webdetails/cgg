package pt.webdetails.cgg;

public class ScriptExecuteException extends Exception
{
  public ScriptExecuteException()
  {
  }

  public ScriptExecuteException(final String message)
  {
    super(message);
  }

  public ScriptExecuteException(final String message, final Throwable cause)
  {
    super(message, cause);
  }

  public ScriptExecuteException(final Throwable cause)
  {
    super(cause);
  }
}
