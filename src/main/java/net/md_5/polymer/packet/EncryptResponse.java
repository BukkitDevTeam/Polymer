package net.md_5.polymer.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EncryptResponse extends Packet {

    private byte[] sharedSecret;
    private byte[] verifyToken;

    public EncryptResponse() {
    }

    @Override
    public void read(ByteBuf in) {
        sharedSecret = readArray(in);
        verifyToken = readArray(in);
    }

    @Override
    public void write(ByteBuf out) {
        writeArray(out, sharedSecret);
        writeArray(out, verifyToken);
    }

    @Override
    public void handle(PacketHandler handler) throws Exception {
        handler.handle(this);
    }
}
