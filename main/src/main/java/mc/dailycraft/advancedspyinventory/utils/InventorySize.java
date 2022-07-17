package mc.dailycraft.advancedspyinventory.utils;

import org.jetbrains.annotations.Nullable;

public enum InventorySize {
    SIZE_1(new Range(4)),
    SIZE_2(SIZE_1, new Range(13)),
    SIZE_3(new Range(3, 5)),
    SIZE_4(SIZE_3, new Range(13)),
    SIZE_5(new Range(2, 6)),
    SIZE_6(SIZE_3, new Range(12, 14)),
    SIZE_7(new Range(1, 7)),
    SIZE_8(SIZE_5, new Range(12, 14)),
    SIZE_9(new Range(0, 8)),
    SIZE_10(SIZE_5, new Range(11, 15)),
    SIZE_11(SIZE_10, new Range(22)),
    SIZE_12(SIZE_7, new Range(11, 15)),
    SIZE_13(SIZE_10, new Range(21, 23)),
    SIZE_14(SIZE_7, new Range(10, 16)),
    SIZE_15(SIZE_10, new Range(20, 24)),
    ;

    private final int[] slotPosition;
    private final Range[] slotPositionRange;

    InventorySize(@Nullable InventorySize replicate, Range... slotPosition) {
        Range[] newArray = slotPosition;

        if (replicate != null) {
            newArray = new Range[replicate.slotPositionRange.length + slotPosition.length];
            System.arraycopy(replicate.slotPositionRange, 0, newArray, 0, replicate.slotPositionRange.length);
            System.arraycopy(slotPosition, 0, newArray, replicate.slotPositionRange.length, slotPosition.length);
        }

        int i = 0;

        for (Range range : slotPositionRange = newArray)
            i += range.to - range.from + 1;

        int[] j = new int[i];
        i = 0;

        for (Range range : newArray)
            for (int k = range.from; k <= range.to; ++k)
                j[i++] = k;

        this.slotPosition = j;
    }

    InventorySize(Range... slotPositionRange) {
        this(null, slotPositionRange);
    }

    public boolean hasSlot(int slot) {
        for (int i : slotPosition)
            if (i == slot)
                return true;

        return false;
    }

    public int toSlot(int slot) {
        for (int i = 0; i < slotPosition.length; i++) {
            if (slotPosition[i] == slot)
                return i;
        }

        return -1;
    }

    public int getNecessaryRows() {
        return slotPositionRange.length;
    }

    public static class Range {
        private final int from, to;

        public Range(int from, int to) {
            this.from = from;
            this.to = to;
        }

        public Range(int value) {
            from = to = value;
        }
    }
}