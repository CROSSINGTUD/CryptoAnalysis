package org.glassfish.grizzly.http.util;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class StringManager
{
  private ResourceBundle bundle;
  
  private StringManager(String packageName, ClassLoader loader)
  {
    this(packageName, Locale.getDefault(), loader);
  }
  
  private StringManager(String packageName, Locale loc, ClassLoader loader)
  {
    String bundleName = packageName + ".LocalStrings";
    try
    {
      this.bundle = ResourceBundle.getBundle(bundleName, loc, loader);
    }
    catch (MissingResourceException ex)
    {
      this.bundle = ResourceBundle.getBundle(bundleName, Locale.US, loader);
    }
  }
  
  private StringManager(ResourceBundle bundle)
  {
    this.bundle = bundle;
  }
  
  public String getString(String key)
  {
    if (key == null)
    {
      String msg = "key may not have a null value";
      
      throw new IllegalArgumentException(msg);
    }
    String str;
    try
    {
      str = this.bundle.getString(key);
    }
    catch (MissingResourceException mre)
    {
      str = null;
    }
    return str;
  }
  
  public String getString(String key, Object[] args)
  {
    String value = getString(key);
    String iString;
    try
    {
      if (args == null) {
        args = new Object[1];
      }
      Object[] nonNullArgs = args;
      for (int i = 0; i < args.length; i++) {
        if (args[i] == null)
        {
          if (nonNullArgs == args) {
            nonNullArgs = (Object[])args.clone();
          }
          nonNullArgs[i] = "null";
        }
      }
      if (value == null) {
        value = key;
      }
      iString = MessageFormat.format(value, nonNullArgs);
    }
    catch (IllegalArgumentException iae)
    {
      StringBuilder buf = new StringBuilder();
      buf.append(value);
      for (int i = 0; i < args.length; i++) {
        buf.append(" arg[").append(i).append("]=").append(args[i]);
      }
      iString = buf.toString();
    }
    return iString;
  }
  
  public String getString(String key, Object arg)
  {
    Object[] args = { arg };
    return getString(key, args);
  }
  
  public String getString(String key, Object arg1, Object arg2)
  {
    Object[] args = { arg1, arg2 };
    return getString(key, args);
  }
  
  public String getString(String key, Object arg1, Object arg2, Object arg3)
  {
    Object[] args = { arg1, arg2, arg3 };
    return getString(key, args);
  }
  
  public String getString(String key, Object arg1, Object arg2, Object arg3, Object arg4)
  {
    Object[] args = { arg1, arg2, arg3, arg4 };
    return getString(key, args);
  }
  
  private static final Map<String, StringManager> managers = new HashMap();
  
  public static synchronized StringManager getManager(String packageName, ClassLoader loader)
  {
    StringManager mgr = (StringManager)managers.get(packageName);
    if (mgr == null)
    {
      mgr = new StringManager(packageName, loader);
      managers.put(packageName, mgr);
    }
    return mgr;
  }
  
  public static synchronized StringManager getManager(ResourceBundle bundle)
  {
    return new StringManager(bundle);
  }
  
  public static synchronized StringManager getManager(String packageName, Locale loc, ClassLoader loader)
  {
    StringManager mgr = (StringManager)managers.get(packageName + '_' + loc.toString());
    if (mgr == null)
    {
      mgr = new StringManager(packageName, loc, loader);
      managers.put(packageName + '_' + loc.toString(), mgr);
    }
    return mgr;
  }
}
