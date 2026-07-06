package ch.zli.m223.dto;

import java.time.LocalDateTime;

public class ReservationRequest {

    public Long userId;
    public Long vehicleId;
    public LocalDateTime startTime;
    public LocalDateTime endTime;
}
