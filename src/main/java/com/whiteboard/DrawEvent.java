package com.whiteboard;

/**
 * Represents a single drawing event sent between clients.
 * Serialized as JSON over WebSocket.
 */
public class DrawEvent {

    public enum Type {
        DRAW,       // freehand stroke point
        LINE,       // straight line segment
        CLEAR,      // clear the entire board
        CURSOR,     // cursor position broadcast
        USER_JOIN,  // new user connected
        USER_LEAVE  // user disconnected
    }

    private Type type;
    private String userId;
    private String color;
    private int brushSize;

    // Coordinates (for DRAW, LINE, CURSOR)
    private double x;
    private double y;
    private double x2;   // for LINE endpoint
    private double y2;

    // For stroke continuity
    private boolean startStroke;

    // Display name shown to peers
    private String username;

    // Room identifier (future multi-room support)
    private String roomId;

    public DrawEvent() {}

    public DrawEvent(Type type, String userId) {
        this.type = type;
        this.userId = userId;
    }

    // ── Getters & Setters ──

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getBrushSize() { return brushSize; }
    public void setBrushSize(int brushSize) { this.brushSize = brushSize; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getX2() { return x2; }
    public void setX2(double x2) { this.x2 = x2; }

    public double getY2() { return y2; }
    public void setY2(double y2) { this.y2 = y2; }

    public boolean isStartStroke() { return startStroke; }
    public void setStartStroke(boolean startStroke) { this.startStroke = startStroke; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    @Override
    public String toString() {
        return "DrawEvent{type=" + type + ", userId=" + userId + ", x=" + x + ", y=" + y + "}";
    }
}