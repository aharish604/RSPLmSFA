package com.rspl.sf.msfa.solist;

import java.io.Serializable;

/**
 * Created by e10769 on 05-06-2017.
 */

public class SOTaskHistoryBean implements Serializable {
    private String InstanceID = "";
    private String Timestamp = "";
    private String ActionName = "";
    private String PerformedBy = "";
    private String PerformedByName = "";
    private String Comments = "";
    private String TaskStatusID = "";
    private String EntityType = "";

    public String getInstanceID() {
        return InstanceID;
    }

    public void setInstanceID(String instanceID) {
        InstanceID = instanceID;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getActionName() {
        return ActionName;
    }

    public void setActionName(String actionName) {
        ActionName = actionName;
    }

    public String getPerformedBy() {
        return PerformedBy;
    }

    public void setPerformedBy(String performedBy) {
        PerformedBy = performedBy;
    }

    public String getPerformedByName() {
        return PerformedByName;
    }

    public void setPerformedByName(String performedByName) {
        PerformedByName = performedByName;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public String getTaskStatusID() {
        return TaskStatusID;
    }

    public void setTaskStatusID(String taskStatusID) {
        TaskStatusID = taskStatusID;
    }

    public String getEntityType() {
        return EntityType;
    }

    public void setEntityType(String entityType) {
        EntityType = entityType;
    }
}
