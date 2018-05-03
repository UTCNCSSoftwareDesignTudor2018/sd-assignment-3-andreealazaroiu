package dataAccess.entities;

import java.util.Objects;

public class Writer {


    private String name;
    private String username;
    private String password;

    public Writer( String name, String username,String password) {

        this.name = name;
        this.username = username;
        this.password=password;
    }
    public Writer(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword()
    {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Writer writer = (Writer) o;
        return Objects.equals(name, writer.name) &&
                Objects.equals(username, writer.username);
    }

    @Override
    public int hashCode() {

        return Objects.hash( name, username);
    }

    @Override
    public String toString() {
        return "Writer{" +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
