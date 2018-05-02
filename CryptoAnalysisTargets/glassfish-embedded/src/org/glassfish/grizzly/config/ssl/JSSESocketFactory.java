package org.glassfish.grizzly.config.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import org.glassfish.grizzly.http.util.StringManager;

public abstract class JSSESocketFactory
  extends ServerSocketFactory
{
  private static final StringManager sm = StringManager.getManager(JSSESocketFactory.class.getPackage().getName(), JSSESocketFactory.class.getClassLoader());
  public static final String defaultProtocol = "TLS";
  public static final String defaultAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
  static final boolean defaultClientAuth = false;
  private static final String defaultKeyPass = "changeit";
  protected static final Logger logger = Logger.getLogger(JSSESocketFactory.class.getName());
  protected boolean initialized;
  protected boolean clientAuthNeed = false;
  protected boolean clientAuthWant = false;
  protected SSLServerSocketFactory sslProxy = null;
  protected String[] enabledCiphers;
  private Map<String,String> attributes;
  
  public ServerSocket createSocket(int port)
    throws IOException
  {
    if (!this.initialized) {
      init();
    }
    ServerSocket socket = this.sslProxy.createServerSocket(port);
    initServerSocket(socket);
    return socket;
  }
  
  public ServerSocket createSocket(int port, int backlog)
    throws IOException
  {
    if (!this.initialized) {
      init();
    }
    ServerSocket socket = this.sslProxy.createServerSocket(port, backlog);
    initServerSocket(socket);
    return socket;
  }
  
  public ServerSocket createSocket(int port, int backlog, InetAddress ifAddress)
    throws IOException
  {
    if (!this.initialized) {
      init();
    }
    ServerSocket socket = this.sslProxy.createServerSocket(port, backlog, ifAddress);
    initServerSocket(socket);
    return socket;
  }
  
  public Socket acceptSocket(ServerSocket socket)
    throws IOException
  {
    SSLSocket asock;
    try
    {
      asock = (SSLSocket)socket.accept();
      if (this.clientAuthNeed) {
        asock.setNeedClientAuth(this.clientAuthNeed);
      } else {
        asock.setWantClientAuth(this.clientAuthWant);
      }
    }
    catch (SSLException e)
    {
      throw new SocketException("SSL handshake error" + e.toString());
    }
    return asock;
  }
  
  public void handshake(Socket sock)
    throws IOException
  {
    ((SSLSocket)sock).startHandshake();
  }
  
  protected String[] getEnabledCiphers(String requestedCiphers, String[] supportedCiphers)
  {
    String[] enabled = null;
    if (requestedCiphers != null)
    {
      List<String> vec = null;
      String cipher = requestedCiphers;
      int index = requestedCiphers.indexOf(',');
      if (index != -1)
      {
        int fromIndex = 0;
        while (index != -1)
        {
          cipher = requestedCiphers.substring(fromIndex, index).trim();
          if (cipher.length() > 0) {
            for (int i = 0; (supportedCiphers != null) && (i < supportedCiphers.length); i++) {
              if (supportedCiphers[i].equals(cipher))
              {
                if (vec == null) {
                  vec = new ArrayList();
                }
                vec.add(cipher);
                break;
              }
            }
          }
          fromIndex = index + 1;
          index = requestedCiphers.indexOf(',', fromIndex);
        }
        cipher = requestedCiphers.substring(fromIndex);
      }
      if (cipher != null)
      {
        cipher = cipher.trim();
        if (cipher.length() > 0) {
          for (int i = 0; (supportedCiphers != null) && (i < supportedCiphers.length); i++) {
            if (supportedCiphers[i].equals(cipher))
            {
              if (vec == null) {
                vec = new ArrayList();
              }
              vec.add(cipher);
              break;
            }
          }
        }
      }
      if (vec != null) {
        enabled = (String[])vec.toArray(new String[vec.size()]);
      }
    }
    return enabled;
  }
  
  protected String getKeystorePassword()
  {
    String keyPass = (String)this.attributes.get("keypass");
    if (keyPass == null) {
      keyPass = "changeit";
    }
    String keystorePass = (String)this.attributes.get("keystorePass");
    if (keystorePass == null) {
      keystorePass = keyPass;
    }
    return keystorePass;
  }
  
  protected KeyStore getKeystore(String pass)
    throws IOException
  {
    String keystoreFile = (String)this.attributes.get("keystore");
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("Keystore file= " + keystoreFile);
    }
    String keystoreType = (String)this.attributes.get("keystoreType");
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("Keystore type= " + keystoreType);
    }
    return getStore(keystoreType, keystoreFile, pass);
  }
  
  protected String getTruststorePassword()
  {
    String truststorePassword = (String)this.attributes.get("truststorePass");
    if (truststorePassword == null)
    {
      truststorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
      if (truststorePassword == null) {
        truststorePassword = getKeystorePassword();
      }
    }
    return truststorePassword;
  }
  
  protected KeyStore getTrustStore()
    throws IOException
  {
    KeyStore ts = null;
    String truststore = (String)this.attributes.get("truststore");
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("Truststore file= " + truststore);
    }
    String truststoreType = (String)this.attributes.get("truststoreType");
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("Truststore type= " + truststoreType);
    }
    String truststorePassword = getTruststorePassword();
    if ((truststore != null) && (truststorePassword != null)) {
      ts = getStore(truststoreType, truststore, truststorePassword);
    }
    return ts;
  }
  
  private KeyStore getStore(String type, String path, String pass)
    throws IOException
  {
	KeyStore ks = null;
    InputStream istream = null;
    try
    {
      ks = KeyStore.getInstance(type);
      if ((!"PKCS11".equalsIgnoreCase(type)) && (!"".equalsIgnoreCase(path)))
      {
        File keyStoreFile = new File(path);
        if (!keyStoreFile.isAbsolute()) {
          keyStoreFile = new File(System.getProperty("catalina.base"), path);
        }
        istream = new FileInputStream(keyStoreFile);
      }
      ks.load(istream, pass.toCharArray());
      
      return ks;
    }
    catch (FileNotFoundException fnfe)
    {
      logger.log(Level.SEVERE, sm.getString("jsse.keystore_load_failed", type, path, fnfe.getMessage()), fnfe);
      throw fnfe;
    }
    catch (IOException ioe)
    {
      logger.log(Level.SEVERE, sm.getString("jsse.keystore_load_failed", type, path, ioe.getMessage()), ioe);
      throw ioe;
    }
    catch (Exception ex)
    {
      logger.log(Level.SEVERE, sm.getString("jsse.keystore_load_failed", type, path, ex.getMessage()), ex);
      throw new IOException(sm.getString("jsse.keystore_load_failed", type, path, ex.getMessage()));
    }
    finally
    {
      if (istream != null) {
        try
        {
          istream.close();
        }
        catch (IOException ioe) {}
      }
    }
  }
  
  public abstract void init()
    throws IOException;
  
  protected abstract String[] getEnabledProtocols(SSLServerSocket paramSSLServerSocket, String paramString);
  
  protected abstract void setEnabledProtocols(SSLServerSocket paramSSLServerSocket, String[] paramArrayOfString);
  
  protected void initServerSocket(ServerSocket ssocket)
  {
    SSLServerSocket socket = (SSLServerSocket)ssocket;
    if (this.attributes.get("ciphers") != null) {
      socket.setEnabledCipherSuites(this.enabledCiphers);
    }
    String requestedProtocols = (String)this.attributes.get("protocols");
    setEnabledProtocols(socket, getEnabledProtocols(socket, requestedProtocols));
    if (this.clientAuthNeed) {
      socket.setNeedClientAuth(this.clientAuthNeed);
    } else {
      socket.setWantClientAuth(this.clientAuthWant);
    }
  }
}
