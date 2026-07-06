package ch.zli.m223.resource;

import ch.zli.m223.dto.LoginRequest;
import ch.zli.m223.dto.LoginResponse;
import ch.zli.m223.dto.RegisterRequest;
import ch.zli.m223.entity.User;
import ch.zli.m223.entity.enums.UserRole;
import ch.zli.m223.util.ApiError;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @POST
    @Path("/register")
    @Transactional
    public Response register(RegisterRequest request) {
        if (request.email == null || request.password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiError.of("Email and password are required"))
                    .build();
        }

        User existing = User.find("email", request.email).firstResult();
        if (existing != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(ApiError.of("Email is already registered"))
                    .build();
        }

        User user = new User();
        user.firstName = request.firstName;
        user.lastName = request.lastName;
        user.email = request.email;
        user.passwordHash = BcryptUtil.bcryptHash(request.password);
        user.role = UserRole.USER;
        user.active = true;
        user.persist();

        return Response
                .created(URI.create("/users/" + user.id))
                .entity(user)
                .build();
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        if (request.email == null || request.password == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ApiError.of("Invalid credentials"))
                    .build();
        }

        User user = User.find("email", request.email).firstResult();

        boolean valid = user != null
                && user.active
                && BcryptUtil.matches(request.password, user.passwordHash);

        if (!valid) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ApiError.of("Invalid credentials"))
                    .build();
        }

        LoginResponse response = new LoginResponse("Login successful", user.id, user.role);
        return Response.ok(response).build();
    }
}
