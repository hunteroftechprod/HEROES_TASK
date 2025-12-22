package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация интерфейса SuitableForAttackUnitsFinder.

 */
public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    /**
     * Метод определяет список юнитов, подходящих для атаки, для атакующего юнита одной из армий.
     *
     * @param unitsByRow       трехслойный массив юнитов противника.
     * @param isLeftArmyTarget параметр, указывающий, юниты какой армии подвергаются атаке.
     *
     * @return возвращает список юнитов, подходящих для атаки, для юнита атакующей армии.
     */
    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> result = new ArrayList<>();

        // Проходим по всем рядам юнитов
        for (List<Unit> row : unitsByRow) {
            // Находим юнитов, подходящих для атаки в этом ряду
            List<Unit> suitableUnits = findSuitableUnitsInRow(row, isLeftArmyTarget);
            result.addAll(suitableUnits);  // Добавляем их в итоговый список
        }

        return result;
    }

    /**
     * Метод для поиска подходящих юнитов в одном ряду.
     * 
     * @param row              ряд с юнитами
     * @param isLeftArmyTarget флаг, определяющий, юниты какой армии будут атакованы
     * @return список юнитов, которые могут быть атакованы
     */
    private List<Unit> findSuitableUnitsInRow(List<Unit> row, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();

        // Проходим по юнитам в ряду
        for (int i = 0; i < row.size(); i++) {
            Unit unit = row.get(i);

            // Проверяем, что юнит жив и подходит по условиям для атаки
            if (unit != null && unit.isAlive() && isValidUnitForAttack(row, i, isLeftArmyTarget)) {
                suitableUnits.add(unit);
            }
        }

        return suitableUnits;
    }

    /**
     * Проверяет, подходит ли юнит в ряду для атаки в зависимости от позиции.
     *
     * @param row              ряд с юнитами
     * @param index            индекс юнита в ряду
     * @param isLeftArmyTarget флаг, определяющий, юниты какой армии будут атакованы
     * @return true, если юнит может быть атакован
     */
    private boolean isValidUnitForAttack(List<Unit> row, int index, boolean isLeftArmyTarget) {
        return isLeftArmyTarget ? isFirstUnitFromLeft(row, index) : isLastUnitFromRight(row, index);
    }

    /**
     * Проверяет, является ли юнит первым слева в ряду.
     */
    private boolean isFirstUnitFromLeft(List<Unit> row, int index) {
        // Юнит - первый слева, если он в начале ряда или перед ним нет других юнитов
        return index == 0 || row.subList(0, index).stream().allMatch(u -> u == null);
    }

    /**
     * Проверяет, является ли юнит последним справа в ряду.
     */
    private boolean isLastUnitFromRight(List<Unit> row, int index) {
        // Юнит - последний справа, если он в конце ряда или после него нет других юнитов
        return index == row.size() - 1 || row.subList(index + 1, row.size()).stream().allMatch(u -> u == null);
    }
}
