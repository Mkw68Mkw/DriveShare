package ch.zli.m223.dto;

import ch.zli.m223.entity.enums.ReservationStatus;

import java.time.LocalDateTime;

public class ReservationUpdateRequest {

    public LocalDateTime startTime;
    public LocalDateTime endTime;
    public ReservationStatus status;
}
