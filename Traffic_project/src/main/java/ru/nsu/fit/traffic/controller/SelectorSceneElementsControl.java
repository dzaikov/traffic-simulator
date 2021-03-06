package ru.nsu.fit.traffic.controller;

public interface SelectorSceneElementsControl {
  void loadFragmentScene(String path);
  void setConnectorIconVisible(boolean visible);
  void redrawConnectorIcon();
  void setCurrentOperation(String currentOperation);
}
