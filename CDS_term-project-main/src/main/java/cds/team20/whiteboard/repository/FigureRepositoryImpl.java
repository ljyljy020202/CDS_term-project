package cds.team20.whiteboard.repository;

import cds.team20.whiteboard.entity.Figure;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
@Repository
public class FigureRepositoryImpl implements FigureRepository{
    private static Map<String, Figure> drawn = new HashMap<>();
    @Override
    public void save(Figure figure) {
        drawn.put(figure.getId(),figure);
    }

    @Override
    public Figure findById(String figureId) {
        return drawn.get(figureId);
    }

    @Override
    public void delete(Figure figure) {
        drawn.remove(figure.getId());
    }
}
