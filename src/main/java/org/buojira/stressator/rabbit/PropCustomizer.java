package org.buojira.stressator.rabbit;

public class PropCustomizer {

    public BrokerProperties customizeProps(BrokerProperties source, String prefix) {
        BrokerProperties res = new BrokerProperties();
        res.setBrokerHost(source.getBrokerHost());
        res.setBrokerPort(source.getBrokerPort());
        res.setVirtualHost(source.getVirtualHost());
        res.setBrokerUserName(source.getBrokerUserName());
        res.setBrokerPassword(source.getBrokerPassword());
        res.setAppCode(source.getAppCode());
        res.setTags(prefix + "_tag");
        res.setExchangeName(getExchange(prefix));
        res.setQueueName(getQueue(prefix));
        res.setBrokerStatusQueue(getCallbackQueue(prefix));
        return res;
    }

    public String getExchange(String prefix) {
        return prefix + "_exchange";
    }

    public String getQueue(String prefix) {
        return prefix + "_queue";
    }

    public String getCallbackQueue(String prefix) {
        return prefix + "_callback_queue";
    }

}
