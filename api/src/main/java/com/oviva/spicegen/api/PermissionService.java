package com.oviva.spicegen.api;

import java.util.List;

public interface PermissionService {

  /**
   * Looks up all resources of a given type that the subject can access with the given permission.
   * For example: "What documents can user:alice read?"
   *
   * @param lookupResources the request
   * @return list of ObjectRefs the subject has access to
   */
  List<ObjectRef> lookupResources(LookupResources lookupResources);

  /**
   * Looks up all subjects of a given type that can access the resource with the given permission.
   * For example: "Who can read document:123?"
   *
   * @param lookupSubjects the request
   * @return list of SubjectRefs that have access
   */
  List<SubjectRef> lookupSubjects(LookupSubjects lookupSubjects);

  /**
   * Updates relationships, optionally with preconditions. The returned consistencyToken should be
   * stored alongside the created resource such that the authorization can be done at the given
   * consistency. This vastly improves the performance and allows the system to function even when
   * it is partitioned.
   *
   * @param updateRelationships the request
   * @return the result, containing the consistencyToken
   */
  UpdateResult updateRelationships(UpdateRelationships updateRelationships);

  /**
   * Checks whether a subject has the given permission on a resource
   *
   * @param checkPermission the request
   * @return true it the subject is permitted, false otherwise
   */
  boolean checkPermission(CheckPermission checkPermission);

  /**
   * This is a batch version of
   *
   * @see PermissionService#checkPermission(CheckPermission)
   * @param checkBulkPermissions the request
   * @return list of results, one for each checkPermission request
   */
  List<CheckBulkPermissionsResult> checkBulkPermissions(CheckBulkPermissions checkBulkPermissions);
}
