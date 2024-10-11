package gusevdmi;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Stateless
@Path("/books")
public class BookResource {

    @GET
    @Path("{title : [a-zA-Z][a-zA-Z_0-9]}")
    public Response getBookByTitle(@PathParam("title") String title) {
        return Response.status(200).entity("getBookByTitle is called, title : " + title).build();
    }

    @GET
    @Path("{isbn : \\d+}")
    public Response getBookByISBN(@PathParam("isbn") String isbn) {
        return Response.status(200).entity("getBookByISBN is called, isbn : "
                + isbn).build();
    }

}