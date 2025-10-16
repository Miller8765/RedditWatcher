# Reddit New Post Bot

A Java-based bot that monitors multiple subreddits for new posts and sends real-time email notifications. Demonstrates API integration, task scheduling, and automated notifications using Java libraries.

---

## Features
- ✅ Monitors specified subreddits for new posts every 5 minutes.  
- ✅ Sends email alerts with post title and URL when a new post is detected.  
- ✅ Tracks previously seen posts to avoid duplicate notifications.  
- ✅ Handles errors and logs activity for reliable operation.

---

## Technologies & Libraries
- **Java** – Core programming language.  
- **JRAW** – Reddit API wrapper for OAuth2 authentication and subreddit interaction.  
- **Jakarta Mail (JavaMail)** – Sending email notifications via SMTP.  
- **Java Standard Library** – Concurrent task scheduling (`ScheduledExecutorService`), collections (`HashSet`), and date/time handling.

---

## Setup & Usage
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/reddit-new-post-bot.git

