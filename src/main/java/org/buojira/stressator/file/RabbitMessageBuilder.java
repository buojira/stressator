package org.buojira.stressator.file;

import java.io.IOException;

public final class RabbitMessageBuilder {

    public static RabbitMessage build() throws IOException {
        RabbitMessage rabbitMessage = new RabbitMessage();
        rabbitMessage.setContent(FileLoader.getInstance().getContent());
        IDGenerator gen = IDGenerator.getInstance();
        rabbitMessage.setId(gen.generateID(), gen.getCount());
        return rabbitMessage;
    }

}
