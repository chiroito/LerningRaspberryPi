package com.example.chapter4;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 */
@Path("/stop")
@Singleton
public class LEDController {

    @GET
    public String stop() {
        Chapter4CliApplication.runningFlag = false;
        return "Stop";
    }
}
