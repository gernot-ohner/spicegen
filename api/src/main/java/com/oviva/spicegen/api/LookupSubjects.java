package com.oviva.spicegen.api;

import com.oviva.spicegen.api.internal.LookupSubjectsImpl;

public interface LookupSubjects {

  static Builder newBuilder() {
    return new LookupSubjectsImpl.Builder();
  }

  ObjectRef resource();

  String permission();

  String subjectType();

  String optionalSubjectRelation();

  Consistency consistency();

  Integer optionalLimit();

  interface Builder {
    Builder resource(ObjectRef resource);

    Builder permission(String permission);

    Builder subjectType(String subjectType);

    Builder optionalSubjectRelation(String relation);

    Builder consistency(Consistency consistency);

    Builder optionalLimit(Integer limit);

    LookupSubjects build();
  }
}
