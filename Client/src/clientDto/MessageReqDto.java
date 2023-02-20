package clientDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageReqDto {
	private String toUser;
	private String messageValue;
}