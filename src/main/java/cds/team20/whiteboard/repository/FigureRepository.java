package cds.team20.whiteboard.repository;

import cds.team20.whiteboard.entity.Figure;

public interface FigureRepository {
    void save(Figure figure);
    Figure findById(Integer figureId);
    void delete(Figure figure);
    String[] figs();
}
