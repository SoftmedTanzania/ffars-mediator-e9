package tz.go.moh.him.ffars.mediator.e9.orchestrator;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.SimpleMediatorRequest;
import tz.go.moh.him.mediator.core.domain.ErrorMessage;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseOrchestrator extends UntypedActor {
    /**
     * The mediator configuration.
     */
    protected final MediatorConfig config;

    /**
     * The logger instance.
     */
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    /**
     * Represents a list of error messages, if any,that have been caught during payload data validation to be returned to the source system as response.
     */
    protected List<ErrorMessage> errorMessages = new ArrayList<>();

    /**
     * Represents a mediator request.
     */
    protected MediatorHTTPRequest originalRequest;

    /**
     * Represents an Error Messages Definition Resource Object defined in <a href="file:../resources/error-messages.json">/resources/error-messages.json</a>.
     */
    protected JSONObject errorMessageResource;

    /**
     * Initializes a new instance of the {@link BaseOrchestrator} class.
     *
     * @param config The mediator configuration.
     */
    public BaseOrchestrator(MediatorConfig config) {
        this.config = config;
        InputStream stream = getClass().getClassLoader().getResourceAsStream("error-messages.json");
        try {
            if (stream != null) {
                errorMessageResource = new JSONObject(IOUtils.toString(stream));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the received message.
     *
     * @param msg The received message.
     */
    @Override
    public void onReceive(Object msg) {
        if (msg instanceof MediatorHTTPRequest) {
            originalRequest = (MediatorHTTPRequest) msg;

            //Converting the received request body to POJO List
            Object object = null;
            try {
                object = convertMessageBodyToPojoList(((MediatorHTTPRequest) msg).getBody());
            } catch (Exception e) {
                //In-case of an exception creating an error message with the stack trace
                ErrorMessage errorMessage = new ErrorMessage(
                        originalRequest.getBody(),
                        Arrays.asList(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, e.getMessage(), StringUtils.writeStackTraceToString(e)))
                );
                errorMessages.add(errorMessage);
            }

            log.info("Received payload in JSON = " + new Gson().toJson(object));

            if (validateData(object))
                sendDataToFfars(object);
            else {
                FinishRequest finishRequest = new FinishRequest(new Gson().toJson(errorMessages), "text/plain", HttpStatus.SC_BAD_REQUEST);
                (originalRequest).getRequestHandler().tell(finishRequest, getSelf());
            }

        } else {
            unhandled(msg);
        }
    }


    /**
     * Abstract method to handle Convertion the msg string payload to Correct POJO list
     *
     * @param msg payload to be converted
     * @return POJO
     * @throws IOException if an I/O exception occurs
     */
    protected abstract Object convertMessageBodyToPojoList(String msg) throws IOException;

    /**
     * Abstract method to handle data validations
     *
     * @param object object to be validated
     * @return true if the object passes all data validation, or false in case the object fails any data validation
     */
    protected abstract boolean validateData(Object object);

    /**
     * Method that handles sending of data to FFARS
     *
     * @param object list of objects that passed data validations to be sent to FFARS
     */
    private void sendDataToFfars(Object object) {
        log.info("Sending data to FFARS Actor");
        ActorRef actor = getContext().actorOf(Props.create(FfarsActor.class, config));
        actor.tell(
                new SimpleMediatorRequest<>(
                        originalRequest.getRequestHandler(),
                        getSelf(),
                        object), getSelf());
    }
}
