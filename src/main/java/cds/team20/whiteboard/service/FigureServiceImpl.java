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
    }

    @Override
    public Figure findFigure(Integer id) {
        return figureRepository.findById(id);
    }

    @Override
    public void modifyFigure(Integer id) {
        //미구현
    }

    @Override
    public void deleteFigure(Integer id) {
        figureRepository.delete(figureRepository.findById(id));
    }

    public String[] getAll(){
        return figureRepository.figs();
    }
}
