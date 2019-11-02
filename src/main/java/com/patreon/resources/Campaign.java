package com.patreon.resources;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import com.patreon.resources.shared.BaseResource;
import com.patreon.resources.shared.Field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Type("campaign")
public class Campaign extends BaseResource {
  
  public enum CampaignField implements Field {
    PledgeSum("pledge_sum", true),
    CreationName("creation_name", true),
    DiscordServerId("discor_server_id", true),
    CreatedAt("created_at", true),
    IsPlural("is_plural", true),
    MainVideoUrl("main_video_url", true),
    IsNsfw("is_nsfw", true),
    IsMonthly("is_monthly", true),
    PublishedAt("published_at", true),
    EarningsVisibility("earnings_visibility", true),
    OutstandingPaymentAmountCents("outstanding_payment_amount_cents", true),
    ImageSmallUrl("image_small_url", true),
    Summary("summary", true),
    ThanksMsg("thanks_msg", true),
    ImageUrl("image_url", true),
    CreationCount("creation_count", true),
    OneLiner("one_liner", true),
    IsChargedImmediately("is_charged_immediately", true),
    PatronCount("patron_count", true),
    DisplayPatronGoals("display_patron_goals", true),
    PledgeUrl("pledge_url", true),
    PayPerName("pay_per_name", true),
    ThanksEmbed("thanks_embed", true),
    MainVideoEmbed("main_video_embed", true),
    ThanksVideoUrl("thanks_video_url", true),
    About("about", true),
    ;

    private final String propertyName;
    private final boolean isDefault;

    CampaignField(String propertyName, boolean isDefault) {
      this.propertyName = propertyName;
      this.isDefault = isDefault;
    }

    public static Collection<CampaignField> getDefaultFields() {
      return Arrays.stream(values()).filter(Field::isDefault).collect(Collectors.toList());
    }

    @Override
    public String getPropertyName() {
      return this.propertyName;
    }

    @Override
    public boolean isDefault() {
      return this.isDefault;
    }
  }

  private int pledgeSum;
  private String creationName;
  private String createdAt;
  private String pledgeUrl;
  private String payPerName;
  private String oneLiner;
  private String imageUrl;
  private String imageSmallUrl;
  private String summary;
  private String about;
  private String mainVideoEmbed;
  private String mainVideoUrl;
  private String thanksMsg;
  private String thanksEmbed;
  private String thanksVideoUrl;
  private String publishedAt;
  private String earningsVisibility;
  private boolean isMonthly;
  private boolean isChargedImmediately;
  private boolean isNsfw;
  private int patronCount;
  private int outstandingPaymentAmountCents;
  private int creationCount;
  private boolean isPlural;
  private boolean displayPatronGoals;
  private String discordServerId;

  @Relationship("pledges")
  private List<Pledge> pledges;

  @Relationship("creator")
  private User creator;

  @Relationship("rewards")
  private List<Reward> rewards;

  @Relationship("goals")
  private List<Goal> goals;

  public Campaign(
                   @JsonProperty("pledge_sum") int pledgeSum,
                   @JsonProperty("creation_name") String creationName,
                   @JsonProperty("discor_server_id") String discordServerId,
                   @JsonProperty("created_at") String createdAt,
                   @JsonProperty("is_plural") boolean isPlural,
                   @JsonProperty("main_video_url") String mainVideoUrl,
                   @JsonProperty("is_nsfw") boolean isNsfw,
                   @JsonProperty("is_monthly") boolean isMonthly,
                   @JsonProperty("published_at") String publishedAt,
                   @JsonProperty("earnings_visibility") String earningsVisibility,
                   @JsonProperty("outstanding_payment_amount_cents") int outstandingPaymentAmountCents,
                   @JsonProperty("image_small_url") String imageSmallUrl,
                   @JsonProperty("summary") String summary,
                   @JsonProperty("thanks_msg") String thanksMsg,
                   @JsonProperty("image_url") String imageUrl,
                   @JsonProperty("creation_count") int creationCount,
                   @JsonProperty("one_liner") String oneLiner,
                   @JsonProperty("is_charged_immediately") boolean isChargedImmediately,
                   @JsonProperty("patron_count") int patronCount,
                   @JsonProperty("display_patron_goals") boolean displayPatronGoals,
                   @JsonProperty("pledge_url") String pledgeUrl,
                   @JsonProperty("pay_per_name") String payPerName,
                   @JsonProperty("thanks_embed") String thanksEmbed,
                   @JsonProperty("main_video_embed") String mainVideoEmbed,
                   @JsonProperty("thanks_video_url") String thanksVideoUrl,
                   @JsonProperty("about") String about,
                   @JsonProperty("pledges") List<Pledge> pledges,
                   @JsonProperty("creator") User creator,
                   @JsonProperty("rewards") List<Reward> rewards,
                   @JsonProperty("goals") List<Goal> goals
  ) {
    this.pledgeSum = pledgeSum;
    this.creationName = creationName;
    this.discordServerId = discordServerId;
    this.createdAt = createdAt;
    this.isPlural = isPlural;
    this.mainVideoUrl = mainVideoUrl;
    this.isNsfw = isNsfw;
    this.isMonthly = isMonthly;
    this.publishedAt = publishedAt;
    this.earningsVisibility = earningsVisibility;
    this.outstandingPaymentAmountCents = outstandingPaymentAmountCents;
    this.imageSmallUrl = imageSmallUrl;
    this.summary = summary;
    this.thanksMsg = thanksMsg;
    this.imageUrl = imageUrl;
    this.creationCount = creationCount;
    this.oneLiner = oneLiner;
    this.isChargedImmediately = isChargedImmediately;
    this.patronCount = patronCount;
    this.displayPatronGoals = displayPatronGoals;
    this.pledgeUrl = pledgeUrl;
    this.payPerName = payPerName;
    this.thanksEmbed = thanksEmbed;
    this.mainVideoEmbed = mainVideoEmbed;
    this.thanksVideoUrl = thanksVideoUrl;
    this.about = about;
    this.pledges = pledges;
    this.creator = creator;
    this.rewards = rewards;
    this.goals = goals;
  }

  public int getPledgeSum() {
    return pledgeSum;
  }

  public String getCreationName() {
    return creationName;
  }

  public String getDiscordServerId() {
    return discordServerId;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public boolean isPlural() {
    return isPlural;
  }

  public String getMainVideoUrl() {
    return mainVideoUrl;
  }

  public boolean isNsfw() {
    return isNsfw;
  }

  public boolean isMonthly() {
    return isMonthly;
  }

  public String getPublishedAt() {
    return publishedAt;
  }

  public String getEarningsVisibility() {
    return earningsVisibility;
  }

  public int getOutstandingPaymentAmountCents() {
    return outstandingPaymentAmountCents;
  }

  public String getImageSmallUrl() {
    return imageSmallUrl;
  }

  public String getSummary() {
    return summary;
  }

  public String getThanksMsg() {
    return thanksMsg;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public int getCreationCount() {
    return creationCount;
  }

  public String getOneLiner() {
    return oneLiner;
  }

  public boolean isChargedImmediately() {
    return isChargedImmediately;
  }

  public int getPatronCount() {
    return patronCount;
  }

  public boolean isDisplayPatronGoals() {
    return displayPatronGoals;
  }

  public String getPledgeUrl() {
    return pledgeUrl;
  }

  public String getPayPerName() {
    return payPerName;
  }

  public String getThanksEmbed() {
    return thanksEmbed;
  }

  public String getMainVideoEmbed() {
    return mainVideoEmbed;
  }

  public String getThanksVideoUrl() {
    return thanksVideoUrl;
  }

  public String getAbout() {
    return about;
  }

  public List<Pledge> getPledges() {
    return pledges;
  }

  public User getCreator() {
    return creator;
  }

  public List<Reward> getRewards() {
    return rewards;
  }

  public List<Goal> getGoals() {
    return goals;
  }
}
