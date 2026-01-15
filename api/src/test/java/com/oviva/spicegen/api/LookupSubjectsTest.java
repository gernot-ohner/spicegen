package com.oviva.spicegen.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class LookupSubjectsTest {

  @Test
  void test_build_success() {
    var permission = "read";
    var subjectType = "user";
    var consistencyToken = "t0ken";

    var resource = ObjectRef.of("document", "doc123");

    var lookupSubjects =
        LookupSubjects.newBuilder()
            .resource(resource)
            .permission(permission)
            .subjectType(subjectType)
            .consistency(Consistency.atLeastAsFreshAs(consistencyToken))
            .build();

    assertEquals(resource, lookupSubjects.resource());
    assertEquals(permission, lookupSubjects.permission());
    assertEquals(subjectType, lookupSubjects.subjectType());
    assertEquals(consistencyToken, lookupSubjects.consistency().consistencyToken());
    assertNull(lookupSubjects.optionalSubjectRelation());
    assertNull(lookupSubjects.optionalLimit());
  }

  @Test
  void test_build_withSubjectRelation() {
    var resource = ObjectRef.of("document", "doc123");
    var subjectRelation = "member";

    var lookupSubjects =
        LookupSubjects.newBuilder()
            .resource(resource)
            .permission("read")
            .subjectType("team")
            .optionalSubjectRelation(subjectRelation)
            .consistency(Consistency.fullyConsistent())
            .build();

    assertEquals(subjectRelation, lookupSubjects.optionalSubjectRelation());
  }

  @Test
  void test_build_withLimit() {
    var resource = ObjectRef.of("document", "doc123");
    var limit = 50;

    var lookupSubjects =
        LookupSubjects.newBuilder()
            .resource(resource)
            .permission("read")
            .subjectType("user")
            .optionalLimit(limit)
            .consistency(Consistency.fullyConsistent())
            .build();

    assertEquals(limit, lookupSubjects.optionalLimit());
  }

  @Test
  void test_defaultConsistency() {
    var lookupSubjects =
        LookupSubjects.newBuilder()
            .resource(ObjectRef.of("document", "doc123"))
            .permission("read")
            .subjectType("user")
            .build();

    // Default should be fully consistent
    assertNull(lookupSubjects.consistency().consistencyToken());
  }

  @Test
  void of_hashCode() {
    var h1 = createLookup("doc1").hashCode();
    var h2 = createLookup("doc1").hashCode();

    assertEquals(h1, h2);
  }

  @Test
  void of_equals_same() {
    var l1 = createLookup("doc1");

    assertEquals(l1, l1);
  }

  @Test
  void of_equals() {
    var l1 = createLookup("doc1");
    var l2 = createLookup("doc1");

    assertEquals(l1, l2);
  }

  @Test
  void of_equals_notEqual() {
    var l1 = createLookup("doc1");
    var l2 = createLookup("doc2");

    assertNotEquals(l1, l2);
  }

  private LookupSubjects createLookup(String resourceId) {
    return LookupSubjects.newBuilder()
        .resource(ObjectRef.of("document", resourceId))
        .permission("read")
        .subjectType("user")
        .build();
  }
}
