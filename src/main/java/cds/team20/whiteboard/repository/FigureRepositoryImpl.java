package cds.team20.whiteboard.repository;

import cds.team20.whiteboard.entity.Figure;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class FigureRepositoryImpl implements FigureRepository{
    private Map<Integer, Figure> drawn = new HashMap<>();
    @Override
    public void save(Figure figure) {
        drawn.put(figure.getStartX(),figure);
        System.out.println(figure.getStartX()+" 등록");
    }

    @Override
    public Figure findById(Integer figureId) {
        return drawn.get(figureId);
    }

    @Override
    public void delete(Figure figure) {
        drawn.remove(figure.getStartX());
    }

    @Override
    public String[] figs(){
        int size = drawn.size();
        String[] tmp = new String[size];
        int i = 0;
        for (Map.Entry<Integer, Figure> entry : drawn.entrySet()) {
            tmp[i] = entry.getValue().toString();
            i++;
        }
        return tmp;
    }
}
