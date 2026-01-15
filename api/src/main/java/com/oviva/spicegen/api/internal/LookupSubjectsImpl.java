package com.oviva.spicegen.api.internal;

import com.oviva.spicegen.api.Consistency;
import com.oviva.spicegen.api.LookupSubjects;
import com.oviva.spicegen.api.ObjectRef;

public record LookupSubjectsImpl(
    ObjectRef resource,
    String permission,
    String subjectType,
    String optionalSubjectRelation,
    Consistency consistency,
    Integer optionalLimit)
    implements LookupSubjects {

  private LookupSubjectsImpl(Builder builder) {
    this(
        builder.resource,
        builder.permission,
        builder.subjectType,
        builder.optionalSubjectRelation,
        builder.consistency,
        builder.optionalLimit);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder implements LookupSubjects.Builder {
    private ObjectRef resource;
    private String permission;
    private String subjectType;
    private String optionalSubjectRelation;
    private Consistency consistency = Consistency.fullyConsistent();
    private Integer optionalLimit;

    @Override
    public Builder resource(ObjectRef resource) {
      this.resource = resource;
      return this;
    }

    @Override
    public Builder permission(String permission) {
      this.permission = permission;
      return this;
    }

    @Override
    public Builder subjectType(String subjectType) {
      this.subjectType = subjectType;
      return this;
    }

    @Override
    public Builder optionalSubjectRelation(String relation) {
      this.optionalSubjectRelation = relation;
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
    public LookupSubjects build() {
      return new LookupSubjectsImpl(this);
    }
  }
}
