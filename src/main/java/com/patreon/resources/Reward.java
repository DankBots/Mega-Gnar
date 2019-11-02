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

@Type("reward")
public class Reward extends BaseResource {
  
  public enum RewardField implements Field {
    AmountCents("amount_cents", true),
    CreatedAt("created_at", true),
    Description("description", true),
    Remaining("remaining", true),
    RequiresShipping("requires_shipping", true),
    Url("url", true),
    UserLimit("user_limit", true),
    EditedAt("edited_at", true),
    PatronCount("patron_count", true),
    Published("published", true),
    PublishedAt("published_at", true),
    ImageUrl("image_url", true),
    DiscordRoleIds("discord_role_ids", true),
    Title("title", true),
    UnpublishedAt("unpublished_at", true),;

    private final String propertyName;
    private final boolean isDefault;

    RewardField(String propertyName, boolean isDefault) {
      this.propertyName = propertyName;
      this.isDefault = isDefault;
    }

    public static Collection<RewardField> getDefaultFields() {
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

  private int amount_cents;
  private String created_at;
  private String description;
  private float remaining;
  private boolean requires_shipping;
  private String url;
  private Integer user_limit;
  private String edited_at;
  private int patron_count;
  private boolean published;
  private String published_at;
  private String image_url;
  private List<String> discord_role_ids;
  private String title;
  private String unpublished_at;

  @Relationship("creator")
  private User creator;

  @Relationship("campaign")
  private Campaign campaign;

  public Reward(
                 @JsonProperty("amount_cents") int amount_cents,
                 @JsonProperty("created_at") String created_at,
                 @JsonProperty("description") String description,
                 @JsonProperty("remaining") float remaining,
                 @JsonProperty("requires_shipping") boolean requires_shipping,
                 @JsonProperty("url") String url,
                 @JsonProperty("user_limit") Integer user_limit,
                 @JsonProperty("edited_at") String edited_at,
                 @JsonProperty("patron_count") int patron_count,
                 @JsonProperty("published") boolean published,
                 @JsonProperty("published_at") String published_at,
                 @JsonProperty("image_url") String image_url,
                 @JsonProperty("discord_role_ids") List<String> discord_role_ids,
                 @JsonProperty("title") String title,
                 @JsonProperty("unpublished_at") String unpublished_at,
                 @JsonProperty("creator") User creator,
                 @JsonProperty("campaign") Campaign campaign
  ) {
    this.amount_cents = amount_cents;
    this.created_at = created_at;
    this.description = description;
    this.remaining = remaining;
    this.requires_shipping = requires_shipping;
    this.url = url;
    this.user_limit = user_limit;
    this.edited_at = edited_at;
    this.patron_count = patron_count;
    this.published = published;
    this.published_at = published_at;
    this.image_url = image_url;
    this.discord_role_ids = discord_role_ids;
    this.title = title;
    this.unpublished_at = unpublished_at;
    this.creator = creator;
    this.campaign = campaign;
  }

  public int getAmountCents() {
    return amount_cents;
  }

  public String getCreatedAt() {
    return created_at;
  }

  public String getDescription() {
    return description;
  }

  public float getRemaining() {
    return remaining;
  }

  public boolean isRequiresShipping() {
    return requires_shipping;
  }

  public String getUrl() {
    return url;
  }

  public Integer getUserLimit() {
    return user_limit;
  }

  public String getEditedAt() {
    return edited_at;
  }

  public String getPublishedAt() {
    return published_at;
  }

  public String getImageUrl() {
    return image_url;
  }

  public List<String> getDiscordRoleIds() {
    return discord_role_ids;
  }

  public String getTitle() {
    return title;
  }

  public String getUnpublishedAt() {
    return unpublished_at;
  }

  public User getCreator() {
    return creator;
  }

  public Campaign getCampaign() {
    return campaign;
  }

  public int getPatronCount() {
    return patron_count;
  }

  public boolean isPublished() {
    return published;
  }
}
