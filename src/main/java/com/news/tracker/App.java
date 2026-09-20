
package com.news.tracker;

import java.util.*;

public class App {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Keep the same fetcher instance
        // so the cache remains available
        NewsFetcher fetcher = new NewsFetcher();

        while (true) {

            System.out.println("\nEnter country code (e.g., us, in, gb):");
            String country = sc.nextLine().trim();

            System.out.println("Enter news category (e.g., business, sports, health):");
            String topic = sc.nextLine().trim();

            UserPreferences prefs = new UserPreferences(country, topic);

            try {

                List<NewsArticle> articles = fetcher.fetch(prefs);

                if (articles.isEmpty()) {

                    System.out.println("No articles found for this country/topic.");

                } else {

                    System.out.println("\nTop News:");

                    for (NewsArticle a : articles) {

                        System.out.println("\n------------------------------");
                        System.out.println("Title: " + a.getTitle());
                        System.out.println("Description: " + a.getDescription());
                        System.out.println("URL: " + a.getUrl());
                    }
                }

            } catch (Exception e) {

                System.err.println("Error fetching news: " + e.getMessage());
            }

            System.out.println("\nDo you want to search again? (yes/no)");

            String choice = sc.nextLine().trim();

            if (!choice.equalsIgnoreCase("yes")) {
                break;
            }
        }

        sc.close();

        System.out.println("Application closed.");
    }
}
