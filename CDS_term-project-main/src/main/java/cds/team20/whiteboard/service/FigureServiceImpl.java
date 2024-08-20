package cds.team20.whiteboard.service;

import cds.team20.whiteboard.entity.Figure;
import cds.team20.whiteboard.repository.FigureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FigureServiceImpl implements FigureService{

    private final FigureRepository figureRepository;

    @Autowired
    public FigureServiceImpl(FigureRepository figureRepository) {
        this.figureRepository = figureRepository;
    }

    @Override
    public void createFigure(Figure figure) {
        figureRepository.save(figure);
        System.out.println(figure.getType()+" 생성 완료");
    }

    @Override
    public Figure findFigure(String id) {
        return figureRepository.findById(id);
    }

    @Override
    public void modifyFigure(String id) {
        //미구현
    }

    @Override
    public void deleteFigure(String id) {
        figureRepository.delete(figureRepository.findById(id));
    }
}
