package community.opencode.minetools4j.util.serverping;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class Players {

    private int max;
    private int online;
    private List<Sample> sample;



}
