/*
 * This software Copyright by the RPTools.net development team, and
 * licensed under the Affero GPL Version 3 or, at your option, any later
 * version.
 *
 * MapTool Source Code is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU Affero General Public
 * License * along with this source Code.  If not, please visit
 * <http://www.gnu.org/licenses/> and specifically the Affero license
 * text at <http://www.gnu.org/licenses/agpl.html>.
 */
package net.rptools.maptool.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;
import net.rptools.maptool.util.PasswordGenerator;

public class ServerConfig {
  public static final int DEFAULT_PORT = 51234;

  public static final int PORT_RANGE_START = 4000;
  public static final int PORT_RANGE_END = 20000;

  private static final String personalServerGMPassword;

  private static final String personalServerPlayerPassword;

  static {
    PasswordGenerator passwordGenerator = new PasswordGenerator();
    // Generate a random password for personal server
    personalServerGMPassword = passwordGenerator.getPassword();
    String playerPass = passwordGenerator.getPassword();
    if (playerPass.equals(personalServerGMPassword)) { // super unlikely but just to play safe
      personalServerPlayerPassword = playerPass + "!";
    } else {
      personalServerPlayerPassword = playerPass;
    }
  }

  private int port;
  private String hostPlayerId;
  private final String gmPassword;
  private final String playerPassword;
  private boolean personalServer;
  private String serverName;
  private String hostName;
  private final boolean useEasyConnect;

  public static String getPersonalServerGMPassword() {
    return personalServerGMPassword;
  }

  public static String getPersonalServerPlayerPassword() {
    return personalServerPlayerPassword;
  }

  public ServerConfig() {
    playerPassword = getPersonalServerPlayerPassword();
    gmPassword = getPersonalServerGMPassword();
    useEasyConnect = false;
  }

  public ServerConfig(
      String hostPlayerId,
      String gmPassword,
      String playerPassword,
      int port,
      String serverName,
      String hostName) {
    this(hostPlayerId, gmPassword, playerPassword, port, serverName, hostName, false);
  }

  public ServerConfig(
      String hostPlayerId,
      String gmPassword,
      String playerPassword,
      int port,
      String serverName,
      String hostName,
      boolean useEasyConnect) {
    this.hostPlayerId = hostPlayerId;
    this.gmPassword = gmPassword;
    this.playerPassword = playerPassword;
    this.port = port;
    this.serverName = serverName;
    this.hostName = hostName;
    this.useEasyConnect = useEasyConnect;
  }

  public String getHostPlayerId() {
    return hostPlayerId;
  }

  public boolean isServerRegistered() {
    return serverName != null && serverName.length() > 0;
  }

  public String getServerName() {
    return serverName;
  }

  public boolean gmPasswordMatches(String password) {
    return safeCompare(gmPassword, password);
  }

  public boolean playerPasswordMatches(String password) {
    return safeCompare(playerPassword, password);
  }

  public boolean isPersonalServer() {
    return personalServer;
  }

  public int getPort() {
    return port;
  }

  public static ServerConfig createPersonalServerConfig() {
    ServerConfig config = new ServerConfig();
    config.serverName = "Personal server";
    config.hostName = "localhost";
    config.personalServer = true;
    config.port = findOpenPort(PORT_RANGE_START, PORT_RANGE_END);
    return config;
  }

  public String getGmPassword() {
    return gmPassword;
  }

  public String getPlayerPassword() {
    return playerPassword;
  }

  public String getHostName() {
    return hostName;
  }

  public boolean getUseEasyConnect() {
    return useEasyConnect;
  }

  private static Random r = new Random();

  private static int findOpenPort(int rangeLow, int rangeHigh) {
    // Presumably there will always be at least one open port between low and high
    while (true) {
      ServerSocket ss = null;
      try {
        int port = rangeLow + (int) ((rangeHigh - rangeLow) * r.nextFloat());
        ss = new ServerSocket(port);

        // This port was open before we took it, so we'll just close it and use this one
        // LATER: This isn't super exact, it's conceivable that another process will take
        // the port between our closing it and the server opening it. But that's the best
        // we can do at the moment until the server is refactored.
        return port;
      } catch (Exception e) {
        // Just keep trying
      } finally {
        if (ss != null) {
          try {
            ss.close();
          } catch (IOException ioe) {
            // No big deal
            ioe.printStackTrace();
          }
        }
      }
    }
  }

  private static boolean safeCompare(String s1, String s2) {
    if (s1 == null) {
      s1 = "";
    }
    if (s2 == null) {
      s2 = "";
    }

    return s1.equals(s2);
  }
}
