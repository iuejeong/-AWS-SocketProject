package serverDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateRoomReqDto {
	private String kingName;
	private String roomname;

}
