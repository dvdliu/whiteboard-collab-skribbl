package com.whiteboard;

import org.java_websocket.WebSocket;

/**
 * Represents a connected user session on the whiteboard.
 * Tracks identity, connection, and drawing preferences.
 */
public class UserSession {

    private final String userId;
    private final WebSocket connection;
    private String username;
    private String color;
    private final long connectedAt;

    // Last known cursor position
    private double cursorX;
    private double cursorY;

    // Palette of colors assigned round-robin to users
    private static final String[] USER_COLORS = {
        "#7F77DD",  // purple
        "#1D9E75",  // teal
        "#D85A30",  // coral
        "#D4537E",  // pink
        "#378ADD",  // blue
        "#639922",  // green
        "#BA7517",  // amber
        "#E24B4A",  // red
    };

    private static int colorIndex = 0;

    public UserSession(String userId, WebSocket connection, String username) {
        this.userId = userId;
        this.connection = connection;
        this.username = username;
        this.connectedAt = System.currentTimeMillis();

        // Assign a color from the palette
        synchronized (UserSession.class) {
            this.color = USER_COLORS[colorIndex % USER_COLORS.length];
            colorIndex++;
        }
    }

    public void sendMessage(String message) {
        if (connection.isOpen()) {
            connection.send(message);
        }
    }

    public boolean isConnected() {
        return connection != null && connection.isOpen();
    }

    // ── Getters & Setters ──

    public String getUserId() { return userId; }
    public WebSocket getConnection() { return connection; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getColor() { return color; }

    public long getConnectedAt() { return connectedAt; }

    public double getCursorX() { return cursorX; }
    public void setCursorX(double cursorX) { this.cursorX = cursorX; }

    public double getCursorY() { return cursorY; }
    public void setCursorY(double cursorY) { this.cursorY = cursorY; }

    @Override
    public String toString() {
        return "UserSession{userId=" + userId + ", username=" + username + ", color=" + color + "}";
    }
}