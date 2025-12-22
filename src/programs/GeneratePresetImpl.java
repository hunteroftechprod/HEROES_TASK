import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GeneratePresetImpl implements GeneratePreset {

    public static final int UNITS_TYPE_COUNT = 11;
    public static final int HEIGHT_ARMY = 21;
    public static final int WIDTH_ARMY = 3;

    private final Random random = new Random();

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        Army result = new Army();
        int[] unitCount = new int[unitList.size()];
        List<Unit> generatedUnits = new ArrayList<>();
        Set<Coordinate> occupiedCoordinates = new HashSet<>();

        // Сортируем юнитов по убыванию их эффективности (атака + здоровье / стоимость)
        unitList.sort(Comparator.comparingDouble(unit -> -((double) (unit.getBaseAttack() + unit.getHealth()) / unit.getCost())));

        int totalPoints = 0;

        // Генерация юнитов
        for (Unit unit : unitList) {
            while (unitCount[unitList.indexOf(unit)] < UNITS_TYPE_COUNT && totalPoints + unit.getCost() <= maxPoints) {
                addUnit(unit, generatedUnits, occupiedCoordinates, unitList.indexOf(unit)); // Добавляем юнита
                unitCount[unitList.indexOf(unit)]++; // увеличиваем счетчик юнитов этого типа
                totalPoints += unit.getCost();
            }
        }

        result.setUnits(generatedUnits);  // Устанавливаем сформированную армию
        result.setPoints(totalPoints);    // Устанавливаем потраченные очки

        return result;
    }

    /**
     * Добавляет юнит в армию с уникальными случайными координатами.
     * @param unit Юнит, который нужно добавить.
     * @param generatedUnits Список сгенерированных юнитов.
     * @param occupiedCoordinates Множество занятых координат.
     * @param index Индекс юнита для создания уникального имени.
     */
    private void addUnit(Unit unit, List<Unit> generatedUnits, Set<Coordinate> occupiedCoordinates, int index) {
        // Генерация случайных координат
        Coordinate coordinate = new Coordinate(random.nextInt(WIDTH_ARMY), random.nextInt(HEIGHT_ARMY));

        // Проверка на занятость координат
        while (occupiedCoordinates.contains(coordinate)) {
            coordinate.setxCoordinate(random.nextInt(WIDTH_ARMY)); // Генерация новых координат
            coordinate.setyCoordinate(random.nextInt(HEIGHT_ARMY));
        }

        // Добавляем занятые координаты в множество
        occupiedCoordinates.add(coordinate);

        // Создаем новый юнит с уникальными координатами
        Unit newUnit = new Unit(
                unit.getUnitType() + " " + index,
                unit.getUnitType(),
                unit.getHealth(),
                unit.getBaseAttack(),
                unit.getCost(),
                unit.getAttackType(),
                unit.getAttackBonuses(),
                unit.getDefenceBonuses(),
                coordinate.getxCoordinate(),
                coordinate.getyCoordinate()
        );

        // Добавляем юнит в армию
        generatedUnits.add(newUnit);
    }

    /**
     * Класс, представляющий координаты юнита.
     * Используется для хранения и обновления x и y координат.
     */
    private static class Coordinate {
        private int xCoordinate;
        private int yCoordinate;

        // Конструктор
        Coordinate(int xCoordinate, int yCoordinate) {
            this.xCoordinate = xCoordinate;
            this.yCoordinate = yCoordinate;
        }

        // Геттеры и сеттеры
        public int getxCoordinate() {
            return xCoordinate;
        }

        public int getyCoordinate() {
            return yCoordinate;
        }

        public void setxCoordinate(int xCoordinate) {
            this.xCoordinate = xCoordinate;
        }

        public void setyCoordinate(int yCoordinate) {
            this.yCoordinate = yCoordinate;
        }

        // Переопределение метода equals и hashCode для проверки на уникальность координат
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Coordinate that = (Coordinate) obj;
            return xCoordinate == that.xCoordinate && yCoordinate == that.yCoordinate;
        }

        @Override
        public int hashCode() {
            return 31 * xCoordinate + yCoordinate;
        }
    }
}
