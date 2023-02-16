package client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JList;
import javax.swing.JTextField;

import com.google.gson.Gson;

import clientDto.RequestDto;

// client들 서버에 보내기
// 제목 : username 보내기
// 방, 제목 생성해서 보내기
// 메세지 보내기 (더블 클릭해서 개인방, 단톡방)
public class SendClient{

	private Gson gson;
	private Socket socket;
	private OutputStream outputStream;
	private JTextField InputMessage;
	private JList<String> userList;
	
	public SendClient(Socket socket) {
		this.socket = socket;
		gson = new Gson();
	}
	

	public void sendRequest(String resource, String body) {

		try {
			outputStream = socket.getOutputStream();
			PrintWriter out = new PrintWriter(outputStream, true);
			RequestDto<?> requestDto = new RequestDto<>(resource, gson.toJson(body));
			out.println(gson.toJson(requestDto));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage() {
		if (!InputMessage.getText().isBlank()) {

			String toUser = userList.getSelectedIndex() == 0 ? "all" : userList.getSelectedValue();

		}
	}
}
