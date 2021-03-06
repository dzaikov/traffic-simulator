package ru.nsu.fit.trafficProjectServer.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.fit.trafficProjectServer.configuration.MapConfiguration;
import ru.nsu.fit.trafficProjectServer.model.Map;
import ru.nsu.fit.trafficProjectServer.model.Room;
import ru.nsu.fit.trafficProjectServer.repository.MapRepository;
import ru.nsu.fit.trafficProjectServer.repository.RoomRepository;
import ru.nsu.fit.trafficProjectServer.security.model.User;
import ru.nsu.fit.trafficProjectServer.security.service.UserService;
import ru.nsu.fit.trafficProjectServer.service.MapService;

@Service
public class MapServiceDBImpl implements MapService {

  @Autowired
  private MapConfiguration configuration;

  @Autowired
  private MapRepository mapRepository;

  @Autowired
  private RoomRepository roomRepository;

  @Autowired
  private UserService userService;

  @Override
  public String storeFile(MultipartFile file, Long id, Long roomId) {
    Room room = roomRepository.getOne(roomId);

    Map map = mapRepository.findByRoomIdAndFollowUpNumber(roomId, id);
    if (map == null) {
      map = new Map();
    }
    enrichMap(map, file, room);
    if (
      id != null
        && map.getGrabbedByUser() != null
        && map.getGrabbedByUser().equals(userService.getCurrentUser())
    ) {
      userService.getCurrentUser().removeMap(map);
      userService.saveUser(userService.getCurrentUser());
      map.setGrabbedByUser(null);
      mapRepository.save(map);
      if (map.getFollowUpNumber() == null) {
        room.addMap(map);
        roomRepository.save(room);
      }
    } else if (
      id != null
        &&
        (
          map.getGrabbedByUser() == null
            || !map.getGrabbedByUser().equals(userService.getCurrentUser())
        )
    ) {
      return null;
    } else {
      map.setGrabbedByUser(null);
      mapRepository.save(map);
    }
    return file.getOriginalFilename();
  }

  private void enrichMap(Map map, MultipartFile file, Room room) {
    try {
      map.setFile(file.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
    map.setRoom(room);
    map.setFilename(file.getOriginalFilename());
  }

  @Override
  public List<Long> getRooms() {
    return roomRepository.findAll().stream()
      .map(Room::getId)
      .collect(Collectors.toList());
  }

  @Override
  public Long createRoom(MultipartFile file, String name) {
    User user = userService.getCurrentUser();
    Room room = new Room();
    room.setName(name);
    room.setAdminUser(user);
    user.addRoom(room);
    userService.saveUser(user);
    roomRepository.save(room);
    String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
    Map map = new Map();
    map.setFilename(fileName);
    try {
      map.setFile(file.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
    map.setRoom(room);
    mapRepository.save(map);
    room.setGlobalMap(map);
    return roomRepository.save(room).getId();
  }

  @Override
  public Map getMap(Long id, Long roomId, Boolean block) {
    Map map = mapRepository.findByRoomIdAndFollowUpNumber(roomId, id);
    if (block) {
      if (map.getGrabbedByUser() != null) {
        return null;
      }
      User user = userService.getCurrentUser();
      map.setGrabbedByUser(user);
      user.addMap(map);
      userService.saveUser(user);
      mapRepository.save(map);
    }
    return map;
  }

  @Override
  public Map getGlobalMapNew(Long roomId) {
    return roomRepository.findById(roomId)
      .map(Room::getGlobalMap)
      .orElse(null);
  }

  @Override
  public void dropBlock(Long roomId, Long mapId) {
    if (isCurrentUserAdminOfRoom(roomId)) {
      Map map = mapRepository.findByRoomIdAndFollowUpNumber(roomId, mapId);
      if (map.getGrabbedByUser() != null) {
        map.getGrabbedByUser().removeMap(map);
        userService.saveUser(map.getGrabbedByUser());
        map.setGrabbedByUser(null);
        mapRepository.save(map);
      }
    }
  }

  @Override
  @Nullable
  public List<Long> getBlocks(Long roomId) {
    return mapRepository.findAlLByRoomId(roomId).stream()
      .filter(map -> map.getGrabbedByUser() != null)
      .map(Map::getFollowUpNumber)
      .collect(Collectors.toList());
  }

  @Override
  public boolean admin(Long roomId) {
    return isCurrentUserAdminOfRoom(roomId);
  }

  private boolean isCurrentUserAdminOfRoom(Long roomId) {
    return roomRepository.getOne(roomId).getAdminUser().equals(userService.getCurrentUser());
  }
}

