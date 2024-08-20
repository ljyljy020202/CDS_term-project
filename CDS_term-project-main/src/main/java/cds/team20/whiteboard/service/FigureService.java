package cds.team20.whiteboard.service;

import cds.team20.whiteboard.entity.Figure;

public interface FigureService {
    void createFigure(Figure figure);    //도형 생성해서 FigureRepository에 저장
    Figure findFigure(String id);
    void modifyFigure(String id);    //기존 도형 삭제 후 다시 생성?
    void deleteFigure(String id);
    //만들지 논의해봐야 함, 도형 수정을 삭제 후 다시 생성하도록 구현하려면 필요함, FigureRepository에서 삭제
}
