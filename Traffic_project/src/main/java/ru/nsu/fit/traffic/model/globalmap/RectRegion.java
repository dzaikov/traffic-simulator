package ru.nsu.fit.traffic.model.globalmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class RectRegion {
  private boolean dirty = true;
  private String name;
  private double x;
  private double y;
  private double width;
  private double height;
  private String regionMapLink = null;
  private List<RoadConnector> connectorList = new ArrayList<>();

  public RectRegion(String name, double x, double y, double width, double height) {
    this.name = name;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public RectRegion(double x, double y, double width, double height) {
    this.name = "";
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public void initConnectors(int size) {
    connectorList = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      connectorList.add(null);
    }
  }

  public void deleteConnector(RoadConnector connector) {
    connectorList.set(connectorList.indexOf(connector), null);
    dirty = true;
  }

  public RoadConnector getConnector(int id) {
    return connectorList.get(id);
  }

  public void addConnector(RoadConnector connector) {
    if (dirty) {
      for (int i = 0; i < getConnectorsCount(); i++) {
        if (connectorList.get(i) == null) {
          connectorList.set(i, connector);
          dirty = connectorList.stream().anyMatch(Objects::isNull);
          return;
        }
      }
    }
    connectorList.add(connector);
  }

  public void foreachConnector(Consumer<RoadConnector> consumer) {
    connectorList.forEach(consumer);
  }

  public int getConnectorsCount() {
    return connectorList.size();
  }

  public String getRegionMapLink() {
    return regionMapLink;
  }

  public void setRegionMapLink(String regionMapLink) {
    this.regionMapLink = regionMapLink;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }

  public double getWidth() {
    return width;
  }

  public void setWidth(double width) {
    this.width = width;
  }

  public double getHeight() {
    return height;
  }

  public void setHeight(double height) {
    this.height = height;
  }

  public List<RoadConnector> getConnectorList() {
    return connectorList;
  }

  public void setConnectorList(List<RoadConnector> connectorList) {
    this.connectorList = connectorList;
  }
}
