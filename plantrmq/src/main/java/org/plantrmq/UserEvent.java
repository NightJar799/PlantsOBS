package org.plantrmq;

public sealed interface UserEvent {
    
    record Created(
        Long userId,
        String name,
        String mail,
        String password,
        String phone
    ) implements UserEvent {}

    record Deleted(
        Long userId,
        String name,
        String mail
    ) implements UserEvent {}

    record Updated(
        Long userId,
        String name,
        String mail,
        String password,
        String phone
    ) implements UserEvent {}
}
