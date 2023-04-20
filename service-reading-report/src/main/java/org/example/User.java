package org.example;

public class User {
    public User(String uuid) {
        this.uuid = uuid;
    }
    private final String uuid;
    public String getUuid() {
        return uuid;
    }

    public String getReportPath() {
        return "service-reading-report/target/" + uuid + "-report.txt";
    }
}
