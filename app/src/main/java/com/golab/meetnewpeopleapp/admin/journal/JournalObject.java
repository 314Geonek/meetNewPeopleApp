package com.golab.meetnewpeopleapp.admin.journal;

import com.google.firebase.firestore.DocumentSnapshot;

public class JournalObject {
    private String reportedBy, reportReason, reportTime;

    public JournalObject(DocumentSnapshot snapshot) {
        reportedBy = snapshot.get("reportedBy")!=null? snapshot.get("reportedBy").toString() : "";
        reportReason = snapshot.get("reason")!=null? snapshot.get("reason").toString() : "";
        reportReason = snapshot.get("date")!=null? snapshot.get("date").toString() : "";
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public String getReportReason() {
        return reportReason;
    }

    public String getReportTime() {
        return reportTime;
    }
}
