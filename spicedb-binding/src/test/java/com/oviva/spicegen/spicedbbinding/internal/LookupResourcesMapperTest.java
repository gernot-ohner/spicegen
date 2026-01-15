package com.oviva.spicegen.spicedbbinding.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.oviva.spicegen.api.Consistency;
import com.oviva.spicegen.api.LookupResources;
import com.oviva.spicegen.api.ObjectRef;
import com.oviva.spicegen.api.SubjectRef;
import org.junit.jupiter.api.Test;

class LookupResourcesMapperTest {

  @Test
  void map_basic() {
    var resourceType = "document";
    var permission = "read";
    var subjectKind = "user";
    var subjectId = "alice";

    var request =
        LookupResources.newBuilder()
            .resourceType(resourceType)
            .permission(permission)
            .subject(SubjectRef.ofObject(ObjectRef.of(subjectKind, subjectId)))
            .consistency(Consistency.fullyConsistent())
            .build();

    var sut = new LookupResourcesMapper(new ConsistencyMapper(), new SubjectReferenceMapper());

    // when
    var got = sut.map(request);

    // then
    assertEquals(resourceType, got.getResourceObjectType());
    assertEquals(permission, got.getPermission());
    assertEquals(subjectKind, got.getSubject().getObject().getObjectType());
    assertEquals(subjectId, got.getSubject().getObject().getObjectId());
    assertTrue(got.getConsistency().getFullyConsistent());
    assertEquals(0, got.getOptionalLimit());
  }

  @Test
  void map_withConsistencyToken() {
    var token = "myToken123";

    var request =
        LookupResources.newBuilder()
            .resourceType("document")
            .permission("read")
            .subject(SubjectRef.ofObject(ObjectRef.of("user", "alice")))
            .consistency(Consistency.atLeastAsFreshAs(token))
            .build();

    var sut = new LookupResourcesMapper(new ConsistencyMapper(), new SubjectReferenceMapper());

    // when
    var got = sut.map(request);

    // then
    assertEquals(token, got.getConsistency().getAtLeastAsFresh().getToken());
  }

  @Test
  void map_withLimit() {
    var limit = 100;

    var request =
        LookupResources.newBuilder()
            .resourceType("document")
            .permission("read")
            .subject(SubjectRef.ofObject(ObjectRef.of("user", "alice")))
            .consistency(Consistency.fullyConsistent())
            .optionalLimit(limit)
            .build();

    var sut = new LookupResourcesMapper(new ConsistencyMapper(), new SubjectReferenceMapper());

    // when
    var got = sut.map(request);

    // then
    assertEquals(limit, got.getOptionalLimit());
  }

  @Test
  void map_withSubjectRelation() {
    var subjectRelation = "member";

    var request =
        LookupResources.newBuilder()
            .resourceType("document")
            .permission("read")
            .subject(
                SubjectRef.ofObjectWithRelation(
                    ObjectRef.of("team", "engineering"), subjectRelation))
            .consistency(Consistency.fullyConsistent())
            .build();

    var sut = new LookupResourcesMapper(new ConsistencyMapper(), new SubjectReferenceMapper());

    // when
    var got = sut.map(request);

    // then
    assertEquals("team", got.getSubject().getObject().getObjectType());
    assertEquals("engineering", got.getSubject().getObject().getObjectId());
    assertEquals(subjectRelation, got.getSubject().getOptionalRelation());
  }
}
