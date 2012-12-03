package net.md_5.polymer.networking;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToByteCodec;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.crypto.BufferedBlockCipher;

@RequiredArgsConstructor
public class CipherCodec extends ByteToByteCodec {

    private final BufferedBlockCipher encrypt;
    private final BufferedBlockCipher decrypt;

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        process(encrypt, in, out);
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        process(decrypt, in, out);
    }

    private void process(BufferedBlockCipher cipher, ByteBuf in, ByteBuf out) {
        int readable = in.readableBytes();
        out.capacity(cipher.getOutputSize(readable));
        int processed = cipher.processBytes(in.array(), 0, readable, out.array(), 0);
        in.readerIndex(readable);
        out.writerIndex(processed);
    }
}
