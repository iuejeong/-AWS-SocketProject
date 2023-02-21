package clientDto;

import java.net.Socket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JoinRoomReqDto {

	private String roomname;
}