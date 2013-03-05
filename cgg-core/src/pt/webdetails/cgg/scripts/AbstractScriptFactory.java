/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */

package pt.webdetails.cgg.scripts;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

import pt.webdetails.cgg.ScriptCreationException;

public abstract class AbstractScriptFactory implements ScriptFactory
{
  private static final Log logger = LogFactory.getLog(AbstractScriptFactory.class);

  public enum ScriptType
  {
    SVG, J2D
  }

  protected AbstractScriptFactory()
  {
  }

  public Reader getContextLibraryScript(final String script) throws IOException
  {
    return new InputStreamReader(getContextResource(script));
  }

  public Reader getSystemLibraryScript(final String script) throws IOException
  {
    final String resource = "org/pentaho/reporting/libraries/cgg/resources/" + script;
    final InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(resource);
    if (resourceAsStream == null)
    {
      throw new FileNotFoundException("Resource not found: " + resource);
    }
    return new BufferedReader(new InputStreamReader(new BufferedInputStream(resourceAsStream)));
  }

  public Script createScript(final String path, final String scriptType)
      throws FileNotFoundException, ScriptCreationException
  {
    try
    {
      final ScriptType st = ScriptType.valueOf(scriptType.toUpperCase(Locale.US));
      final Script script = createScript(path, st);
      script.setScope(lookupScope(st));
      return script;
    }
    catch (IllegalArgumentException ex)
    {
      throw new ScriptCreationException("No such script type: " + scriptType, ex);
    }
  }

  protected Script createScript(final String path, final ScriptType scriptType) throws FileNotFoundException
  {
    final Script script;
    switch (scriptType) {
        case SVG:
            script = new SvgScript(path);
            break;
        case J2D:
            script = new Java2DScript(path);
            break;
        default:
            throw new IllegalArgumentException();
    }
    return script;
  }

  protected Scriptable lookupScope(final ScriptType type)
  {
    return createScope(type);
  }

  protected Scriptable createScope(final ScriptType type)
  {
    final String[] dependencies = computeDependencies(type);

    final Context cx = ContextFactory.getGlobal().enter();
    final BaseScope scope = new BaseScope();
    scope.setScriptFactory(this);
    scope.init(cx);
    for (final String file : dependencies)
    {
      try
      {
        cx.evaluateReader(scope, new FileReader(file), "<file>", 1, null);
      }
      catch (IOException ex)
      {
        logger.error("Failed to read " + file + ": " + ex.toString());
      }
    }
    Context.exit();
    return scope;
  }

  protected String[] computeDependencies(final ScriptType type)
  {
    return new String[0];
  }

  public void clearCachedScopes()
  {
  }
}
