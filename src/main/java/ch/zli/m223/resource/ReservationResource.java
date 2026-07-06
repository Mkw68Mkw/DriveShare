package ch.zli.m223.resource;

import ch.zli.m223.dto.ReservationRequest;
import ch.zli.m223.dto.ReservationUpdateRequest;
import ch.zli.m223.entity.Reservation;
import ch.zli.m223.entity.User;
import ch.zli.m223.entity.Vehicle;
import ch.zli.m223.entity.enums.ReservationStatus;
import ch.zli.m223.util.ApiError;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Path("/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {

    @GET
    public List<Reservation> getAll() {
        return Reservation.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Reservation reservation = Reservation.findById(id);

        if (reservation == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiError.of("Reservation not found"))
                    .build();
        }

        return Response.ok(reservation).build();
    }

    @POST
    @Transactional
    public Response create(ReservationRequest request) {
        if (request.userId == null || request.vehicleId == null
                || request.startTime == null || request.endTime == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiError.of("userId, vehicleId, startTime and endTime are required"))
                    .build();
        }

        if (!request.startTime.isBefore(request.endTime)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiError.of("startTime must be before endTime"))
                    .build();
        }

        User user = User.findById(request.userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiError.of("User not found"))
                    .build();
        }
        if (!user.active) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiError.of("User is not active"))
                    .build();
        }

        Vehicle vehicle = Vehicle.findById(request.vehicleId);
        if (vehicle == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiError.of("Vehicle not found"))
                    .build();
        }
        if (!vehicle.active) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiError.of("Vehicle is not active"))
                    .build();
        }

        if (hasConflict(vehicle.id, request.startTime, request.endTime, null)) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(ApiError.of("Vehicle is already reserved in this period"))
                    .build();
        }

        Reservation reservation = new Reservation();
        reservation.user = user;
        reservation.vehicle = vehicle;
        reservation.startTime = request.startTime;
        reservation.endTime = request.endTime;
        reservation.status = ReservationStatus.PLANNED;
        reservation.createdAt = LocalDateTime.now();
        reservation.persist();

        return Response
                .created(URI.create("/reservations/" + reservation.id))
                .entity(reservation)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, ReservationUpdateRequest request) {
        Reservation reservation = Reservation.findById(id);

        if (reservation == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiError.of("Reservation not found"))
                    .build();
        }

        LocalDateTime newStart = request.startTime != null ? request.startTime : reservation.startTime;
        LocalDateTime newEnd = request.endTime != null ? request.endTime : reservation.endTime;

        if (!newStart.isBefore(newEnd)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiError.of("startTime must be before endTime"))
                    .build();
        }

        // Die aktuelle Reservation wird bei der Konfliktprüfung ausgeschlossen.
        if (hasConflict(reservation.vehicle.id, newStart, newEnd, reservation.id)) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(ApiError.of("Vehicle is already reserved in this period"))
                    .build();
        }

        reservation.startTime = newStart;
        reservation.endTime = newEnd;
        if (request.status != null) {
            reservation.status = request.status;
        }

        return Response.ok(reservation).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = Reservation.deleteById(id);

        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiError.of("Reservation not found"))
                    .build();
        }

        return Response.noContent().build();
    }

    /**
     * Prüft, ob es für ein Fahrzeug im gewünschten Zeitraum eine kollidierende
     * Reservation gibt. Kollidierend sind alle Reservationen, die nicht CANCELLED
     * sind und deren Zeitraum sich überschneidet:
     * bestehender Start < neuer Endzeitpunkt UND bestehendes Ende > neuer Startzeitpunkt.
     * excludeId erlaubt es, eine bestimmte Reservation (z. B. die eigene) auszunehmen.
     */
    private boolean hasConflict(Long vehicleId, LocalDateTime start, LocalDateTime end, Long excludeId) {
        String query = "vehicle.id = ?1 and status <> ?2 and startTime < ?3 and endTime > ?4";

        if (excludeId == null) {
            return Reservation.count(query, vehicleId, ReservationStatus.CANCELLED, end, start) > 0;
        }

        return Reservation.count(query + " and id <> ?5",
                vehicleId, ReservationStatus.CANCELLED, end, start, excludeId) > 0;
    }
}
