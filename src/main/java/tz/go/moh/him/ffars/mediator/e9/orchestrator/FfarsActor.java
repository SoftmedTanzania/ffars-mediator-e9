package tz.go.moh.him.ffars.mediator.e9.orchestrator;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FfarsActor extends UntypedActor {
    private final MediatorConfig config;
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private ActorRef requestHandler;


    public FfarsActor(MediatorConfig config) {
        this.config = config;
    }

    private void forwardToFfars(String message) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String scheme;
        if (config.getProperty("ffars.secure").equals("true")) {
            scheme = "https";
        } else {
            scheme = "http";
        }

        List<Pair<String, String>> params = new ArrayList<>();

        MediatorHTTPRequest forwardToHdrRequest = new MediatorHTTPRequest(
                requestHandler, getSelf(), "Sending Data to the FFARS Server", "POST", scheme,
                config.getProperty("ffars.host"), Integer.parseInt(config.getProperty("ffars.api.port")), config.getProperty("ffars.api.path"),
                message, headers, params
        );

        ActorSelection httpConnector = getContext().actorSelection(config.userPathFor("http-connector"));
        httpConnector.tell(forwardToHdrRequest, getSelf());
    }


    private void finalizeResponse(MediatorHTTPResponse response) {
        requestHandler.tell(response.toFinishRequest(), getSelf());
    }


    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof MediatorHTTPRequest) { //process message
            log.info("Sending data to FFARS ...");

            requestHandler = ((MediatorHTTPRequest) msg).getRequestHandler();
            forwardToFfars(new Gson().toJson(((MediatorHTTPRequest) msg).getBody()));

        } else if (msg instanceof MediatorHTTPResponse) { //respond
            log.info("Received response from FFARS");
            finalizeResponse((MediatorHTTPResponse) msg);
        } else {
            unhandled(msg);
        }
    }
}
