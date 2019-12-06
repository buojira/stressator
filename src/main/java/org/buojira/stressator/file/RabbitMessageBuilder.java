package org.buojira.stressator.file;

import java.io.IOException;

public final class RabbitMessageBuilder {

    public static RabbitMessage build() throws IOException {
        RabbitMessage rabbitMessage = new RabbitMessage();
        rabbitMessage.setContent(FileLoader.getInstance().getContent());
        rabbitMessage.setId(IDGenerator.getInstance().generateID());
        return rabbitMessage;
    }

}
