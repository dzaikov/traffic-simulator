package ru.nsu.fit.traffic.interfaces.control;

import ru.nsu.fit.traffic.model.logic.GlobalMapUpdateObserver;

public interface GlobalMapControlInitializerInterface {
  void initialize(GlobalMapUpdateObserver updateObserver);

  GlobalMapEditControlInterface getEditControl();

}
