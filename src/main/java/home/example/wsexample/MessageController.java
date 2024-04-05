package home.example.wsexample;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // этот объект отправляет сообщение конкретному получателю
    private final SimpMessagingTemplate simpMessagingTemplate;
    @MessageMapping("/message/{sessionId}")
    @SendTo("/commonTopic/{sessionId}/messages")
    public ResponseMessage getMessage(@Payload Message message, Principal principal, @DestinationVariable String sessionId){
        System.out.println("Получено сообщения от сессии #" + sessionId);
        return messageService.createCommonMessage(message, principal);
    }

    @MessageMapping("/private-message/{sessionId}/{userId}")
    @SendToUser("/privateTopic/{sessionId}/private-messages") // эта аннотация нужна чтобы отправителю вернулся ответ на фронт ( объект ResponseMessage)
    public ResponseMessage getPrivateMessage(@DestinationVariable String userId, @DestinationVariable String sessionId,
                                             @Payload Message message, Principal principal){
        System.out.println("private-message from : "+ principal.getName() + " to " + userId + " sessionId: " + sessionId);
        return messageService.createPrivateMessage(userId, message, principal, sessionId);
    }

    @MessageExceptionHandler
    @SendToUser("/privateTopic/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }
}
