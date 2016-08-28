package com.nex3z.shalarm.presentation.alert;

import com.nex3z.shalarm.presentation.model.AlarmModel;

import java.util.Comparator;

public class AlarmComparator implements Comparator<AlarmModel> {

    @Override
    public int compare(AlarmModel left, AlarmModel right) {
        int result = 0;
        long diff = left.getNextAlertTime().getTime() - right.getNextAlertTime().getTime();
        if(diff > 0){
            return 1;
        }else if (diff < 0){
            return -1;
        }
        return result;
    }
}
