package com.example.demo.domain;

public enum FileServiceEnum {
    
    FIRSTSLOT(1), SECONDSLOT(2), THIRDSLOT(3);

    private final int slotNumber;

    private FileServiceEnum(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public int getSlotNumber() {
        return slotNumber;
    }
}
