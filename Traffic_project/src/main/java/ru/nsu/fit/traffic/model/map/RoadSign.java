package ru.nsu.fit.traffic.model.map;

import java.util.Map;

public interface RoadSign {
  public SignType getSignType();

  public Map<String, String> getSettings();

  public void setSettings(Map<String, String> settings);

  public RoadSign getCopySign();
}
