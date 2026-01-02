package iems.model;

import java.time.LocalDate;

public class Attendance {
    private long id;
    private long studentId;
    private long teacherId;
    private LocalDate date;
    private boolean present;

    public Attendance(long studentId, long teacherId, LocalDate date, boolean present) {
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.date = date;
        this.present = present;
    }

    public Attendance() {
        // TODO Auto-generated constructor stub
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }
}