package org.plantrmq;

public final class RoutingKeys {

    private RoutingKeys() {
    }

    public static final String EXCHANGE = "plants.events";

    public static final String HOME_PLANT_CREATED = "homeplant.created";
    public static final String ROBOT_CREATED = "robot.created";

    public static final String HOME_PLANT_DELETED = "homeplant.deleted";
    public static final String ROBOT_DELETED = "robot.deleted";

    public static final String HOME_PLANT_UPDATED = "homeplant.updated";
    public static final String ROBOT_UPDATED = "robot.updated";

    public static final String HOME_PLANT_EVENTS = "homeplant.*";
    public static final String ROBOT_EVENTS = "robot.*";
    public static final String ALL_EVENTS = "#";
}
