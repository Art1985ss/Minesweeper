package minesweeper;

import java.awt.*;
import java.util.Scanner;

public class Application {
    private static final int WIDTH = 9;
    private static final int HEIGHT = 9;
    private final Scanner scanner = new Scanner(System.in);
    private Board board;

    public void execute() {
        System.out.println("How many mines do you want on the field?");
        int mines = Integer.parseInt(scanner.nextLine().trim());
        board = Board.create(WIDTH, HEIGHT, mines);
        update();
    }

    private void update() {
        do {
            System.out.println(board);
            System.out.print("Set/unset mines marks or claim a cell as free: ");
            String[] input = scanner.nextLine().trim().split("\\s+");
            if (input.length != 3) {
                System.out.println("There should be 3 params");
                continue;
            }
            Point p = new Point(Integer.parseInt(input[1]) - 1, Integer.parseInt(input[0]) - 1);
            if ("free".equals(input[2])) {
                if (!board.isInitialized()) {
                    board.randomize(p);
                }
                if (board.isMine(p)){
                    System.out.println(board);
                    System.out.println("You stepped on a mine and failed!");
                    return;
                }
                board.recursiveFreeMark(p);
            } else {
                try {
                    board.addUserMark(p);
                } catch (BoardException e) {
                    System.out.println(e.getMessage());
                }
            }
        } while (!board.checkStatus());
        System.out.println(board);
        System.out.println("Congratulations! You found all mines!");
    }

}
