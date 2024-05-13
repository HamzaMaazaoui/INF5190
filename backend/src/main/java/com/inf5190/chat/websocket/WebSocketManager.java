package com.inf5190.chat.websocket;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
public class WebSocketManager {
    private final LinkedHashMap<String, WebSocketSession> sessions = new LinkedHashMap<String, WebSocketSession>();

    public void addSession(WebSocketSession session) {
        this.sessions.put(session.getId(), session);
    }

    public void removeSession(WebSocketSession session) {
        this.sessions.remove(session.getId());
    }

    public void notifySessions() {
        for (WebSocketSession s : sessions.values()) {
            try {
                s.sendMessage(new TextMessage("notif"));
            } catch (IOException e) {
                System.out.print(e);
            }
        }
    }

}
