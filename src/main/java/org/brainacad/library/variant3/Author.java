package org.brainacad.library.variant3;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Author implements Externalizable {
    private String firstName;
    private String lastName;

    public Author() {
    }

    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(firstName != null);
        if (firstName != null) {
            out.writeUTF(firstName);
        }
        out.writeBoolean(lastName != null);
        if (lastName != null) {
            out.writeUTF(lastName);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        firstName = in.readBoolean() ? in.readUTF() : null;
        lastName = in.readBoolean() ? in.readUTF() : null;
    }

    @Override
    public String toString() {
        return "Author{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}

