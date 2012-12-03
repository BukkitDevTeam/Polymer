package net.md_5.polymer.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ClientStatus extends Packet {

    private byte status;

    public ClientStatus() {
    }

    @Override
    public void read(ByteBuf in) {
        status = in.readByte();
    }

    @Override
    public void write(ByteBuf out) {
        out.writeByte(status);
    }

    @Override
    public void handle(PacketHandler handler) throws Exception {
        handler.handle(this);
    }
}
