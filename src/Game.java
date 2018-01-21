import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Game {
    List<Battle> battleList;
    Bet trueBet;

    Game(String path) {
        List<String> lines = loadingFile(path);
        List<List<String> > blocks = parseFile(lines);
        parseBattle(blocks.get(0));
        parseTeam(blocks.subList(1, blocks.size() - 1));
        trueBet = new Bet(blocks.get(blocks.size() - 1).get(0));
    }

    private void parseTeam(List<List<String>> blocks) {
        for(List<String> block : blocks) {
            Team cur = null;
            for(Battle battle : battleList) {
                if(battle.first.name.equals(block.get(0).trim())) {
                    cur = battle.first;
                }
                if(battle.second.name.equals(block.get(0).trim())) {
                    cur = battle.second;
                }
            }

            cur.setHeadPlayer(block.get(1).trim());

            for(int i = 2; i < 7; ++i) {
                cur.addPlayer(parsePlayer(block.get(i)));
            }
        }
    }

    private Player parsePlayer(String s) {
        s = s.substring(2, s.length()).trim();

        return new Player(
                s.substring(0, s.indexOf('(')).trim(),
                new Bet(s.substring(s.indexOf('(') + 1, s.length() - 1).trim(), 7)
        );
    }

    private void parseBattle(List<String> list) {
        battleList = new ArrayList<>();

        for(String i : list) {
            Team first = new Team(i.substring(0, i.indexOf('-')).trim());
            Team second = new Team(i.substring(i.indexOf('-') + 1, i.length()).trim());

            battleList.add(new Battle(first, second));
        }
    }

    private List<List<String> > parseFile(List<String> lines) {
        if(lines == null) {
            return null;
        }

        List<List<String> > blocks = new ArrayList<>();
        List<String> temp = new ArrayList<>();

        for(int i = 0; i <= lines.size(); ++i) {
            if(i == lines.size() || lines.get(i).length() == 0) {
                if(temp.size() == 0) {
                    continue;
                }

                blocks.add(temp);
                temp = new ArrayList<>();
            } else {
                temp.add(lines.get(i));
            }
        }

        return blocks;
    }

    private List<String> loadingFile(String path) {
        List<String> lines = null;

        try {
            lines = Files.readAllLines(Paths.get(path));
        } catch (IOException e) {}

        return lines;
    }

    public List<String> getReport() {
        List<String> report = new ArrayList<>();

        report.add("<html>");
        report.add("<strong>Сыграли варианты: " + trueBet.toString() + "</strong></p>");

        int[] used = new int[41];
        for(Battle battle : battleList) {
            report.add("<p>&nbsp;</p>");

            report.add("<p>Тренеры: " + battle.first.headPlayer + " - " + battle.second.headPlayer);

            int sumImportant = 0;
            for(int i = 0; i < 5; ++i) {
                for(int j : battle.first.team.get(i).bet.getIntersectionBet(trueBet)) {
                    used[j] |= 1;
                    ++battle.first.team.get(i).score;
                }
                for(int j : battle.second.team.get(i).bet.getIntersectionBet(trueBet)) {
                    if(used[j] == 1) {
                        --battle.first.team.get(i).score;
                    } else {
                        ++battle.second.team.get(i).score;
                    }
                    used[j] |= 2;
                }

                StringBuilder str = new StringBuilder("<p>" + new Integer(i + 1).toString() + ". ");
                str.append(getReportBattle(battle.first.team.get(i), battle.second.team.get(i), used, 0) + " - ");
                str.append(getReportBattle(battle.second.team.get(i), battle.first.team.get(i), used, 1) + " ");
                str.append("<strong>" + new Integer(battle.first.team.get(i).score).toString() + ":" +
                        new Integer(battle.second.team.get(i).score).toString() + "</strong>");

                int important = 7 - intersectionList(
                        battle.first.team.get(i).bet.getIntersectionBet(trueBet),
                        battle.second.team.get(i).bet.getIntersectionBet(trueBet)
                );
                sumImportant += important;

                str.append(" (" + new Integer(important).toString() + ")</p>");

                report.add(str.toString());

                battle.first.sumScorePlayer += battle.first.team.get(i).score;
                battle.second.sumScorePlayer += battle.second.team.get(i).score;

                if(battle.first.team.get(i).score > battle.second.team.get(i).score) {
                    ++battle.first.score;
                } else if(battle.first.team.get(i).score < battle.second.team.get(i).score) {
                    ++battle.second.score;
                }

                for(int j : battle.first.team.get(i).bet.getIntersectionBet(trueBet)) {
                    used[j] = 0;
                }
                for(int j : battle.second.team.get(i).bet.getIntersectionBet(trueBet)) {
                    used[j] = 0;
                }
            }

            StringBuilder str = new StringBuilder("<p>");

            if(battle.first.score > battle.second.score) {
                str.append("<strong>" + battle.first.name + "</strong>");
            } else {
                str.append(battle.first.name);
            }
            str.append(" - ");

            if(battle.first.score < battle.second.score) {
                str.append("<strong>" + battle.second.name + "</strong>");
            } else {
                str.append(battle.first.name);
            }
            str.append(" ");

            str.append("<strong>" + new Integer(battle.first.score).toString() + ":" +
                    new Integer(battle.second.score).toString() + "</strong>");

            str.append(" (УВ " + new Integer(battle.first.sumScorePlayer).toString() + "-");
            str.append(new Integer(battle.second.sumScorePlayer).toString() + " из ");
            str.append(new Integer(sumImportant).toString() + ")</p>");

            report.add(report.size() - 6, str.toString());
        }

        return report;
    }

    private int intersectionList(List<Integer> a, List<Integer> b) {
        int ans = 0;
        for(int i : a) {
            boolean only = false;
            for(int j : b) {
                only |= (i == j);
            }
            ans += (only ? 1 : 0);
        }
        return ans;
    }

    private String getReportBattle(Player a, Player b, int[] used, int numberPlayer) {
        StringBuilder report = new StringBuilder();

        if(a.score > b.score) {
            report.append("<strong>" + a.name + "</strong>");
        } else {
            report.append(a.name);
        }

        report.append(" (");

        boolean first = true;
        for(Integer j : a.bet.bet) {
            if(!first) {
                report.append("-");
            }

            switch (used[j]) {
                case 0:
                    report.append(j.toString());
                    break;
                case 3:
                    report.append("<u>" + j.toString() + "</u>");
                    break;
            } ;
            if(used[j] == numberPlayer + 1) {
                report.append("<strong>" + j.toString() + "</strong>");
            } else if(used[j] == (1 - numberPlayer) + 1) {
                report.append("<u>" + j.toString() + "</u>");
            }
            first = false;
        }

        report.append(")");

        return report.toString();
    }
}
