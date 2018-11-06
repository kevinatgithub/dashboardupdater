package com.hino.dev.dashboardupdater;

public class User {
    public String id;
    public String firstName;
    public String lastName;
    public String userName;
    public Section[] sections;

    public class Section{
        public String id;
        public String name;

        public Section(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
