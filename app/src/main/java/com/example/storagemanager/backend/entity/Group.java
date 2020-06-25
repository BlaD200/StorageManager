package com.example.storagemanager.backend.entity;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Group {

    private String name;
    private String description;


    public Group(String name) {
        this.name = name;
    }


    public Group(String name, String description) {
        this.name = name;
        this.description = description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return name.equals(group.name) &&
                Objects.equals(description, group.description);
    }


    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }


//    @Override
//    public int compareTo(Group o) {
//        if (name.compareTo(o.name) < 0 || description.compareTo(o.description) < 0) return -1;
//        else if (name.compareTo(o.name) > 0 || description.compareTo(o.description) > 0) return 1;
//        return name.compareTo(o.name) && description.compareTo(o.description);
//    }
}
