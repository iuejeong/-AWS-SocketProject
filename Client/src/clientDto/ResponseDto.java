package clientDto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponseDto {
	private String resource;
	private String status;
	private String body;
	private String welcomeMessage;
	private List<String> connectedUsers;
	private String title;
	
}
