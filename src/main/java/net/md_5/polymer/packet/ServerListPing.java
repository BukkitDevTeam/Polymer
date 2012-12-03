package net.md_5.polymer.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ServerListPing extends Packet {

    private byte version;

    public ServerListPing() {
    }

    @Override
    public void read(ByteBuf in) {
        version = in.readByte();
    }

    @Override
    public void write(ByteBuf out) {
        out.writeByte(version);
    }

    @Override
    public void handle(PacketHandler handler) throws Exception {
        handler.handle(this);
    }
}
