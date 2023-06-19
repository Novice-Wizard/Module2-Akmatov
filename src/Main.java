import java.util.*;

class Cell {
    private boolean isShip;
    private boolean isHit;

    public Cell() {
        isShip = false;
        isHit = false;
    }

    public boolean isShip() {
        return isShip;
    }

    public void setShip(boolean ship) {
        isShip = ship;
    }

    public boolean isHit() {
        return isHit;
    }

    public void setHit(boolean hit) {
        isHit = hit;
    }

    public String toString() {
        if (isHit) {
            if (isShip) {
                return "X";
            } else {
                return "o";
            }
        } else {
            return "-";
        }
    }
}

class Board {
    private Cell[][] cells;

    public Board() {
        cells = new Cell[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    public void placeShips() {
        placeSingleDeckShip();
        placeDoubleDeckShips();
    }

    private void placeSingleDeckShip() {
        Random rand = new Random();
        int row = rand.nextInt(8);
        int col = rand.nextInt(8);
        cells[row][col].setShip(true);
    }

    private void placeDoubleDeckShips() {
        Random rand = new Random();

        for (int i = 0; i < 2; i++) {
            int row, col;
            boolean horizontal;
            boolean isValid;

            do {
                row = rand.nextInt(8);
                col = rand.nextInt(7);
                horizontal = rand.nextBoolean();
                isValid = checkValidPosition(row, col, horizontal);
            } while (!isValid);

            if (horizontal) {
                cells[row][col].setShip(true);
                cells[row][col + 1].setShip(true);
            } else {
                cells[row][col].setShip(true);
                cells[row + 1][col].setShip(true);
            }
        }
    }

    private boolean checkValidPosition(int row, int col, boolean horizontal) {
        if (horizontal) {
            if (col + 1 >= 8) {
                return false;
            }
            if (cells[row][col].isShip() || cells[row][col + 1].isShip()) {
                return false;
            }
            if (row > 0 && (cells[row - 1][col].isShip() || cells[row - 1][col + 1].isShip())) {
                return false;
            }
            if (row < 7 && (cells[row + 1][col].isShip() || cells[row + 1][col + 1].isShip())) {
                return false;
            }
        } else {
            if (row + 1 >= 8) {
                return false;
            }
            if (cells[row][col].isShip() || cells[row + 1][col].isShip()) {
                return false;
            }
            if (col > 0 && (cells[row][col - 1].isShip() || cells[row + 1][col - 1].isShip())) {
                return false;
            }
            if (col < 7 && (cells[row][col + 1].isShip() || cells[row + 1][col + 1].isShip())) {
                return false;
            }
        }
        return true;
    }

    public boolean allShipsDestroyed() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (cells[i][j].isShip() && !cells[i][j].isHit()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void markHit(int row, int col) {
        cells[row][col].setHit(true);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("  A B C D E F G H\n");
        for (int i = 0; i < 8; i++) {
            sb.append(i + 1).append(" ");
            for (int j = 0; j < 8; j++) {
                sb.append(cells[i][j]).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

class Game {
    private Board board;
    private int numOfAttempts;
    private long startTime;
    private List<Long> topScores;

    public Game() {
        board = new Board();
        numOfAttempts = 0;
        topScores = new ArrayList<>();
    }

    public void play() {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("Меню:");
            System.out.println("1. Новая игра");
            System.out.println("2. Результаты");
            System.out.println("3. Выход");
            System.out.print("Выберите действие: ");
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    startNewGame();
                    break;
                case 2:
                    showTopScores();
                    break;
                case 3:
                    System.out.println("До свидания!");
                    break;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
                    break;
            }
        } while (choice != 3);
        scanner.close();
    }

    private void startNewGame() {
        board.placeShips();
        numOfAttempts = 0;
        startTime = System.currentTimeMillis();
        playGame();
        long endTime = System.currentTimeMillis();
        long score = (endTime - startTime) / 1000;
        topScores.add(score);
        Collections.sort(topScores);
        if (topScores.size() > 3) {
            topScores = topScores.subList(0, 3);
        }
        System.out.println("Поздравляем! Вы уничтожили все корабли.");
        System.out.println("Время игры: " + score + " сек.");
    }

    private void showTopScores() {
        if (topScores.isEmpty()) {
            System.out.println("Нет результатов для отображения.");
        } else {
            System.out.println("Топ 3 самых быстрых игр:");
            for (int i = 0; i < topScores.size(); i++) {
                System.out.println((i + 1) + ". " + topScores.get(i) + " сек.");
            }
        }
    }

    private void playGame() {
        Scanner scanner = new Scanner(System.in);
        while (!board.allShipsDestroyed()) {
            System.out.println(board);
            System.out.print("Куда стреляем (введите координаты в формате A1, B2 и т.д.): ");
            String input = scanner.nextLine();
            if (input.length() != 2) {
                System.out.println("Неверный формат ввода. Попробуйте снова.");
                continue;
            }
            char colChar = input.charAt(0);
            char rowChar = input.charAt(1);
            if (colChar < 'A' || colChar > 'H' || rowChar < '1' || rowChar > '8') {
                System.out.println("Неверные координаты. Попробуйте снова.");
                continue;
            }
            int col = colChar - 'A';
            int row = rowChar - '1';
            numOfAttempts++;
            board.markHit(row, col);
        }
        scanner.close();
    }
}

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.play();
    }
}