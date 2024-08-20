package cds.team20.whiteboard.entity;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
@Getter
@Setter
public class Figure {
    private String id;
    private Type type;
    private String lineWidth;
    private String strokeColor;
    private String fillColor;
    private Point startPoint;
    private Point endPoint;
    private String msg;
}