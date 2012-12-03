package net.md_5.polymer.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Handshake extends Packet {

    private byte version;
    private String name;
    private String host;
    private int port;

    public Handshake() {
    }

    @Override
    public void read(ByteBuf in) {
        version = in.readByte();
        name = readString(in);
        host = readString(in);
        port = in.readInt();
    }

    @Override
    public void write(ByteBuf out) {
        out.writeByte(version);
        writeString(out, name);
        writeString(out, host);
        out.writeInt(port);
    }

    @Override
    public void handle(PacketHandler handler) throws Exception {
        handler.handle(this);
    }
}
