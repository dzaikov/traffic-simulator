package ru.nsu.fit.traffic;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Scanner;
import org.junit.Assert;
import org.junit.Test;
import ru.nsu.fit.traffic.interfaces.network.Connection;
import ru.nsu.fit.traffic.network.ConnectionImpl;

public class NetworkTests {
//  @Test
//  public void testPutAndGet() throws FileNotFoundException, URISyntaxException {
//    Connection connection = new ConnectionImpl();
//
//    Path path = Path.of(NetworkTests.class.getResource("/connection/map_1.json").toURI());
//    String val = new Scanner(path.toFile()).next();
//
//    connection.pushMap(999, path.toString(), );
//    Assert.assertEquals(val, new Scanner(Path.of(connection.getMapFromServer(999)).toFile())
//      .next());
//  }
//
//  @Test
//  public void testPutAndGetGlobal() throws FileNotFoundException, URISyntaxException {
//    Connection connection = new ConnectionImpl();
//
//    Path path = Path.of(NetworkTests.class.getResource("/connection/global.json").toURI());
//    String val = new Scanner(path.toFile()).next();
//
//    connection.pushGlobalMap(path.toString());
//    Assert.assertEquals(val, new Scanner(Path.of(connection.getGlobalMapFromServer()).toFile())
//      .next());
//  }

  @Test
  public void getRooms() {
    Connection connection = new ConnectionImpl("http://localhost:8080/");
    System.out.println(connection.getRooms());
  }

  @Test
  public void createRoom() {
    Connection connection = new ConnectionImpl("http://localhost:8080/");
    try {
      Path path = Path.of(NetworkTests.class.getResource("/connection/map_1.json").toURI());
      System.out.println(connection.createRoom(path.toAbsolutePath().toString()));
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

  }


}
