package org.buojira.stressator.rabbit;

import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import org.buojira.stressator.file.RabbitMessage;

import com.google.common.collect.Queues;

public class MessageRepository {

    private static MessageRepository instance;

    private final Map<String, Number> queueAgeMap;
    private final Queue<String> messageCache;
    private final Queue<RabbitMessage> rabbitMessages;

    public static MessageRepository getInstance() {
        if (instance == null) {
            instance = new MessageRepository();
        }
        return instance;
    }

    public MessageRepository() {
        queueAgeMap = new TreeMap<>();
        messageCache = Queues.newConcurrentLinkedQueue();
        rabbitMessages = Queues.newConcurrentLinkedQueue();
    }

    public Map<String, Number> getQueueAgeMap() {
        return queueAgeMap;
    }

    public Queue<String> getMessageCache() {
        return messageCache;
    }

    public Queue<RabbitMessage> getRabbitMessagesCache() {
        return rabbitMessages;
    }

    public boolean isRabbitMessageQueueEmpty() {
        return rabbitMessages.isEmpty();
    }
}
