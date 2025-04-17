package by.kirylarol.spendsculptor.utils;

import org.jetbrains.annotations.NotNull;

public enum Hotkeys {

    TOTAL("total");

    private String name;

    Hotkeys(String name) {
        this.name = name;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
