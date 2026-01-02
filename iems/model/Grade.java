package iems.model;

import java.time.LocalDateTime;

public class Grade {
    private long id;
    private long assignmentId;
    private long studentId;
    private int marks; // numeric score
    private int totalMarks; // max possible marks
    private String feedback; // optional teacher feedback
    private long gradedBy; // teacher who graded
    private LocalDateTime gradedAt; // timestamp
    private String studentName; // for reporting
    private String assignmentTitle; // for reporting
    private long userId; // student user_id
    private long teacherId; // teacher user_id
    private int Value;

    public Grade(long studentId, long assignmentId, long teacherId, int marks) {
        this.studentId = studentId;
        this.assignmentId = assignmentId;
        this.teacherId = teacherId;
        this.marks = marks;
    }

    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        Value = value;
    }

    // Constructors
    public Grade() {
        this.gradedAt = LocalDateTime.now();
    }

    public Grade(long assignmentId, long studentId, int marks, String feedback, long gradedBy) {
        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.marks = marks;
        this.feedback = feedback;
        this.gradedBy = gradedBy;
        this.gradedAt = LocalDateTime.now();
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public long getGradedBy() {
        return gradedBy;
    }

    public void setGradedBy(long gradedBy) {
        this.gradedBy = gradedBy;
    }

    public LocalDateTime getGradedAt() {
        return gradedAt;
    }

    public void setGradedAt(LocalDateTime gradedAt) {
        this.gradedAt = gradedAt;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAssignmentTitle() {
        return assignmentTitle;
    }

    public void setAssignmentTitle(String assignmentTitle) {
        this.assignmentTitle = assignmentTitle;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }

    // Helper methods
    public double getPercentage() {
        if (totalMarks > 0) {
            return (marks * 100.0) / totalMarks;
        }
        return 0.0;
    }

    public String getLetterGrade() {
        double percentage = getPercentage();
        if (percentage >= 90)
            return "A";
        if (percentage >= 80)
            return "B";
        if (percentage >= 70)
            return "C";
        if (percentage >= 60)
            return "D";
        return "F";
    }

    public String getGradeStatus() {
        return marks > 0 ? "Graded" : "Pending";
    }

    public boolean isGraded() {
        return marks > 0;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "assignmentId=" + assignmentId +
                ", studentId=" + studentId +
                ", marks=" + marks +
                ", feedback='" + (feedback != null ? feedback : "null") + '\'' +
                ", gradedAt=" + gradedAt +
                '}';
    }
}