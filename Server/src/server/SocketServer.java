  package server;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
 
import com.google.gson.Gson;
 
import lombok.Getter;
import serverDto.CreateRoomReqDto;
import serverDto.CreateRoomRespDto;
import serverDto.JoinReqDto;
import serverDto.JoinRespDto;
import serverDto.MessageReqDto;
import serverDto.MessageRespDto;
import serverDto.RequestDto;
import serverDto.ResponseDto;
 
@Getter
public class SocketServer extends Thread {
 
    private static List<SocketServer> socketServers = new ArrayList<>();
    private static List<String> connectedRooms = new ArrayList<>();
    
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Gson gson;
    private String username;
    private String roomname;
     
    public SocketServer(Socket socket) {
        this.socket = socket;
        gson = new Gson();
        socketServers.add(this);
    }
 
    @Override
    public void run() {
        try {
            inputStream = socket.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
 
            while (true) {
                String request = in.readLine();
                RequestDto requestDto = gson.fromJson(request, RequestDto.class);
                 
                switch (requestDto.getResource()) {
                case "join":
                    JoinReqDto joinReqDto = gson.fromJson(requestDto.getBody(), JoinReqDto.class);
                    username = requestDto.getBody();
                    JoinRespDto joinRespDto = new JoinRespDto(username, connectedRooms);
                    sendToAll(requestDto.getResource(), "ok", gson.toJson(joinRespDto));
                    
                    break;
                case "createRoom":
                    CreateRoomReqDto createRoomReqDto = gson.fromJson(requestDto.getBody(), CreateRoomReqDto.class);
                    roomname = createRoomReqDto.getRoomname();
                    connectedRooms.add(roomname);
                    CreateRoomRespDto createRoomRespDto = new CreateRoomRespDto(connectedRooms);
                    sendToAll(requestDto.getResource(), "ok", gson.toJson(createRoomRespDto));
                    break;
                case "sendMessage":
                    MessageReqDto messageReqDto = gson.fromJson(requestDto.getBody(), MessageReqDto.class);
                    String message = messageReqDto.getToUser() + ": " + messageReqDto.getMessageValue();
                    MessageRespDto messageRespDto = new MessageRespDto(message);
                    sendToAll(requestDto.getResource(), "ok", gson.toJson(messageRespDto));
                     
                    break;
                     
                default:
                    System.out.println("해당 요청은 처리할 수 없습니다.(404)");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    public void sendResponse(ResponseDto responseDto) {
        try {
            String response = gson.toJson(responseDto);
            for (SocketServer socketServer : socketServers) {
                outputStream = socket.getOutputStream();
                PrintWriter out = new PrintWriter(outputStream, true);
                out.println(response);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    private void sendToAll(String resource, String status, String body) throws IOException {
        ResponseDto responseDto = new ResponseDto(resource, status, body);
        for (SocketServer socketServer : socketServers) {
            OutputStream outputStream = socketServer.getSocket().getOutputStream();
            PrintWriter out = new PrintWriter(outputStream, true);
 
            out.println(gson.toJson(responseDto));
 
        }
 
    }
}
