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
import serverDto.UsernameReqDto;
import serverDto.UsernameRespDto;


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
		gson = new Gson();
		socketServers.add(this);
	}
	
	@Override
	public void run() {
		reciveRequest();
	}
	
	public void reciveRequest() {
		try {
			inputStream = socket.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			
			
			while(true) {
				String request = in.readLine();
				RequestDto<?> requestDto = gson.fromJson(request, RequestDto.class);
				String resource = requestDto.getResource();
				switch (resource) {
				
					case "username":
						UsernameReqDto usernameReqDto = gson.fromJson((String) requestDto.getBody(), UsernameReqDto.class);
						username = usernameReqDto.getUsername();
						UsernameRespDto usernameRespDto = new UsernameRespDto(username);
						sendResponse(requestDto.getResource(), "ok", gson.toJson(usernameRespDto));
						
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
	
	public void sendResponse(String resource, String status, String body) {
		ResponseDto<String> responseDto = new ResponseDto<String>(resource, status, body); 
		try {
			String response = gson.toJson(responseDto);
			
			for (SocketServer socketServer : socketServers) {
				outputStream = socket.getOutputStream();
				PrintWriter out = new PrintWriter(outputStream, true);
				out.println(gson.toJson(response));
				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
