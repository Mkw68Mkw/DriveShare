package ch.zli.m223.resource;

import ch.zli.m223.dto.ExpenseRequest;
import ch.zli.m223.dto.ExpenseUpdateRequest;
import ch.zli.m223.entity.Expense;
import ch.zli.m223.entity.Trip;
import ch.zli.m223.util.ApiError;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@Path("/expenses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExpenseResource {

    @GET
    public List<Expense> getAll() {
        return Expense.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Expense expense = Expense.findById(id);

        if (expense == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiError.of("Expense not found"))
                    .build();
        }

        return Response.ok(expense).build();
    }

    @POST
    @Transactional
    public Response create(ExpenseRequest request) {
        if (request.tripId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiError.of("tripId is required"))
                    .build();
        }

        Trip trip = Trip.findById(request.tripId);
        if (trip == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiError.of("Trip not found"))
                    .build();
        }

        if (request.amount == null || request.amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiError.of("amount must be greater than 0"))
                    .build();
        }

        Expense expense = new Expense();
        expense.trip = trip;
        expense.amount = request.amount;
        expense.category = request.category;
        expense.description = request.description;
        expense.expenseDate = request.expenseDate;
        expense.persist();

        return Response
                .created(URI.create("/expenses/" + expense.id))
                .entity(expense)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, ExpenseUpdateRequest request) {
        Expense expense = Expense.findById(id);

        if (expense == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiError.of("Expense not found"))
                    .build();
        }

        if (request.amount == null || request.amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiError.of("amount must be greater than 0"))
                    .build();
        }

        expense.amount = request.amount;
        expense.category = request.category;
        expense.description = request.description;
        expense.expenseDate = request.expenseDate;

        return Response.ok(expense).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = Expense.deleteById(id);

        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiError.of("Expense not found"))
                    .build();
        }

        return Response.noContent().build();
    }
}
