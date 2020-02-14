package koreatech.in.service;

import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Homepage.Activity;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.repository.ActivityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static koreatech.in.domain.DomainToMap.domainToMap;

@Service("activityService")
public class ActivityServiceImpl implements ActivityService {
    @Resource(name = "activityMapper")
    private ActivityMapper activityMapper;

    @Autowired
    private JsonConstructor con;

    @Override
    public Map<String, Object> getActivities(String year) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        List<Activity> activities;
        if(year == null) {
            activities = activityMapper.getActivityList();
        } else {
            activities = activityMapper.getActivityListByYear(year);
        }
        List<Map<String, Object>> appendActivities = new ArrayList<Map<String, Object>>();

        Map<String, Object> convertActivity;
        for(Activity activity : activities) {
            convertActivity = domainToMap(activity);
            convertActivity.replace("image_urls", con.parseJsonArrayWithOnlyString(activity.getImage_urls()));

            appendActivities.add(convertActivity);
        }

        map.put("Activities", appendActivities);

        return map;
    }

    // ===== ADMIN APIs =====
    @Override
    public Map<String, Object> getActivitiesForAdmin(String year) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        List<Activity> activities;
        if(year == null) {
            activities = activityMapper.getActivityListForAdmin();
        } else {
            activities = activityMapper.getActivityListByYearForAdmin(year);
        }
        List<Map<String, Object>> appendActivities = new ArrayList<Map<String, Object>>();

        Map<String, Object> convertActivity;
        for(Activity activity : activities) {
            convertActivity = domainToMap(activity);
            convertActivity.replace("image_urls", con.parseJsonArrayWithOnlyString(activity.getImage_urls()));

            appendActivities.add(convertActivity);
        }

        map.put("Activities", appendActivities);

        return map;
    }

    @Override
    public Activity getActivityForAdmin(int id) throws Exception {
        Activity activity = activityMapper.getActivityForAdmin(id);
        if(activity == null) {
            throw new NotFoundException(new ErrorMessage("Activity not found", 0));
        }

        return activity;
    }

    @Override
    public Activity createActivityForAdmin(Activity activity) throws Exception {
        if(activity.getIs_deleted() == null)
            activity.setIs_deleted(false);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(activity.getDate());
        } catch (Exception e) {
            throw new PreconditionFailedException(new ErrorMessage("Date format invalid", 0));
        }

        activityMapper.createActivityForAdmin(activity);

        return activity;
    }

    @Override
    public Activity updateActivityForAdmin(Activity activity, int id) throws Exception {
        Activity activity_old = activityMapper.getActivityForAdmin(id);
        if(activity_old == null) {
            throw new NotFoundException(new ErrorMessage("Activity not found", 0));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(activity.getDate());
        } catch (Exception e) {
            throw new PreconditionFailedException(new ErrorMessage("Date format invalid", 0));
        }

        activity_old.update(activity);
        activityMapper.updateActivityForAdmin(activity_old);

        return activity_old;
    }

    @Override
    public Map<String, Object> deleteActivityForAdmin(int id) throws Exception {
        Activity selectActivity = activityMapper.getActivityForAdmin(id);
        if(selectActivity == null) {
            throw new NotFoundException(new ErrorMessage("Activity not found", 0));
        }

        activityMapper.deleteActivityForAdmin(id);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }
}
