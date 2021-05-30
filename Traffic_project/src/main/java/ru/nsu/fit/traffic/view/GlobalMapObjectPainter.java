package ru.nsu.fit.traffic.view;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import ru.nsu.fit.traffic.model.globalmap.RectRegion;
import ru.nsu.fit.traffic.model.globalmap.RoadConnector;


public class GlobalMapObjectPainter {

  public Rectangle paintRegion(RectRegion region) {
    Rectangle rect = new Rectangle();
    rect.setStroke(Color.valueOf("#707070"));
    rect.setHeight(region.getHeight());
    rect.setWidth(region.getWidth());
    rect.setX(region.getX());
    rect.setY(region.getY());
    rect.setStrokeWidth(5);
    rect.setFill(Color.valueOf("#808080"));
    return rect;
  }

  public Circle paintConnector(RoadConnector connector, boolean isSet) {
    Circle circle = new Circle();
    if (isSet) {
      circle.setRadius(10);
    }
    else{
      circle.setRadius(8);
    }
    double x = connector.getGlobalX();
    double y = connector.getGlobalY();
    if (isSet) {
      if (Math.abs(connector.getX()) < 0.001) {
        x += 8;
        circle.setRotate(-90);
      } else if (Math.abs(connector.getY()) < 0.001) {
        y += 8;
      } else if (Math.abs(connector.getX() - connector.getRegion().getWidth()) < 0.001) {
        x -= 8;
        circle.setRotate(90);
      } else if (Math.abs(connector.getY() - connector.getRegion().getHeight()) < 0.001) {
        y -= 8;
        circle.setRotate(180);
      }
    }
    circle.setCenterX(x);
    circle.setCenterY(y);
    Image img;
    if (isSet) {
      img = new Image("ru/nsu/fit/traffic/view/Images/connector_on_map.png");
    }else{
      img = new Image("ru/nsu/fit/traffic/view/Images/pre_road_connector.png");
    }
    if (img != null) {
      circle.setFill(new ImagePattern(img));
    } else {
      circle.setFill(Paint.valueOf("#101010"));
    }
    return circle;
  }
}
