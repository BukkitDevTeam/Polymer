package net.md_5.polymer.packet;

import lombok.Data;
import net.md_5.polymer.networking.InboundHandler;

@Data
public class PacketHandler {

    protected final InboundHandler handler;

    public void handle(Packet packet) throws Exception {
        throw new UnsupportedOperationException("Don't know how to deal with packet class " + packet.getClass().getSimpleName());
    }

    public void handle(ServerListPing ping) throws Exception {
        handle(ping);
    }

    public void handle(Handshake handshake) throws Exception {
        handle(handshake);
    }

    public void handle(EncryptResponse response) throws Exception {
        handle(response);
    }

    public void handle(ClientStatus status) throws Exception {
        handle(status);
    }
}
