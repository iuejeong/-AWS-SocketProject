package clientDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExitReqDto {

	private String roomname;
	private String username;
}
