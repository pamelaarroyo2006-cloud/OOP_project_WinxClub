package iems.model;

import java.util.Objects;

/**
 * Represents a person in the IEMS system.
 * Can be extended by specific roles (e.g., User, Teacher, Staff).
 */
public class Person {
    private long id;
    private String firstName;
    private String lastName;
    private int age;
    private String email;

    // Constructors
    public Person() {
    }

    public Person(long id, String firstName, String lastName, int age, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName != null ? firstName.trim() : null;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName != null ? lastName.trim() : null;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age < 0)
            throw new IllegalArgumentException("Age cannot be negative");
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : null;
    }

    // Convenience method
    public String getFullName() {
        return (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName);
    }

    // Object overrides
    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + getFullName() + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Person))
            return false;
        Person person = (Person) o;
        return id == person.id &&
                age == person.age &&
                Objects.equals(firstName, person.firstName) &&
                Objects.equals(lastName, person.lastName) &&
                Objects.equals(email, person.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, age, email);
    }
}