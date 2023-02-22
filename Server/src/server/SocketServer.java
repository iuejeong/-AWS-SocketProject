package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import lombok.Getter;
import serverDto.CreateRoomReqDto;
import serverDto.CreateRoomRespDto;
import serverDto.ExitAllRespDto;
import serverDto.ExitReqDto;
import serverDto.ExitRespDto;
import serverDto.JoinReqDto;
import serverDto.JoinRespDto;
import serverDto.JoinRoomReqDto;
import serverDto.JoinRoomRespDto;
import serverDto.MessageReqDto;
import serverDto.MessageRespDto;
import serverDto.RequestDto;
import serverDto.ResponseDto;

@Getter
public class SocketServer extends Thread {

	private static List<SocketServer> socketServers = new ArrayList<>();
	private static List<String> connectedRooms = new ArrayList<>();
	private static List<Room> Rooms = new ArrayList<>();

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
		try {
			inputStream = socket.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

			while (true) {
				String request = in.readLine();
				RequestDto requestDto = gson.fromJson(request, RequestDto.class);

				switch (requestDto.getResource()) {
				case "join":
					JoinReqDto joinReqDto = gson.fromJson(requestDto.getBody(), JoinReqDto.class);
					username = joinReqDto.getUsername();
					JoinRespDto joinRespDto = new JoinRespDto(username, connectedRooms);
					sendToAll(requestDto.getResource(), "ok", gson.toJson(joinRespDto));

					break;
				case "createRoom":
					CreateRoomReqDto createRoomReqDto = gson.fromJson(requestDto.getBody(), CreateRoomReqDto.class);
					String kingname = createRoomReqDto.getKingName();
					String roomname = createRoomReqDto.getRoomname();
					Room room = new Room(kingname, roomname);
					Rooms.add(room);
					connectedRooms.add(roomname);

					for (Room jRoom : Rooms) {
						if (jRoom.getRoomName().equals(roomname)) {
							jRoom.addCilent(this.socket);
						}
					}
					CreateRoomRespDto createRoomRespDto = new CreateRoomRespDto(connectedRooms, roomname);
					sendToAll(requestDto.getResource(), "ok", gson.toJson(createRoomRespDto));
					break;

				case "joinRoom":
					JoinRoomReqDto joinRoomReqDto = gson.fromJson(requestDto.getBody(), JoinRoomReqDto.class);
					String jRoomname = joinRoomReqDto.getRoomname();
					String joinMessage = joinRoomReqDto.getUsername() + "님이 접속하였습니다.";
					JoinRoomRespDto joinRoomRespDto = new JoinRoomRespDto(joinMessage, jRoomname);
					ResponseDto responseDto2 = new ResponseDto(requestDto.getResource(), "ok",
							gson.toJson(joinRoomRespDto));
					for (Room jRoom : Rooms) {
						if (jRoom.getRoomName().equals(jRoomname)) {
							jRoom.addCilent(this.socket);
							jRoom.broadcast(responseDto2);
						}
					}

					break;
				case "sendMessage":
					MessageReqDto messageReqDto = gson.fromJson(requestDto.getBody(), MessageReqDto.class);
					String message = messageReqDto.getToUser() + ": " + messageReqDto.getMessageValue();
					MessageRespDto messageRespDto = new MessageRespDto(message);
					String mRoomname = messageReqDto.getRoomname();
					ResponseDto responseDto3 = new ResponseDto(requestDto.getResource(), "ok",
							gson.toJson(messageRespDto));
					for (Room jRoom : Rooms) {

						if (jRoom.getRoomName().equals(mRoomname)) {
							jRoom.broadcast(responseDto3);
						}
					}
					break;
				case "exit":
					ExitReqDto exitReqDto = gson.fromJson(requestDto.getBody(), ExitReqDto.class);
					String username = exitReqDto.getUsername();
					String exitMessage = username + "님이 나갔습니다.";
					String exitRoomname = exitReqDto.getRoomname();
					ExitRespDto exitRespDto = new ExitRespDto(exitMessage, connectedRooms);
					ResponseDto responseDto4 = new ResponseDto(requestDto.getResource(), "ok", gson.toJson(exitRespDto));
					for (Room jRoom : Rooms) {
						
						if (jRoom.getKingName().equals(username)) {
							connectedRooms.remove(exitRoomname);
							exitRespDto.setConnectedRooms(connectedRooms);
							responseDto4.setBody(gson.toJson(exitRespDto));
							sendExit(responseDto4, jRoom);
							jRoom.removeAllClient();
							Rooms.remove(jRoom);
						} else if (jRoom.getRoomName().equals(exitRoomname)) {
							jRoom.removeClient(this.socket);
							jRoom.broadcast(responseDto4);
						}
					}
					
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
	
	public void sendExit(ResponseDto responseDto, Room room) {
		try {
			
			for (SocketServer socketServer : socketServers) {
				if(room.getClients().contains(socketServer.getSocket())) {
					responseDto.setStatus("all");
					room.broadcast(responseDto);
				} else {
					responseDto.setStatus("ok");
					outputStream = socketServer.getSocket().getOutputStream();
					PrintWriter out = new PrintWriter(outputStream, true);
					out.println(gson.toJson(responseDto));
					out.flush();
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}