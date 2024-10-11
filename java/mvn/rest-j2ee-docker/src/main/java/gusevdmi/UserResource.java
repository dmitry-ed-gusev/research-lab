package gusevdmi;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.registry.infomodel.User;

@Stateless
@Path("/users")
public class UserResource {

    //@EJB
    //private UserService userService;

    @GET
    @Path("/query")
    @Produces("application/json")
    public Response getUsers(@QueryParam("from") int from, @QueryParam("to") int to, @QueryParam("orderBy") List<String> orderBy) {
        //List<User> users = userService.getUsers(from, to, orderBy);
        //return Response.status(200).entity(users).build();
        return null;
    }

}