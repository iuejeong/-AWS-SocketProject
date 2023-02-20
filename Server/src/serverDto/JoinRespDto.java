package serverDto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class JoinRespDto {
	private String username;
	private List<String> createRooms;
}
