/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
* 
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

package pt.webdetails.cgg;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import junit.framework.AssertionFailedError;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Assert;
import org.junit.Before;


public class CggGoldTestBase
{
  private static File checkMarkerExists(final String filename)
  {
    final File file = new File(filename);
    if (file.canRead())
    {
      return file;
    }
    return null;
  }

  @Before
  public void setUp() throws Exception
  {
    CggBoot.init();
  }

  protected void run(final File file, final File gold)
      throws Exception
  {
    final String fileNameWithoutExtension = file.getName().substring(0, file.getName().length() - 3);

    final String scriptType;
    final String outputType;
    final String fileName = file.getName();
    if (fileName.endsWith("-svg-test.js"))
    {
      scriptType = "SVG";
      outputType = "svg";
    }
    else if (fileName.endsWith("-j2d-test.js"))
    {
      scriptType = "J2D";
      outputType = "png";
    }
    else
    {
      throw new IllegalArgumentException(fileName);
    }

    handleContent(executeCgg(file, scriptType, outputType),
        new File(gold, fileNameWithoutExtension + "." + outputType), outputType);
  }

  private byte[] executeCgg(final File file, final String scriptType, final String outputType)
      throws IOException, ScriptCreationException, ScriptExecuteException
  {
    final Map<String, Object> parameter = loadParameter(file);
    final ByteArrayOutputStream out = new ByteArrayOutputStream();

    final URL scriptContext = file.getParentFile().toURI().toURL();
    final DefaultCgg cgg = new DefaultCgg(out, scriptContext);
    cgg.draw(file.getName(), scriptType, outputType, 800, 600, parameter);
    return out.toByteArray();
  }

  protected Map<String, Object> loadParameter(final File file) throws IOException
  {
    final String properties = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 3) + ".properties";
    final Properties p = new Properties();
    final File propertiesFile = new File(properties);
    if (propertiesFile.exists() == false)
    {
      return new HashMap<String, Object>();
    }

    final FileInputStream inStream = new FileInputStream(properties);
    try
    {
      p.load(inStream);
    }
    finally
    {
      inStream.close();
    }
    final HashMap<String, Object> retval = new HashMap<String, Object>();
    for (final Map.Entry<Object, Object> entry : p.entrySet())
    {
      retval.put(String.valueOf(entry.getKey()), entry.getValue());
    }

    return retval;
  }

  protected void runAllGoldCharts() throws Exception
  {
    initializeTestEnvironment();
    processCharts("cgg");
  }

  private void processCharts(final String sourceDirectoryName) throws Exception
  {
    final File marker = findMarker();
    final File cggCharts = new File(marker, sourceDirectoryName);
    final File gold = new File(marker, "gold");
    final FilenameFilter filter = createChartFilter();

    final Map<File, File> files = findFilesToProcess(cggCharts, gold, filter);

    for (final Map.Entry<File, File> testSpec : files.entrySet())
    {
      final File file = testSpec.getKey();
      final File goldTargetDirectory = testSpec.getValue();
      if (file.isDirectory())
      {
        continue;
      }

      try
      {
        System.out.printf("Processing %s%n", file);
        run(file, goldTargetDirectory);
        System.out.printf("Finished   %s%n", file);
      }
      catch (Throwable re)
      {
        throw new Exception("Failed at " + file, re);
      }
    }
  }

  private static Map<File, File> findFilesToProcess(final File cggCharts,
                                                    final File goldDirectory,
                                                    final FilenameFilter filter)
  {
    final Map<File, File> resultFiles = new HashMap<File, File>();

    final File[] files = cggCharts.listFiles(filter);
    final HashSet<String> fileSet = new HashSet<String>();
    for (final File file : files)
    {
      final String fileName = file.getName();
      if (fileSet.add(fileName.toLowerCase(Locale.ENGLISH)) == false)
      {
        // the toy systems MacOS X and Windows use case-insensitive file systems and completely
        // mess up when there are two files with what they consider the same name.
        throw new IllegalStateException("There is a golden sample with the same Windows/Mac " +
            "filename in the directory. Make sure your files are unique and lowercase.");
      }

      if (file.isDirectory())
      {
        final File directory = new File(goldDirectory, fileName);
        final Map<File, File> fromSubDir = findFilesToProcess(file, directory, filter);
        resultFiles.putAll(fromSubDir);
      }
      else
      {
        resultFiles.put(file, goldDirectory);
      }
    }
    return resultFiles;
  }

  protected void initializeTestEnvironment() throws Exception
  {
  }

  protected void runSingleGoldChart(final String file) throws Exception
  {
    initializeTestEnvironment();

    final File marker = findMarker();
    final File gold = new File(marker, "gold");

    try
    {
      final File cggChartFile = findChart(file);

      System.out.printf("Processing %s%n", file);
      run(cggChartFile, gold);
      System.out.printf("Finished   %s%n", file);
    }
    catch (Throwable re)
    {
      throw new Exception("Failed at " + file, re);
    }

    System.out.println(marker);
  }

  private File findChart(final String file) throws FileNotFoundException
  {
    final File marker = findMarker();
    final File cggCharts = new File(marker, "cgg");
    final File cggChartFile = new File(cggCharts, file);
    if (cggChartFile.exists())
    {
      return cggChartFile;
    }

    throw new FileNotFoundException(file);
  }

  protected FilenameFilter createChartFilter()
  {
    return new FilesystemFilter(new String[]{"-svg-test.js", "-j2d-test.js"}, "Test scripts", true);
  }

  public static File locateGoldenSample(final String name)
  {
    final FilesystemFilter filesystemFilter = new FilesystemFilter(name, "Charts", true);
    final File marker = findMarker();
    final File gold = new File(marker, "gold");
    final File cggCharts = new File(marker, "cggCharts");
    final Map<File, File> files = findFilesToProcess(cggCharts, gold, filesystemFilter);

    //noinspection LoopStatementThatDoesntLoop
    for (final Map.Entry<File, File> fileFileEntry : files.entrySet())
    {
      return fileFileEntry.getKey();
    }

    return null;
  }

  protected void handleContent(final byte[] cggChartOutput,
                               final File goldSample,
                               final String outputType) throws Exception
  {
    
    FileOutputStream fos = new FileOutputStream(goldSample.getName());

    fos.write(cggChartOutput);  
    
    if ("svg".equals(outputType))
    {
      final Reader reader = new InputStreamReader(new FileInputStream(goldSample), "UTF-8");
      final ByteArrayInputStream inputStream = new ByteArrayInputStream(cggChartOutput);
      final Reader cggChart = new InputStreamReader(inputStream, "UTF-8");
      try
      {
        XMLAssert.assertXMLEqual("File " + goldSample + " failed", new Diff(reader, cggChart), true);
      }
      catch (AssertionFailedError afe)
      {
        throw afe;
      }
      finally
      {
        reader.close();
      }
    }

    final FileInputStream in = new FileInputStream(goldSample);
    try
    {
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      IOUtils.copy(in, out);
      Assert.assertArrayEquals(out.toByteArray(), cggChartOutput);
    }
    finally
    {
      in.close();
    }
  }


  public static File findMarker()
  {
    final ArrayList<String> positions = new ArrayList<String>();
    positions.add("test-gold/marker.properties");
    for (final String pos : positions)
    {
      final File file = checkMarkerExists(pos);
      if (file != null)
      {
        return file.getAbsoluteFile().getParentFile();
      }
    }
    throw new IllegalStateException("Cannot find marker, please run from the correct directory");
  }

}
