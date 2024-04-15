package com.consol.entity;

import java.util.Objects;

public class PersonTO {
    private String firstname;
    private String lastname;
    private int age;

    public PersonTO() {
        // intentionally empty
    }

    public PersonTO(final String firstname, final String lastname, final int age) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public int getAge() {
        return age;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PersonTO personTO = (PersonTO) o;
        return age == personTO.age && Objects.equals(firstname, personTO.firstname) && Objects.equals(lastname, personTO.lastname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstname, lastname, age);
    }

    @Override
    public String toString() {
        return "PersonTO{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", age=" + age +
                '}';
    }
}

