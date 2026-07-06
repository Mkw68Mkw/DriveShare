package ch.zli.m223.resource;

import ch.zli.m223.dto.TripRequest;
import ch.zli.m223.dto.TripUpdateRequest;
import ch.zli.m223.entity.Reservation;
import ch.zli.m223.entity.Trip;
import ch.zli.m223.entity.enums.ReservationStatus;
import ch.zli.m223.util.ApiError;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/trips")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TripResource {

    @GET
    public List<Trip> getAll() {
        return Trip.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Trip trip = Trip.findById(id);

        if (trip == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiError.of("Trip not found"))
                    .build();
        }

        return Response.ok(trip).build();
    }

    @POST
    @Transactional
    public Response create(TripRequest request) {
        if (request.reservationId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiError.of("reservationId is required"))
                    .build();
        }

        Reservation reservation = Reservation.findById(request.reservationId);
        if (reservation == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiError.of("Reservation not found"))
                    .build();
        }

        Trip existing = Trip.find("reservation.id", request.reservationId).firstResult();
        if (existing != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(ApiError.of("Trip already exists for this reservation"))
                    .build();
        }

        if (request.endMileage < request.startMileage) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiError.of("endMileage must not be smaller than startMileage"))
                    .build();
        }

        Trip trip = new Trip();
        trip.reservation = reservation;
        trip.startMileage = request.startMileage;
        trip.endMileage = request.endMileage;
        trip.startLocation = request.startLocation;
        trip.destination = request.destination;
        trip.notes = request.notes;
        trip.persist();

        // Reservation abschliessen und Kilometerstand des Fahrzeugs aktualisieren.
        reservation.status = ReservationStatus.COMPLETED;
        reservation.vehicle.mileage = request.endMileage;

        return Response
                .created(URI.create("/trips/" + trip.id))
                .entity(trip)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, TripUpdateRequest request) {
        Trip trip = Trip.findById(id);

        if (trip == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiError.of("Trip not found"))
                    .build();
        }

        if (request.endMileage < request.startMileage) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiError.of("endMileage must not be smaller than startMileage"))
                    .build();
        }

        trip.startMileage = request.startMileage;
        trip.endMileage = request.endMileage;
        trip.startLocation = request.startLocation;
        trip.destination = request.destination;
        trip.notes = request.notes;

        // Kilometerstand des zugehörigen Fahrzeugs mit aktualisieren.
        trip.reservation.vehicle.mileage = request.endMileage;

        return Response.ok(trip).build();
    }
}
