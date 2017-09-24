package community.opencode.minetools4j;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import community.opencode.minetools4j.util.http.HttpRequest;
import community.opencode.minetools4j.util.http.HttpRequestMethod;
import community.opencode.minetools4j.util.serverping.Players;
import community.opencode.minetools4j.util.serverping.Sample;
import community.opencode.minetools4j.util.serverping.ServerPing;
import community.opencode.minetools4j.util.serverping.Version;
import lombok.Getter;
import lombok.NonNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Getter
public class MineTools4J {

    public static final String API_URI = "https://api.minetools.eu";
    public static final Gson GSON = new Gson();

    public static void main(String[] args) {

        if (args.length == 0 || !args[0].equalsIgnoreCase("test")) {
            System.out.println("[MineTools4J] This is an API and can't run standalone. If you want to test the API, please add the argument 'test'.");
            System.out.println("[MineTools4J] The process will exit now...");
            System.exit(0);
        } else {

            MineTools4J mineTools4J = new MineTools4J();

            System.out.println("[MineTools4J] Found the 'test' argument... the test mode will be activated...");
            System.out.println("");
            System.out.println("[MineTools4J] The ping test will be started...");

            if (mineTools4J.pingAPI()) {
                System.out.println("[MineTools4J] The ping test was sucessfull.");
                System.out.println("");
                System.out.println("[MineTools4J] The ready test will be started...");

                if (mineTools4J.isAPIReady()) {
                    System.out.println("[MineTools4J] The ready test was sucessfull.");
                    System.out.println("");
                    System.out.println("[MineTools4J] The API works correctly. You can use it at the moment.");

                    System.out.println(mineTools4J.getPlayerName(UUID.fromString("39f79b73-992e-43f3-8950-ae005bd3f718")));
                    System.out.println(mineTools4J.getPlayerName("39f79b73-992e-43f3-8950-ae005bd3f718"));
                } else {
                    System.out.println("[MineTools4J] The ready test was not sucessfull.");
                    System.out.println("");
                    System.out.println("[MineTools4J] The API do not work correctly. Please try again later.");
                }

            } else {
                System.out.println("[MineTools4J] The ping test was not sucessfull.");
                System.out.println("");
                System.out.println("[MineTools4J] The API do not work correctly. Please try again later.");
            }

            System.out.println("");
            System.out.println("[MineTools4J] The process will exit now...");
            System.exit(0);

        }

    }

    public boolean pingAPI() {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress("api.minetools.eu", 443), 10000);
            socket.close();
            return true;
        } catch (IOException exception) {
            try {
                socket.close();
            } catch (IOException exception2) {
                exception2.printStackTrace();
            }
            return false;
        }
    }

    public boolean isAPIReady() {
        try {
            HttpRequest.RequestResponse requestResponse = HttpRequest.performRequest(new HttpRequest.RequestBuilder(API_URI, HttpRequestMethod.GET));

            if (requestResponse.getStatus() == 200) {
                return true;
            }

        } catch (IOException exception) {
            return false;
        }
        return false;
    }

    public ServerPing getServerPing(@NonNull final String host, @NonNull final int port) {

        try {
            HttpRequest.RequestResponse requestResponse = HttpRequest.performRequest(new HttpRequest.RequestBuilder(API_URI + "/ping/" + host + "/" + port, HttpRequestMethod.GET));

            JsonObject jsonObject = GSON.fromJson(requestResponse.getResultMessage(), JsonObject.class);

            if (jsonObject.has("error")) {
                System.out.println("[MineTools4J] Could not get server ping from '" + host + "' on port '" + port + "': " + jsonObject.get("error").getAsString());
                return null;
            }

            BufferedImage favicon = null;
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(jsonObject.get("favicon").getAsString().replaceAll(jsonObject.get("favicon").getAsString().split(",")[0] + ",", "")));
            favicon = ImageIO.read(byteArrayInputStream);
            byteArrayInputStream.close();

            JsonObject players = jsonObject.get("players").getAsJsonObject();
            List<Sample> sample = new ArrayList<>();

            players.get("sample").getAsJsonArray().forEach(jsonElement -> {
                JsonObject sampleObject = jsonElement.getAsJsonObject();
                sample.add(new Sample(
                        UUID.fromString(sampleObject.get("id").getAsString()),
                        sampleObject.get("name").getAsString()
                ));
            });

            JsonObject version = jsonObject.get("version").getAsJsonObject();

            return new ServerPing(
                    host,
                    port,
                    jsonObject.get("description").getAsString(),
                    favicon,
                    jsonObject.get("latency").getAsDouble(),
                    new Players(
                            players.get("max").getAsInt(),
                            players.get("online").getAsInt(),
                            sample
                    ),
                    new Version(
                            version.get("name").getAsString(),
                            version.get("protocol").getAsInt()
                    )
            );

        } catch (IOException exception) {
            System.out.println("[MineTools4J] Could not perform http request to '" + API_URI + "/ping/" + host + "/" + port + "': " + exception.getCause());
            return null;
        }

    }

    public String getPlayerName(final UUID uuid) {
        try {
            HttpRequest.RequestResponse requestResponse = HttpRequest.performRequest(new HttpRequest.RequestBuilder(API_URI + "/uuid/" + uuid.toString().replace("-", ""), HttpRequestMethod.GET));

            JsonObject jsonObject = GSON.fromJson(requestResponse.getResultMessage(), JsonObject.class);

            if (jsonObject.has("error") || jsonObject.has("errorMessage")) {
                System.out.println("[MineTools4J] Could not get player name from '" + uuid.toString().replace("-", "") + "': " + jsonObject.get("errorMessage").getAsString());
                return null;
            }

            return jsonObject.get("name").getAsString();

        } catch (IOException exception) {
            System.out.println("[MineTools4J] Could not perform http request to '" + API_URI + "/uuid/" + uuid.toString().replace("-", "") + "': " + exception.getCause());
            return null;
        }

    }

    public String getPlayerName(final String uuid) {
        try {
            HttpRequest.RequestResponse requestResponse = HttpRequest.performRequest(new HttpRequest.RequestBuilder(API_URI + "/uuid/" + uuid.replace("-", ""), HttpRequestMethod.GET));

            JsonObject jsonObject = GSON.fromJson(requestResponse.getResultMessage(), JsonObject.class);

            if (jsonObject.has("error") || jsonObject.has("errorMessage")) {
                System.out.println("[MineTools4J] Could not get player name from '" + uuid.replace("-", "") + "': " + jsonObject.get("errorMessage").getAsString());
                return null;
            }

            return jsonObject.get("name").getAsString();

        } catch (IOException exception) {
            System.out.println("[MineTools4J] Could not perform http request to '" + API_URI + "/uuid/" + uuid.replace("-", "") + "': " + exception.getCause());
            return null;
        }

    }

    public UUID getPlayerUUID(final String name) {
        try {
            HttpRequest.RequestResponse requestResponse = HttpRequest.performRequest(new HttpRequest.RequestBuilder(API_URI + "/uuid/" + name, HttpRequestMethod.GET));

            JsonObject jsonObject = GSON.fromJson(requestResponse.getResultMessage(), JsonObject.class);

            if (jsonObject.has("error") || jsonObject.has("errorMessage")) {
                System.out.println("[MineTools4J] Could not get player uuid from '" + name + "': " + jsonObject.get("errorMessage").getAsString());
                return null;
            }

            return UUID.fromString(jsonObject.get("id").getAsString().replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
            ));

        } catch (IOException exception) {
            System.out.println("[MineTools4J] Could not perform http request to '" + API_URI + "/uuid/" + name + "': " + exception.getCause());
            return null;
        }

    }

}
