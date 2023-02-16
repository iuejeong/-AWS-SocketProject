package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.Gson;

import clientDto.JoinRespDto;
<<<<<<< HEAD
=======
import clientDto.ResponseDto;
import clientDto.UsernameRespDto;
>>>>>>> origin/main
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientRecive extends Thread {

	private ResponseDto<?> responseDto;
	private final Socket socket;
	private InputStream inputStream;
	private Gson gson;

	@Override
	public void run() {
		try {
			inputStream = socket.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			gson = new Gson();
			while (true) {
				String request = in.readLine();
				responseDto = gson.fromJson(request, ResponseDto.class);
				switch (responseDto.getResource()) {
					case "join":
						// equlasIgnoreCase는 대소문자 구분
						JoinRespDto joinRespDto = gson.fromJson((String) responseDto.getBody(), JoinRespDto.class);
						Client.getInstance().getContentView().append(joinRespDto.getWelcomeMessage() + "\n");
						Client.getInstance().getUserListModel().clear();
						Client.getInstance().getUserListModel().addElement("--- 전체 ---");
						Client.getInstance().getUserListModel().addAll(joinRespDto.getConnectedUsers());
						break;
					case "username":
						UsernameRespDto usernameRespDto = gson.fromJson((String) responseDto.getBody(),
								UsernameRespDto.class);
						Client.getInstance().getUsernameView().append(usernameRespDto.getUsername());
						System.out.println(usernameRespDto.getUsername());
						break;
					}
			}

		} catch (UnknownHostException e) { // ip를 잡지 못했을 때
			e.printStackTrace();
		} catch (IOException e) { // 통신을 잡지 못했을 때
			e.printStackTrace();
		}
	}

}
