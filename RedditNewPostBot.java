package com.example;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.Paginator;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.dean.jraw.models.SubredditSort;

public class RedditNewPostBot {

    // Replace with your Reddit API credentials
    private static final String CLIENT_ID = "YOUR_CLIENT_ID";
    private static final String CLIENT_SECRET = "YOUR_CLIENT_SECRET";
    private static final String USERNAME = "YOUR_REDDIT_USERNAME";
    private static final String PASSWORD = "YOUR_REDDIT_PASSWORD";

    // Replace with your email credentials
    private static final String EMAIL_USERNAME = "YOUR_EMAIL@gmail.com";
    private static final String EMAIL_PASSWORD = "YOUR_EMAIL_PASSWORD";
    private static final String RECIPIENT_EMAIL = "RECIPIENT_EMAIL@gmail.com";

    private static final String[] SUBREDDITS_TO_MONITOR = {
        "Sub_reddit_names"
    };

    private static final Set<String> seenPostIds = new HashSet<>();
    private static long lastCheckedTime = System.currentTimeMillis() / 1000;

    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error");

        RedditClient reddit = authenticateReddit();
        if (reddit == null) {
            System.out.println("Failed to authenticate with Reddit. Exiting.");
            return;
        }
        System.out.println("Successfully authenticated as: " + reddit.me().getUsername());

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        System.out.println("Starting Reddit bot. Checking for new posts every 5 minutes...");

        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkForNewPosts(reddit);
            } catch (Exception e) {
                System.out.println("An error occurred during post check: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.MINUTES);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down scheduler...");
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
            System.out.println("Scheduler shut down.");
        }));
    }

    private static RedditClient authenticateReddit() {
        try {
            Credentials credentials = Credentials.script(USERNAME, PASSWORD, CLIENT_ID, CLIENT_SECRET);
            NetworkAdapter adapter = new OkHttpNetworkAdapter(
                new net.dean.jraw.http.UserAgent("desktop", "com.example.redditbot", "v1.0", USERNAME)
            );
            return OAuthHelper.automatic(adapter, credentials);
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static void checkForNewPosts(RedditClient reddit) {
        System.out.println("\nChecking for new posts at " + new java.util.Date());

        for (String subredditName : SUBREDDITS_TO_MONITOR) {
            try {
                Paginator<Submission> newPostsPaginator = reddit.subreddit(subredditName).posts()
                        .sorting(SubredditSort.NEW)
                        .limit(60)
                        .build();

                Listing<Submission> newSubmissions = newPostsPaginator.next();

                for (Submission submission : newSubmissions) {
                    long createdUtc = submission.getCreated().toInstant().getEpochSecond();

                    if (createdUtc > lastCheckedTime && !seenPostIds.contains(submission.getId())) {
                        String subject = "New post in r/" + subredditName;
                        String body = "Title: " + submission.getTitle() + "\nURL: " + submission.getUrl();

                        System.out.println("-- New Post in r/" + subredditName);
                        System.out.println("Title: " + submission.getTitle());
                        System.out.println("URL: " + submission.getUrl());

                        sendEmail(subject, body);
                        seenPostIds.add(submission.getId());
                    }
                }
            } catch (Exception e) {
                System.out.println("Error checking subreddit r/" + subredditName + " : " + e.getMessage());
            }
        }
        lastCheckedTime = System.currentTimeMillis() / 1000;
    }

    private static void sendEmail(String subject, String body) {
        String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(RECIPIENT_EMAIL));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent: " + subject);
        } catch (MessagingException e) {
            System.out.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
