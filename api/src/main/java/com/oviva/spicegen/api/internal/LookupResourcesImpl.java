package com.oviva.spicegen.api.internal;

import com.oviva.spicegen.api.Consistency;
import com.oviva.spicegen.api.LookupResources;
import com.oviva.spicegen.api.SubjectRef;

public record LookupResourcesImpl(
    String resourceType,
    String permission,
    SubjectRef subject,
    Consistency consistency,
    Integer optionalLimit)
    implements LookupResources {

  private LookupResourcesImpl(Builder builder) {
    this(
        builder.resourceType,
        builder.permission,
        builder.subject,
        builder.consistency,
        builder.optionalLimit);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder implements LookupResources.Builder {
    private String resourceType;
    private String permission;
    private SubjectRef subject;
    private Consistency consistency = Consistency.fullyConsistent();
    private Integer optionalLimit;

    @Override
    public Builder resourceType(String resourceType) {
      this.resourceType = resourceType;
      return this;
    }

    @Override
    public Builder permission(String permission) {
      this.permission = permission;
      return this;
    }

    @Override
    public Builder subject(SubjectRef subject) {
      this.subject = subject;
      return this;
    }

    @Override
    public Builder consistency(Consistency consistency) {
      this.consistency = consistency;
      return this;
    }

    @Override
    public Builder optionalLimit(Integer limit) {
      this.optionalLimit = limit;
      return this;
    }

    @Override
    public LookupResources build() {
      return new LookupResourcesImpl(this);
    }
  }
}
