package org.glassfish.grizzly.config.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import javax.net.ssl.SSLServerSocket;

public class ConcreteSSL extends JSSESocketFactory {

	@Override
	public void init() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	protected String[] getEnabledProtocols(SSLServerSocket paramSSLServerSocket, String paramString) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setEnabledProtocols(SSLServerSocket paramSSLServerSocket, String[] paramArrayOfString) {
		// TODO Auto-generated method stub

	}

	@Override
	public ServerSocket createServerSocket(int port) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerSocket createServerSocket(int port, int backlog) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerSocket createServerSocket(int port, int backlog, InetAddress ifAddress) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
