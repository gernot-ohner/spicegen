package com.oviva.spicegen.spicedbbinding.internal;

import com.authzed.api.v1.LookupResourcesRequest;
import com.oviva.spicegen.api.LookupResources;

public class LookupResourcesMapper {

  private final ConsistencyMapper consistencyMapper;
  private final SubjectReferenceMapper subjectReferenceMapper;

  public LookupResourcesMapper(
      ConsistencyMapper consistencyMapper, SubjectReferenceMapper subjectReferenceMapper) {
    this.consistencyMapper = consistencyMapper;
    this.subjectReferenceMapper = subjectReferenceMapper;
  }

  public LookupResourcesRequest map(LookupResources request) {
    var builder =
        LookupResourcesRequest.newBuilder()
            .setConsistency(consistencyMapper.map(request.consistency()))
            .setResourceObjectType(request.resourceType())
            .setPermission(request.permission())
            .setSubject(subjectReferenceMapper.map(request.subject()));

    if (request.optionalLimit() != null) {
      builder.setOptionalLimit(request.optionalLimit());
    }

    return builder.build();
  }
}
