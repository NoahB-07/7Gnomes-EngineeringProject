package com.seven_gnomes.map;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class LockedDoor {
    private final int number;
    private final String leadsTo;
    private final boolean exit;
    private final Rectangle bounds;
    private boolean locked = true;
    final Array<int[]> cellPositions = new Array<>();
    final Array<TiledMapTileLayer.Cell> savedCells = new Array<>();

    LockedDoor(int number, String leadsTo, boolean exit, Rectangle bounds) {
        this.number = number;
        this.leadsTo = leadsTo;
        this.exit = exit;
        this.bounds = bounds;
    }
    public int getNumber() { return number; }

    public String getLeadsTo() { return leadsTo; }

    public boolean isExit() { return exit; }

    public Rectangle getBounds() { return bounds; }

    public boolean isLocked() { return locked; }
    void setLocked(boolean locked) { this.locked = locked; }
    @Override
    public String toString() {
        return "door_" + number + (locked ? " (locked)" : " (open)") + " -> " + leadsTo;
    }
}
