package org.buojira.stressator.file;

import java.io.File;
import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FileLoaderTest {

    @Test
    public void readFileThenSuccess() {
        for (int i = 0; i < 100; i++) {
            File file = FileLoader.getInstance().getSomeFile();
            System.out.println(file.getAbsolutePath());
            Assertions.assertThat(file).exists();
        }
    }

    @Test
    public void readContentThenSuccess() throws IOException {
        for (int i = 0; i < 100; i++) {
            String content = FileLoader.getInstance().getContent();
            Assertions.assertThat(content).isNotBlank();
        }
    }
}
