# Collaborative Whiteboard

A real-time collaborative drawing app. Multiple users can connect from different browsers and draw together on a shared canvas — strokes, lines, cursor positions, and a clear button all sync live over WebSockets.

## How it works

The project has two halves:

- **Frontend** (`index.html`) — pure HTML/CSS/JavaScript. Renders the canvas and UI, captures drawing input, and talks to the server over a WebSocket.
- **Backend** (Java, under `src/main/java/com/whiteboard/`) — a small WebSocket server that acts as a message switchboard. It receives draw events from one client and broadcasts them to all the others, and keeps a short history so late joiners see what's already on the board.

```
Browser A ──┐
            ├──▶  WebSocketServer (port 8080)  ──▶ broadcasts to all peers
Browser B ──┘
```

## Requirements

- **JDK 17+** — check with `java -version`
- **Maven 3.6+** — check with `mvn -v`
- A modern browser

## Quick start

The included `start-server.sh` handles building and launching:

```bash
chmod +x start-server.sh
./start-server.sh
```

Then open `index.html` in your browser — and open it in a second tab or window to test the collaboration.

Other ways to run it:

```bash
./start-server.sh 9090           # custom port
SKIP_BUILD=1 ./start-server.sh   # skip rebuild, run existing jar
```

If you'd rather drive Maven yourself:

```bash
mvn clean package
java -jar target/collaborative-whiteboard-1.0-SNAPSHOT.jar
```

You should see `Whiteboard server started on ws://localhost:8080` in the terminal.

## Project layout

```
whiteboard/
├── pom.xml                          # Maven config: dependencies, build settings
├── index.html                       # Frontend (entire UI in one file)
├── start-server.sh                  # Build + run helper
├── src/main/java/com/whiteboard/
│   ├── WhiteboardServer.java        # Entry point + WebSocket lifecycle handlers
│   ├── RoomManager.java             # Broadcast logic, session map, history buffer
│   ├── UserSession.java             # Per-user state (id, name, assigned color)
│   └── DrawEvent.java               # Message data container (serialized as JSON)
└── target/                          # Maven build output (gitignored)
```

## Message protocol

Clients and the server exchange JSON messages over the WebSocket. Every message has a `type`:

| Type         | Sent by | Purpose                                  |
| ------------ | ------- | ---------------------------------------- |
| `USER_JOIN`  | client  | Initial handshake with username          |
| `USER_LEAVE` | server  | Announces a user disconnected            |
| `DRAW`       | both    | A single point in a freehand stroke      |
| `LINE`       | both    | A straight line segment                  |
| `CLEAR`      | both    | Clears the entire canvas                 |
| `CURSOR`     | both    | Broadcasts a user's cursor position      |

Example draw event:

```json
{
  "type": "DRAW",
  "userId": "user_a3f9",
  "username": "Alice",
  "color": "#7F77DD",
  "brushSize": 4,
  "x": 120.5,
  "y": 88.0,
  "startStroke": true
}
```

The server stamps each event with the user's server-assigned color before re-broadcasting it (so clients can't spoof colors). New connections receive a replay of the last 500 events so the board looks consistent on join.

## Dependencies

Pulled in by Maven from `pom.xml`:

- [`Java-WebSocket`](https://github.com/TooTallNate/Java-WebSocket) — WebSocket server implementation
- [`Gson`](https://github.com/google/gson) — JSON serialization
- `slf4j-simple` — logging

## Troubleshooting

**Port 8080 already in use.** Check with `ss -tlnp | grep :8080` (Linux/WSL) or `netstat -ano | findstr :8080` (Windows). Either stop the other process or pass a different port: `./start-server.sh 9090` (and update the WebSocket URL in `index.html` to match).

**`404 WebSocket Upgrade Failure` in the browser.** Usually one of:
- Something else is on port 8080 returning a normal 404. See above.
- On WSL2, the Windows ↔ Linux localhost forwarder failed. Try `curl -i http://localhost:8080/` from inside WSL to confirm the server is reachable, or connect using WSL's IP from `hostname -I`.
- Hard refresh the browser with `Ctrl+Shift+R` to clear cached responses.

**Build fails with "class not found" or filename errors.** Java requires `.java` (lowercase) and the filename must match the public class name. The `start-server.sh` script auto-corrects this if you ever drop loose `.java` files at the repo root.
