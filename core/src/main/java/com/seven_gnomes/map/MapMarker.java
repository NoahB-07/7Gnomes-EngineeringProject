package com.seven_gnomes.map;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class MapMarker {
    private final String name;
    private final String type;
    private final Rectangle bounds;
    private final MapProperties properties;
    public MapMarker(String name, String type, Rectangle bounds, MapProperties properties) {
        this.name = name == null ? "" : name;
        this.type = type == null ? "" : type;
        this.bounds = bounds;
        this.properties = properties;
    }
    public String getName() { return name; }

    public String getType() { return type; }
    public Rectangle getBounds() { return bounds; }
    public Vector2 getCenter() { return bounds.getCenter(new Vector2()); }

    public MapProperties getProperties() { return properties; }

    public String getString(String key, String fallback) {
        Object v = properties.get(key);
        return v == null ? fallback : v.toString();
    }
    public int getInt(String key, int fallback) {
        Object v = properties.get(key);
        if (v instanceof Number) return ((Number) v).intValue();
        if (v != null) {
            try { return Integer.parseInt(v.toString()); } catch (NumberFormatException ignored) { }
        }
        return fallback;
    }
    public boolean getBoolean(String key, boolean fallback) {
        Object v = properties.get(key);
        if (v instanceof Boolean) return (Boolean) v;
        if (v != null) return Boolean.parseBoolean(v.toString());
        return fallback;
    }
    @Override
    public String toString() {
        return type + ":" + name + " @ " + bounds;
    }
}