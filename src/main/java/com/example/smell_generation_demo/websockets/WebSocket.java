package com.example.smell_generation_demo.websockets;

import javax.ejb.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/ws")
@Singleton
public class WebSocket {
    Session session;

    @OnOpen
    public void open(Session session) {
        this.session = session;
        System.out.println("Socket is opened");
    }

    @OnClose
    public void close() {
        this.session = null;
        System.out.println("Socket is closed");
    }

    @OnMessage
    public void message(String msg) {
        if(session != null) {
            try {
                session.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
