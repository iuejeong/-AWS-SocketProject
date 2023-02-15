package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.DomainCombiner;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

import clientDto.ResponseDto;
import simplechatting2.dto.JoinRespDto;

public class ClientRecive {

	private Socket socket;
	private InputStream inputStream;
	private Gson gson;
	private String username;
	private ResponseDto responseDto;
	
	
	private ClientRecive() {
		String ip = null;
		int port = 0;
		
		try {
			socket = new Socket(ip, port);
			
			inputStream = socket.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			gson = new Gson();
			
			while(true) {
				String request = in.readLine();
				responseDto = gson.fromJson(request, ResponseDto.class);
				
				switch(responseDto.getResource()) {
					case "join":
						JoinRespDto joinRespDto = gson.fromJson(responseDto.getBody(), JoinRespDto.class);
					case "createRoom":
					
					case "sendMessage":
						
					case "deleteRoom":
						
						
				}
				
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
