package com.inf5190.chat.messages;

import com.inf5190.chat.auth.session.SessionData;
import com.inf5190.chat.auth.session.SessionDataAccessor;
import com.inf5190.chat.messages.model.Message;
import com.inf5190.chat.messages.model.MessageRequest;
import com.inf5190.chat.messages.repository.DocumentNotFoundException;
import com.inf5190.chat.messages.repository.MessageRepository;
import com.inf5190.chat.websocket.WebSocketManager;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class MessageController {
    public static final String ROOT_PATH = "/messages";

    private MessageRepository messageRepository;
    private WebSocketManager webSocketManager;
    private SessionDataAccessor sessionDataAccessor;

    public MessageController(MessageRepository messageRepository,
            WebSocketManager webSocketManager,
            SessionDataAccessor sessionDataAccessor) {
        this.messageRepository = messageRepository;
        this.webSocketManager = webSocketManager;
        this.sessionDataAccessor = sessionDataAccessor;
    }

    @GetMapping(ROOT_PATH)
    public List<Message> getMessages(@RequestParam(name = "fromId") Optional<String> fromId) {
        try {
            return this.messageRepository.getMessages(fromId);
        } catch (DocumentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error on get message.");
        }
    }

    @PostMapping(ROOT_PATH)
    public Message newMessage(@RequestBody MessageRequest message, HttpServletRequest request) {
        try {
            final SessionData authData = this.sessionDataAccessor.getSessionData(request);

            if (!message.username().equals(authData.username())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }

            final Message m = this.messageRepository.createMessage(message);
            this.webSocketManager.notifySessions();
            return m;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error on create message.");
        }
    }
}
