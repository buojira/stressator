package org.buojira.stressator.rabbit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fluig.broker.domain.BrokerRequestHeader;
import com.fluig.broker.domain.ChannelVO;
import com.fluig.broker.rabbit.RabbitMQAbstractConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

public class MessageConsumer extends RabbitMQAbstractConsumer<String> {

    private final MessageConsumerService service;

    public MessageConsumer(ChannelVO channel, String consumerTag, MessageConsumerService service) {
        super(channel, consumerTag);
        this.service = service;
    }

    @Override
    public void handleDelivery(String consumerTag,
            Envelope envelope, AMQP.BasicProperties properties, byte[] content) throws IOException {
        try {
            handleDelivery(null, getBody(content));
        } catch (Exception ex) {
            throw new IOException(ex);
        } finally {
            if (!isAutoAcknowledgeEnabled()) {
                acknowledge(envelope.getDeliveryTag(), false);
            }
        }
    }

    @Override
    protected String getBody(byte[] RequestInBytes) {
        JSONParser p = new JSONParser();
        try {
            Object parsed = p.parse(new String(RequestInBytes, StandardCharsets.UTF_8));
            JSONObject request = (JSONObject) parsed;
            String body = String.valueOf(request.get("body"));
            return new String(Base64.getDecoder().decode(body), StandardCharsets.UTF_8);
        } catch (ParseException e) {
            e.printStackTrace();
            return new String(RequestInBytes, StandardCharsets.UTF_8);
        }
    }

    @Override
    public void handleDelivery(BrokerRequestHeader brokerRequestHeader, String register) {
        String[] split = register.split("\\|");
        countMessage(split[0]);
        if (getTotalAmmount() % 10000 == 0) {
            System.out.println(register);
        }
    }

    protected int getTotalAmmount() {
        return service.getTotalAmmount();
    }

    protected void countMessage(String host) {
        service.countMessage(host);
    }

    protected void wait4It(Number milliseconds) {
        try {
            Thread.sleep(milliseconds.longValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
