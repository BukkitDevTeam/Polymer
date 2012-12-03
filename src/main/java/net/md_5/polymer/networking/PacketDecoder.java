package net.md_5.polymer.networking;

import net.md_5.polymer.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class PacketDecoder extends ReplayingDecoder<Packet, Void> {

    @Override
    public Packet decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        short id = in.readUnsignedByte();
        Packet packet = Packet.newInstance(id);
        packet.read(in);
        return packet;
    }
}
