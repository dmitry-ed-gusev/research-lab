package gusevdmi;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Bean;
import javax.ws.rs.*;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collection;
import java.util.Map;

@ApplicationScoped
@Path("beans")
public class MyBeansExposure {

    @Context ResourceContext rc;
    private Map<String, Bean> myBeans;

    @GET
    @Produces("application/json")
    public Collection<Bean> allBeans() {
        //return Response.status(200).entity(myBeans.values()).build();
        return null;
    }

    @GET
    @Produces("application/json")
    @Path("{id}")
    public Bean singleBean(@PathParam("id") String id) {
        //return Response.status(200).entity(myBeans.get(id)).build();
        return null;
    }

    @POST
    @Consumes("application/json")
    public Response add(Bean bean) {
        if (bean != null) {
            myBeans.put(bean.getName(), bean);
        }
        final URI id = URI.create(bean.getName());
        return Response.created(id).build();
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") String id) {
        myBeans.remove(id);
    }

}