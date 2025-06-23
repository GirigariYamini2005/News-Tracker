package com.news.tracker;

public class UserPreferences {
    private String country;
    private String topic;

    public UserPreferences(String country, String topic) {
        this.country = country.toLowerCase();
        this.topic = topic.toLowerCase();
    }

    public String getCountry() {
        return country;
    }

    public String getTopic() {
        return topic;
    }
}
