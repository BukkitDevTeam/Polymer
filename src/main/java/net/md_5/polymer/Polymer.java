package net.md_5.polymer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import lombok.Getter;
import net.md_5.polymer.networking.PacketDecoder;
import net.md_5.polymer.networking.PacketEncoder;
import net.md_5.polymer.networking.InboundHandler;

public class Polymer {

    public static final byte PROTOCOL_VERSION = 49;
    public static final String MINECRAFT_VERSION = "1.4.5";
    private static final int TPS = 20;
    private static final int TICK_TIME = 1000000000 / TPS;
    /*========================================================================*/
    @Getter
    private static final Polymer instance = new Polymer();
    @Getter
    private static final Logger logger = Logger.getLogger("Polymer");
    /*========================================================================*/
    @Getter
    private Configuration config;
    private ServerBootstrap serverBootstrap;
    private Channel serverChannel;
    private int elapsedTicks;
    @Getter
    private KeyPair keyPair;
    private final List<String> pendingCommands = new ArrayList<>();
    /*========================================================================*/
    @Getter
    private volatile boolean running;

    public static void main(String[] args) throws Exception {
        instance.start();
    }

    public void start() throws Exception {
        running = true;
        LogManager.init(logger);

        getLogger().info("Loading configuration");
        config = Configuration.load("config/polymer.yml");

        getLogger().info("Generating key pairs");
        keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();

        getLogger().info("Binding server");
        serverBootstrap = new ServerBootstrap().channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer() {
            @Override
            public void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast("timer", new ReadTimeoutHandler(config.getTimeout()));
                ch.pipeline().addLast("decoder", new PacketDecoder());
                ch.pipeline().addLast("encoder", new PacketEncoder());
                ch.pipeline().addLast("handler", new InboundHandler());
            }
        }).childOption(ChannelOption.IP_TOS, 0x18).childOption(ChannelOption.TCP_NODELAY, true).group(new NioEventLoopGroup()).localAddress(config.getIp(), config.getPort());
        serverChannel = serverBootstrap.bind().channel();

        getLogger().info("Starting tick loop");
        long lastTick = System.nanoTime();
        try {
            while (running) {
                long wait = TICK_TIME - (System.nanoTime() - lastTick);
                if (wait > 0) {
                    Thread.sleep(wait / 1000000);
                }
                lastTick = System.nanoTime();
                elapsedTicks++;
                tick();
            }
        } finally {
            getLogger().info("Shutting down");
            running = false;
            serverChannel.close();
            serverBootstrap.shutdown();
        }
    }

    private void tick() {
        synchronized (pendingCommands) {
            for (String command : pendingCommands) {
                Polymer.getLogger().info("ECHO1: " + command);
                System.out.println("ECHO2: " + command);
            }
            pendingCommands.clear();
        }
    }

    public int getOnlinePlayers() {
        return 0;
    }

    public void dispatchConsoleCommand(String command) {
        synchronized (pendingCommands) {
            pendingCommands.add(command);
        }
    }
}
