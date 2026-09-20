
package com.news.tracker;

import com.google.gson.*;
import org.apache.http.client.fluent.Request;

import java.util.*;

public class NewsFetcher {

    private static final String API_KEY = "API_KEY";

    // Cache validity: 5 minutes
    private static final long CACHE_DURATION = 5 * 60 * 1000;

    // Stores cached news using country + topic as the key
    private final Map<String, CacheEntry> cache = new HashMap<>();

    // Inner class to store news and its expiry time
    private static class CacheEntry {
        private final List<NewsArticle> articles;
        private final long expiryTime;

        public CacheEntry(List<NewsArticle> articles, long expiryTime) {
            this.articles = articles;
            this.expiryTime = expiryTime;
        }
    }

    public List<NewsArticle> fetch(UserPreferences prefs) throws Exception {

        // Create a unique key for each country and category
        String cacheKey = prefs.getCountry() + "_" + prefs.getTopic();

        // 1. Check whether valid data exists in cache
        CacheEntry cachedEntry = cache.get(cacheKey);

        if (cachedEntry != null) {

            if (System.currentTimeMillis() < cachedEntry.expiryTime) {

                System.out.println("Cache hit! Returning stored news.");

                return cachedEntry.articles;

            } else {

                // Remove expired cache entry
                cache.remove(cacheKey);

                System.out.println("Cache expired. Fetching fresh news.");
            }
        }

        // 2. Cache miss: call the external API
        System.out.println("Cache miss. Calling News API...");

        String url = String.format(
                "https://newsapi.org/v2/top-headlines?country=%s&category=%s&apiKey=%s",
                prefs.getCountry(),
                prefs.getTopic(),
                API_KEY
        );

        String response = Request.Get(url)
                .execute()
                .returnContent()
                .asString();

        JsonObject json = JsonParser
                .parseString(response)
                .getAsJsonObject();

        List<NewsArticle> articles = new ArrayList<>();

        // 3. Parse the API response
        if (json.has("articles") && json.get("articles").isJsonArray()) {

            for (JsonElement elem : json.getAsJsonArray("articles")) {

                JsonObject obj = elem.getAsJsonObject();

                String title = obj.get("title").getAsString();

                String desc = obj.get("description").isJsonNull()
                        ? "No description available"
                        : obj.get("description").getAsString();

                String link = obj.get("url").getAsString();

                articles.add(new NewsArticle(title, desc, link));
            }
        }

        // 4. Store the result in the cache
        long expiryTime = System.currentTimeMillis() + CACHE_DURATION;

        cache.put(
                cacheKey,
                new CacheEntry(articles, expiryTime)
        );

        System.out.println("News stored in cache.");

        return articles;
    }

    // Optional method to clear all cached data
    public void clearCache() {
        cache.clear();
        System.out.println("Cache cleared.");
    }
}
