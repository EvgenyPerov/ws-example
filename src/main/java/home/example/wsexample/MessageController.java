package home.example.wsexample;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    @MessageMapping("/message/{sessionId}")
    @SendTo("/commonTopic/{sessionId}/messages")
    public ResponseMessage getMessage(@Payload Message message, Principal principal, @DestinationVariable String sessionId){
        System.out.println("Получено сообщения от сессии #" + sessionId);
        return messageService.createCommonMessage(message, principal);
    }

    @MessageMapping("/private-message/{userId}")
    @SendToUser("/privateTopic/{userId}/private-messages") // эта аннотация нужна чтобы отправителю вернулся ответ на фронт ( объект ResponseMessage)
    public ResponseMessage getPrivateMessage(@DestinationVariable String userId, @Payload Message message, Principal principal){
        System.out.println("private-message from : "+ principal.getName() + " to " + userId);
        return messageService.createPrivateMessage(userId, message, principal);
    }

    @MessageExceptionHandler
    @SendToUser("/privateTopic/{userId}/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }
}
