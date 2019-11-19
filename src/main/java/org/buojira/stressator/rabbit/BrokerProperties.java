package org.buojira.stressator.rabbit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BrokerProperties {

    @Value("${fluig.locksmith.queue.name}")
    private String queueName;

    @Value("${fluig.locksmith.app.code}")
    private String appCode;

    @Value("${fluig.locksmith.tags}")
    private String tags;

    @Value("${fluig.broker.host}")
    private String brokerHost;

    @Value("${fluig.broker.port}")
    private String brokerPort;

    @Value("${fluig.broker.username}")
    private String brokerUserName;

    @Value("${fluig.broker.password}")
    private String brokerPassword;

    @Value("${fluig.broker.virtual.host}")
    private String virtualHost;

    public String getQueueName() {
        return queueName;
    }

    public String getAppCode() {
        return appCode;
    }

    public String getTags() {
        return tags;
    }

    public String getBrokerHost() {
        return brokerHost;
    }

    public String getBrokerPort() {
        return brokerPort;
    }

    public String getBrokerUserName() {
        return brokerUserName;
    }

    public String getBrokerPassword() {
        return brokerPassword;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

}
