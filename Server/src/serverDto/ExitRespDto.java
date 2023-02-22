package serverDto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExitRespDto {

	private String message;
	private List<String> connectedRooms;
}
