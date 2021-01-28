package tz.go.moh.him.ffars.mediator.e9.orchestrator;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPResponse;
import org.openhim.mediator.engine.messages.SimpleMediatorRequest;
import tz.go.moh.him.ffars.mediator.e9.domain.Expenditure;
import tz.go.moh.him.ffars.mediator.e9.domain.FundAllocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FfarsActor extends UntypedActor {
    /**
     * The String constants representing Expenditure payload type
     */
    public static final String EXPENDITURE = "expenditure";

    /**
     * The String constants representing Facility fund allocation payload type
     */
    public static final String FACILITY_FUNDS_ALLOCATIONS = "facility_funds_allocations";

    /**
     * The mediator configuration.
     */
    private final MediatorConfig config;

    /**
     * The logger instance.
     */
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    /**
     * Represents the requestHandler that will be used to process requests and responses.
     */
    private ActorRef requestHandler;

    public FfarsActor(MediatorConfig config) {
        this.config = config;
    }

    private void forwardToFfars(String message, String type) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String scheme;
        String host;
        String path="";
        int portNumber;

        if (config.getDynamicConfig().isEmpty()) {
            if (config.getProperty("ffars.secure").equals("true")) {
                scheme = "https";
            } else {
                scheme = "http";
            }

            host = config.getProperty("ffars.host");
            portNumber = Integer.parseInt(config.getProperty("ffars.api.port"));
            if (type.equals(FACILITY_FUNDS_ALLOCATIONS)) {
                path = config.getProperty("ffars.api.fund_allocation.path");
            } else if (type.equals(EXPENDITURE)) {
                path = config.getProperty("ffars.api.expenditure.path");
            }
        } else {
            JSONObject connectionProperties = new JSONObject(config.getDynamicConfig()).getJSONObject("ffarsConnectionProperties");

            host = connectionProperties.getString("ffarsHost");
            portNumber = connectionProperties.getInt("ffarsPort");

            if (type.equals(FACILITY_FUNDS_ALLOCATIONS)) {
                path = connectionProperties.getString("ffarsFundAllocationPath");
            } else if (type.equals(EXPENDITURE)) {
                path = connectionProperties.getString("ffarsExpenditurePath");
            }
            scheme = connectionProperties.getString("ffarsScheme");
        }

        List<Pair<String, String>> params = new ArrayList<>();

        MediatorHTTPRequest forwardToFffarsRequest = new MediatorHTTPRequest(
                requestHandler, getSelf(), "Sending Data to the FFARS Server", "POST", scheme,
                host, portNumber, path, message, headers, params
        );

        ActorSelection httpConnector = getContext().actorSelection(config.userPathFor("http-connector"));
        httpConnector.tell(forwardToFffarsRequest, getSelf());
    }


    private void finalizeResponse(MediatorHTTPResponse response) {
        requestHandler.tell(response.toFinishRequest(), getSelf());
    }


    @Override
    public void onReceive(Object msg) throws Exception {
        if (SimpleMediatorRequest.isInstanceOf(Expenditure.class, msg)) {
            log.info("Sending Expenditure Payload to FFARS ...");

            requestHandler = ((SimpleMediatorRequest) msg).getRequestHandler();
            forwardToFfars(new Gson().toJson(((SimpleMediatorRequest<?>) msg).getRequestObject()), EXPENDITURE);

        } else if (SimpleMediatorRequest.isInstanceOf(FundAllocation.class, msg)) {
            log.info("Sending Fund Allocation Payload to FFARS ...");

            requestHandler = ((SimpleMediatorRequest) msg).getRequestHandler();
            forwardToFfars(new Gson().toJson(((SimpleMediatorRequest) msg).getRequestObject()), FACILITY_FUNDS_ALLOCATIONS);

        } else if (msg instanceof MediatorHTTPResponse) { //respond
            log.info("Received response from FFARS");
            finalizeResponse((MediatorHTTPResponse) msg);
        } else {
            unhandled(msg);
        }
    }
}
