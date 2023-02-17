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
			
			
			while(true) {
				String request = in.readLine();
				RequestDto<?> requestDto = gson.fromJson(request, RequestDto.class);
				String resource = requestDto.getResource();
				switch (resource) {
					case "join":
						username = (String) requestDto.getBody();
						ResponseDto<?> responseDto = new ResponseDto<String>(resource, "ok", gson.toJson(username));
						sendResponse(responseDto);
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
