package com.sr5dice;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by nbp184 on 2016/04/12.
 */
public class DiceRoll {

    private static final Random rand  = new Random();

    private ArrayList<Integer> rolls;
    private int[] edgeDice;
    private ArrayList<int[]> explodedRolls;
    private int[] secondChanceRolls;
    //private ArrayList<int[]> explodedSecondChanceRolls;
    private boolean secondChance;
    private boolean limitPush;

    public DiceRoll() {
        rolls = new ArrayList<>();
        secondChance = false;
        limitPush = false;
        edgeDice = null;
    }

    public void add(int count) {
        int count6 = 0;
        int roll;
        for(int i = 0; i < count; i++) {
            roll = rand.nextInt(6) + 1;
            rolls.add(roll);
            if(roll == 6) {
                count6++;
            }
        }
        if(limitPush && edgeDice == null) {
            addToExplosions(count6, 0);
        }
    }

    private void addToExplosions(int count, int depth) {
        int[] nextSet;
        int start;
        if(depth < explodedRolls.size()) {
            int[] oldSet = explodedRolls.get(depth);
            nextSet = new int[oldSet.length + count];
            System.arraycopy(oldSet, 0, nextSet, 0, oldSet.length);
            start = oldSet.length;
        } else {
            nextSet = new int[count];
            start = 0;
        }
        count = 0;
        for(int i = start; i < nextSet.length; i++) {
            nextSet[i] = rand.nextInt(6) + 1;
            if(nextSet[i] == 6) {
                count++;
            }
        }
        if(depth < explodedRolls.size()) {
            explodedRolls.set(depth, nextSet);
        } else {
            explodedRolls.add(nextSet);
        }
        if(count > 0) {
            addToExplosions(count, depth + 1);
        }
    }

    public int getTotalHits() {
        int total = 0;
        for(Integer roll : rolls) {
            if(roll >= 5) {
                total++;
            }
        }
        if(secondChance) {
            for(int roll : secondChanceRolls) {
                if(roll >= 5) {
                    total++;
                }
            }
        }
        if(limitPush) {
            if(edgeDice != null) {
                for (Integer roll : edgeDice) {
                    if (roll >= 5) {
                        total++;
                    }
                }
            }
            for(int[] rolls : explodedRolls) {
                for(int roll : rolls) {
                    if(roll >= 5) {
                        total++;
                    }
                }
            }
        }
        /*if(limitPush && secondChance) {
            for(int[] rolls : explodedSecondChanceRolls) {
                for(int roll : rolls) {
                    if(roll >= 5) {
                        total++;
                    }
                }
            }
        }*/
        return total;
    }

    public int rollCount() {
        if(edgeDice != null) {
            return rolls.size() + edgeDice.length;
        } else {
            return rolls.size();
        }
    }

    public int getRoll(int index) {
        if(index >= rolls.size()) {
            return edgeDice[index - rolls.size()];
        } else {
            return rolls.get(index);
        }
    }

    public int getTotalRolled() {
        return rolls.size();
    }

    public boolean isLimitPushed() {
        return limitPush;
    }

    public boolean isLimitPushed(int rollIndex) {
        if(limitPush && edgeDice != null) {
            return rollIndex >= rolls.size();
        } else {
            return limitPush;
        }
    }

    public boolean isSecondChanced() {
        return secondChance;
    }

    public boolean doLimitPush(int edge) {
        explodedRolls = new ArrayList<>();
        int[] nextSet;
        int count = 0;
        if(rolls.isEmpty()) {
            add(edge);
            for(int roll : rolls) {
                if(roll == 6) {
                    count++;
                }
            }
        } else {
            edgeDice = new int[edge];
            for(int i = 0; i < edgeDice.length; i++) {
                edgeDice[i] = rand.nextInt(6) + 1;
                if(edgeDice[i] == 6) {
                    count++;
                }
            }
        }
        limitPush = true;
        while(count > 0) {
            nextSet = new int[count];
            count = 0;
            for(int i = 0; i < nextSet.length; i++) {
                nextSet[i] = rand.nextInt(6) + 1;
                if(nextSet[i] == 6) {
                    count++;
                }
            }
            explodedRolls.add(nextSet);
        }
        /*if(secondChance) {
            doSecondChanceLimitPush();
        }*/
        return edgeDice == null;
    }

    /*private void doSecondChanceLimitPush() {
        explodedSecondChanceRolls = new ArrayList<>();
        int count = 0;
        int[] nextSet;
        for(int roll : secondChanceRolls) {
            if(roll == 6) {
                count++;
            }
        }
        while(count > 0) {
            nextSet = new int[count];
            count = 0;
            for(int i = 0; i < nextSet.length; i++) {
                nextSet[i] = rand.nextInt(6) + 1;
                if(nextSet[i] == 6) {
                    count++;
                }
            }
            explodedSecondChanceRolls.add(nextSet);
        }
    }*/

    public int explodedRollCount() {
        return explodedRolls.size();
    }

    public int explodedRollCount(int index) {
        return explodedRolls.get(index).length;
    }

    public int getExplodedRoll(int i, int j) {
        return explodedRolls.get(i)[j];
    }

    public void doSecondChance() {
        secondChance = true;
        int count = 0;
        for(int roll : rolls) {
            if(roll < 5) {
                count++;
            }
        }
        secondChanceRolls = new int[count];
        for(int i = 0; i < secondChanceRolls.length; i++) {
            secondChanceRolls[i] = rand.nextInt(6) + 1;
        }
    }

    public int secondChanceCount() {
        return secondChanceRolls.length;
    }

    public int getSecondChanceRoll(int index) {
        return secondChanceRolls[index];
    }
}
