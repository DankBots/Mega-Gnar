package com.patreon.resources.shared;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Links;
import com.github.jasminb.jsonapi.annotations.Type;


@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseResource {
  @Id
  private String id;

  @Links
  private com.github.jasminb.jsonapi.Links links;

  public String getId() {
    return id;
  }

  public static String getType(Class<? extends BaseResource> resourceClass) {
    Type type = resourceClass.getAnnotation(Type.class);
    if (type != null) {
      return type.value();
    } else {
      return null;
    }
  }

  public String getType() {
    return getType(this.getClass());
  }

  public com.github.jasminb.jsonapi.Links getLinks() {
    return links;
  }

  @Override
  public int hashCode() {
    String typeAndId = getType().concat(getId());
    return typeAndId.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    return this.hashCode() == o.hashCode();
  }
}
