package cds.team20.whiteboard.controller;

import cds.team20.whiteboard.entity.Figure;
import cds.team20.whiteboard.entity.Type;
import cds.team20.whiteboard.service.FigureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(WebSocketHandler.class);
    private List<WebSocketSession> sessionList = new ArrayList<>();
    private final FigureService figureService;
    @Autowired
    public WebSocketHandler(FigureService figureService) {
        this.figureService = figureService;

        // 파일로부터 데이터 불러와 repository에 등록
        try{
            File file = new File("data.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(fileReader);
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                enrollFig(line);
            }
            bufReader.close();
            fileReader.close();

        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("세션 [ {} ]  연결됨", session.getId());
        sessionList.add(session);

        // 현재 그려진 모든 도형 정보 figs에 저장 후 하나씩 전송
        String[] figs = figureService.getAll();
        //System.out.println("figs 생성 완");
        for (String s : figs) {
            session.sendMessage(new TextMessage(s));
        }
        
        // 현재 접속 중인 모든 세션에 접속 정보 전송
        sessionList.forEach(s-> {
            try {
                s.sendMessage(new TextMessage(session.getId()+"님께서 입장하셨습니다."));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("handleTextMessage is called...");
        log.info("payload {}", payload);

        // 어떤 메세지인지 구분이 필요함
        // 1. 클라이언트의 접속/해제 메세지 2. 새로 그려진 도형 정보 메세지
        String tmp[] = payload.split(" ");
        if(tmp.length>=8)    // 2 - 새로 그려진 도형 정보인 경우
            enrollFig(payload);
        
        // 모든 세션에 새로 그려진 도형 정보 전송
        sessionList.forEach(s-> {
            try {
                s.sendMessage(new TextMessage(payload));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("{} 연결 끊김", session.getId());
        sessionList.remove(session);

        // 현재 접속 중인 모든 세션에 접속 해제 정보 전송
        sessionList.forEach(s-> {
            try {
                s.sendMessage(new TextMessage(session.getId()+"님께서 퇴장하셨습니다."));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // 파일에 현재 그려진 도형 정보 저장
        File file = new File("data.txt");
        FileWriter fileWriter = new FileWriter(file);

        String[] figs = figureService.getAll();
        for (String s : figs) {
            fileWriter.write(s+"\r\n");
        }
        fileWriter.flush();
        fileWriter.close();
    }

    public int enrollFig(String payload){
        String[] tmp = payload.split(" ");
        Figure figure = new Figure();

        if(tmp[0].equals("circle")) figure.setType(Type.circle);
        else if(tmp[0].equals("rectangle")) figure.setType(Type.rectangle);
        else if(tmp[0].equals("line")) figure.setType(Type.line);
        else if(tmp[0].equals("text")) figure.setType(Type.text);
        else System.out.println("type error");

        figure.setLineWidth(tmp[1]);
        figure.setStrokeColor(tmp[2]);
        figure.setFillColor(tmp[3]);
        figure.setStartX(Integer.parseInt(tmp[4]));
        figure.setStartY(Integer.parseInt(tmp[5]));
        figure.setEndX(Integer.parseInt(tmp[6]));
        figure.setEndY(Integer.parseInt(tmp[7]));
        if(figure.getType()==Type.text)
            figure.setMsg(tmp[8]);

        figureService.createFigure(figure);
        return 0;
    }
}
