package com.sfl.pms.api.internal.rest.resources.maintanance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 5/19/15
 * Time: 1:32 AM
 */
@Component
@Path("heartbeat")
public class HeartBeatResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatResource.class);

    //region Constructors
    public HeartBeatResource() {
        LOGGER.debug("Initializing - {}", getClass().getCanonicalName());
    }
    //endregion

    //region Public methods
    @GET
    public Response checkHearBeat() {
        LOGGER.info("Get health messages - {}", getClass().getCanonicalName());
        return Response.ok("OK").build();
    }
    //endregion
}
