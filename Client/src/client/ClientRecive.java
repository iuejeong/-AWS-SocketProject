package client;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

import clientDto.CreateRoomRespDto;
import clientDto.ExitRespDto;
import clientDto.JoinRespDto;
import clientDto.JoinRoomRespDto;
import clientDto.MessageRespDto;
import clientDto.ResponseDto;
import lombok.RequiredArgsConstructor;
 
@RequiredArgsConstructor
public class ClientRecive extends Thread {
 
    private final Socket socket;
    private InputStream inputStream;
    private Gson gson;
    private boolean isFirst = true;
     
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
                     
                    if(isFirst) {
                        Client.getInstance().getRoomListModel().addAll(joinRespDto.getCreateRooms());
                        isFirst = false;
                    }
                     
                    
                    break;
                case "joinRoom":
                	JoinRoomRespDto joinRoomRespDto = gson.fromJson(responseDto.getBody(), JoinRoomRespDto.class);
                	Client.getInstance().getContentView().append(joinRoomRespDto.getMessage() + "\n");
                	Client.getInstance().getRoomLabel().setText("방이름: " + joinRoomRespDto.getRoomname());
                	break;
                case "createRoom":
                    CreateRoomRespDto createRoomRespDto = gson.fromJson(responseDto.getBody(), CreateRoomRespDto.class);
                    Client.getInstance().getRoomListModel().clear();
                    Client.getInstance().getRoomListModel().addAll(createRoomRespDto.getCreateRooms());
                    break;
                case "sendMessage":
                    MessageRespDto messageRespDto = gson.fromJson(responseDto.getBody(), MessageRespDto.class);
                    Client.getInstance().getContentView().append(messageRespDto.getMessageValue() + "\n");
                    Client.getInstance().getContentView().setCaretPosition(Client.getInstance().getContentView().getDocument().getLength());
                    break;
                case "exit":
                	ExitRespDto exitRespDto = gson.fromJson(responseDto.getBody(), ExitRespDto.class);
                	
                	
                	if(responseDto.getStatus().equalsIgnoreCase("conncetUsers")) {
                		JOptionPane.showMessageDialog(null, "방장이 나갔습니다.", "방나가짐", JOptionPane.ERROR_MESSAGE);
                		Client.getInstance().getMainCard().show(Client.getInstance().getMainPanel(), "listPanel");
                		Client.getInstance().getContentView().setText("");
                		Client.getInstance().getRoomListModel().clear();
                        Client.getInstance().getRoomListModel().addAll(exitRespDto.getConnectedRooms());
                	} else if(responseDto.getStatus().equalsIgnoreCase("others")) {
                		Client.getInstance().getRoomListModel().clear();
                        Client.getInstance().getRoomListModel().addAll(exitRespDto.getConnectedRooms());
                	} else if (responseDto.getStatus().equalsIgnoreCase("all")) {
                		Client.getInstance().getRoomListModel().clear();
                        Client.getInstance().getRoomListModel().addAll(exitRespDto.getConnectedRooms());
                    	Client.getInstance().getContentView().append(exitRespDto.getMessage() + "\n");
                	}

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