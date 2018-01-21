import java.util.ArrayList;
import java.util.List;

public class Team {
    List<Player> team = new ArrayList<>();
    String name;
    String headPlayer;
    int score;
    int sumScorePlayer;

    Team(String name) {
        this.name = name;
    }

    public void setHeadPlayer(String name) {
        headPlayer = name;
    }

    public void addPlayer(Player a) {
        team.add(a);
    }
}
