package home.example.wsexample;

import com.sun.security.auth.UserPrincipal;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(@Nonnull ServerHttpRequest request, @Nonnull WebSocketHandler wsHandler,
                                      @Nonnull Map<String, Object> attributes) {

        String path = request.getURI().getPath();
        log.info("Path: " + path);

        String messageId = extractMessageIdFromPath(path);
        log.info("Message id: " + messageId);

        Principal user = new UserPrincipal(UUID.randomUUID().toString());
        log.info("Principal User id: " + user.getName());

//        if (request instanceof ServletServerHttpRequest){
//            attributes.put("sessionId", ((ServletServerHttpRequest) request).getServletRequest().getSession().getId());
//        }
        return user;
    }

    private String extractMessageIdFromPath(String path) {
        String[] pathParts = path.split("/");
        if (pathParts.length < 2) { // TODO: я бы изменил условие на <2
            return null;
        }
            return pathParts[pathParts.length - 2];
    }
}