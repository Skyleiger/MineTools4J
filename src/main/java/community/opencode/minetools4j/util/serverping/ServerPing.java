package community.opencode.minetools4j.util.serverping;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.awt.image.BufferedImage;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class ServerPing {

    private String host;
    private int port;
    private String description;
    private BufferedImage favicon;
    private double latency;
    private Players players;
    private Version version;

}
