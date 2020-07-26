package minesweeper;

import java.awt.*;
import java.util.*;

public class Board {
    private static final char USER_MARK = '*';
    private final int width;
    private final int height;
    private final int mines;
    private final FieldSign[][] field;
    private final Set<Point> userMarks = new HashSet<>();
    private boolean initialized = false;
    private boolean exploded = false;

    public Board(int width, int height, int mines) {
        this.width = width;
        this.height = height;
        this.mines = mines;
        field = new FieldSign[height][width];
        fillSafe();

    }

    public static Board create(int width, int height, int mines) {
        return new Board(width, height, mines);
    }

    public void randomize(Point initial) {
        Random random = new Random();
        int m = 0;
        while (mines > m) {
            Point p = new Point(random.nextInt(height), random.nextInt(width));
            if (getField(p).equals(FieldSign.SAFE) || !p.equals(initial)) {
                setField(p, FieldSign.MINE);
                m++;
            }
        }
        initialized = true;
    }

    public void update() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Point p = new Point(row, col);
                if (getField(p).equals(FieldSign.MINE)) {
                    continue;
                }
                setField(p, updateSurrounding(p));
            }
        }
    }

    public boolean isMine(Point p) {
        exploded = getField(p).equals(FieldSign.MINE);
        return exploded;
    }

    public void recursiveFreeMark(Point p) {
        FieldSign fs = updateSurrounding(p);
        setField(p, fs);
        userMarks.remove(p);
        if (!fs.isExplorable()) {
            return;
        }
        for (int row = p.x - 1; row <= p.x + 1; row++) {
            for (int col = p.y - 1; col <= p.y + 1; col++) {
                Point p1 = new Point(row, col);
                if (isValid(p1) && !p.equals(p1)) {
                    FieldSign fs1 = getField(p1);
                    if (fs1.isExplorable() && !fs1.equals(FieldSign.EXPLORED)) {
                        recursiveFreeMark(p1);
                    }
                }
            }
        }
    }

    private boolean isValid(Point p) {
        if (p.x < 0 || p.x >= height) {
            return false;
        }
        return p.y >= 0 && p.y < width;
    }

    private FieldSign updateSurrounding(Point p) {
        int minesAround = 0;
        for (int row = p.x - 1; row <= p.x + 1; row++) {
            for (int col = p.y - 1; col <= p.y + 1; col++) {
                Point point = new Point(row, col);
                if (!isValid(point) || point.equals(p)) {
                    continue;
                }
                minesAround += getField(point).equals(FieldSign.MINE) ? 1 : 0;
            }
        }
        return FieldSign.getByMineState(minesAround);
    }

    public boolean addUserMark(Point p) throws BoardException {
        if (!isValid(p)) {
            throw new BoardException("Those coordinates are not valid");
        }
        FieldSign fs = getField(p);
        int mines = fs.getMineState();
        if (mines > 0 && mines < 9) {
            throw new BoardException("There is a number here!");
        }
        if (userMarks.contains(p)) {
            return userMarks.remove(p);
        }
        return userMarks.add(p);
    }

    private boolean checkIfOnlyMines() {
        char[][] f = getHidden();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (f[row][col] == FieldSign.SAFE.getSign() || f[row][col] == USER_MARK) {
                    if (f[row][col] != getField(new Point(row, col)).getSign()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean checkUserMarks() {
        if (mines != userMarks.size()) {
            return false;
        }
        int detectedMines = 0;
        for (Point p : userMarks) {
            FieldSign fs = getField(p);
            if (fs.equals(FieldSign.MINE)) {
                detectedMines++;
            }
        }
        return mines == detectedMines;
    }

    public boolean checkStatus() {
        return checkIfOnlyMines() && checkUserMarks();
    }

    public FieldSign getField(Point p) {
        return field[p.x][p.y];
    }

    public void setField(Point p, FieldSign fs) {
        field[p.x][p.y] = fs;
    }

    public boolean isInitialized() {
        return initialized;
    }

    private void fillSafe() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                setField(new Point(row, col), FieldSign.SAFE);
            }
        }
    }

    private char[][] getHidden() {
        char[][] chars = new char[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                FieldSign fs = field[row][col];
                if (fs.equals(FieldSign.MINE) && !exploded) {
                    fs = FieldSign.SAFE;
                }
                chars[row][col] = fs.getSign();
            }
        }
        for (Point p : userMarks) {
            chars[p.x][p.y] = USER_MARK;
        }
        return chars;
    }

    @Override
    public String toString() {
        char[][] field = getHidden();
        StringBuilder sb = new StringBuilder();
        StringBuilder lineSb = new StringBuilder();
        sb.append(" ").append("|");
        lineSb.append("—").append("|");
        for (int i = 0; i < width; i++) {
            sb.append(i + 1);
            lineSb.append("—");
        }
        sb.append("|").append("\n");
        lineSb.append("|").append("\n");
        sb.append(lineSb);
        for (int row = 0; row < height; row++) {
            sb.append(row + 1).append("|");
            for (int col = 0; col < width; col++) {
                sb.append(field[row][col]);
            }
            sb.append("|").append('\n');
        }
        sb.append(lineSb);
        return sb.toString();
    }
}
