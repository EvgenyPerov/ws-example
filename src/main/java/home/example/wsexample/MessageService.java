package home.example.wsexample;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class MessageService {
        // этот объект отправляет сообщение конкретному получателю
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ResponseMessage createCommonMessage(Message message, Principal principal){
        return new ResponseMessage("From " + principal.getName() + " to everyone: " +message.name());
    }

    public ResponseMessage createPrivateMessage(String userId, Message message, Principal principal){

        ResponseMessage responseMessage = new ResponseMessage("This is private message from user " + principal.getName() + ": " + message.name());

        simpMessagingTemplate.convertAndSendToUser(userId, "/privateTopic/" + userId + "/private-messages", responseMessage);

        return new ResponseMessage("You sent private message to user " + userId);
    }
}
