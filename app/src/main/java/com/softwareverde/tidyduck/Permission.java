package com.softwareverde.tidyduck;

public enum Permission {
    LOGIN,              // Ability to authenticate into application
    ADMIN_CREATE_USERS, // Ability to create new users
    ADMIN_MODIFY_USERS, // Ability to assign permissions to other users
    ADMIN_DELETE_USERS, // Ability to delete users
    ADMIN_RESET_PASSWORD, // Ability to reset other user's passwords
    MOST_COMPONENTS_RELEASE, // Ability to release a new version of a function catalog
    MOST_COMPONENTS_CREATE, // Ability to create/fork MOST components
    MOST_COMPONENTS_MODIFY, // Ability to alter a MOST component (change metadata, add children)
    MOST_COMPONENTS_VIEW, // Ability to see all MOST components and their metadata and children
    TYPES_CREATE, // Ability to create a type
    TYPES_MODIFY, // Ability to modify an existing type
    REVIEWS_APPROVAL, // Ability to approve a review
    REVIEWS_COMMENTS, // Ability to comment on a review (and see existing comments)
    REVIEWS_VOTING, // Ability up-vote/down-vote a review
    REVIEWS_VIEW // Ability to see reviews
}
