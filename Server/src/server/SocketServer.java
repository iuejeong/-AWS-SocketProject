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
import serverDto.JoinReqDto;
import serverDto.JoinRespDto;
import serverDto.RequestDto;
import serverDto.ResponseDto;


@Getter
public class SocketServer extends Thread{

	private static List<SocketServer> socketServers = new ArrayList<>();
	
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private Gson gson;
	private String username;
	
	public SocketServer(Socket socket) {
		this.socket = socket;
		socketServers.add(this);
		gson = new Gson();
	}
	
	@Override
	public void run() {
		reciveRequest();
	}
	
	public void reciveRequest() {
		try {
			inputStream = socket.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			String request = in.readLine();
			
			while(true) {
				RequestDto<?> requestDto = gson.fromJson(request, RequestDto.class);
				String resource = requestDto.getResource();
				switch (resource) {
					case "join":
						JoinReqDto joinReqDto = gson.fromJson((String) requestDto.getBody(), JoinReqDto.class);
						username = joinReqDto.getUsername();
						List<String> connectedUsers = new ArrayList<>();
						
						for (SocketServer socketServer : socketServers) {
							connectedUsers.add(socketServer.getUsername());
						}
						JoinRespDto joinRespDto = new JoinRespDto(username + "님이 접속하였습니다.\n", connectedUsers);
						ResponseDto<?> responseDto = new ResponseDto<>(requestDto.getResource(), "ok", gson.toJson(joinRespDto));
						sendResponse(responseDto);
						break;
					case "createRoom":
						
					case "deleteRoom":
						
					default:
						System.out.println("해당 요청은 처리할 수 없습니다.(404)");
						break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendResponse(ResponseDto<?> responseDto) {
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
}
