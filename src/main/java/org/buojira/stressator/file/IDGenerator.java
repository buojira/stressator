package org.buojira.stressator.file;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class IDGenerator {

    private static IDGenerator instance;

    private long count;
    private String hostName ;

    public static IDGenerator getInstance() {
        if (instance == null) {
            instance = new IDGenerator();
        }
        return instance;
    }

    private IDGenerator() {
        count = 0;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            hostName = InetAddress.getLoopbackAddress().getHostAddress();
        }
    }

    public String generateID() {
        return hostName + "|" + (count++) + "|" + UUID.randomUUID();
    }

}
