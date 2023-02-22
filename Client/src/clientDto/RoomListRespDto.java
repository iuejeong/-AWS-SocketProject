package clientDto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomListRespDto {
	private List<String> connectedRooms;
}
