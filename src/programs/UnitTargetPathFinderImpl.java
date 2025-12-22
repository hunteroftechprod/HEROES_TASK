package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.EdgeDistance;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

/**
 * Реализация интерфейса UnitTargetPathFinder для поиска пути от атакующего юнита к атакуемому.
 */
public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {

    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};  // Направления для поиска соседей

    /**
     * Метод определяет кратчайший маршрут между атакующим и атакуемым юнитом.
     *
     * @param attackUnit       юнит, который атакует.
     * @param targetUnit       юнит, который подвергается атаке.
     * @param existingUnitList список всех существующих юнитов.
     * @return список объектов Edge, содержащих координаты точек маршрута от атакующего юнита до атакуемого.
     */
    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        // Массив расстояний от стартовой точки
        int[][] distances = initializeDistances();
        boolean[][] visited = new boolean[WIDTH][HEIGHT];  // Массив посещенных клеток
        Edge[][] predecessors = new Edge[WIDTH][HEIGHT];  // Массив для хранения предшественников

        Set<String> blockedCells = getBlockedCells(existingUnitList, attackUnit, targetUnit);

        // Очередь с приоритетом для поиска пути
        Queue<EdgeDistance> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(EdgeDistance::getDistance));
        addStartingPointToQueue(priorityQueue, attackUnit, distances);

        while (!priorityQueue.isEmpty()) {
            EdgeDistance current = priorityQueue.poll();
            int x = current.getX();
            int y = current.getY();

            if (visited[x][y]) {
                continue;
            }

            visited[x][y] = true;

            // Если достигли цели, выходим
            if (targetReached(current, targetUnit)) {
                break;
            }

            updateNeighbors(current, blockedCells, distances, predecessors, priorityQueue);
        }

        return buildPath(predecessors, targetUnit);
    }

    /**
     * Инициализация массива расстояний до клеток.
     */
    private int[][] initializeDistances() {
        int[][] distances = new int[WIDTH][HEIGHT];
        for (int[] row : distances) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        return distances;
    }

    /**
     * Получение заблокированных клеток (где находятся юниты).
     */
    private Set<String> getBlockedCells(List<Unit> existingUnitList, Unit attackUnit, Unit targetUnit) {
        Set<String> blockedCells = new HashSet<>();
        for (Unit unit : existingUnitList) {
            if (unit.isAlive() && unit != attackUnit && unit != targetUnit) {
                blockedCells.add(unit.getxCoordinate() + "," + unit.getyCoordinate());
            }
        }
        return blockedCells;
    }

    /**
     * Добавление начальной точки в очередь с приоритетом.
     */
    private void addStartingPointToQueue(Queue<EdgeDistance> priorityQueue, Unit attackUnit, int[][] distances) {
        int x = attackUnit.getxCoordinate();
        int y = attackUnit.getyCoordinate();
        distances[x][y] = 0;
        priorityQueue.offer(new EdgeDistance(x, y, 0));
    }

    /**
     * Проверка, достиг ли текущий юнит цели.
     */
    private boolean targetReached(EdgeDistance current, Unit targetUnit) {
        return current.getX() == targetUnit.getxCoordinate() && current.getY() == targetUnit.getyCoordinate();
    }

    /**
     * Обновление соседних клеток для поиска пути.
     */
    private void updateNeighbors(EdgeDistance current, Set<String> blockedCells,
                                 int[][] distances, Edge[][] predecessors, Queue<EdgeDistance> priorityQueue) {
        for (int[] direction : DIRECTIONS) {
            int neighborX = current.getX() + direction[0];
            int neighborY = current.getY() + direction[1];

            if (isValid(neighborX, neighborY, blockedCells)) {
                int newDistance = distances[current.getX()][current.getY()] + 1;
                if (newDistance < distances[neighborX][neighborY]) {
                    distances[neighborX][neighborY] = newDistance;
                    predecessors[neighborX][neighborY] = new Edge(current.getX(), current.getY());
                    priorityQueue.offer(new EdgeDistance(neighborX, neighborY, newDistance));
                }
            }
        }
    }

    /**
     * Проверка, является ли клетка допустимой для посещения (не заблокирована).
     */
    private boolean isValid(int x, int y, Set<String> blockedCells) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && !blockedCells.contains(x + "," + y);
    }

    /**
     * Собирает путь от цели к стартовой точке.
     */
    private List<Edge> buildPath(Edge[][] cameFrom, Unit targetUnit) {
        Deque<Edge> path = new ArrayDeque<>();
        int x = targetUnit.getxCoordinate();
        int y = targetUnit.getyCoordinate();

        while (cameFrom[x][y] != null) {
            path.push(new Edge(x, y));
            x = cameFrom[x][y].getX();
            y = cameFrom[x][y].getY();
        }

        return new ArrayList<>(path);
    }
}
