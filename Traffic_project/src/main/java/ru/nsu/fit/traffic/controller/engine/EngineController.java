package ru.nsu.fit.traffic.controller.engine;

import ru.nsu.fit.traffic.controller.BaseControl;
import ru.nsu.fit.traffic.controller.SceneElementsControl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class EngineController extends BaseControl {

  private String heatMapPath;
  private String carStatePath;
  private String enginePath;
  private String mapPath;
  private Process engineProcess;
  private Thread thread;

  public EngineController(String enginePath, SceneElementsControl sceneElementsControl) {
    super(sceneElementsControl);
    this.enginePath = enginePath;
  }

  public String getMapPath() {
    return mapPath;
  }

  public void setMapPath(String mapPath) {
    this.mapPath = mapPath;
  }

  public String getHeatMapPath() {
    try {
      if (thread.isAlive()) {
        thread.join();
      }
    } catch (InterruptedException e) {
      assert false;
    }
    return heatMapPath;
  }

  public void setHeatMapPath(String heatMapPath) {
    this.heatMapPath = heatMapPath;
  }

  public String getCarStatePath() {
    try {
      if (thread.isAlive()) {
        thread.join();
      }
    } catch (InterruptedException e) {
      assert false;
    }
    return carStatePath;
  }

  public void setCarStatePath(String carStatePath) {
    this.carStatePath = carStatePath;
  }

  public void setEnginePath(String enginePath) {
    this.enginePath = enginePath;
  }

  public void startEngine() {
    thread = new Thread(() -> {
      try {
        engineProcess = Runtime.getRuntime().exec(
          "java -jar "
                  + '"'
                  + enginePath
                  + "\" \""
                  + mapPath
                  + "\" \""
                  + heatMapPath
                  + "\" \""
                  + carStatePath + '"');
        System.out.println("START");
        engineProcess.waitFor();
        //DEBUG
        Thread.sleep(5000);
        //DEBUG
        System.out.println("END");
        sceneElementsControl.simulationEndModeEnable();
      } catch (InterruptedException | IOException e) {
        System.err.println(e.getMessage());
        assert false;
      }
    });
    thread.start();
  }

  public void continueEngine() {
    sendCommand(EngineCommand.CONTINUE);
  }

  public void pauseEngine() {
    sendCommand(EngineCommand.PAUSE);
  }

  public void stopEngine() {
    sendCommand(EngineCommand.STOP);
  }

  private void sendCommand(EngineCommand command) {
    if (engineProcess != null && engineProcess.isAlive()) {
      try {
        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(engineProcess.getOutputStream()));
        w.write(command.toString() + "\n");
        w.flush();
      } catch (IOException e) {
        System.err.println(e.getMessage());
        assert false;
      }
    }
  }
}
