package org.buojira.stressator.rabbit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.stereotype.Component;

@Component
public class BrokerProperties {

    @Value("${application.exchange}")
    private String exchangeName;

    @Value("${application.queue.name}")
    private String queueName;

    @Value("${application.code}")
    private String appCode;

    @Value("${broker.listener.tags}")
    private String tags;

    @Value("${broker.host}")
    private String brokerHost;

    @Value("${broker.port}")
    private String brokerPort;

    @Value("${broker.username}")
    private String brokerUserName;

    @Value("${broker.password}")
    private String brokerPassword;

    @Value("${broker.virtual.host}")
    private String virtualHost;

    @Value("${application.status.queue}")
    private String brokerStatusQueue;

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

    public String getExchangeName() {
        return exchangeName;
    }

    public void setBrokerHost(String brokerHost) {
        this.brokerHost = brokerHost;
    }

    public void setBrokerPort(String brokerPort) {
        this.brokerPort = brokerPort;
    }

    public void setBrokerUserName(String brokerUserName) {
        this.brokerUserName = brokerUserName;
    }

    public void setBrokerPassword(String brokerPassword) {
        this.brokerPassword = brokerPassword;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getBrokerStatusQueue() {
        return brokerStatusQueue;
    }

    public void setBrokerStatusQueue(String brokerStatusQueue) {
        this.brokerStatusQueue = brokerStatusQueue;
    }

}
