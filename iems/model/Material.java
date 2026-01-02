package iems.model;

import java.time.LocalDateTime;

public class Material {
    private long id;
    private String title;
    private String topic;
    private String level;
    private String format;
    private String filePath;
    private long teacherId;
    private LocalDateTime createdAt;

    public Material() {

    }

    public Material(String title, String topic, String level, String format, String filePath, long teacherId) {
        this.title = title;
        this.topic = topic;
        this.level = level;
        this.format = format;
        this.filePath = filePath;
        this.teacherId = teacherId;
        this.createdAt = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}