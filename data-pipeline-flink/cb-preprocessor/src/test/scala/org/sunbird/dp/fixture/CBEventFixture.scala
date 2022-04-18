package org.sunbird.dp.fixture

object CBEventFixture {

  val WO_EVENT: String =
    """{
      |  "actor": {
      |    "id": "59c3c2b7-b32b-4d9a-9d02-220e73004d66",
      |    "type": "User"
      |  },
      |  "eid": "CB_AUDIT",
      |  "edata": {
      |    "state": "Draft",
      |    "props": ["WAT"],
      |    "cb_object": {
      |      "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |      "type": "WorkOrder",
      |      "ver": "1.0",
      |      "name": "Work order - Finance wing",
      |      "org": "New NHTest"
      |    },
      |    "cb_data": {
      |      "data": {
      |        "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |        "name": "Work order - Finance wing",
      |        "deptId": "013260789496258560586",
      |        "deptName": "New NHTest",
      |        "status": "Draft",
      |        "userIds": [
      |          "3f90ed64-2cba-4e14-8844-1ec53da454f8"
      |        ],
      |        "createdBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |        "createdAt": 1628844512397,
      |        "updatedBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |        "updatedAt": 1628845042921,
      |        "progress": 91,
      |        "errorCount": 0,
      |        "rolesCount": 1,
      |        "activitiesCount": 1,
      |        "competenciesCount": 1,
      |        "publishedPdfLink": null,
      |        "signedPdfLink": null,
      |        "mdo_name": "New NHTest",
      |        "users": [
      |          {
      |            "id": "3f90ed64-2cba-4e14-8844-1ec53da454f8",
      |            "userId": "535c8d83-e5ed-4b91-82eb-89031702dcc9",
      |            "roleCompetencyList": [
      |              {
      |                "roleDetails": {
      |                  "type": "ROLE",
      |                  "id": "id01",
      |                  "name": "Management role",
      |                  "description": "",
      |                  "status": null,
      |                  "source": null,
      |                  "childNodes": [
      |                    {
      |                      "type": "ACTIVITY",
      |                      "id": "id01",
      |                      "name": "",
      |                      "description": "Manager role",
      |                      "status": null,
      |                      "source": null,
      |                      "parentRole": null,
      |                      "submittedFromId": null,
      |                      "submittedToId": "",
      |                      "level": null
      |                    }
      |                  ],
      |                  "addedAt": 0,
      |                  "updatedAt": 0,
      |                  "updatedBy": null,
      |                  "archivedAt": 0,
      |                  "archived": false
      |                },
      |                "competencyDetails": [
      |                  {
      |                    "type": "COMPETENCY",
      |                    "id": "id01",
      |                    "name": "behavioural competency profiling",
      |                    "description": "behavioural competency profiling desc",
      |                    "source": null,
      |                    "status": null,
      |                    "level": "Level 1",
      |                    "additionalProperties": {
      |                      "competencyArea": "Area",
      |                      "competencyType": "Behavioural"
      |                    },
      |                    "children": null
      |                  }
      |                ]
      |              }
      |            ],
      |            "unmappedActivities": [],
      |            "unmappedCompetencies": [],
      |            "userPosition": "Team management",
      |            "positionId": "id01",
      |            "positionDescription": "manage-teams",
      |            "workOrderId": "9a99e795-c652-4c0d-9f9f-960c737e15f3",
      |            "updatedAt": 1628845041770,
      |            "updatedBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |            "errorCount": 0,
      |            "progress": 91,
      |            "createdAt": 1628845041770,
      |            "createdBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c"
      |          }
      |        ]
      |      }
      |    }
      |  },
      |  "ver": "3.0",
      |  "ets": 1629109359638,
      |  "context": {
      |    "channel": "013260789496258560586",
      |    "pdata": {
      |      "id": "dev.mdo.portal",
      |      "pid": "mdo",
      |      "ver": "1.0"
      |    },
      |    "env": "WAT"
      |  },
      |  "mid": "CB.b4b1a956-d8d5-48e4-8cee-0dc616823402",
      |  "object": {
      |    "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |    "type": "WorkOrder"
      |  }
      |}""".stripMargin

  val WO_EVENT_RESULT: String =
    """[
      |  {
      |    "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |    "name": "Work order - Finance wing",
      |    "deptId": "013260789496258560586",
      |    "deptName": "New NHTest",
      |    "status": "Draft",
      |    "userIds": [
      |      "3f90ed64-2cba-4e14-8844-1ec53da454f8"
      |    ],
      |    "createdBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |    "createdAt": 1628844512397,
      |    "updatedBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |    "updatedAt": 1628845042921,
      |    "progress": 91,
      |    "errorCount": 0,
      |    "rolesCount": 1,
      |    "activitiesCount": 1,
      |    "competenciesCount": 1,
      |    "publishedPdfLink": null,
      |    "signedPdfLink": null,
      |    "mdo_name": "New NHTest",
      |    "wa_id": "3f90ed64-2cba-4e14-8844-1ec53da454f8",
      |    "wa_userId": "535c8d83-e5ed-4b91-82eb-89031702dcc9",
      |    "wa_userPosition": "Team management",
      |    "wa_positionId": "id01",
      |    "wa_positionDescription": "manage-teams",
      |    "wa_workOrderId": "9a99e795-c652-4c0d-9f9f-960c737e15f3",
      |    "wa_updatedAt": 1628845041770,
      |    "wa_updatedBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |    "wa_errorCount": 0,
      |    "wa_progress": 91,
      |    "wa_createdAt": 1628845041770,
      |    "wa_createdBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |    "wa_role_type": "ROLE",
      |    "wa_role_id": "id01",
      |    "wa_role_name": "Management role",
      |    "wa_role_description": "",
      |    "wa_role_status": null,
      |    "wa_role_source": null,
      |    "wa_role_addedAt": 0,
      |    "wa_role_updatedAt": 0,
      |    "wa_role_updatedBy": null,
      |    "wa_role_archivedAt": 0,
      |    "wa_role_archived": false,
      |    "wa_activity_type": "ACTIVITY",
      |    "wa_activity_id": "id01",
      |    "wa_activity_name": "",
      |    "wa_activity_description": "Manager role",
      |    "wa_activity_status": null,
      |    "wa_activity_source": null,
      |    "wa_activity_parentRole": null,
      |    "wa_activity_submittedFromId": null,
      |    "wa_activity_submittedToId": "",
      |    "wa_activity_level": null
      |  },
      |  {
      |    "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |    "name": "Work order - Finance wing",
      |    "deptId": "013260789496258560586",
      |    "deptName": "New NHTest",
      |    "status": "Draft",
      |    "userIds": [
      |      "3f90ed64-2cba-4e14-8844-1ec53da454f8"
      |    ],
      |    "createdBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |    "createdAt": 1628844512397,
      |    "updatedBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |    "updatedAt": 1628845042921,
      |    "progress": 91,
      |    "errorCount": 0,
      |    "rolesCount": 1,
      |    "activitiesCount": 1,
      |    "competenciesCount": 1,
      |    "publishedPdfLink": null,
      |    "signedPdfLink": null,
      |    "mdo_name": "New NHTest",
      |    "wa_id": "3f90ed64-2cba-4e14-8844-1ec53da454f8",
      |    "wa_userId": "535c8d83-e5ed-4b91-82eb-89031702dcc9",
      |    "wa_userPosition": "Team management",
      |    "wa_positionId": "id01",
      |    "wa_positionDescription": "manage-teams",
      |    "wa_workOrderId": "9a99e795-c652-4c0d-9f9f-960c737e15f3",
      |    "wa_updatedAt": 1628845041770,
      |    "wa_updatedBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |    "wa_errorCount": 0,
      |    "wa_progress": 91,
      |    "wa_createdAt": 1628845041770,
      |    "wa_createdBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |    "wa_role_type": "ROLE",
      |    "wa_role_id": "id01",
      |    "wa_role_name": "Management role",
      |    "wa_role_description": "",
      |    "wa_role_status": null,
      |    "wa_role_source": null,
      |    "wa_role_addedAt": 0,
      |    "wa_role_updatedAt": 0,
      |    "wa_role_updatedBy": null,
      |    "wa_role_archivedAt": 0,
      |    "wa_role_archived": false,
      |    "wa_competency_type": "COMPETENCY",
      |    "wa_competency_id": "id01",
      |    "wa_competency_name": "behavioural competency profiling",
      |    "wa_competency_description": "behavioural competency profiling desc",
      |    "wa_competency_source": null,
      |    "wa_competency_status": null,
      |    "wa_competency_level": "Level 1",
      |    "wa_competency_additionalProperties": {
      |      "competencyArea": "Area",
      |      "competencyType": "Behavioural"
      |    },
      |    "wa_competency_children": null
      |  }
      |]""".stripMargin

  val WO_EVENT_POSITIONS_RESULT: String =
    """[
      |  {
      |    "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |    "name": "Work order - Finance wing",
      |    "deptId": "013260789496258560586",
      |    "deptName": "New NHTest",
      |    "status": "Draft",
      |    "userIds": [
      |      "3f90ed64-2cba-4e14-8844-1ec53da454f8"
      |    ],
      |    "createdBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |    "createdAt": 1628844512397,
      |    "updatedBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |    "updatedAt": 1628845042921,
      |    "progress": 91,
      |    "errorCount": 0,
      |    "rolesCount": 1,
      |    "activitiesCount": 1,
      |    "competenciesCount": 1,
      |    "publishedPdfLink": null,
      |    "signedPdfLink": null,
      |    "mdo_name": "New NHTest",
      |    "wa_id": "3f90ed64-2cba-4e14-8844-1ec53da454f8",
      |    "wa_userId": "535c8d83-e5ed-4b91-82eb-89031702dcc9",
      |    "wa_userPosition": "Team management",
      |    "wa_positionId": "id01",
      |    "wa_positionDescription": "manage-teams",
      |    "wa_workOrderId": "9a99e795-c652-4c0d-9f9f-960c737e15f3",
      |    "wa_updatedAt": 1628845041770,
      |    "wa_updatedBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |    "wa_errorCount": 0,
      |    "wa_progress": 91,
      |    "wa_createdAt": 1628845041770,
      |    "wa_createdBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c"
      |  }
      |]""".stripMargin

  val WO_EVENT_NO_USERS: String =
    """{
      |  "actor": {
      |    "id": "59c3c2b7-b32b-4d9a-9d02-220e73004d66",
      |    "type": "User"
      |  },
      |  "eid": "CB_AUDIT",
      |  "edata": {
      |    "state": "Draft",
      |    "props": ["WAT"],
      |    "cb_object": {
      |      "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |      "type": "WorkOrder",
      |      "ver": "1.0",
      |      "name": "Work order - Finance wing",
      |      "org": "New NHTest"
      |    },
      |    "cb_data": {
      |      "data": {
      |        "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |        "name": "Work order - Finance wing",
      |        "deptId": "013260789496258560586",
      |        "deptName": "New NHTest",
      |        "status": "Draft",
      |        "userIds": [
      |          "3f90ed64-2cba-4e14-8844-1ec53da454f8"
      |        ],
      |        "createdBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |        "createdAt": 1628844512397,
      |        "updatedBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |        "updatedAt": 1628845042921,
      |        "progress": 91,
      |        "errorCount": 0,
      |        "rolesCount": 1,
      |        "activitiesCount": 1,
      |        "competenciesCount": 1,
      |        "publishedPdfLink": null,
      |        "signedPdfLink": null,
      |        "mdo_name": "New NHTest"
      |      }
      |    }
      |  },
      |  "ver": "3.0",
      |  "ets": 1629109359638,
      |  "context": {
      |    "channel": "013260789496258560586",
      |    "pdata": {
      |      "id": "dev.mdo.portal",
      |      "pid": "mdo",
      |      "ver": "1.0"
      |    },
      |    "env": "WAT"
      |  },
      |  "mid": "CB.b4b1a956-d8d5-48e4-8cee-0dc616823402",
      |  "object": {
      |    "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |    "type": "WorkOrder"
      |  }
      |}""".stripMargin

  val WO_EVENT_NO_USERS_RESULT: String =
    """[]""".stripMargin

  val WO_EVENT_USERS_NULL: String =
    """{
      |  "actor": {
      |    "id": "59c3c2b7-b32b-4d9a-9d02-220e73004d66",
      |    "type": "User"
      |  },
      |  "eid": "CB_AUDIT",
      |  "edata": {
      |    "state": "Draft",
      |    "props": ["WAT"],
      |    "cb_object": {
      |      "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |      "type": "WorkOrder",
      |      "ver": "1.0",
      |      "name": "Work order - Finance wing",
      |      "org": "New NHTest"
      |    },
      |    "cb_data": {
      |      "data": {
      |        "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |        "name": "Work order - Finance wing",
      |        "deptId": "013260789496258560586",
      |        "deptName": "New NHTest",
      |        "status": "Draft",
      |        "userIds": [
      |          "3f90ed64-2cba-4e14-8844-1ec53da454f8"
      |        ],
      |        "createdBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |        "createdAt": 1628844512397,
      |        "updatedBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |        "updatedAt": 1628845042921,
      |        "progress": 91,
      |        "errorCount": 0,
      |        "rolesCount": 1,
      |        "activitiesCount": 1,
      |        "competenciesCount": 1,
      |        "publishedPdfLink": null,
      |        "signedPdfLink": null,
      |        "mdo_name": "New NHTest",
      |        "users": null
      |      }
      |    }
      |  },
      |  "ver": "3.0",
      |  "ets": 1629109359638,
      |  "context": {
      |    "channel": "013260789496258560586",
      |    "pdata": {
      |      "id": "dev.mdo.portal",
      |      "pid": "mdo",
      |      "ver": "1.0"
      |    },
      |    "env": "WAT"
      |  },
      |  "mid": "CB.b4b1a956-d8d5-48e4-8cee-0dc616823402",
      |  "object": {
      |    "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |    "type": "WorkOrder"
      |  }
      |}""".stripMargin

  val WO_EVENT_USERS_NULL_RESULT: String =
    """[]""".stripMargin

  val WO_EVENT_NO_RCL: String =
    """{
      |  "actor": {
      |    "id": "59c3c2b7-b32b-4d9a-9d02-220e73004d66",
      |    "type": "User"
      |  },
      |  "eid": "CB_AUDIT",
      |  "edata": {
      |    "state": "Draft",
      |    "props": ["WAT"],
      |    "cb_object": {
      |      "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |      "type": "WorkOrder",
      |      "ver": "1.0",
      |      "name": "Work order - Finance wing",
      |      "org": "New NHTest"
      |    },
      |    "cb_data": {
      |      "data": {
      |        "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |        "name": "Work order - Finance wing",
      |        "deptId": "013260789496258560586",
      |        "deptName": "New NHTest",
      |        "status": "Draft",
      |        "userIds": [
      |          "3f90ed64-2cba-4e14-8844-1ec53da454f8"
      |        ],
      |        "createdBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |        "createdAt": 1628844512397,
      |        "updatedBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |        "updatedAt": 1628845042921,
      |        "progress": 91,
      |        "errorCount": 0,
      |        "rolesCount": 1,
      |        "activitiesCount": 1,
      |        "competenciesCount": 1,
      |        "publishedPdfLink": null,
      |        "signedPdfLink": null,
      |        "mdo_name": "New NHTest",
      |        "users": [
      |          {
      |            "id": "3f90ed64-2cba-4e14-8844-1ec53da454f8",
      |            "userId": "535c8d83-e5ed-4b91-82eb-89031702dcc9",
      |            "unmappedActivities": [],
      |            "unmappedCompetencies": [],
      |            "userPosition": "Team management",
      |            "positionId": "id01",
      |            "positionDescription": "manage-teams",
      |            "workOrderId": "9a99e795-c652-4c0d-9f9f-960c737e15f3",
      |            "updatedAt": 1628845041770,
      |            "updatedBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |            "errorCount": 0,
      |            "progress": 91,
      |            "createdAt": 1628845041770,
      |            "createdBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c"
      |          }
      |        ]
      |      }
      |    }
      |  },
      |  "ver": "3.0",
      |  "ets": 1629109359638,
      |  "context": {
      |    "channel": "013260789496258560586",
      |    "pdata": {
      |      "id": "dev.mdo.portal",
      |      "pid": "mdo",
      |      "ver": "1.0"
      |    },
      |    "env": "WAT"
      |  },
      |  "mid": "CB.b4b1a956-d8d5-48e4-8cee-0dc616823402",
      |  "object": {
      |    "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |    "type": "WorkOrder"
      |  }
      |}""".stripMargin

  val WO_EVENT_NO_RCL_RESULT: String =
    """[]""".stripMargin

  val WO_EVENT_NO_ACTIVITY: String =
    """{
      |  "actor": {
      |    "id": "59c3c2b7-b32b-4d9a-9d02-220e73004d66",
      |    "type": "User"
      |  },
      |  "eid": "CB_AUDIT",
      |  "edata": {
      |    "state": "Draft",
      |    "props": ["WAT"],
      |    "cb_object": {
      |      "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |      "type": "WorkOrder",
      |      "ver": "1.0",
      |      "name": "Work order - Finance wing",
      |      "org": "New NHTest"
      |    },
      |    "cb_data": {
      |      "data": {
      |        "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |        "name": "Work order - Finance wing",
      |        "deptId": "013260789496258560586",
      |        "deptName": "New NHTest",
      |        "status": "Draft",
      |        "userIds": [
      |          "3f90ed64-2cba-4e14-8844-1ec53da454f8"
      |        ],
      |        "createdBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |        "createdAt": 1628844512397,
      |        "updatedBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |        "updatedAt": 1628845042921,
      |        "progress": 91,
      |        "errorCount": 0,
      |        "rolesCount": 1,
      |        "activitiesCount": 1,
      |        "competenciesCount": 1,
      |        "publishedPdfLink": null,
      |        "signedPdfLink": null,
      |        "mdo_name": "New NHTest",
      |        "users": [
      |          {
      |            "id": "3f90ed64-2cba-4e14-8844-1ec53da454f8",
      |            "userId": "535c8d83-e5ed-4b91-82eb-89031702dcc9",
      |            "roleCompetencyList": [
      |              {
      |                "roleDetails": {
      |                  "type": "ROLE",
      |                  "id": "id01",
      |                  "name": "Management role",
      |                  "description": "",
      |                  "status": null,
      |                  "source": null,
      |                  "addedAt": 0,
      |                  "updatedAt": 0,
      |                  "updatedBy": null,
      |                  "archivedAt": 0,
      |                  "archived": false
      |                },
      |                "competencyDetails": [
      |                  {
      |                    "type": "COMPETENCY",
      |                    "id": "id01",
      |                    "name": "behavioural competency profiling",
      |                    "description": "behavioural competency profiling desc",
      |                    "source": null,
      |                    "status": null,
      |                    "level": "Level 1",
      |                    "additionalProperties": {
      |                      "competencyArea": "Area",
      |                      "competencyType": "Behavioural"
      |                    },
      |                    "children": null
      |                  }
      |                ]
      |              }
      |            ],
      |            "unmappedActivities": [],
      |            "unmappedCompetencies": [],
      |            "userPosition": "Team management",
      |            "positionId": "id01",
      |            "positionDescription": "manage-teams",
      |            "workOrderId": "9a99e795-c652-4c0d-9f9f-960c737e15f3",
      |            "updatedAt": 1628845041770,
      |            "updatedBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |            "errorCount": 0,
      |            "progress": 91,
      |            "createdAt": 1628845041770,
      |            "createdBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c"
      |          }
      |        ]
      |      }
      |    }
      |  },
      |  "ver": "3.0",
      |  "ets": 1629109359638,
      |  "context": {
      |    "channel": "013260789496258560586",
      |    "pdata": {
      |      "id": "dev.mdo.portal",
      |      "pid": "mdo",
      |      "ver": "1.0"
      |    },
      |    "env": "WAT"
      |  },
      |  "mid": "CB.b4b1a956-d8d5-48e4-8cee-0dc616823402",
      |  "object": {
      |    "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |    "type": "WorkOrder"
      |  }
      |}""".stripMargin

  val WO_EVENT_NO_ACTIVITY_RESULT: String =
    """[
      |  {
      |    "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |    "name": "Work order - Finance wing",
      |    "deptId": "013260789496258560586",
      |    "deptName": "New NHTest",
      |    "status": "Draft",
      |    "userIds": [
      |      "3f90ed64-2cba-4e14-8844-1ec53da454f8"
      |    ],
      |    "createdBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |    "createdAt": 1628844512397,
      |    "updatedBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |    "updatedAt": 1628845042921,
      |    "progress": 91,
      |    "errorCount": 0,
      |    "rolesCount": 1,
      |    "activitiesCount": 1,
      |    "competenciesCount": 1,
      |    "publishedPdfLink": null,
      |    "signedPdfLink": null,
      |    "mdo_name": "New NHTest",
      |    "wa_id": "3f90ed64-2cba-4e14-8844-1ec53da454f8",
      |    "wa_userId": "535c8d83-e5ed-4b91-82eb-89031702dcc9",
      |    "wa_userPosition": "Team management",
      |    "wa_positionId": "id01",
      |    "wa_positionDescription": "manage-teams",
      |    "wa_workOrderId": "9a99e795-c652-4c0d-9f9f-960c737e15f3",
      |    "wa_updatedAt": 1628845041770,
      |    "wa_updatedBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |    "wa_errorCount": 0,
      |    "wa_progress": 91,
      |    "wa_createdAt": 1628845041770,
      |    "wa_createdBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |    "wa_role_type": "ROLE",
      |    "wa_role_id": "id01",
      |    "wa_role_name": "Management role",
      |    "wa_role_description": "",
      |    "wa_role_status": null,
      |    "wa_role_source": null,
      |    "wa_role_addedAt": 0,
      |    "wa_role_updatedAt": 0,
      |    "wa_role_updatedBy": null,
      |    "wa_role_archivedAt": 0,
      |    "wa_role_archived": false,
      |    "wa_competency_type": "COMPETENCY",
      |    "wa_competency_id": "id01",
      |    "wa_competency_name": "behavioural competency profiling",
      |    "wa_competency_description": "behavioural competency profiling desc",
      |    "wa_competency_source": null,
      |    "wa_competency_status": null,
      |    "wa_competency_level": "Level 1",
      |    "wa_competency_additionalProperties": {
      |      "competencyArea": "Area",
      |      "competencyType": "Behavioural"
      |    },
      |    "wa_competency_children": null
      |  }
      |]""".stripMargin

  val WO_EVENT_NO_COMPETENCY: String =
    """{
      |  "actor": {
      |    "id": "59c3c2b7-b32b-4d9a-9d02-220e73004d66",
      |    "type": "User"
      |  },
      |  "eid": "CB_AUDIT",
      |  "edata": {
      |    "state": "Draft",
      |    "props": ["WAT"],
      |    "cb_object": {
      |      "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |      "type": "WorkOrder",
      |      "ver": "1.0",
      |      "name": "Work order - Finance wing",
      |      "org": "New NHTest"
      |    },
      |    "cb_data": {
      |      "data": {
      |        "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |        "name": "Work order - Finance wing",
      |        "deptId": "013260789496258560586",
      |        "deptName": "New NHTest",
      |        "status": "Draft",
      |        "userIds": [
      |          "3f90ed64-2cba-4e14-8844-1ec53da454f8"
      |        ],
      |        "createdBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |        "createdAt": 1628844512397,
      |        "updatedBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |        "updatedAt": 1628845042921,
      |        "progress": 91,
      |        "errorCount": 0,
      |        "rolesCount": 1,
      |        "activitiesCount": 1,
      |        "competenciesCount": 1,
      |        "publishedPdfLink": null,
      |        "signedPdfLink": null,
      |        "mdo_name": "New NHTest",
      |        "users": [
      |          {
      |            "id": "3f90ed64-2cba-4e14-8844-1ec53da454f8",
      |            "userId": "535c8d83-e5ed-4b91-82eb-89031702dcc9",
      |            "roleCompetencyList": [
      |              {
      |                "roleDetails": {
      |                  "type": "ROLE",
      |                  "id": "id01",
      |                  "name": "Management role",
      |                  "description": "",
      |                  "status": null,
      |                  "source": null,
      |                  "childNodes": [
      |                    {
      |                      "type": "ACTIVITY",
      |                      "id": "id01",
      |                      "name": "",
      |                      "description": "Manager role",
      |                      "status": null,
      |                      "source": null,
      |                      "parentRole": null,
      |                      "submittedFromId": null,
      |                      "submittedToId": "",
      |                      "level": null
      |                    }
      |                  ],
      |                  "addedAt": 0,
      |                  "updatedAt": 0,
      |                  "updatedBy": null,
      |                  "archivedAt": 0,
      |                  "archived": false
      |                }
      |              }
      |            ],
      |            "unmappedActivities": [],
      |            "unmappedCompetencies": [],
      |            "userPosition": "Team management",
      |            "positionId": "id01",
      |            "positionDescription": "manage-teams",
      |            "workOrderId": "9a99e795-c652-4c0d-9f9f-960c737e15f3",
      |            "updatedAt": 1628845041770,
      |            "updatedBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |            "errorCount": 0,
      |            "progress": 91,
      |            "createdAt": 1628845041770,
      |            "createdBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c"
      |          }
      |        ]
      |      }
      |    }
      |  },
      |  "ver": "3.0",
      |  "ets": 1629109359638,
      |  "context": {
      |    "channel": "013260789496258560586",
      |    "pdata": {
      |      "id": "dev.mdo.portal",
      |      "pid": "mdo",
      |      "ver": "1.0"
      |    },
      |    "env": "WAT"
      |  },
      |  "mid": "CB.b4b1a956-d8d5-48e4-8cee-0dc616823402",
      |  "object": {
      |    "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |    "type": "WorkOrder"
      |  }
      |}""".stripMargin

  val WO_EVENT_NO_COMPETENCY_RESULT: String =
    """[
      |  {
      |    "id": "643cb47c-3e8a-4d5e-9fd4-45302a9ae09a",
      |    "name": "Work order - Finance wing",
      |    "deptId": "013260789496258560586",
      |    "deptName": "New NHTest",
      |    "status": "Draft",
      |    "userIds": [
      |      "3f90ed64-2cba-4e14-8844-1ec53da454f8"
      |    ],
      |    "createdBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |    "createdAt": 1628844512397,
      |    "updatedBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |    "updatedAt": 1628845042921,
      |    "progress": 91,
      |    "errorCount": 0,
      |    "rolesCount": 1,
      |    "activitiesCount": 1,
      |    "competenciesCount": 1,
      |    "publishedPdfLink": null,
      |    "signedPdfLink": null,
      |    "mdo_name": "New NHTest",
      |    "wa_id": "3f90ed64-2cba-4e14-8844-1ec53da454f8",
      |    "wa_userId": "535c8d83-e5ed-4b91-82eb-89031702dcc9",
      |    "wa_userPosition": "Team management",
      |    "wa_positionId": "id01",
      |    "wa_positionDescription": "manage-teams",
      |    "wa_workOrderId": "9a99e795-c652-4c0d-9f9f-960c737e15f3",
      |    "wa_updatedAt": 1628845041770,
      |    "wa_updatedBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |    "wa_errorCount": 0,
      |    "wa_progress": 91,
      |    "wa_createdAt": 1628845041770,
      |    "wa_createdBy": "075e3a3f-1a56-4ea3-9042-c66e2288e60c",
      |    "wa_role_type": "ROLE",
      |    "wa_role_id": "id01",
      |    "wa_role_name": "Management role",
      |    "wa_role_description": "",
      |    "wa_role_status": null,
      |    "wa_role_source": null,
      |    "wa_role_addedAt": 0,
      |    "wa_role_updatedAt": 0,
      |    "wa_role_updatedBy": null,
      |    "wa_role_archivedAt": 0,
      |    "wa_role_archived": false,
      |    "wa_activity_type": "ACTIVITY",
      |    "wa_activity_id": "id01",
      |    "wa_activity_name": "",
      |    "wa_activity_description": "Manager role",
      |    "wa_activity_status": null,
      |    "wa_activity_source": null,
      |    "wa_activity_parentRole": null,
      |    "wa_activity_submittedFromId": null,
      |    "wa_activity_submittedToId": "",
      |    "wa_activity_level": null
      |  }
      |]""".stripMargin
}
