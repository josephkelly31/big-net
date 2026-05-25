package io.github.josephkelly31;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.util.logging.Level;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class App {

    private static final Logger logger = Logger.getLogger(App.class.getName());

    static {
        try {
            ConsoleHandler handler = new ConsoleHandler();
            handler.setFormatter(new Formatter() {
                private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                @Override
                public String format(LogRecord record) {
                    return dtf.format(LocalDateTime.now()) + " [" + record.getLevel() + "] " + record.getMessage()
                            + "\n";
                }
            });

            logger.setUseParentHandlers(false); // disables the default handler
            logger.addHandler(handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        App.greeting();

        Map<String, String> kwargs = new HashMap<>();
        String flagName;
        boolean debug = false;
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].startsWith("--")) {
                flagName = args[i].substring(2);
                if (flagName.equals("debug")) {
                    debug = true;
                } else {
                    kwargs.put(flagName, args[i + 1]);
                }
            }
        }

        if (debug) {
            logger.setLevel(Level.FINE);
            logger.getHandlers()[0].setLevel(Level.FINE);
            logger.fine("Debug mode: ON");
        }

        if (!kwargs.containsKey("path")) {
            logger.severe("No `--path` argument given. One is required.");
            System.exit(1);
        }

        Path p = Path.of(kwargs.get("path"));
        if (Files.isDirectory(p)) {
            App.processDirectory(p);
        } else if (Files.isRegularFile(p)) {
            App.processFile(p);
        } else {
            logger.severe("File path provided corresponds to neither a directory or file.");
        }

        App.printFarewell();
    }

    private static final void greeting() {
        logger.info("-----------------------------");
        logger.info("-£-£-£-£-£-BIG NET-£-£-£-£-£-");
        logger.info("-----------------------------");
    }

    private static final void processDirectory(Path p) {
        try (Stream<Path> entries = Files.list(p)) {
            entries.forEach(App::processFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final void processFile(Path p) {
        logger.fine("Processing total of: " + p.toString());

        // Check if directory or single file

        try (BufferedReader file = Files.newBufferedReader(p)) {

            String header = file.readLine();
            String[] headers = header.split(",");
            int totalIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].equals("Total")) {
                    totalIndex = i;
                }
            }

            if (totalIndex == -1) {
                System.out
                        .println("Unable to find 'Total' in provided csv file keys. Provided keys: "
                                + Arrays.toString(headers));
                return;
            }

            String line;
            float sum = 0f;
            while ((line = file.readLine()) != null) {
                sum += Float.parseFloat(line.split(",")[totalIndex]);
            }
            logger.info("Total: £" + String.format("%.2f", sum));
            file.close();
        } catch (Exception e) {
            logger.severe(e.getMessage());
        } finally {
            logger.fine("Processed: " + p.toString());
        }
    }

    public static void printFarewell() {
        // Print farewell message
        logger.info("-----------------------------");
        logger.info("-£-£-£-£-£-£-£-£-£-£-£-£-£-£-");
        logger.info("-----------------------------");
    }

}
