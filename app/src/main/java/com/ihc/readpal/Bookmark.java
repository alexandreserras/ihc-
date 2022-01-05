package com.ihc.readpal;

public class Bookmark {

    private final String name;
    private final int page;

    public Bookmark(String name, int page) {
        this.name = name;
        this.page = page;
    }

    public String getName() {
        return name;
    }

    public int getPage() {
        return page;
    }
}
