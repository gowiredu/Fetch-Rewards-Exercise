package com.gordonowiredu.fetchrewardsexercise;

public class ListActivityItem {
    String id;
    String name;

    public ListActivityItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public ListActivityItem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
