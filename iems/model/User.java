package iems.model;

public class User {
    private long id;
    private String fullName;
    private String email;
    private Role role; // enum STUDENT, TEACHER, ADMIN
    private String Password;
    private int Age;
    private String PasswordHash;
    private boolean HighContrast;
    private int PreferredFontScale;
    private String Theme;
    private String resetToken; // 🔹 new field

    public String getTheme() {
        return Theme;
    }

    public void setTheme(String theme) {
        Theme = theme;
    }

    public int getPreferredFontScale() {
        return PreferredFontScale;
    }

    public void setPreferredFontScale(int preferredFontScale) {
        PreferredFontScale = preferredFontScale;
    }

    public boolean isHighContrast() {
        return HighContrast;
    }

    public void setHighContrast(boolean highContrast) {
        HighContrast = highContrast;
    }

    public String getPasswordHash() {
        return PasswordHash;
    }

    public void setPasswordHash(String passwordHash) {
        PasswordHash = passwordHash;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int age) {
        Age = age;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // 🔹 New getter/setter for resetToken
    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
}