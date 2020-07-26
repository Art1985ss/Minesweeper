package minesweeper;

import java.util.Arrays;
import java.util.function.Predicate;

public enum FieldSign {
    MINE('X', -1, false),
    SAFE('.', 9, true),
    ONE('1', 1, false),
    TWO('2', 2, false),
    THREE('3', 3, false),
    FOUR('4', 4, false),
    FIVE('5', 5, false),
    SIX('6', 6, false),
    SEVEN('7', 7, false),
    EIGHT('8', 8, false),
    EXPLORED('/', 0, true);

    private final char sign;
    private final int mineState;
    private final boolean explorable;

    FieldSign(char sign, int mineState, boolean explorable) {
        this.sign = sign;
        this.mineState = mineState;
        this.explorable = explorable;
    }

    public static FieldSign getByMineState(int mineState) throws BoardException {
        Predicate<FieldSign> predicate = fieldSign -> fieldSign.mineState == mineState;
        return Arrays.stream(values()).filter(predicate).findAny()
                .orElseThrow(() -> new BoardException("Not valid field mine state"));
    }

    public boolean isExplorable() {
        return explorable;
    }

    public char getSign() {
        return sign;
    }

    public int getMineState() {
        return mineState;
    }

    @Override
    public String toString() {
        return String.valueOf(sign);
    }
}
