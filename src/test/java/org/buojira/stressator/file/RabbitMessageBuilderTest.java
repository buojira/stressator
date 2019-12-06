package org.buojira.stressator.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RabbitMessageBuilderTest {

    @Test
    public void createMessagesThenSuccess() throws IOException {

        List<RabbitMessage> messages = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            messages.add(RabbitMessageBuilder.build());
        }

        Assertions.assertThat(messages.get(4).getContent()).isEqualTo(messages.get(9).getContent());
        Assertions.assertThat(messages.get(4).getContent()).isEqualTo(messages.get(14).getContent());
        Assertions.assertThat(messages.get(8).getContent()).isEqualTo(messages.get(17).getContent());

    }

}
