package net.md_5.polymer.networking;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import java.util.logging.Level;
import lombok.Getter;
import net.md_5.polymer.Polymer;
import net.md_5.polymer.packet.Packet;
import net.md_5.polymer.packet.Kick;
import net.md_5.polymer.packet.PacketHandler;

public class InboundHandler extends ChannelInboundMessageHandlerAdapter<Packet> {

    @Getter
    private Channel channel;
    private PacketHandler handler = new LoginHandler(this);
    private volatile boolean connected;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
        connected = true;
        Polymer.getLogger().log(Level.SEVERE, channel + " has connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        connected = false;
        Polymer.getLogger().log(Level.SEVERE, channel + " has disconnected");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        error(cause);
    }

    public void error(Throwable t) {
        if (connected) {
            connected = false;
            disconnect("[Error] " + t.getClass().getSimpleName() + ": " + t.getMessage());
            Polymer.getLogger().log(Level.WARNING, "", t);
        }
    }

    public void write(Packet packet) {
        channel.write(packet);
    }

    public void disconnect(String message) {
        channel.write(new Kick(message));
        channel.close();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, Packet msg) throws Exception {
        try {
            msg.handle(handler);
        } catch (Exception t) {
            error(t);
        }
    }
}
