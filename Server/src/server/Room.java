package server;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Room {
	private String kingName;
	private String roomName;
	private List<SocketServer> clients;
	
	public Room(String kingname, String roomName) {
		this.kingName = kingname;
		this.roomName = roomName;
		this.clients = new ArrayList<>();
	}
	
	public void addCilent(SocketServer socketServer) {
		clients.add(socketServer);
	}
	
	public void removeClient(SocketServer socketServer) {
		clients.remove(socketServer);
	}
	
	public void broadcast(String message, SocketServer socketServer) {
		for(SocketServer socketServer1 : clients) {
			if(!socketServer1.equals(socketServer)) {
				OutputStream outputStream = socketServer1.getOutputStream();
				PrintWriter out = new PrintWriter(outputStream, true);
				out.println(message);
			}
		}
	}
	
}
