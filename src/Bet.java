import java.util.List;
import java.util.ArrayList;

public class Bet {
    List<Integer> bet;

    Bet(String betString) {
        bet = new ArrayList<>();

        int cur = 0;
        int numCollected = 0;
        for (int i = 0; i <= betString.length(); ++i) {
            if (i == betString.length() || betString.charAt(i) == '-') {
                boolean repeat = false;
                for(Integer j : bet) {
                    repeat |= j.equals(cur);
                }

                if(!repeat) {
                    bet.add(cur);
                }
                cur = 0;

                ++numCollected;
            } else {
                cur = cur * 10 + Integer.parseInt(Character.toString(betString.charAt(i)));
            }
        }
    }

    Bet(String betString, int maxNumberBet) {
        bet = new ArrayList<>();

        int cur = 0;
        int numCollected = 0;
        for (int i = 0; i <= betString.length() && numCollected < maxNumberBet; ++i) {
            if (i == betString.length() || betString.charAt(i) == '-') {
                boolean repeat = false;
                for(Integer j : bet) {
                    repeat |= j.equals(cur);
                }

                if(!repeat) {
                    bet.add(cur);
                }
                cur = 0;

                ++numCollected;
            } else {
                cur = cur * 10 + Integer.parseInt(Character.toString(betString.charAt(i)));
            }
        }
    }

    public String toString() {
        StringBuffer str = new StringBuffer(bet.get(0).toString());
        for(int i = 1; i < bet.size(); ++i) {
            str.append('-' + bet.get(i).toString());
        }
        return str.toString();
    }

    public List<Integer> getIntersectionBet(Bet second) {
        List<Integer> ans = new ArrayList<>();

        for(Integer i : bet) {
            boolean both = false;
            for(Integer j : second.bet) {
                both |= i.equals(j);
            }
            if(both) {
                ans.add(i);
            }
        }

        return ans;
    }
}
