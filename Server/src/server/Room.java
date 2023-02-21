   
  package server;
 
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import lombok.Data;
import serverDto.ResponseDto;
 
@Data
public class Room {
    private String kingName;
    private String roomName;
    private List<Socket> clients;
    private Gson gson;
     
    public Room(String kingname, String roomName) {
        this.kingName = kingname;
        this.roomName = roomName;
        this.clients = new ArrayList<>();
        gson = new Gson();
    }
     
    public void addCilent(Socket socket) {
        clients.add(socket);
    }
     
    public void removeClient(Socket socket) {
        clients.remove(socket);
    }
    
    public void removeAllClient() {
    	clients.removeAll(clients);
    }
     
    public void broadcast(ResponseDto responseDto) throws IOException {
        
    	for(Socket socket1 : clients) {
    			
                OutputStream outputStream = socket1.getOutputStream();
                PrintWriter out = new PrintWriter(outputStream, true);
                out.println(gson.toJson(responseDto));
                System.out.println(responseDto);
        }
    }
     
}