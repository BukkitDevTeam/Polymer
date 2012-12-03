package net.md_5.polymer.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EncryptRequest extends Packet {

    private String serverId;
    private byte[] publicKey;
    private byte[] verifyToken;

    public EncryptRequest() {
    }

    @Override
    public void read(ByteBuf in) {
        serverId = readString(in);
        publicKey = readArray(in);
        verifyToken = readArray(in);
    }

    @Override
    public void write(ByteBuf out) {
        writeString(out, serverId);
        writeArray(out, publicKey);
        writeArray(out, verifyToken);
    }

    @Override
    public void handle(PacketHandler handler) throws Exception {
        handler.handle(this);
    }
}
