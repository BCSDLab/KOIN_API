package koreatech.in.service;

import koreatech.in.domain.Homepage.Activity;

import java.util.Map;

public interface ActivityService {
    Map<String, Object> getActivities(String year) throws Exception;

    // ===== ADMIN APIs =====
    Map<String, Object> getActivitiesForAdmin(String year) throws Exception;

    Activity getActivityForAdmin(int id) throws Exception;

    Activity createActivityForAdmin(Activity activity) throws Exception;

    Activity updateActivityForAdmin(Activity activity, int id) throws Exception;

    Map<String, Object> deleteActivityForAdmin(int id) throws Exception;
}
