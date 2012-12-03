package net.md_5.polymer.networking;

import com.google.common.base.Preconditions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import net.md_5.polymer.ChatColor;
import net.md_5.polymer.Polymer;
import net.md_5.polymer.packet.ClientStatus;
import net.md_5.polymer.packet.EncryptRequest;
import net.md_5.polymer.packet.EncryptResponse;
import net.md_5.polymer.packet.Handshake;
import net.md_5.polymer.packet.PacketHandler;
import net.md_5.polymer.packet.ServerListPing;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class LoginHandler extends PacketHandler {

    private static final Random random = new Random();
    /*========================================================================*/
    private LoginState state = LoginState.HANDSHAKE;
    private Handshake handshake;
    private EncryptRequest encryptRequest;
    private Key secretKey;

    private enum LoginState {

        HANDSHAKE, RESPONSE, LOGIN;
    }

    private void checkState(LoginState state) {
        Preconditions.checkState(this.state == state, "Not expecting %s at this time!", state);
    }

    private BufferedBlockCipher getCipher(boolean forEncryption) {
        BufferedBlockCipher cip = new BufferedBlockCipher(new CFBBlockCipher(new AESFastEngine(), 8));
        cip.init(forEncryption, new ParametersWithIV(new KeyParameter(secretKey.getEncoded()), secretKey.getEncoded()));
        return cip;
    }

    public LoginHandler(InboundHandler handler) {
        super(handler);
    }

    @Override
    public void handle(ServerListPing ping) {
        String kickMessage = ""
                + ChatColor.DARK_BLUE + "\00"
                + Polymer.PROTOCOL_VERSION + "\00"
                + Polymer.MINECRAFT_VERSION + "\00"
                + Polymer.getInstance().getConfig().getMotd() + "\00"
                + Polymer.getInstance().getOnlinePlayers() + "\00"
                + Polymer.getInstance().getConfig().getMaxPlayers();
        handler.disconnect(kickMessage);
    }

    @Override
    public void handle(Handshake handshake) {
        checkState(LoginState.HANDSHAKE);
        this.handshake = handshake;

        String serverId = Polymer.getInstance().getConfig().isOnlineMode() ? Long.toString(random.nextLong(), 16) : "-";
        byte[] publicKey = Polymer.getInstance().getKeyPair().getPublic().getEncoded();
        byte[] verifyToken = new byte[4];
        random.nextBytes(verifyToken);
        encryptRequest = new EncryptRequest(serverId, publicKey, verifyToken);
        handler.write(encryptRequest);

        state = LoginState.RESPONSE;
    }

    @Override
    public void handle(EncryptResponse response) throws Exception {
        checkState(LoginState.RESPONSE);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, Polymer.getInstance().getKeyPair().getPrivate());
        byte[] decrypted = cipher.doFinal(response.getVerifyToken());

        Preconditions.checkState(Arrays.equals(encryptRequest.getVerifyToken(), decrypted), "Unable to confirm key pair negotiation!");

        cipher.init(Cipher.DECRYPT_MODE, Polymer.getInstance().getKeyPair().getPrivate());
        byte[] secret = cipher.doFinal(response.getSharedSecret());

        secretKey = new SecretKeySpec(secret, "AES");
        new AuthThread().start();
    }

    @Override
    public void handle(ClientStatus status) throws Exception {
        checkState(LoginState.LOGIN);
    }

    private class AuthThread extends Thread {

        public AuthThread() {
            setDaemon(true);
        }

        @Override
        public void run() {
            try {
                if (Polymer.getInstance().getConfig().isOnlineMode()) {
                    String username = URLEncoder.encode(handshake.getName(), "UTF-8");

                    MessageDigest digest = MessageDigest.getInstance("SHA-1");
                    for (byte[] bit : new byte[][]{encryptRequest.getServerId().getBytes("ISO_8859_1"), secretKey.getEncoded(), encryptRequest.getPublicKey()}) {
                        digest.update(bit);
                    }

                    String serverHash = URLEncoder.encode(new BigInteger(digest.digest()).toString(16), "UTF-8");
                    String authURL = "http://session.minecraft.net/game/checkserver.jsp?user=" + username + "&serverId=" + serverHash;
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(authURL).openStream()))) {
                        if (!"YES".equals(in.readLine())) {
                            throw new NotPremiumException();
                        }
                    }
                }

                handler.write(new EncryptResponse(new byte[0], new byte[0]));
                handler.getChannel().pipeline().addBefore("decoder", "crypter", new CipherCodec(getCipher(true), getCipher(false)));
                state = LoginState.LOGIN;
            } catch (NoSuchAlgorithmException | IOException ex) {
                handler.error(ex);
            }
        }
    }

    private static class NotPremiumException extends RuntimeException {

        public NotPremiumException() {
            super("You must have a premium account to use this server");
        }
    }
}
