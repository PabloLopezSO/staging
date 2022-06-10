package com.example.demo.domain;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public enum TaskStatus {
    TO_DO(1), IN_PROGRESS(2), DONE(3), DELETED(4), INVALID(0);

    private final Integer statusNumber;
    private static final Map<Integer, TaskStatus> BY_STATUSNUMBER = new HashMap<>();

    // Mapping
    static {
        for (TaskStatus t : values()) {
            BY_STATUSNUMBER.put(t.statusNumber, t);
        }
    }

    // private enum constructor
    private TaskStatus(Integer status) {
        this.statusNumber = status;
    }

    // methods
    public Integer getStatusNumber() {
        return statusNumber;
    }

    public static TaskStatus from(Number status) {        
        if (status != null) {
            Integer statusInt = status.intValue();
            switch (statusInt) {
                case 1:
                case 2:
                case 3:
                case 4:
                    return BY_STATUSNUMBER.get(statusInt);
                default:
                    return TaskStatus.INVALID;
            }
        }
        return TaskStatus.INVALID;
    }

    public static TaskStatus from(String status) {
        if (StringUtils.isNumeric(status)) {
            switch (status) {
                case "1":
                case "2":
                case "3":
                case "4":
                    return BY_STATUSNUMBER.get(Integer.parseInt(status));
                default:
                    return TaskStatus.INVALID;
            }
        }
        return TaskStatus.INVALID;
    }

}
