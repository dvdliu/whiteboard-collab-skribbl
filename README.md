# Doodle Room
A collaborate whiteboard made to draw with friends. This is an initial draft I had for an indea to make a team based version of skribbl.io, where multiple people can participate together to draw out different objects/words that people can guess. Currently only works as a whiteboard.

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

