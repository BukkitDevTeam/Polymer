package net.md_5.polymer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import jline.console.ConsoleReader;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class LogManager {

    @Getter
    private static ConsoleReader reader;
    private static final DateFormat date = new SimpleDateFormat("HH:mm:ss");

    public static void init(Logger logger) throws Exception {
        reader = new ConsoleReader(System.in, System.out);
        Runtime.getRuntime().addShutdownHook(new Shutdown());

        logger.setUseParentHandlers(false);
        logger.addHandler(new TerminalHandler());

        System.setErr(new PrintStream(new LoggerOutputStream(logger, Level.SEVERE), true));
        System.setOut(new PrintStream(new LoggerOutputStream(logger, Level.INFO), true));

        new CommandReader().start();
    }

    private static class ConsoleFormatter extends Formatter {

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            Throwable ex = record.getThrown();

            builder.append(date.format(record.getMillis()));
            builder.append(" [");
            builder.append(record.getLevel().getLocalizedName().toUpperCase());
            builder.append("] ");
            builder.append(formatMessage(record));
            builder.append('\n');

            if (ex != null) {
                StringWriter writer = new StringWriter();
                ex.printStackTrace(new PrintWriter(writer));
                builder.append(writer);
            }

            return builder.toString();
        }
    }

    private static class TerminalHandler extends ConsoleHandler {

        public TerminalHandler() {
            setFormatter(new ConsoleFormatter());
        }

        @Override
        public synchronized void flush() {
            try {
                reader.print(ConsoleReader.RESET_LINE + "");
                reader.flush();
                super.flush();
                reader.drawLine();
                reader.flush();
            } catch (Exception ex) {
                reader.getCursorBuffer().clear();
            }
        }
    }

    @AllArgsConstructor
    private static class LoggerOutputStream extends ByteArrayOutputStream {

        private final Logger logger;
        private final Level level;

        @Override
        public void flush() throws IOException {
            super.flush();
            String record = toString().trim();
            super.reset();

            if (!record.isEmpty()) {
                logger.log(level, record);
            }
        }
    }

    private static class Shutdown extends Thread {

        @Override
        public void run() {
            try {
                reader.getTerminal().restore();
            } catch (Exception ex) {
            }
        }
    }
}
