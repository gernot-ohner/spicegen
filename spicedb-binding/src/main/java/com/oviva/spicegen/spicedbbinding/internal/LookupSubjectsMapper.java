package com.oviva.spicegen.spicedbbinding.internal;

import com.authzed.api.v1.LookupSubjectsRequest;
import com.oviva.spicegen.api.LookupSubjects;

public class LookupSubjectsMapper {

  private final ConsistencyMapper consistencyMapper;
  private final ObjectReferenceMapper objectReferenceMapper;

  public LookupSubjectsMapper(
      ConsistencyMapper consistencyMapper, ObjectReferenceMapper objectReferenceMapper) {
    this.consistencyMapper = consistencyMapper;
    this.objectReferenceMapper = objectReferenceMapper;
  }

  public LookupSubjectsRequest map(LookupSubjects request) {
    var builder =
        LookupSubjectsRequest.newBuilder()
            .setConsistency(consistencyMapper.map(request.consistency()))
            .setResource(objectReferenceMapper.map(request.resource()))
            .setPermission(request.permission())
            .setSubjectObjectType(request.subjectType());

    if (request.optionalSubjectRelation() != null) {
      builder.setOptionalSubjectRelation(request.optionalSubjectRelation());
    }

    if (request.optionalLimit() != null) {
      builder.setOptionalConcreteLimit(request.optionalLimit());
    }

    return builder.build();
  }
}
