package org.buojira.stressator.file;

public class RabbitMessage {

    private String id;
    private Long count;
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id, Long count) {
        this.id = id;
        this.count = count;
    }

    public Long getCount() {
        return count;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
