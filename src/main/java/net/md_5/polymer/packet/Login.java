package net.md_5.polymer.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Login extends Packet {

    private int entityId;
    private String levelType;
    private byte gameMode;
    private byte dimesion;
    private byte difficulty;
    private byte unused;
    private byte maxPlayers;

    public Login() {
    }

    @Override
    public void read(ByteBuf in) {
        entityId = in.readInt();
        levelType = readString(in);
        gameMode = in.readByte();
        dimesion = in.readByte();
        difficulty = in.readByte();
        unused = in.readByte();
        maxPlayers = in.readByte();
    }

    @Override
    public void write(ByteBuf out) {
        out.writeInt(entityId);
        writeString(out, levelType);
        out.writeByte(gameMode);
        out.writeByte(dimesion);
        out.writeByte(difficulty);
        out.writeByte(unused);
        out.writeByte(maxPlayers);
    }

    @Override
    public void handle(PacketHandler handler) throws Exception {
        handler.handle(this);
    }
}
