package net.md_5.polymer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandReader extends Thread {

    public CommandReader() {
        super("Console Input Thread");
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (Polymer.getInstance().isRunning()) {
                String line = LogManager.getReader().readLine(">").trim();
                if (!line.isEmpty()) {
                    Polymer.getInstance().dispatchConsoleCommand(line);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CommandReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
