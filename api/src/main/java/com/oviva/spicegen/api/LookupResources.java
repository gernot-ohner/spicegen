package com.oviva.spicegen.api;

import com.oviva.spicegen.api.internal.LookupResourcesImpl;

public interface LookupResources {

  static Builder newBuilder() {
    return new LookupResourcesImpl.Builder();
  }

  String resourceType();

  String permission();

  SubjectRef subject();

  Consistency consistency();

  Integer optionalLimit();

  interface Builder {
    Builder resourceType(String resourceType);

    Builder permission(String permission);

    Builder subject(SubjectRef subject);

    Builder consistency(Consistency consistency);

    Builder optionalLimit(Integer limit);

    LookupResources build();
  }
}
