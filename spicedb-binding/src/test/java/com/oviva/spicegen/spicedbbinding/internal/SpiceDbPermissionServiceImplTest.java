package com.oviva.spicegen.spicedbbinding.internal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.authzed.api.v1.*;
import com.oviva.spicegen.api.*;
import com.oviva.spicegen.api.Consistency;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class SpiceDbPermissionServiceImplTest {

  @Test
  void updateRelationships() {

    var stub = mock(PermissionsServiceGrpc.PermissionsServiceBlockingStub.class);
    var sut = new SpiceDbPermissionServiceImpl(stub, Duration.ofSeconds(3));

    var o = ObjectRef.of("file", "/test.txt");
    var s = ObjectRef.of("user", "bob");
    var updates =
        UpdateRelationships.newBuilder().update(UpdateRelationship.ofUpdate(o, "owner", s)).build();

    var token = "atXyz";
    var res =
        WriteRelationshipsResponse.newBuilder()
            .setWrittenAt(ZedToken.newBuilder().setToken(token).build())
            .build();
    when(stub.withDeadlineAfter(any(Duration.class))).thenReturn(stub);
    when(stub.writeRelationships(any())).thenReturn(res);

    // when
    var got = sut.updateRelationships(updates);

    // then
    assertEquals(token, got.consistencyToken());
  }

  @Test
  void checkPermission() {

    var stub = mock(PermissionsServiceGrpc.PermissionsServiceBlockingStub.class);
    var sut = new SpiceDbPermissionServiceImpl(stub, Duration.ofSeconds(3));

    var permission = "write";
    var consistency = Consistency.fullyConsistent();

    var o = ObjectRef.of("file", "/test.txt");
    var s = SubjectRef.ofObject(ObjectRef.of("user", "bob"));

    var res =
        CheckPermissionResponse.newBuilder()
            .setPermissionship(CheckPermissionResponse.Permissionship.PERMISSIONSHIP_HAS_PERMISSION)
            .build();
    when(stub.withDeadlineAfter(any(Duration.class))).thenReturn(stub);
    when(stub.checkPermission(any())).thenReturn(res);

    // when
    var got =
        sut.checkPermission(
            CheckPermission.newBuilder()
                .permission(permission)
                .consistency(consistency)
                .resource(o)
                .subject(s)
                .build());

    // then
    assertTrue(got);
  }

  @Test
  void checkBulkPermissions() {

    var stub = mock(PermissionsServiceGrpc.PermissionsServiceBlockingStub.class);
    var sut = new SpiceDbPermissionServiceImpl(stub, Duration.ofSeconds(3));

    var permission1 = "read";
    var permission2 = "write";

    var o = ObjectRef.of("file", "/test.txt");
    var s = SubjectRef.ofObject(ObjectRef.of("user", "bob"));

    var res =
        CheckBulkPermissionsResponse.newBuilder()
            .addPairs(
                CheckBulkPermissionsPair.newBuilder()
                    .setItem(
                        CheckBulkPermissionsResponseItem.newBuilder()
                            .setPermissionship(
                                CheckPermissionResponse.Permissionship
                                    .PERMISSIONSHIP_HAS_PERMISSION)))
            .addPairs(
                CheckBulkPermissionsPair.newBuilder()
                    .setItem(
                        CheckBulkPermissionsResponseItem.newBuilder()
                            .setPermissionship(
                                CheckPermissionResponse.Permissionship
                                    .PERMISSIONSHIP_NO_PERMISSION)))
            .build();
    when(stub.withDeadlineAfter(any(Duration.class))).thenReturn(stub);
    when(stub.checkBulkPermissions(any())).thenReturn(res);

    // when
    var got =
        sut.checkBulkPermissions(
            CheckBulkPermissions.newBuilder()
                .item(
                    CheckBulkPermissionItem.newBuilder()
                        .permission(permission1)
                        .resource(o)
                        .subject(s)
                        .build())
                .item(
                    CheckBulkPermissionItem.newBuilder()
                        .permission(permission2)
                        .resource(o)
                        .subject(s)
                        .build())
                .build());

    // then
    assertEquals(2, got.size());
    assertTrue(got.get(0).permissionGranted());
    assertFalse(got.get(1).permissionGranted());
  }

  @Test
  void lookupResources() {
    var stub = mock(PermissionsServiceGrpc.PermissionsServiceBlockingStub.class);
    var sut = new SpiceDbPermissionServiceImpl(stub, Duration.ofSeconds(3));

    var resourceType = "document";
    var permission = "read";
    var consistency = Consistency.fullyConsistent();
    var subject = SubjectRef.ofObject(ObjectRef.of("user", "alice"));

    var response1 = LookupResourcesResponse.newBuilder().setResourceObjectId("doc1").build();
    var response2 = LookupResourcesResponse.newBuilder().setResourceObjectId("doc2").build();

    @SuppressWarnings("unchecked")
    java.util.Iterator<LookupResourcesResponse> responseIterator = mock(java.util.Iterator.class);
    when(responseIterator.hasNext()).thenReturn(true, true, false);
    when(responseIterator.next()).thenReturn(response1, response2);

    when(stub.withDeadlineAfter(any(Duration.class))).thenReturn(stub);
    when(stub.lookupResources(any())).thenReturn(responseIterator);

    // when
    var got =
        sut.lookupResources(
            LookupResources.newBuilder()
                .resourceType(resourceType)
                .permission(permission)
                .subject(subject)
                .consistency(consistency)
                .build());

    // then
    assertEquals(2, got.size());
    assertEquals(resourceType, got.get(0).kind());
    assertEquals("doc1", got.get(0).id());
    assertEquals(resourceType, got.get(1).kind());
    assertEquals("doc2", got.get(1).id());
  }

  @Test
  void lookupResources_empty() {
    var stub = mock(PermissionsServiceGrpc.PermissionsServiceBlockingStub.class);
    var sut = new SpiceDbPermissionServiceImpl(stub, Duration.ofSeconds(3));

    @SuppressWarnings("unchecked")
    java.util.Iterator<LookupResourcesResponse> responseIterator = mock(java.util.Iterator.class);
    when(responseIterator.hasNext()).thenReturn(false);

    when(stub.withDeadlineAfter(any(Duration.class))).thenReturn(stub);
    when(stub.lookupResources(any())).thenReturn(responseIterator);

    // when
    var got =
        sut.lookupResources(
            LookupResources.newBuilder()
                .resourceType("document")
                .permission("read")
                .subject(SubjectRef.ofObject(ObjectRef.of("user", "alice")))
                .consistency(Consistency.fullyConsistent())
                .build());

    // then
    assertTrue(got.isEmpty());
  }

  @Test
  void lookupSubjects() {
    var stub = mock(PermissionsServiceGrpc.PermissionsServiceBlockingStub.class);
    var sut = new SpiceDbPermissionServiceImpl(stub, Duration.ofSeconds(3));

    var resource = ObjectRef.of("document", "doc123");
    var permission = "read";
    var subjectType = "user";
    var consistency = Consistency.fullyConsistent();

    var response1 =
        LookupSubjectsResponse.newBuilder()
            .setSubject(ResolvedSubject.newBuilder().setSubjectObjectId("alice"))
            .build();
    var response2 =
        LookupSubjectsResponse.newBuilder()
            .setSubject(ResolvedSubject.newBuilder().setSubjectObjectId("bob"))
            .build();

    @SuppressWarnings("unchecked")
    java.util.Iterator<LookupSubjectsResponse> responseIterator = mock(java.util.Iterator.class);
    when(responseIterator.hasNext()).thenReturn(true, true, false);
    when(responseIterator.next()).thenReturn(response1, response2);

    when(stub.withDeadlineAfter(any(Duration.class))).thenReturn(stub);
    when(stub.lookupSubjects(any())).thenReturn(responseIterator);

    // when
    var got =
        sut.lookupSubjects(
            LookupSubjects.newBuilder()
                .resource(resource)
                .permission(permission)
                .subjectType(subjectType)
                .consistency(consistency)
                .build());

    // then
    assertEquals(2, got.size());
    assertEquals(subjectType, got.get(0).kind());
    assertEquals("alice", got.get(0).id());
    assertEquals(subjectType, got.get(1).kind());
    assertEquals("bob", got.get(1).id());
  }

  @Test
  void lookupSubjects_empty() {
    var stub = mock(PermissionsServiceGrpc.PermissionsServiceBlockingStub.class);
    var sut = new SpiceDbPermissionServiceImpl(stub, Duration.ofSeconds(3));

    @SuppressWarnings("unchecked")
    java.util.Iterator<LookupSubjectsResponse> responseIterator = mock(java.util.Iterator.class);
    when(responseIterator.hasNext()).thenReturn(false);

    when(stub.withDeadlineAfter(any(Duration.class))).thenReturn(stub);
    when(stub.lookupSubjects(any())).thenReturn(responseIterator);

    // when
    var got =
        sut.lookupSubjects(
            LookupSubjects.newBuilder()
                .resource(ObjectRef.of("document", "doc123"))
                .permission("read")
                .subjectType("user")
                .consistency(Consistency.fullyConsistent())
                .build());

    // then
    assertTrue(got.isEmpty());
  }

  @Test
  void lookupSubjects_withSubjectRelation() {
    var stub = mock(PermissionsServiceGrpc.PermissionsServiceBlockingStub.class);
    var sut = new SpiceDbPermissionServiceImpl(stub, Duration.ofSeconds(3));

    var subjectRelation = "member";

    var response1 =
        LookupSubjectsResponse.newBuilder()
            .setSubject(ResolvedSubject.newBuilder().setSubjectObjectId("team1"))
            .build();

    @SuppressWarnings("unchecked")
    java.util.Iterator<LookupSubjectsResponse> responseIterator = mock(java.util.Iterator.class);
    when(responseIterator.hasNext()).thenReturn(true, false);
    when(responseIterator.next()).thenReturn(response1);

    when(stub.withDeadlineAfter(any(Duration.class))).thenReturn(stub);
    when(stub.lookupSubjects(any())).thenReturn(responseIterator);

    // when
    var got =
        sut.lookupSubjects(
            LookupSubjects.newBuilder()
                .resource(ObjectRef.of("document", "doc123"))
                .permission("read")
                .subjectType("team")
                .optionalSubjectRelation(subjectRelation)
                .consistency(Consistency.fullyConsistent())
                .build());

    // then
    assertEquals(1, got.size());
    assertEquals("team", got.get(0).kind());
    assertEquals("team1", got.get(0).id());
    assertEquals(subjectRelation, got.get(0).relation());
  }
}
