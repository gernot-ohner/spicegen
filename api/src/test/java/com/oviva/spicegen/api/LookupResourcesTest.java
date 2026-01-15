package com.oviva.spicegen.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class LookupResourcesTest {

  @Test
  void test_build_success() {
    var resourceType = "document";
    var permission = "read";
    var consistencyToken = "t0ken";

    var subjectRef = SubjectRef.ofObject(ObjectRef.of("user", "alice"));

    var lookupResources =
        LookupResources.newBuilder()
            .resourceType(resourceType)
            .permission(permission)
            .subject(subjectRef)
            .consistency(Consistency.atLeastAsFreshAs(consistencyToken))
            .build();

    assertEquals(resourceType, lookupResources.resourceType());
    assertEquals(permission, lookupResources.permission());
    assertEquals(subjectRef, lookupResources.subject());
    assertEquals(consistencyToken, lookupResources.consistency().consistencyToken());
    assertNull(lookupResources.optionalLimit());
  }

  @Test
  void test_build_withLimit() {
    var resourceType = "document";
    var permission = "read";
    var limit = 100;

    var subjectRef = SubjectRef.ofObject(ObjectRef.of("user", "alice"));

    var lookupResources =
        LookupResources.newBuilder()
            .resourceType(resourceType)
            .permission(permission)
            .subject(subjectRef)
            .consistency(Consistency.fullyConsistent())
            .optionalLimit(limit)
            .build();

    assertEquals(limit, lookupResources.optionalLimit());
  }

  @Test
  void test_defaultConsistency() {
    var lookupResources =
        LookupResources.newBuilder()
            .resourceType("document")
            .permission("read")
            .subject(SubjectRef.ofObject(ObjectRef.of("user", "alice")))
            .build();

    // Default should be fully consistent
    assertNull(lookupResources.consistency().consistencyToken());
  }

  @Test
  void of_hashCode() {
    var h1 = createLookup("alice").hashCode();
    var h2 = createLookup("alice").hashCode();

    assertEquals(h1, h2);
  }

  @Test
  void of_equals_same() {
    var l1 = createLookup("alice");

    assertEquals(l1, l1);
  }

  @Test
  void of_equals() {
    var l1 = createLookup("alice");
    var l2 = createLookup("alice");

    assertEquals(l1, l2);
  }

  @Test
  void of_equals_notEqual() {
    var l1 = createLookup("alice");
    var l2 = createLookup("bob");

    assertNotEquals(l1, l2);
  }

  private LookupResources createLookup(String subjectId) {
    return LookupResources.newBuilder()
        .resourceType("document")
        .permission("read")
        .subject(SubjectRef.ofObject(ObjectRef.of("user", subjectId)))
        .build();
  }
}
