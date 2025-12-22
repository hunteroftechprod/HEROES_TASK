package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SimulateBattleImpl implements SimulateBattle {

    // PrintBattleLog ПЕРЕДАЁТСЯ извне, его нельзя new()
    private PrintBattleLog printBattleLog;

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {

        List<Unit> playerUnits = new ArrayList<>(playerArmy.getUnits());
        List<Unit> computerUnits = new ArrayList<>(computerArmy.getUnits());

        while (true) {
            // убираем мёртвых
            playerUnits.removeIf(u -> u == null || !u.isAlive());
            computerUnits.removeIf(u -> u == null || !u.isAlive());

            if (playerUnits.isEmpty() || computerUnits.isEmpty()) {
                break;
            }

            // сортировка по атаке
            playerUnits.sort(Comparator.comparingInt(Unit::getBaseAttack).reversed());
            computerUnits.sort(Comparator.comparingInt(Unit::getBaseAttack).reversed());

            processTurns(playerUnits);
            processTurns(computerUnits);
        }
    }

    private void processTurns(List<Unit> attackers) throws InterruptedException {
        for (Iterator<Unit> it = attackers.iterator(); it.hasNext(); ) {
            Unit attacker = it.next();

            if (attacker == null || !attacker.isAlive()) {
                it.remove();
                continue;
            }

            Unit target = attacker.getProgram().attack();

            if (target != null && printBattleLog != null) {
                printBattleLog.printBattleLog(attacker, target);
            }
        }
    }
}
