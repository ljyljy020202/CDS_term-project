package cds.team20.whiteboard.entity;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
@Getter
@Setter
public class Figure {
    //private String id;
    private Type type;
    private String lineWidth;
    private String strokeColor;
    private String fillColor;
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private String msg;

    @Override
    public String toString() {
        String tmp = type + " " + lineWidth + " " + strokeColor + " " + fillColor + " " +
                startX + " " + startY + " " + endX + " " + endY ;
        if(type == Type.text)
            tmp += " " + msg;
        return tmp;
    }
}