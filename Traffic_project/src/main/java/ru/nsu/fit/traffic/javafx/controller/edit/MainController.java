package ru.nsu.fit.traffic.javafx.controller.edit;

import com.jfoenix.controls.JFXTimePicker;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ru.nsu.fit.traffic.App;
import ru.nsu.fit.traffic.config.ConnectionConfig;
import ru.nsu.fit.traffic.controller.FragmentEditControlsInitializer;
import ru.nsu.fit.traffic.controller.SceneElementsControl;
import ru.nsu.fit.traffic.controller.notification.NotificationType;
import ru.nsu.fit.traffic.event.wrappers.MouseEventWrapper;
import ru.nsu.fit.traffic.interfaces.control.ControlInitializerInterface;
import ru.nsu.fit.traffic.interfaces.control.EditControlInterface;
import ru.nsu.fit.traffic.interfaces.control.MenuControlInterface;
import ru.nsu.fit.traffic.interfaces.network.Connection;
import ru.nsu.fit.traffic.javafx.controller.menubar.MenuBarController;
import ru.nsu.fit.traffic.javafx.controller.notification.NotificationController;
import ru.nsu.fit.traffic.javafx.controller.settings.BuildingController;
import ru.nsu.fit.traffic.javafx.controller.settings.NodeSettingsController;
import ru.nsu.fit.traffic.javafx.controller.settings.RoadSettingsController;
import ru.nsu.fit.traffic.javafx.controller.settings.TrafficLightController;
import ru.nsu.fit.traffic.javafx.controller.statistic.StatisticsController;
import ru.nsu.fit.traffic.javafx.paiters.UiPainter;
import ru.nsu.fit.traffic.view.MapEditorViewUpdater;

/**
 * Контроллер основной сцены, на которой располагаются все остальные.
 */
public class MainController {
  @FXML
  private JFXTimePicker simTimePicker;
  @FXML
  private Pane statistics;
  @FXML
  private ScrollPane mainScrollPane;
  @FXML
  private Pane mainPane;
  @FXML
  private AnchorPane basePane;
  @FXML
  private Pane numberOfLanesPane;
  @FXML
  private Pane roadSignPane;
  @FXML
  private TextField backLanesTextField;
  @FXML
  private TextField forwardLanesTextField;
  @FXML
  private ComboBox<Integer> speedComboBox;
  @FXML
  private Group scrollPaneContent;
  @FXML
  private VBox centeredField;
  @FXML
  private BuildingController buildingSettingsController;
  @FXML
  private TrafficLightController trafficLightController;
  @FXML
  private MenuBarController menuBarController;
  @FXML
  private RoadSettingsController roadSettingsController;
  @FXML
  private StatisticsController statisticsController;
  @FXML
  private NodeSettingsController nodeSettingsController;
  @FXML
  private NotificationController notificationController;
  @FXML
  private Slider timeLineReportSlider;
  @FXML
  private Slider timeLinePlaybackSlider;
  @FXML
  private Button simulationStartButton;
  @FXML
  private Button simulationStopButton;
  @FXML
  private Button buttonBack;
  @FXML
  private Button buttonForward;
  @FXML
  private Button playbackButton;
  @FXML
  private Button reportButton;
  @FXML
  private Button editButton;
  @FXML
  private AnchorPane editButtonsPane;
  @FXML
  private HBox progressIndicator;
  @FXML
  private Button goBackButton;
  @FXML
  private Button saveButton;

  private MenuControlInterface saveLoadControl;
  private Stage stage;
  private EditControlInterface editControl;
  private boolean timePickerFlag = false;
  private double scaleValue = 1;
  private Rectangle selectRect;
  private boolean drawSelectRect = false;
  private final SceneElementsControl sceneElementsControl =
      new SceneElementsControl() {

    @Override
    public void setSelectRectVisible(boolean visible) {
      drawSelectRect = visible;
    }

    @Override
    public void buildingSettingsSetVisible(boolean status) {
      buildingSettingsController.getPane().setVisible(status);
    }

    @Override
    public void buildingSettingsSetPos(double x, double y) {
      buildingSettingsController.getPane().setLayoutX(x);
      buildingSettingsController.getPane().setLayoutY(y);
    }

    @Override
    public void buildingSettingsSetParams(double weight, int parkingPlaces) {
      buildingSettingsController.getSlider().setValue(weight);
      buildingSettingsController.getParkingPlaces().setText
          (String.valueOf(parkingPlaces));
    }

    @Override
    public void roadSettingsSetVisible(boolean status) {
      roadSettingsController.getRoadSettingsPane().setVisible(status);
    }

    @Override
    public void roadSettingsSetPos(double x, double y) {
      roadSettingsController.getRoadSettingsHelperPane().setLayoutX(x);
      roadSettingsController.getRoadSettingsHelperPane().setLayoutY(y);
    }

    @Override
    public void roadSettingsSetParams(int lanesNum, String street) {
      roadSettingsController.getLanesTextField().setText(String.valueOf(lanesNum));
      roadSettingsController.getStreetName().setText(street);
    }

    @Override
    public void roadSignMenuSetVisible(boolean status) {
      roadSignPane.setVisible(status);
    }

    @Override
    public void nodeSettingsSetVisible(boolean status) {
      nodeSettingsController.getNodeSettingPane().setVisible(status);
    }

    @Override
    public void nodeSettingsSetPos(double x, double y) {
      nodeSettingsController.getNodeSettingPane().setLayoutX(x);
      nodeSettingsController.getNodeSettingPane().setLayoutY(y);
    }

    @Override
    public void nodeSettingsSetParams(LocalTime start, LocalTime end, int spawnRate) {
      nodeSettingsController.getStartTime().setValue(start);
      nodeSettingsController.getEndTime().setValue(end);
      nodeSettingsController.getSpawnerRate().setText(String.valueOf(spawnRate));
    }

    @Override
    public void numberOfLanesPaneSetVisible(boolean status) {
      numberOfLanesPane.setVisible(status);
    }

    @Override
    public void trafficLightSettingsSetVisible(boolean status) {
      trafficLightController.getTrafficLightPane().setVisible(status);
    }

    @Override
    public void updateStatistic(int carSpawnersCnt, int streetsCnt, int roadsCnt, int buildingCnt, int connectivity, List<String> streets) {
      statisticsController.getCarSpawners().setText(String.valueOf(carSpawnersCnt));
      statisticsController.getStreets().setText(String.valueOf(streetsCnt));
      statisticsController.getRoads().setText(String.valueOf(roadsCnt));
      statisticsController.getBuildings().setText(String.valueOf(buildingCnt));
      statisticsController.getConnectivity().setText(String.valueOf(connectivity));
      streets.forEach(s -> {
        Text text = new Text(s);
        text.setFill(Paint.valueOf("#ffffff"));
        statisticsController.getStreetsView().getChildren().add(text);
      });
    }

    @Override
    public void trafficLightSettingsSetPos(double x, double y) {
      trafficLightController.getTrafficLightPane().setLayoutX(x);
      trafficLightController.getTrafficLightPane().setLayoutY(y);
    }

    @Override
    public void trafficLightSettingsSetParams(int greenDelay, int redDelay) {
      trafficLightController.getGreenDelay().setText(String.valueOf(greenDelay));
      trafficLightController.getRedDelay().setText(String.valueOf(redDelay));
    }

    @Override
    public void statisticSwitchVisible() {
      statistics.setVisible(!statistics.isVisible());
    }

    @Override
    public void showNotification(String title, String text, NotificationType notificationType) {
      notificationController.showNotification(title, text, notificationType);
    }

    @Override
    public void timeLineReportSliderInit(int windowsListSize, Function<Integer, Long> endGetter) {
      //timeLineSlider.setMax(Math.max(reportStruct.getWindowList().size() - 1, 0));
      timeLineReportSlider.setMax(Math.max(windowsListSize - 1, 0));
      timeLineReportSlider.setLabelFormatter(new StringConverter<>() {
        @Override
        public String toString(Double object) {
          int id = (int) Math.round(object);
          if (id < windowsListSize) {
            return String.format("%02d:%02d",
                TimeUnit.SECONDS.toHours(endGetter.apply(id)),
                TimeUnit.SECONDS.toMinutes(endGetter.apply(id)) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(endGetter.apply(id))));
          }
          return "";
        }

        @Override
        public Double fromString(String string) {
          return null;
        }
      });
    }

    @Override
    public void timeLineReportSliderSetMax(double max) {
      timeLineReportSlider.setMax(max);
    }

    @Override
    public void playbackSliderInit(double maxVal, double minVal) {
      timeLinePlaybackSlider.setMax(maxVal);
      timeLinePlaybackSlider.setMin(minVal);
      if (timeLinePlaybackSlider.getMajorTickUnit() > maxVal - minVal) {
        timeLinePlaybackSlider.setMajorTickUnit(maxVal - minVal - 1);
      }
      timeLinePlaybackSlider.setLabelFormatter(new StringConverter<>() {
        @Override
        public String toString(Double object) {
          int id = (int) Math.round(object);
          if (id < maxVal) {
            long minUnit = TimeUnit.SECONDS.toMinutes(id);
            return String.format("%02d:%02d:%02d",
                TimeUnit.SECONDS.toHours(id),
                TimeUnit.SECONDS.toMinutes(id) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(id)),
                id - TimeUnit.MINUTES.toSeconds(minUnit));
          }
          return "";
        }

        @Override
        public Double fromString(String string) {
          return null;
        }
      });
    }

    @Override
    public void playBackSliderSetMax(double max) {
      timeLinePlaybackSlider.setMax(max);
    }

    @Override
    public void playBackSliderSetMin(double min) {
      timeLinePlaybackSlider.setMin(min);
    }

    @Override
    public void playbackSliderAddValue(double val) {
      if (timeLinePlaybackSlider.getValue() + val < timeLinePlaybackSlider.getMin()) {
        timeLinePlaybackSlider.setValue(timeLinePlaybackSlider.getMin());
      } else
        timeLinePlaybackSlider.setValue(Math.min(timeLinePlaybackSlider.getValue() + val, timeLinePlaybackSlider.getMax()));
    }

    @Override
    public void timeLineReportSliderAddValue(double val) {
      if (timeLineReportSlider.getValue() + val < timeLineReportSlider.getMin()) {
        timeLineReportSlider.setValue(timeLineReportSlider.getMin());
      } else
        timeLineReportSlider.setValue(Math.min(timeLineReportSlider.getValue() + val, timeLineReportSlider.getMax()));
    }

    @Override
    public void simulationProcessModeEnable() {
      simulationStopButton.setDisable(false);
      simulationStartButton.setDisable(true);
      editButton.setDisable(true);
      playbackButton.setDisable(true);
      reportButton.setDisable(true);
      buttonForward.setDisable(true);
      buttonBack.setDisable(true);
      mainScrollPane.setDisable(true);
      timeLinePlaybackSlider.setDisable(true);
      timeLineReportSlider.setDisable(true);
      timeLinePlaybackSlider.setVisible(false);
      timeLineReportSlider.setVisible(false);
      editButtonsPane.setDisable(true);
      progressIndicator.setVisible(true);
    }

        @Override
        public void optimizationProcessModeEnabled() {
          //simulationStopButton.setDisable(false);
          simulationStartButton.setDisable(true);
          editButton.setDisable(true);
          playbackButton.setDisable(true);
          reportButton.setDisable(true);
          buttonForward.setDisable(true);
          buttonBack.setDisable(true);
          mainScrollPane.setDisable(true);
          timeLinePlaybackSlider.setDisable(true);
          timeLineReportSlider.setDisable(true);
          timeLinePlaybackSlider.setVisible(false);
          timeLineReportSlider.setVisible(false);
          editButtonsPane.setDisable(true);
          progressIndicator.setVisible(true);
        }

        @Override
    public void simulationEndModeEnable() {
      simulationStopButton.setDisable(true);
      simulationStartButton.setDisable(false);
      editButton.setDisable(false);
      playbackButton.setDisable(false);
      reportButton.setDisable(false);
      buttonForward.setDisable(true);
      buttonBack.setDisable(true);
      mainScrollPane.setDisable(false);
      timeLinePlaybackSlider.setDisable(true);
      timeLineReportSlider.setDisable(true);
      timeLinePlaybackSlider.setVisible(false);
      timeLineReportSlider.setVisible(false);
      editButtonsPane.setDisable(true);
      progressIndicator.setVisible(false);
    }

    @Override
    public void simulationStopModeEnable() {
      simulationStopButton.setDisable(true);
      simulationStartButton.setDisable(false);
      editButton.setDisable(false);
      playbackButton.setDisable(true);
      reportButton.setDisable(true);
      buttonForward.setDisable(true);
      buttonBack.setDisable(true);
      mainScrollPane.setDisable(true);
      timeLinePlaybackSlider.setDisable(true);
      timeLineReportSlider.setDisable(true);
      timeLinePlaybackSlider.setVisible(false);
      timeLineReportSlider.setVisible(false);
      editButtonsPane.setDisable(true);
      progressIndicator.setVisible(true);
    }

    @Override
    public void editModeEnable() {
      simulationStopButton.setDisable(true);
      simulationStartButton.setDisable(false);
      editButton.setDisable(true);
      playbackButton.setDisable(true);
      reportButton.setDisable(true);
      buttonForward.setDisable(true);
      buttonBack.setDisable(true);
      mainScrollPane.setDisable(false);
      timeLinePlaybackSlider.setDisable(true);
      timeLineReportSlider.setDisable(true);
      timeLinePlaybackSlider.setVisible(false);
      timeLineReportSlider.setVisible(false);
      editButtonsPane.setDisable(false);
      progressIndicator.setVisible(false);
    }

        @Override
        public void optimizingModeEnabled(String path) {
          saveLoadControl.onOpenProject(new File(path));
          editModeEnable();
        }

        @Override
    public void reportModeEnable() {
      simulationStopButton.setDisable(true);
      simulationStartButton.setDisable(false);
      editButton.setDisable(false);
      playbackButton.setDisable(false);
      reportButton.setDisable(true);
      buttonForward.setDisable(false);
      buttonBack.setDisable(false);
      mainScrollPane.setDisable(false);
      timeLinePlaybackSlider.setDisable(true);
      timeLineReportSlider.setDisable(false);
      timeLinePlaybackSlider.setVisible(false);
      timeLineReportSlider.setVisible(true);
      editButtonsPane.setDisable(true);
      progressIndicator.setVisible(false);
    }

    @Override
    public void playBackModeEnable() {
      simulationStopButton.setDisable(true);
      simulationStartButton.setDisable(false);
      editButton.setDisable(false);
      playbackButton.setDisable(true);
      reportButton.setDisable(false);
      buttonForward.setDisable(false);
      buttonBack.setDisable(false);
      mainScrollPane.setDisable(false);
      timeLinePlaybackSlider.setDisable(false);
      timeLineReportSlider.setDisable(true);
      timeLinePlaybackSlider.setVisible(true);
      timeLineReportSlider.setVisible(false);
      editButtonsPane.setDisable(true);
      progressIndicator.setVisible(false);
    }
  };

  public void removeSelectRect() {
    UiPainter.removeSelectRect(selectRect, mainPane);
  }

  public void addSelectRect(double x, double y) {
    UiPainter.addSelectRect(x, y, selectRect, mainPane);
  }

  public void resizeSelectRect(double x, double y) {
    UiPainter.resizeSelectRect(x, y, selectRect);
  }

  public Stage getStage() {
    return stage;
  }

  public void setStage(Stage stage) {
    this.stage = stage;
    menuBarController.setStage(stage);
  }

  public void initMap(String mapPath) {
    saveLoadControl.onOpenProject(new File(mapPath));

    mainPane.setPrefWidth(editControl.getMapWidth());
    mainPane.setMaxWidth(editControl.getMapWidth());
    mainPane.setPrefHeight(editControl.getMapHeight());
    mainPane.setMaxHeight(editControl.getMapHeight());
  }

  /**
   * инициализация.
   */
  @FXML
  public void initialize() {
    ControlInitializerInterface controlsInitializer = new FragmentEditControlsInitializer(sceneElementsControl);
    editControl = controlsInitializer.getEditControl();
    mainScrollPane.setHvalue(0.5);
    mainScrollPane.setVvalue(0.5);

    MapEditorViewUpdater viewUpdater = new MapEditorViewUpdater(
        (shape, placeOfInterestId) ->
            shape.setOnMouseClicked(event ->
                editControl
                    .onPoiClicked(placeOfInterestId, MouseEventWrapper.getMouseEventWrapper(event))),
        (shape, road, i) ->
            shape.setOnMouseClicked(event ->
                editControl
                    .onRoadClick(road, i, MouseEventWrapper.getMouseEventWrapper(event))),
        (shape, node) ->
            shape.setOnMouseClicked(event ->
                editControl
                    .onNodeClick(node, MouseEventWrapper.getMouseEventWrapper(event))),
        mainPane
    );
    controlsInitializer.initialize(viewUpdater::updateMapView);
    //menuBarController.setMap(currMap);

    //System.out.println(stage);
    selectRect = UiPainter.getSelectRect();
    trafficLightController.setTrafficLightSettingsControl(
        controlsInitializer.getTrafficLightSettingsControl()
    );
    roadSettingsController.setRoadSettingsControl(
        controlsInitializer.getRoadSettingsControl()
    );
    nodeSettingsController.setNodeSettingsControl(
        controlsInitializer.getNodeSettingsControl()
    );
    buildingSettingsController.setBuildingSettingsControl(
        controlsInitializer.getBuildingSettingsControl()
    );
    menuBarController.setSaveLoadControl(
        controlsInitializer.getSaveLoadControl()
    );

    statistics.setVisible(false);
    numberOfLanesPane.setVisible(false);
    buildingSettingsController.getPane().setVisible(false);
    roadSettingsController.getRoadSettingsPane().setVisible(false);
    trafficLightController.getTrafficLightPane().setVisible(false);
    nodeSettingsController.getNodeSettingPane().setVisible(false);
    roadSignPane.setVisible(false);
    speedComboBox.setItems(FXCollections.observableArrayList(20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120));
    speedComboBox.setValue(60);

    mainScrollPane.setPannable(false);

    timeLineReportSlider.setMin(0);

    timeLineReportSlider.valueProperty().addListener((observable, oldValue, newValue) ->
        editControl.onTimeLineSliderChange(newValue));

    timeLinePlaybackSlider.valueProperty().addListener((observable, oldValue, newValue) ->
        editControl.onPlaybackLineSliderChange(newValue));


    mainScrollPane.setOnMousePressed(event -> {
      if (event.getButton() == MouseButton.MIDDLE) mainScrollPane.setPannable(true);
    });
    mainScrollPane.setOnMouseReleased(event -> {
      if (event.getButton() == MouseButton.MIDDLE) mainScrollPane.setPannable(false);
    });

    centeredField.setOnScroll(event -> {
      zoom(event);
      event.consume();
    });

    UnaryOperator<TextFormatter.Change> integerFilter = change -> {
      String input = change.getText();
      int text_size = change.getControlNewText().length();

      if (input.matches("[0-5]*") && text_size <= 1) {
        return change;
      }
      return null;
    };

    backLanesTextField.setTextFormatter(new TextFormatter<String>(integerFilter));
    backLanesTextField.setText("1");
    backLanesTextField.textProperty().addListener((observable, oldValue, newValue) ->
        editControl.onRightLanesTextFieldChange(newValue));

    roadSettingsController.getLanesTextField().setTextFormatter(new TextFormatter<String>(integerFilter));
    roadSettingsController.getLanesTextField().setText("1");

    forwardLanesTextField.setTextFormatter(new TextFormatter<String>(integerFilter));
    forwardLanesTextField.setText("1");
    forwardLanesTextField.textProperty().addListener((observable, oldValue, newValue) ->
        editControl.onLeftLanesTextFieldChange(newValue));

    mainPane.setOnMousePressed(event -> {
      editControl.onMainPanePressed(MouseEventWrapper.getMouseEventWrapper(event));
      if (drawSelectRect && event.getButton() == MouseButton.PRIMARY) {
        addSelectRect(event.getX(), event.getY());
      }
    });
    mainPane.setOnMouseClicked(event ->
        editControl.onMainPaneClicked(MouseEventWrapper.getMouseEventWrapper(event)));
    mainPane.setOnMouseDragged(event -> {
      editControl.onMainPaneDrag(MouseEventWrapper.getMouseEventWrapper(event));
      if (drawSelectRect && event.getButton() == MouseButton.PRIMARY) {
        resizeSelectRect(event.getX(), event.getY());
      }
    });
    mainPane.setOnMouseReleased(event -> {
      editControl.onMainPaneReleased(MouseEventWrapper.getMouseEventWrapper(event));
      removeSelectRect();
    });

    basePane.addEventFilter(MouseEvent.MOUSE_CLICKED, event ->
        editControl.onBasePaneClickedFilter(
            MouseEventWrapper.getMouseEventWrapper(event),
            basePane.getWidth(),
            basePane.getHeight(),
            100, 100
        ));
    saveLoadControl = controlsInitializer.getSaveLoadControl();
  }

  @FXML
  public void startSimulation() {
    editControl.startSimulation();
  }

  @FXML
  public void stopSimulation() {
    editControl.stopSimulation();
  }

  @FXML
  public void rewindForward() {
    editControl.rewindForward();
  }

  @FXML
  public void rewindBack() {
    editControl.rewindBack();
  }

  @FXML
  public void roadButtonClicked() {
    editControl.roadButtonClicked();
  }

  @FXML
  public void trafficLightButtonClicked() {
    editControl.trafficLightButtonClicked();
  }

  @FXML
  public void buildingButtonClicked() {
    editControl.buildingButtonClicked();
    drawSelectRect = true;
  }

  @FXML
  public void playbackButtonClicked() {
    editControl.playbackClicked();
  }

  @FXML
  public void reportButtonClicked() {
    editControl.reportClicked();
  }

  @FXML
  public void simTimePickerEnterPressed(KeyEvent key) {
    if (!timePickerFlag) {
      if (key.getCode() == KeyCode.ENTER) {
        String time =
            simTimePicker.getValue() != null
                ? simTimePicker.getValue().toString()
                : LocalTime.now().toString();
        System.out.println(time);
        editControl.startTimePicker(time);
        sceneElementsControl.showNotification("Simulation time",
            "Now set time of simulation\nfinish and press Enter",
            NotificationType.INFORMATION);
        timePickerFlag = true;
      }
    }
    else{
      timePickerFlag = false;
      simTimePicker.set24HourView(false);
      sceneElementsControl.showNotification("Simulation time",
          "Simulation time set",
          NotificationType.INFORMATION);
    }
  }

  @FXML
  public void simTimePickerClicked() {
    simTimePicker.set24HourView(true);
    sceneElementsControl.showNotification("Simulation time",
        "Set time of simulation\nstart and press Enter",
        NotificationType.INFORMATION);
  }

  @FXML
  public void editButtonClicked() {
    editControl.editClicked();
  }

  @FXML
  public void roadSignButtonClicked() {
    editControl.roadSignButtonClicked();
  }

  @FXML
  public void showStatistic() {
    editControl.showStatistic();
  }

  @FXML
  public void setSpeedSign() {
    editControl.setSpeedSign(speedComboBox.getValue());
  }

  @FXML
  public void saveMap() {
    saveLoadControl.onSave();
    if (editControl.saveMap()) {
      Connection connection = ConnectionConfig.getConnectionConfig().getConnection();
      FXMLLoader loader = new FXMLLoader(App.class.getResource("view/GlobalMapSelectorView.fxml"));
      try {
        Parent root = loader.load();
        Scene scene = new Scene(root);

        stage.setTitle("Traffic simulator");
        stage.setScene(scene);
        GlobalSelectorController controller = loader.getController();
        controller.setStage(stage);
        controller.setMap(
            connection.getGlobalMapFromServer(ConnectionConfig.getConnectionConfig().getRoomId()));
      } catch (IOException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }
      stage.show();
    } else {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("Connection error");
        errorAlert.setContentText("You has no access to edit this map.");
        errorAlert.showAndWait();

        FXMLLoader loader = new FXMLLoader(App.class.getResource("view/GlobalMapSelectorView.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Connection connection = ConnectionConfig.getConnectionConfig().getConnection();
            stage.setTitle("Traffic simulator");
            stage.setScene(scene);
            GlobalSelectorController controller = loader.getController();
            controller.setStage(stage);
            controller.setMap(
                    connection.getGlobalMapFromServer(ConnectionConfig.getConnectionConfig().getRoomId()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        stage.show();
    }
  }

  @FXML
  public void setMainRoad() {
    editControl.setMainRoad();
  }

  private void zoom(ScrollEvent event) {
    double zoomFactor = Math.exp(event.getDeltaY() * 0.001);

    Bounds innerBounds = scrollPaneContent.getLayoutBounds();
    Bounds viewportBounds = mainScrollPane.getViewportBounds();

    double valX = mainScrollPane.getHvalue() * (innerBounds.getWidth() - viewportBounds.getWidth());
    double valY = mainScrollPane.getVvalue() * (innerBounds.getHeight() - viewportBounds.getHeight());

    scaleValue = scaleValue * zoomFactor;
    mainPane.setScaleX(scaleValue);
    mainPane.setScaleY(scaleValue);
    mainScrollPane.layout();

    Point2D posInZoomTarget = mainPane
        .parentToLocal(scrollPaneContent
            .parentToLocal(new Point2D(
                event.getX(),
                event.getY())));

    Point2D adjustment = mainPane.getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));

    Bounds updatedInnerBounds = scrollPaneContent.getBoundsInLocal();
    mainScrollPane.setHvalue((valX + adjustment.getX()) / (updatedInnerBounds.getWidth() - viewportBounds.getWidth()));
    mainScrollPane.setVvalue((valY + adjustment.getY()) / (updatedInnerBounds.getHeight() - viewportBounds.getHeight()));
  }

  @FXML
  public void startOptimization(){
    editControl.startOptimization();
    //todo send
  }

  @FXML
  public void goBack(){
    FXMLLoader loader = new FXMLLoader(App.class.getResource("view/GlobalMapSelectorView.fxml"));
    try {
      Parent root = loader.load();
      Scene scene = new Scene(root);
        stage.setScene(scene);
    } catch (Exception e) {
      e.printStackTrace();
    }

    stage.show();
    GlobalSelectorController controller = loader.getController();
    controller.setStage(stage);
    ConnectionConfig connectionConfig = ConnectionConfig.getConnectionConfig();
    Connection connection = connectionConfig.getConnection();
    controller.setMap(connection.getGlobalMapFromServer(connectionConfig.getRoomId()));
  }

  public void showGoBack() {
    goBackButton.setVisible(true);
    saveButton.setVisible(false);
  }

  public void showSaveMap() {
    goBackButton.setVisible(false);
    saveButton.setVisible(true);
  }
}