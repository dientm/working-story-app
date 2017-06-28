package com.castis.service;

import com.castis.model.ActivityDto;

import java.util.List;

/**
 * Created by trand on 6/28/2017.
 */

public class Activities {
    private List<ActivityDto> activities;

    public List<ActivityDto> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDto> activities) {
        this.activities = activities;
    }
}
