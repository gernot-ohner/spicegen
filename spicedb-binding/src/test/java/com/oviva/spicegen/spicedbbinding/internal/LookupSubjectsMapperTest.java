package com.oviva.spicegen.spicedbbinding.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.oviva.spicegen.api.Consistency;
import com.oviva.spicegen.api.LookupSubjects;
import com.oviva.spicegen.api.ObjectRef;
import org.junit.jupiter.api.Test;

class LookupSubjectsMapperTest {

  @Test
  void map_basic() {
    var resourceKind = "document";
    var resourceId = "doc123";
    var permission = "read";
    var subjectType = "user";

    var request =
        LookupSubjects.newBuilder()
            .resource(ObjectRef.of(resourceKind, resourceId))
            .permission(permission)
            .subjectType(subjectType)
            .consistency(Consistency.fullyConsistent())
            .build();

    var sut = new LookupSubjectsMapper(new ConsistencyMapper(), new ObjectReferenceMapper());

    // when
    var got = sut.map(request);

    // then
    assertEquals(resourceKind, got.getResource().getObjectType());
    assertEquals(resourceId, got.getResource().getObjectId());
    assertEquals(permission, got.getPermission());
    assertEquals(subjectType, got.getSubjectObjectType());
    assertTrue(got.getConsistency().getFullyConsistent());
    assertTrue(got.getOptionalSubjectRelation().isEmpty());
    assertEquals(0, got.getOptionalConcreteLimit());
  }

  @Test
  void map_withConsistencyToken() {
    var token = "myToken123";

    var request =
        LookupSubjects.newBuilder()
            .resource(ObjectRef.of("document", "doc123"))
            .permission("read")
            .subjectType("user")
            .consistency(Consistency.atLeastAsFreshAs(token))
            .build();

    var sut = new LookupSubjectsMapper(new ConsistencyMapper(), new ObjectReferenceMapper());

    // when
    var got = sut.map(request);

    // then
    assertEquals(token, got.getConsistency().getAtLeastAsFresh().getToken());
  }

  @Test
  void map_withSubjectRelation() {
    var subjectRelation = "member";

    var request =
        LookupSubjects.newBuilder()
            .resource(ObjectRef.of("document", "doc123"))
            .permission("read")
            .subjectType("team")
            .optionalSubjectRelation(subjectRelation)
            .consistency(Consistency.fullyConsistent())
            .build();

    var sut = new LookupSubjectsMapper(new ConsistencyMapper(), new ObjectReferenceMapper());

    // when
    var got = sut.map(request);

    // then
    assertEquals(subjectRelation, got.getOptionalSubjectRelation());
  }

  @Test
  void map_withLimit() {
    var limit = 50;

    var request =
        LookupSubjects.newBuilder()
            .resource(ObjectRef.of("document", "doc123"))
            .permission("read")
            .subjectType("user")
            .optionalLimit(limit)
            .consistency(Consistency.fullyConsistent())
            .build();

    var sut = new LookupSubjectsMapper(new ConsistencyMapper(), new ObjectReferenceMapper());

    // when
    var got = sut.map(request);

    // then
    assertEquals(limit, got.getOptionalConcreteLimit());
  }
}
