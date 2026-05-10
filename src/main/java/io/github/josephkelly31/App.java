package io.github.josephkelly31;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

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

        if (args.length == 0) {
            logger.severe("No arguments given. CSV path is required");
            return;
        } else if (args.length > 1) {
            logger.severe("Too many arguments given. One is required");

        }

        App.greeting();
        App.processTradingCSV(args[0]);
        App.printFarewell();
    }

    private static final void greeting() {
        logger.info("-----------------------------");
        logger.info("-£-£-£-£-£-BIG NET-£-£-£-£-£-");
        logger.info("-----------------------------");
    }

    private static final void processTradingCSV(String csvPath) {
        logger.info("Processing total of: " + csvPath);

        try (BufferedReader file = new BufferedReader(new FileReader(csvPath))) {

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
            logger.info("Processed: " + csvPath);
        }
    }

    public static void printFarewell() {
        // Print farewell message
        logger.info("-----------------------------");
        logger.info("-£-£-£-£-£-£-£-£-£-£-£-£-£-£-");
        logger.info("-----------------------------");
    }

}
