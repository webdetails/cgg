package pt.webdetails.cgg.scripts;

public class ScriptResourceNotFoundException extends Exception
{
  public ScriptResourceNotFoundException()
  {
  }

  public ScriptResourceNotFoundException(final String message)
  {
    super(message);
  }

  public ScriptResourceNotFoundException(final String message, final Throwable cause)
  {
    super(message, cause);
  }

  public ScriptResourceNotFoundException(final Throwable cause)
  {
    super(cause);
  }

  public ScriptResourceNotFoundException(final String message,
                                         final Throwable cause,
                                         final boolean enableSuppression,
                                         final boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
