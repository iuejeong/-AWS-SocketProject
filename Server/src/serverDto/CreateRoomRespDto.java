package serverDto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CreateRoomRespDto {
	private List<String> createRooms;
	private String roomname;
}
