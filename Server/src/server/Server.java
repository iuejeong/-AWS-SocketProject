package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

	private static final int PORT = 9090;
	private ServerSocket serverSocket;

	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("Server accept");
			socketConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(serverSocket != null) {
					serverSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void socketConnection() throws IOException {
			while (true) {
				Socket socket = serverSocket.accept();
				SocketServer socketServer = new SocketServer(socket);
				socketServer.start();
			}
	}
		
	
	
	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

}
