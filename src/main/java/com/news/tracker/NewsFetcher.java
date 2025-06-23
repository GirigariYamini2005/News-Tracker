package com.news.tracker;

import com.google.gson.*;
import org.apache.http.client.fluent.Request;
import java.util.*;

public class NewsFetcher {
    private static final String API_KEY = "8b3962c89aca49bf8f47c7bd66bb169a"; // Replace with your real key

    public List<NewsArticle> fetch(UserPreferences prefs) throws Exception {
        String url = String.format(
            "https://newsapi.org/v2/top-headlines?country=%s&category=%s&apiKey=%s",
            prefs.getCountry(), prefs.getTopic(), API_KEY);

        String response = Request.Get(url).execute().returnContent().asString();
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();

        List<NewsArticle> articles = new ArrayList<>();
        for (JsonElement elem : json.getAsJsonArray("articles")) {
            JsonObject obj = elem.getAsJsonObject();
            String title = obj.get("title").getAsString();
            String desc = obj.get("description").isJsonNull() ? "No description available" : obj.get("description").getAsString();
            String link = obj.get("url").getAsString();
            articles.add(new NewsArticle(title, desc, link));
        }
        return articles;
    }
}
