package org.buojira.stressator;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionDecider {

    public static final String ACTION_TEST_RABBITMQ_DDS = "ddsrabbitmq";
    public static final String ACTION_CLEAR_RABBITMQ = "clearrabbitmq";
    public static final String BROKER_HOST = "broker.host";
    public static final String BROKER_PORT = "broker.port";
    public static final String BROKER_USER = "broker.username";
    public static final String BROKER_PASS = "broker.password";
    public static final String DURATION = "duration";
    public static final String DURATIONS = "durations";
    public static final String TOTALS = "totals";

    private final String argLine;

    private boolean testRabbitMQDDS;
    private boolean clearRabbitMQ;
    private String rabbitMQHost;
    private Number rabbitMQPort;
    private String rabbitMQUSER;
    private String rabbitMQPassword;

    public ActionDecider(String[] params) {
        setDefaultValues();
        argLine = getArgLine(params);

        System.out.println(" ");
        System.out.println("-----------------------------------");
        System.out.println("-----------------------------------");
        System.out.println("Argument Line: " + argLine);
        System.out.println("-----------------------------------");
        System.out.println("-----------------------------------");
        System.out.println(" ");

        setActions(argLine);
        setBrokerProperties(argLine);
    }

    private void setDefaultValues() {
        testRabbitMQDDS = false;
    }

    private void setBrokerProperties(String argLine) {
        rabbitMQHost = getProperty(argLine, BROKER_HOST, null);
        rabbitMQPort = getNumericProperty(argLine, BROKER_PORT, null);
        rabbitMQUSER = getProperty(argLine, BROKER_USER, null);
        rabbitMQPassword = getProperty(argLine, BROKER_PASS, null);
    }

    private Number getNumericProperty(String argLine, String property, Number defaultValue) {
        Matcher matcher = getPropertyMatcher(property, argLine);
        if (matcher.find()) {
            return Integer.valueOf(matcher.group(1));
        } else {
            return defaultValue;
        }
    }

    private String getProperty(String argLine, String property, String defaultValue) {
        Matcher matcher = getPropertyMatcher(property, argLine);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return defaultValue;
        }
    }

    private Matcher getPropertyMatcher(String property, String argLine) {
        return Pattern.compile(property + "[\\=\\:](\\S+)\\s").matcher(argLine);
    }

    private void setActions(String argLine) {
        Pattern actionsPattern = Pattern.compile("action[s]*[\\=\\:]([\\S;]+)\\s");

        String[] actions = getMultiValues(
                actionsPattern,
                argLine.toLowerCase(),
                "\\W"
        );

        for (int i = 0; i < actions.length; i++) {
            switch (actions[i].trim()) {
                case ACTION_TEST_RABBITMQ_DDS:
                    testRabbitMQDDS = true;
                    break;
                case ACTION_CLEAR_RABBITMQ:
                    clearRabbitMQ = true;
                    break;
            }
        }

    }

    private String getArgLine(String[] params) {
        StringBuilder line = new StringBuilder();
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                line.append(" ").append(params[i]);
            }
        }
        return line.append(" ").toString();
    }

    public boolean isTestRabbitMQDDS() {
        return testRabbitMQDDS;
    }

    public boolean isClearRabbitMQ() {
        return clearRabbitMQ;
    }

    public String getRabbitMQHost() {
        return rabbitMQHost;
    }

    public Number getRabbitMQPort() {
        return rabbitMQPort;
    }

    public String getRabbitMQUSER() {
        return rabbitMQUSER;
    }

    public String getRabbitMQPassword() {
        return rabbitMQPassword;
    }

    public Number getDuration() {
        Matcher matcher = Pattern.compile(
                DURATION + "[\\=\\:]([\\d;]+)"
        ).matcher(argLine.toLowerCase());

        if (matcher.find()) {
            return Integer.valueOf(matcher.group(1));
        } else {
            return 5;
        }

    }

    public Number[] getTotals() throws ParseException {
        return getMultiNumbers(TOTALS, argLine.toLowerCase());
    }

    public Number[] getDurations() throws ParseException {
        return getMultiNumbers(DURATIONS, argLine.toLowerCase());
    }

    private Number[] getMultiNumbers(String property, String line)
            throws ParseException {

        Matcher matcher = Pattern.compile(
                property + "[\\=\\:]([\\S]+)\\s"
        ).matcher(line);

        if (matcher.find()) {
            String[] values = matcher.group(1).split(";");
            Number[] numbers = new Number[values.length];
            for (int i = 0; i < values.length; i++) {
                numbers[i] = Formatter.DECIMAL_FORMAT.parse(values[i]);
            }
            return numbers;
        } else {
            return new Number[]{0};
        }
    }

    private String[] getMultiValues(Pattern pattern, String line, String regexArgSeparator) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String actions = matcher.group(1);
            return actions.split(regexArgSeparator);
        }
        return new String[]{""};
    }

}
