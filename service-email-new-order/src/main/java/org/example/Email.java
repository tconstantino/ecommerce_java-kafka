package org.example;

public class Email {
    public Email(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    private final String to;
    private final String subject;
    private final String body;
}
