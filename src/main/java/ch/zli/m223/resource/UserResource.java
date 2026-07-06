package ch.zli.m223.resource;

import ch.zli.m223.entity.User;
import ch.zli.m223.util.ApiError;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @GET
    public List<User> getAll() {
        return User.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        User user = User.findById(id);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiError.of("User not found"))
                    .build();
        }

        return Response.ok(user).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, User updatedUser) {
        User user = User.findById(id);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiError.of("User not found"))
                    .build();
        }

        // E-Mail darf nicht von einem anderen Benutzer verwendet werden.
        User existingWithEmail = User.find("email", updatedUser.email).firstResult();
        if (existingWithEmail != null && !existingWithEmail.id.equals(id)) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(ApiError.of("Email is already in use"))
                    .build();
        }

        user.firstName = updatedUser.firstName;
        user.lastName = updatedUser.lastName;
        user.email = updatedUser.email;
        user.role = updatedUser.role;
        user.active = updatedUser.active;
        // passwordHash wird hier bewusst nicht verändert.

        return Response.ok(user).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = User.deleteById(id);

        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiError.of("User not found"))
                    .build();
        }

        return Response.noContent().build();
    }
}
