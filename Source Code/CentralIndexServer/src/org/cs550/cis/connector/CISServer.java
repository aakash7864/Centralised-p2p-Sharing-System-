package org.cs550.cis.connector;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.cs550.cis.api.CentralIndexServerAPI;
import org.cs550.cis.api.impl.CentralIndexServerAPIimpl;
import org.cs550.cis.exception.CentralIndexServerException;

/**
 * CIS server class responsible for starting server at given server port from
 * configuration file and accept connection from peer and put @see
 * org.cs550.cis.connector.PeerConnection to queue that is pullled by
 * connectionManager working as consumer
 * 
 * @author Aishwarya Anand <A20331867>
 * @mail aanand12@hawk.iit.edu
 */
public class CISServer {
	/**
	 * Server port
	 */
	private int serverPort;
	/**
	 * value to check server running status
	 */
	public volatile boolean isRunning = false;
	/**
	 * Server socket
	 */
	protected ServerSocket serverSocket = null;
	/**
	 * Blocking queue that keeps the new connection made by peer
	 */
	private LinkedBlockingQueue<Socket> connectionQueue = new LinkedBlockingQueue<Socket>();

	/**
	 * @see CentralIndexServerAPI
	 */
	private CentralIndexServerAPI centralIndexServerAPI = null;

	/**
	 * Making default constructor as private
	 */
	private CISServer() {

	}

	/**
	 * Static factory method to start the server
	 * 
	 * @param port
	 *            - port no on which server will start
	 * @return server running status
	 */
	public static String start(int port) {
		String status = "";
		System.out.println("Starting server ...");
		CISServer cisServer = new CISServer();
		try {
			status = cisServer.runServer(port);
		} catch (CentralIndexServerException e) {
			System.out.println(e.getMessage());
		}
		System.out.println(status);
		return status;
	}

	private String runServer(int port) throws CentralIndexServerException {
		String serverStatus = "";
		if (isRunning == false) {
			initServer(port);
			isRunning = true;
			centralIndexServerAPI = new CentralIndexServerAPIimpl();
			ConnectionHandler connectionHandler = new ConnectionHandler();
			serverStatus = "Server Started at connection port " + port;
			System.out.println(serverStatus);
			while (isRunning) {
				connectionHandler.run();
				Socket connection;
				try {
					connection = serverSocket.accept();
					if (connection.isConnected()) {
						System.out.println("adding Connection To queue");
						connectionQueue.put(connection);
					}
				} catch (IOException | InterruptedException e) {
					throw new CentralIndexServerException("Application is already running at port no " + port + ". Cannot open port at port no " + port, e);
				}
			}
		} else {
			serverStatus = "Server is already Running";
		}
		return serverStatus;
	}

	private void initServer(int port) throws CentralIndexServerException {
		this.serverPort = port;
		openServerSocket(this.serverPort);
	}

	private void openServerSocket(int port) throws CentralIndexServerException {
		try {
			this.serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			throw new CentralIndexServerException("Application is already running at port no " + port + ". Cannot open port at port no " + port, e);
		}
	}

	public class ConnectionHandler implements Runnable {
		ExecutorService threadPool = Executors.newFixedThreadPool(10);
		ConcurrentHashMap<Integer, PeerConnection> concurrentHashMap = new ConcurrentHashMap<>();

		public void run() {
			if (!connectionQueue.isEmpty()) {
				threadPool.execute(new Runnable() {
					@Override
					public void run() {
						Socket peerConnection = connectionQueue.poll();
						System.out.println("Pulling Connection Form Queue");
						CISConnectionManager.getConnectionManager().addConnection(peerConnection.getPort(),
								new PeerConnection(peerConnection, centralIndexServerAPI));
					}
				});
			}
		}

	}
}
