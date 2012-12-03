package net.md_5.polymer;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class LogManager {

    public static void init(Logger logger) {
        logger.setUseParentHandlers(false);
        logger.addHandler(new ConsoleHandler());
    }
}
