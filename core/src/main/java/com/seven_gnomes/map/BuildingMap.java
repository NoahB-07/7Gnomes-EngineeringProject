package com.seven_gnomes.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class BuildingMap implements Disposable {
    public static final String DEFAULT_PATH = "maps/building.tmx";
    public static final int EXIT_DOOR = 5;
    private final TiledMap tiledMap;
    private final OrthogonalTiledMapRenderer renderer;
    private final int[] belowLayers;
    private final int[] aboveLayers;
    private final TiledMapTileLayer doorsLayer;
    private final Array<Rectangle> solids = new Array<>();
    private final Array<LockedDoor> doors = new Array<>();
    private final Array<MapMarker> gooseSpawns = new Array<>();
    private final Array<MapMarker> keySpots = new Array<>();
    private final Array<MapMarker> itemSpots = new Array<>();
    private final Array<MapMarker> eventSpots = new Array<>();
    private final Array<MapMarker> rooms = new Array<>();
    private MapMarker playerSpawn;
    private Rectangle exitArea;

    private final int tileSize;
    private final float widthPx;
    private final float heightPx;
    private final Rectangle probe = new Rectangle();
    private final Array<MapMarker> hits = new Array<>();
    public BuildingMap() {
        this(DEFAULT_PATH);
    }
    public BuildingMap(String tmxPath) {
        tiledMap = new TmxMapLoader().load(tmxPath);
        renderer = new OrthogonalTiledMapRenderer(tiledMap, 1f);
        MapProperties p = tiledMap.getProperties();
        tileSize = p.get("tilewidth", Integer.class);
        widthPx = p.get("width", Integer.class) * tileSize;
        heightPx = p.get("height", Integer.class) * tileSize;

        belowLayers = layerIndices("floor", "rugs", "furniture", "doors");
        aboveLayers = layerIndices("above");
        MapLayer dl = tiledMap.getLayers().get("doors");
        doorsLayer = dl instanceof TiledMapTileLayer ? (TiledMapTileLayer) dl : null;
        for (MapMarker m : readLayer("collision")) solids.add(m.getBounds());
        for (MapMarker m : readLayer("spawns")) {
            if ("player_spawn".equals(m.getType()) || "player".equals(m.getName())) playerSpawn = m;
            else gooseSpawns.add(m);
        }
        keySpots.addAll(readLayer("keys"));
        itemSpots.addAll(readLayer("items"));
        eventSpots.addAll(readLayer("events"));
        rooms.addAll(readLayer("rooms"));
        for (MapMarker m : readLayer("exit")) exitArea = m.getBounds();

        for (MapMarker m : readLayer("doors")) {
            LockedDoor door = new LockedDoor(m.getInt("key", 0), m.getString("leadsTo", ""),
                m.getBoolean("isExit", false), m.getBounds());
            rememberDoorTiles(door);
            doors.add(door);
        }
        if (playerSpawn == null) throw new IllegalStateException(tmxPath + ": no player_spawn in 'spawns'");
        if (exitArea == null) throw new IllegalStateException(tmxPath + ": no 'exit' area");
    }
    public void renderBelow(OrthographicCamera camera) {
        renderer.setView(camera);
        if (belowLayers.length > 0) renderer.render(belowLayers);
    }
    // draw after player
    public void renderAbove(OrthographicCamera camera) {
        renderer.setView(camera);
        if (aboveLayers.length > 0) renderer.render(aboveLayers);
    }

    public boolean isBlocked(Rectangle r) {
        if (r.x < 0 || r.y < 0 || r.x + r.width > widthPx || r.y + r.height > heightPx) return true;
        for (Rectangle s : solids) if (s.overlaps(r)) return true;
        for (LockedDoor d : doors) if (d.isLocked() && d.getBounds().overlaps(r)) return true;
        return false;
    }

    public boolean isWalkable(float x, float y) {
        probe.set(x - 0.5f, y - 0.5f, 1f, 1f);
        return !isBlocked(probe);
    }

    public boolean move(Rectangle bounds, float dx, float dy) {
        // small steps so no tunnelling thru walls
        float maxStep = tileSize / 4f;
        int steps = Math.max(1, (int) Math.ceil(Math.max(Math.abs(dx), Math.abs(dy)) / maxStep));
        float sx = dx / steps, sy = dy / steps;
        boolean blocked = false;
        for (int i = 0; i < steps; i++) {
            if (sx != 0) {
                bounds.x += sx;
                if (isBlocked(bounds)) { bounds.x -= sx; blocked = true; sx = 0; }
            }
            if (sy != 0) {
                bounds.y += sy;
                if (isBlocked(bounds)) { bounds.y -= sy; blocked = true; sy = 0; }
            }
        }
        return blocked;
    }
    public Array<Rectangle> getSolids() { return solids; }
    public Array<LockedDoor> getDoors() { return doors; }
    public LockedDoor getDoor(int number) {
        for (LockedDoor d : doors) if (d.getNumber() == number) return d;
        return null;
    }
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
        camera.position.x = halfW * 2 >= widthPx ? widthPx / 2f : MathUtils.clamp(camera.position.x, halfW, widthPx - halfW);
        camera.position.y = halfH * 2 >= heightPx ? heightPx / 2f : MathUtils.clamp(camera.position.y, halfH, heightPx - halfH);
        // whole pixels, stops seams
        camera.position.x = Math.round(camera.position.x);
        camera.position.y = Math.round(camera.position.y);
        camera.update();
    }
    public float getWidth() { return widthPx; }

    public float getHeight() { return heightPx; }

    public int getTileSize() { return tileSize; }
    public TiledMap getTiledMap() { return tiledMap; }

    private void rememberDoorTiles(LockedDoor d) {
        if (doorsLayer == null) return;
        Rectangle b = d.getBounds();
        int x0 = (int) (b.x / tileSize), y0 = (int) (b.y / tileSize);
        int x1 = (int) Math.ceil((b.x + b.width) / tileSize), y1 = (int) Math.ceil((b.y + b.height) / tileSize);
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
    private int[] layerIndices(String... names) {
        int[] tmp = new int[names.length];
        int n = 0;
        for (String name : names) {
            int idx = tiledMap.getLayers().getIndex(name);
            if (idx >= 0) tmp[n++] = idx;
        }
        int[] out = new int[n];
        System.arraycopy(tmp, 0, out, 0, n);
        return out;
    }

    private Array<MapMarker> readLayer(String layerName) {
        Array<MapMarker> out = new Array<>();
        MapLayer layer = tiledMap.getLayers().get(layerName);
        if (layer == null) return out;
        for (MapObject obj : layer.getObjects()) {
            if (!(obj instanceof RectangleMapObject)) continue;
            Rectangle r = new Rectangle(((RectangleMapObject) obj).getRectangle());
            Object type = obj.getProperties().get("type");
            out.add(new MapMarker(obj.getName(), type == null ? layerName : type.toString(), r, obj.getProperties()));
        }
        return out;
    }
    @Override
    public void dispose() {
        renderer.dispose();
        tiledMap.dispose();
    }
}
