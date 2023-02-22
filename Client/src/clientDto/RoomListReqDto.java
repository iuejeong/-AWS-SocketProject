package clientDto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomListReqDto {
	private List<String> connectedRooms;
}
