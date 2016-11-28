package com.clife.identity.domain;

public enum Authority {
    ROOT(Application.SYSTEM, AuthorityType.ROOT),
    IDENTITY_ADMIN(Application.IDENTITY, AuthorityType.ADMIN),
    BODY_TRACKING_ADMIN(Application.BODY_TRACKING, AuthorityType.ADMIN),
    FOOD_TRACKING_ADMIN(Application.FOOD_TRACKING, AuthorityType.ADMIN),
    CARDIO_TRACKING_ADMIN(Application.CARDIO_TRACKING, AuthorityType.ADMIN),
    STRENGTH_TRACKING_ADMIN(Application.STRENGTH_TRACKING, AuthorityType.ADMIN),
    RECIPES_ADMIN(Application.RECIPES, AuthorityType.ADMIN),
    MEALS_ADMIN(Application.MEALS, AuthorityType.ADMIN),
    CALENDAR_ADMIN(Application.CALENDAR, AuthorityType.ADMIN),
    IDENTITY(Application.IDENTITY, AuthorityType.USER),
    BODY_TRACKING(Application.BODY_TRACKING, AuthorityType.USER),
    FOOD_TRACKING(Application.FOOD_TRACKING, AuthorityType.USER),
    CARDIO_TRACKING(Application.CARDIO_TRACKING, AuthorityType.USER),
    STRENGTH_TRACKING(Application.STRENGTH_TRACKING, AuthorityType.USER),
    RECIPES(Application.RECIPES, AuthorityType.USER),
    MEALS(Application.MEALS, AuthorityType.USER),
    CALENDAR(Application.CALENDAR, AuthorityType.USER);


    public final Application application;
    public final AuthorityType authorityType;
    Authority(Application application, AuthorityType authorityType) {
        this.application = application;
        this.authorityType = authorityType;
    }
}
