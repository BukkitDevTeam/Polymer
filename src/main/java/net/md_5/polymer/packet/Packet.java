package net.md_5.polymer.packet;

import com.google.common.base.Preconditions;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import io.netty.buffer.ByteBuf;
import java.lang.reflect.InvocationTargetException;

public abstract class Packet {

    private static final Class<? extends Packet>[] byId = new Class[256];
    private static final TObjectIntMap<Class<? extends Packet>> byClass = new TObjectIntHashMap<>(256);
    private int id = -1;

    private static void register(int id, Class<? extends Packet> packetClass) {
        Preconditions.checkArgument(byId[id] == null, "Cannot register already registered packet id %s", id);
        Preconditions.checkArgument(!byClass.containsKey(packetClass), "Cannot register already registered packet class %s", packetClass);

        byId[id] = packetClass;
        byClass.put(packetClass, id);
    }

    public int getId() {
        return id == -1 ? id = byClass.get(getClass()) : id;
    }

    public static Packet newInstance(short id) {
        Class<? extends Packet> clazz = byId[id];
        Preconditions.checkArgument(clazz != null, "Bad packet %s", String.format("0x%02X", id));
        Packet ret = null;

        try {
            ret = clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException("Could not construct packet " + clazz.getSimpleName(), ex);
        }

        return ret;
    }

    public abstract void read(ByteBuf in);

    public abstract void write(ByteBuf out);

    public abstract void handle(PacketHandler handler) throws Exception;

    protected static byte[] readArray(ByteBuf in) {
        short len = in.readShort();
        Preconditions.checkArgument(len > 0, "Received array length less than 0! (%s)", len);

        byte[] value = new byte[len];
        for (short pos = 0; pos < len; pos++) {
            value[pos] = in.readByte();
        }

        return value;
    }

    protected static void writeArray(ByteBuf out, byte[] array) {
        Preconditions.checkArgument(array.length < Short.MAX_VALUE, "Attempted to send array bigger than %s! (%s)", Short.MAX_VALUE, array.length);

        out.writeShort(array.length);
        for (byte b : array) {
            out.writeByte(b);
        }
    }

    protected static String readString(ByteBuf in) {
        short len = in.readShort();
        Preconditions.checkArgument(len > 0, "Received string length less than 0! (%d)", len);

        char[] value = new char[len];
        for (short pos = 0; pos < len; pos++) {
            value[pos] = in.readChar();
        }

        return new String(value);
    }

    protected static void writeString(ByteBuf out, String string) {
        int len = string.length();
        Preconditions.checkArgument(len < Short.MAX_VALUE, "Attempted to send string bigger than %1$d! (%2$s)", Short.MAX_VALUE, string);

        out.writeShort(len);
        for (char c : string.toCharArray()) {
            out.writeChar(c);
        }
    }

    static {
        register(0x01, Login.class);
        register(0x02, Handshake.class);
        register(0xCD, ClientStatus.class);
        register(0xFC, EncryptResponse.class);
        register(0xFD, EncryptRequest.class);
        register(0xFE, ServerListPing.class);
        register(0xFF, Kick.class);
    }
}
