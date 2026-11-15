package com.seven_gnomes.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Building map used in game where the player character is
 */
public class BuildingMap {
    public static final int EXIT_DOOR = 5;

    private final TiledMap tiledMap;
    private final float unitScale;
    private final float tileSize;
    private final float width;
    private final float height;
    private final TiledMapTileLayer doorsLayer;

    private final Array<Rectangle> solids = new Array<>();
    private final Array<LockedDoor> doors = new Array<>();
    private final Array<MapMarker> gooseSpawns = new Array<>();
    private final Array<MapMarker> keySpots = new Array<>();
    private final Array<MapMarker> itemSpots = new Array<>();
    private final Array<MapMarker> eventSpots = new Array<>();
    private final Array<MapMarker> rooms = new Array<>();
    private final Array<MapMarker> lockers = new Array<>();
    private final Array<MapMarker> usedLockers = new Array<>();
    private MapMarker playerSpawn;
    private Rectangle exitArea;

    private final Rectangle probe = new Rectangle();
    private final Array<MapMarker> hits = new Array<>();

    public BuildingMap(TiledMap tiledMap, float unitScale) {
        // map comes from asset service so dont dispose it here
        this.tiledMap = tiledMap;
        this.unitScale = unitScale;

        MapProperties p = tiledMap.getProperties();
        tileSize = p.get("tilewidth", Integer.class) * unitScale;
        width = p.get("width", Integer.class) * tileSize;
        height = p.get("height", Integer.class) * tileSize;
        doorsLayer = findTileLayer("doors");

        for (MapMarker m : readLayer("collision")) solids.add(m.getBounds());
        for (MapMarker m : readLayer("spawns")) {
            if ("player_spawn".equals(m.getType()) || "player".equals(m.getName())) playerSpawn = m;
            else gooseSpawns.add(m);
        }
        keySpots.addAll(readLayer("keys"));
        itemSpots.addAll(readLayer("items"));
        eventSpots.addAll(readLayer("events"));
        rooms.addAll(readLayer("rooms"));
        lockers.addAll(readLayer("lockers"));
        for (MapMarker m : readLayer("exit")) exitArea = m.getBounds();

        for (MapMarker m : readLayer("doors")) {
            LockedDoor door = new LockedDoor(m.getInt("key", 0), m.getString("leadsTo", ""),
                m.getBoolean("isExit", false), m.getBounds());
            rememberDoorTiles(door);
            doors.add(door);
        }

        if (playerSpawn == null) throw new IllegalStateException("map has no player_spawn in spawns");
        if (exitArea == null) throw new IllegalStateException("map has no exit area");
    }

    /**
     * Checks if the area is blocked
     * @param r rectangle
     * @return true or false whether it is blocked
     */
    public boolean isBlocked(Rectangle r) {
        if (r.x < 0 || r.y < 0 || r.x + r.width > width || r.y + r.height > height) return true;
        for (Rectangle s : solids) if (s.overlaps(r)) return true;
        for (LockedDoor d : doors) if (d.isLocked() && d.getBounds().overlaps(r)) return true;
        return false;
    }

    /**
     * checks whether it is walkable
     * @param x pos in the x axis
     * @param y pos in the y axis
     * @return true or false for if it is walkable
     */
    public boolean isWalkable(float x, float y) {
        float s = tileSize * 0.02f;
        probe.set(x - s / 2f, y - s / 2f, s, s);
        return !isBlocked(probe);
    }

    public boolean move(Rectangle bounds, float dx, float dy) {
        // small steps so no tunnelling thru walls
        float maxStep = tileSize / 4f;
        int steps = Math.max(1, (int) Math.ceil(Math.max(Math.abs(dx), Math.abs(dy)) / maxStep));
        float sx = dx / steps, sy = dy / steps;
        boolean blocked = false;
        for (int i = 0; i < steps; i++) {
            if (sx != 0 && !tryStep(bounds, sx, 0)) { blocked = true; sx = 0; }
            if (sy != 0 && !tryStep(bounds, 0, sy)) { blocked = true; sy = 0; }
        }
        return blocked;
    }

    private boolean tryStep(Rectangle b, float dx, float dy) {
        b.x += dx;
        b.y += dy;
        if (!isBlocked(b)) return true;
        b.x -= dx;
        b.y -= dy;
        // creep up to wall so no gap
        for (int i = 0; i < 4; i++) {
            dx /= 2f;
            dy /= 2f;
            b.x += dx;
            b.y += dy;
            if (isBlocked(b)) {
                b.x -= dx;
                b.y -= dy;
            }
        }
        return false;
    }

    /**
     * Gets all solid objects on the map
     * @return an array of all the solids
     */
    public Array<Rectangle> getSolids() { return solids; }

    /**
     * Gets all doors on the map
     * @return an array of all the doors
     */
    public Array<LockedDoor> getDoors() { return doors; }

    /**
     * Gets a specific locked door
     * @param number the id of the door
     * @return the door or null
     */
    public LockedDoor getDoor(int number) {
        for (LockedDoor d : doors) if (d.getNumber() == number) return d;
        return null;
    }

    /**
     * Checks whether a door is locked
     * @param number the id of the door
     * @return whether the door is locked
     */
    public boolean isDoorLocked(int number) {
        LockedDoor d = getDoor(number);
        return d != null && d.isLocked();
    }

    // call when player uses key n
    public boolean unlockDoor(int number) {
        LockedDoor d = getDoor(number);
        if (d == null || !d.isLocked()) return false;
        d.setLocked(false);
        if (doorsLayer != null) {
            for (int[] c : d.cellPositions) doorsLayer.setCell(c[0], c[1], null);
        }
        return true;
    }

    public LockedDoor getLockedDoorNear(Rectangle bounds, float reach) {
        probe.set(bounds.x - reach, bounds.y - reach, bounds.width + 2 * reach, bounds.height + 2 * reach);
        for (LockedDoor d : doors) if (d.isLocked() && d.getBounds().overlaps(probe)) return d;
        return null;
    }

    public boolean isAtExit(Rectangle playerBounds) {
        return !isDoorLocked(EXIT_DOOR) && exitArea.overlaps(playerBounds);
    }

    public Rectangle getExitArea() { return exitArea; }

    public void resetDoors() {
        for (LockedDoor d : doors) {
            d.setLocked(true);
            if (doorsLayer != null) {
                for (int i = 0; i < d.cellPositions.size; i++) {
                    int[] c = d.cellPositions.get(i);
                    doorsLayer.setCell(c[0], c[1], d.savedCells.get(i));
                }
            }
        }
    }

    public Vector2 getPlayerSpawn() { return playerSpawn.getCenter(); }

    public Array<MapMarker> getGooseSpawns() { return gooseSpawns; }

    public Vector2 randomGooseSpawn() {
        return gooseSpawns.get(MathUtils.random(gooseSpawns.size - 1)).getCenter();
    }

    public MapMarker getKeySpot(int number) {
        for (MapMarker k : keySpots) if (k.getInt("key", 0) == number) return k;
        return null;
    }

    public Array<MapMarker> getKeySpots() { return keySpots; }

    public Array<MapMarker> getItemSpots() { return itemSpots; }

    public Array<MapMarker> getEventSpots() { return eventSpots; }

    public Array<MapMarker> getEventSpotsAt(Rectangle bounds) {
        hits.clear();
        for (MapMarker e : eventSpots) if (e.getBounds().overlaps(bounds)) hits.add(e);
        return hits;
    }

    // locker player is stood under or null and it only works once
    public MapMarker useLockerAt(Rectangle bounds) {
        for (MapMarker l : lockers) {
            if (!usedLockers.contains(l, true) && l.getBounds().overlaps(bounds)) {
                usedLockers.add(l);
                return l;
            }
        }
        return null;
    }

    public Array<MapMarker> getLockers() { return lockers; }

    public void resetLockers() {
        usedLockers.clear();
    }

    public String getRoomName(float x, float y) {
        for (MapMarker r : rooms) if (r.getBounds().contains(x, y)) return r.getName();
        return "Corridor";
    }

    public boolean isRedHerring(String roomName) {
        for (MapMarker r : rooms) if (r.getName().equals(roomName)) return r.getBoolean("redHerring", false);
        return false;
    }

    public Array<MapMarker> getRooms() { return rooms; }

    public void clampCamera(OrthographicCamera camera) {
        float halfW = camera.viewportWidth * camera.zoom / 2f;
        float halfH = camera.viewportHeight * camera.zoom / 2f;
        camera.position.x = halfW * 2 >= width ? width / 2f : MathUtils.clamp(camera.position.x, halfW, width - halfW);
        camera.position.y = halfH * 2 >= height ? height / 2f : MathUtils.clamp(camera.position.y, halfH, height - halfH);
        // snap to map pixels stops seams
        camera.position.x = Math.round(camera.position.x / unitScale) * unitScale;
        camera.position.y = Math.round(camera.position.y / unitScale) * unitScale;
        camera.update();
    }

    public float getWidth() { return width; }

    public float getHeight() { return height; }

    public float getTileSize() { return tileSize; }

    public TiledMap getTiledMap() { return tiledMap; }

    private void rememberDoorTiles(LockedDoor d) {
        if (doorsLayer == null) return;
        Rectangle b = d.getBounds();
        int x0 = Math.round(b.x / tileSize), y0 = Math.round(b.y / tileSize);
        int x1 = Math.round((b.x + b.width) / tileSize), y1 = Math.round((b.y + b.height) / tileSize);
        for (int y = y0; y < y1; y++) {
            for (int x = x0; x < x1; x++) {
                TiledMapTileLayer.Cell cell = doorsLayer.getCell(x, y);
                if (cell != null) {
                    d.cellPositions.add(new int[] {x, y});
                    d.savedCells.add(cell);
                }
            }
        }
    }

    // tile layer and object layer both called doors so check type
    private TiledMapTileLayer findTileLayer(String name) {
        for (MapLayer layer : tiledMap.getLayers()) {
            if (name.equals(layer.getName()) && layer instanceof TiledMapTileLayer) return (TiledMapTileLayer) layer;
        }
        return null;
    }

    private Array<MapMarker> readLayer(String layerName) {
        Array<MapMarker> out = new Array<>();
        for (MapLayer layer : tiledMap.getLayers()) {
            if (!layerName.equals(layer.getName()) || layer instanceof TiledMapTileLayer) continue;
            for (MapObject obj : layer.getObjects()) {
                if (!(obj instanceof RectangleMapObject)) continue;
                Rectangle r = ((RectangleMapObject) obj).getRectangle();
                Rectangle scaled = new Rectangle(r.x * unitScale, r.y * unitScale, r.width * unitScale, r.height * unitScale);
                Object type = obj.getProperties().get("type");
                out.add(new MapMarker(obj.getName(), type == null ? layerName : type.toString(), scaled, obj.getProperties()));
            }
        }
        return out;
    }
}
