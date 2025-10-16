
A Java-based bot that monitors multiple subreddits for new posts and sends real-time email notifications. This project demonstrates API integration, task scheduling, and automated notifications using Java libraries.

Features

Monitors specified subreddits for new posts every 5 minutes.

Sends email alerts with post title and URL when a new post is detected.

Tracks previously seen posts to avoid duplicate notifications.

Handles errors and logs activity for reliable operation.

Technologies & Libraries

Java – Core programming language.

JRAW – Reddit API wrapper for OAuth2 authentication and subreddit interaction.

Jakarta Mail (JavaMail) – Sending email notifications via SMTP.

Java Standard Library – Concurrent task scheduling (ScheduledExecutorService), collections (HashSet), and date/time handling.

Usage

Configure Reddit API credentials (CLIENT_ID, CLIENT_SECRET, USERNAME, PASSWORD).

Set your email credentials (EMAIL_USERNAME, EMAIL_PASSWORD, RECIPIENT_EMAIL).

Specify the subreddits to monitor in SUBREDDITS_TO_MONITOR.

Run the bot: it will check for new posts every 5 minutes and send email notifications.

Learning Outcomes

API integration with OAuth2 authentication.

Automating real-time notifications via email.

Implementing concurrent scheduled tasks in Java.

Managing application state to prevent duplicate alerts.
