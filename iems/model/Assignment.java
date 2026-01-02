package iems.model;

import java.time.LocalDate;

public class Assignment {
    private long id;
    private String title;
    private String subject;
    private LocalDate dueDate;
    private int totalMarks;
    private long teacherId;

    public Assignment() {
    }

    public Assignment(String title, String subject, LocalDate dueDate, int totalMarks, long teacherId) {
        this.title = title;
        this.subject = subject;
        this.dueDate = dueDate;
        this.totalMarks = totalMarks;
        this.teacherId = teacherId;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }
}