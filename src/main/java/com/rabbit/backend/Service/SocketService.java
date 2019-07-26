package com.rabbit.backend.Service;

import org.springframework.stereotype.Service;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/websocket")
@Service
public class SocketService {
    private static CopyOnWriteArraySet<SocketService> webSocketSet = new CopyOnWriteArraySet<>();
    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
    }

    private void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static void sendAllMessage(String message) {
        for (SocketService socketService : webSocketSet) {
            try {
                socketService.sendMessage(message);
            } catch (Exception e) {
                // do nothing...
            }
        }
    }

    @OnMessage
    public void onMessage(String messsage, Session session) {

    }
}
