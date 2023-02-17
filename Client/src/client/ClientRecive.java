package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;

import com.google.gson.Gson;

import clientDto.CreateRoomRespDto;
import clientDto.JoinRespDto;
import clientDto.ResponseDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientRecive extends Thread {

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
				ResponseDto responseDto = gson.fromJson(request, ResponseDto.class);
				switch (responseDto.getResource()) {
					case "join":
						JoinRespDto joinRespDto = gson.fromJson(responseDto.getBody(), JoinRespDto.class);
//						Client.getInstance().getUsernameView().append(joinRespDto.getUsername());
						System.out.println(responseDto.getBody());
						break;
					case "createRoom":
						CreateRoomRespDto createRoomRespDto = gson.fromJson(responseDto.getBody(), CreateRoomRespDto.class);
						Client.getInstance().getRoomListModel().clear();
						Client.getInstance().getRoomListModel().addAll(createRoomRespDto.getCreateRooms());
						Client.getInstance().getRoomList().setSelectedIndex(0);
						System.out.println(responseDto.getBody());
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
