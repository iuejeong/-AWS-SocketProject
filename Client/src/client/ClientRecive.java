package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.Gson;

import clientDto.ResponseDto;
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
						Client.getInstance().getContentView().append((String) responseDto.getBody());
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
