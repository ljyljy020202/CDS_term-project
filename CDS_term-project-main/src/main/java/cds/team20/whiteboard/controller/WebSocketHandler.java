package cds.team20.whiteboard.controller;

import cds.team20.whiteboard.entity.Figure;
import cds.team20.whiteboard.service.FigureService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private List<WebSocketSession> sessionList = new ArrayList<>();
    private final FigureService figureService;
    @Autowired
    public WebSocketHandler(FigureService figureService) {
        this.figureService = figureService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        String name = session.getHandshakeHeaders().get("name").get(0);
        System.out.println(name+"입장");
        sessionList.add(session);
        sessionList.forEach(s-> {
            try {
                s.sendMessage(new TextMessage(name+"님께서 입장하셨습니다."));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        super.handleTextMessage(session, message);
        //String msgtype = session.getHandshakeHeaders().get("msgtype").get(0);

        //if문으로 해당 메세지가 도형 정보인지(figure) 접속/해제 정보인지 판단(connection)
        //if(msgtype.equals("figure")) {
            Gson gson = new Gson();
            Figure figure = gson.fromJson(message.getPayload(), Figure.class);
            figureService.createFigure(figure);
            //session.sendMessage(new TextMessage("Hello World"));
            sessionList.forEach(s -> {
                try {
                    s.sendMessage(new TextMessage(gson.toJson(figure)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        /*}else{
            sessionList.forEach(s-> {
                try {
                    s.sendMessage(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }*/
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessionList.remove(session);
        String name = session.getHandshakeHeaders().get("name").get(0);
        sessionList.forEach(s-> {
            try {
                s.sendMessage(new TextMessage(name+"님께서 퇴장하셨습니다."));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
