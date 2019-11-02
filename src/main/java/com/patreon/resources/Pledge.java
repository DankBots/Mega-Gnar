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

@Type("pledge")
public class Pledge extends BaseResource {
  
  public enum PledgeField implements Field {
    AmountCents("amount_cents", true),
    CreatedAt("created_at", true),
    DeclinedSince("declined_since", true),
    PatronPaysFees("patron_pays_fees", true),
    PledgeCapCents("pledge_cap_cents", true),
    TotalHistoricalAmountCents("total_historical_amount_cents", false),
    IsPaused("is_paused", false),
    HasShippingAddress("has_shipping_address", false),
    ;

    private final String propertyName;
    private final boolean isDefault;

    PledgeField(String propertyName, boolean isDefault) {
      this.propertyName = propertyName;
      this.isDefault = isDefault;
    }

    public static Collection<PledgeField> getDefaultFields() {
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

  private int amountCents;
  private String createdAt;
  private String declinedSince;
  private boolean patronPaysFees;
  private int pledgeCapCents;

  //Optional properties.  Will be null if not requested
  private Integer totalHistoricalAmountCents;
  private Boolean isPaused;
  private Boolean hasShippingAddress;

  @Relationship("creator")
  private User creator;

  @Relationship("patron")
  private User patron;

  @Relationship("reward")
  private Reward reward;

  public Pledge(
                 @JsonProperty("amount_cents") int amount_cents,
                 @JsonProperty("created_at") String created_at,
                 @JsonProperty("declined_since") String declined_since,
                 @JsonProperty("patron_pays_fees") boolean patron_pays_fees,
                 @JsonProperty("pledge_cap_cents") int pledge_cap_cents,
                 @JsonProperty("total_historical_amount_cents") Integer total_historical_amount_cents,
                 @JsonProperty("is_paused") Boolean is_paused,
                 @JsonProperty("has_shipping_address") Boolean has_shipping_address,
                 @JsonProperty("creator") User creator,
                 @JsonProperty("patron") User patron,
                 @JsonProperty("reward") Reward reward
  ) {
    this.amountCents = amount_cents;
    this.createdAt = created_at;
    this.declinedSince = declined_since;
    this.patronPaysFees = patron_pays_fees;
    this.pledgeCapCents = pledge_cap_cents;
    this.totalHistoricalAmountCents = total_historical_amount_cents;
    this.isPaused = is_paused;
    this.hasShippingAddress = has_shipping_address;
    this.creator = creator;
    this.patron = patron;
    this.reward = reward;
  }

  public int getAmountCents() {
    return amountCents;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public String getDeclinedSince() {
    return declinedSince;
  }

  public boolean getPatronPaysFees() {
    return patronPaysFees;
  }

  public int getPledgeCapCents() {
    return pledgeCapCents;
  }

  /**
   * @return The lifetime value this patron has paid to the campaign, or null
   * if this field was not requested
   */
  public Integer getTotalHistoricalAmountCents() {
    return totalHistoricalAmountCents;
  }

  /**
   * @return Whether the pledge is paused, or null if this field wasn't requested.
   */
  public Boolean getPaused() {
    return isPaused;
  }

  /**
   * @return Whether this patron has a shipping address, or null if this field wasn't requested
   */
  public Boolean getHasShippingAddress() {
    return hasShippingAddress;
  }

  public User getCreator() {
    return creator;
  }

  public User getPatron() {
    return patron;
  }

  public Reward getReward() {
    return reward;
  }
}
