package com.patreon.resources.shared;


import java.util.List;

public class SocialConnections {
  private UserIdObject youtube;
  private UserIdObject twitter;
  private UserIdObject deviantart;
  private UserIdObject discord;
  private UserIdObject twitch;
  private UserIdObject facebook;
  private UserIdObject spotify;

  public UserIdObject getYoutube() {
    return youtube;
  }

  public UserIdObject getTwitter() {
    return twitter;
  }

  public UserIdObject getDeviantart() {
    return deviantart;
  }

  public UserIdObject getDiscord() {
    return discord;
  }

  public UserIdObject getTwitch() {
    return twitch;
  }

  public UserIdObject getFacebook() {
    return facebook;
  }

  public UserIdObject getSpotify() {
    return spotify;
  }

  public static class UserIdObject {
    private String user_id;
    private List<String> scopes;
    private String url;

    public String getUser_id() {
      return user_id;
    }

    public List<String> getScopes() {
      return scopes;
    }

    public String getUrl() {return url;}
  }
}
