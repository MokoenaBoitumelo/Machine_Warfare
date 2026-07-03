package com.machinewarfare.network;

import com.machinewarfare.engine.Position;
import com.machinewarfare.engine.World;
import com.machinewarfare.model.Machine;
import com.machinewarfare.model.TankMech;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class ClientHandlerTest {

    /**
     * A lightweight test stub that overrides Socket's stream behavior.
     * This completely bypasses Mockito and Byte Buddy.
     */
    private static class StubSocket extends Socket {
        private final ByteArrayInputStream inputStream;
        private final ByteArrayOutputStream outputStream;
        private boolean isClosed = false;

        public StubSocket(String inputData) {
            this.inputStream = new ByteArrayInputStream(inputData.getBytes());
            this.outputStream = new ByteArrayOutputStream();
        }

        @Override
        public InputStream getInputStream() { return this.inputStream; }

        @Override
        public OutputStream getOutputStream() { return this.outputStream; }

        @Override
        public void close() { this.isClosed = true; }

        public String getReceivedOutput() { return this.outputStream.toString(); }

        public boolean isSocketClosed() { return this.isClosed; }
    }

    @Test
    @DisplayName("ClientHandler should read telemetry streams and send back acknowledgements")
    void testClientHandlerStreamLifecycle() {
        World realWorld = new World(10, 10);
        // PING_PAYLOAD is plain text, which will cause a JSON parsing error frame response
        StubSocket stubSocket = new StubSocket("PING_PAYLOAD\nQUIT\n");

        ClientHandler handler = new ClientHandler(stubSocket, realWorld);
        handler.run();

        String output = stubSocket.getReceivedOutput();
        assertTrue(output.contains("CONNECTION_SUCCESSFUL"));
        // Fix: Expect the JSON parsing error frame response instead of the old echo
        assertTrue(output.contains("ERROR: Invalid payload structural frame."));
        assertTrue(output.contains("DISCONNECTING"));
        assertTrue(stubSocket.isSocketClosed(), "Socket should be explicitly closed by handler.");
    }

    @Test
    @DisplayName("ClientHandler should parse polymorphic JSON payloads and spawn machines in the World")
    void testClientHandlerJsonParsingAndSpawning() {
        World realWorld = new World(10, 10);
        String jsonPayload = "{\"type\":\"tank\",\"id\":\"TK-JSON-ALPHA\",\"health\":200,\"position\":{\"x\":4,\"y\":5},\"destroyed\":false}\nQUIT\n";
        StubSocket stubSocket = new StubSocket(jsonPayload);

        ClientHandler handler = new ClientHandler(stubSocket, realWorld);
        handler.run();

        String output = stubSocket.getReceivedOutput();
        assertTrue(output.contains("DEPLOY_SUCCESS: Registered ID [TK-JSON-ALPHA]"));

        // Spatial Grid Verification
        Machine trackedMachine = realWorld.getMachineAt(new Position(4, 5));
        assertNotNull(trackedMachine, "Machine was not found in the spatial grid index.");
        assertEquals("TK-JSON-ALPHA", trackedMachine.getId());
        assertTrue(trackedMachine instanceof TankMech);
    }
}