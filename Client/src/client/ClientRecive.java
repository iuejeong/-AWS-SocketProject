package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.Gson;

import clientDto.JoinRespDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientRecive extends Thread{
	
	private final Socket socket;
	private InputStream inputStream;
	private Gson gson;
	
	@Override
	public void run() {
		try {
			inputStream = socket.getInputStream();				
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));	
			switch(responseDto.getResource()){
				case "join":
					// equlasIgnoreCase는 대소문자 구분 
					JoinRespDto joinRespDto = gson.fromJson(responseDto.getBody(), JoinRespDto.class);
					Client.getInstance().getContentView().append(joinRespDto.getWelcomeMessage() + "\n");
					Client.getInstance().getUserListModel().clear();
					Client.getInstance().getUserListModel().addElement("--- 전체 ---");
					Client.getInstance().getUserListModel().addAll(joinRespDto.getConnectedUsers());
					Client.getInstance().getUserList().setSelectedIndex(0);
					break;
			
			
		} catch (UnknownHostException e) {			// ip를 잡지 못했을 때
			e.printStackTrace();
		} catch (IOException e) {					// 통신을 잡지 못했을 때
			e.printStackTrace();
		}
	}
	
	
		
		
		
}
	

