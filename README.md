<img align="right" src="https://gnarbot.xyz/assets/img/logo.jpg" height="250" width="250">

# Gnar-bot [![Dependency Status](https://www.versioneye.com/user/projects/59516ed46725bd003cde1144/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/59516ed46725bd003cde1144) [![License](https://img.shields.io/github/license/mashape/apistatus.svg?style=flat-square)](LICENSE) [![Chat](https://img.shields.io/badge/chat-discord-blue.svg?style=flat-square)](https://discord.gg/NQRpmr2)
**Gnar** is an open-source Discord bot written in Java and Kotlin, using JDA 3 and Lavaplayer.
Gnar provides various features including music playback, fun and moderation commands.

## The Official Gnar-bot
Click the following [link](https://discordapp.com/oauth2/authorize?client_id=201492375653056512&scope=bot&permissions=8) and add Gnar to a server, provided that you have the
    `Manage Server` permission. The official website for Gnar-bot can be founded [here](https://gnarbot.xyz/).

## Self-hosting Gnar-bot
- Though we do not disallow user-hosted versions of Gnar, we will not provide **any** support for it.
- We are not responsible for anything that this project does to you or your server and can not be held liable 
    for anything pertaining to it. 

#### Instructions
Note that only experienced developers should use or modify the bot.

- **Step 1:** Fork and clone the repository using your Git client.
    - Fork the repository by clicking the __Fork__ button located on the top right of the project.
    - Run the Git command `git clone git@github.com:Gnar-Team/Gnar-bot.git` or use other services to
        clone your fork.
- **Step 2:** You will see a `credentials.conf.example` file, this will 
        lay out the required credentials for the bot's many APIs and Discord token. 
        Replace each one with the necessary token needed for the supplied section.
- **Step 3:** Compile the bot. This project uses **Java 8** and **Kotlin 1.1.3**.
    - Run the Gradle command `gradlew shadowJar` to create a fully shaded jar with all of the necessary
        dependencies.
- **Step 4:** Gnar-bot requires RethinkDB in order to setup. The RethinkDB requires a database name that
    can be specified inside `credentials.conf` along with other necessary credentials. The database needs to
    have 3 tables: `guilds_v2` `keys` `users` in order for the bot to work.
- **Step 5:** Once compilation is done, you can run the bot by grabbing the `.jar` file from `build/libs` 
        and running `java -jar Gnar-bot-4.8.3-all.jar`.

## Main Contributors
* [Avarel](https://github.com/Avarel)
* [Xevryll](https://github.com/Xevryll)
* [Gatt](https://github.com/RealGatt)

## Dependencies
* [Kotlin 1.1](https://kotlinlang.org/)
* [JDA 3.4](https://github.com/DV8FromTheWorld/JDA)
* [LavaPlayer](https://github.com/sedmelluq/lavaplayer)
* [Guava](https://github.com/google/guava)
* [Configurate](https://github.com/zml2008/configurate)
* [OkHttp](https://github.com/square/okhttp)
