package ch.zli.m223.resource;

import ch.zli.m223.entity.Vehicle;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/vehicles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VehicleResource {

    @GET
    public List<Vehicle> getAll() {
        return Vehicle.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Vehicle vehicle = Vehicle.findById(id);

        if (vehicle == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(vehicle).build();
    }

    @POST
    @Transactional
    public Response create(Vehicle vehicle) {
        vehicle.id = null;
        vehicle.persist();

        return Response
                .created(URI.create("/vehicles/" + vehicle.id))
                .entity(vehicle)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, Vehicle updatedVehicle) {
        Vehicle vehicle = Vehicle.findById(id);

        if (vehicle == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        vehicle.brand = updatedVehicle.brand;
        vehicle.model = updatedVehicle.model;
        vehicle.licensePlate = updatedVehicle.licensePlate;
        vehicle.mileage = updatedVehicle.mileage;
        vehicle.status = updatedVehicle.status;
        vehicle.active = updatedVehicle.active;

        return Response.ok(vehicle).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = Vehicle.deleteById(id);

        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.noContent().build();
    }
}
