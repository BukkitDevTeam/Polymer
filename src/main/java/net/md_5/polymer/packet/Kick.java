package net.md_5.polymer.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Kick extends Packet {

    private String message;

    public Kick() {
    }

    @Override
    public void read(ByteBuf in) {
        message = readString(in);
    }

    @Override
    public void write(ByteBuf out) {
        writeString(out, message);
    }

    @Override
    public void handle(PacketHandler handler) throws Exception {
        handler.handle(this);
    }
}
