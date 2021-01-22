package tz.go.moh.him.ffars.mediator.e9.orchestration;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.RegistrationConfig;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.testing.TestingUtils;
import tz.go.moh.him.ffars.mediator.e9.MediatorMain;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseTest {
    /**
     * Represents the system actor.
     */
    protected static ActorSystem system;

    /**
     * Represents the configuration.
     */
    protected MediatorConfig testConfig;

    /**
     * Represents an Error Messages Definition Resource Object defined in <a href="file:../resources/error-messages.json">/resources/error-messages.json</a>.
     */
    protected JSONObject errorMessageResource;

    /**
     * Runs initialization before each class execution.
     */
    @Before
    public void before() throws Exception {
        system = ActorSystem.create();

        testConfig = new MediatorConfig();
        testConfig.setName("ffars-mediator-e9-tests");
        testConfig.setProperties("mediator-unit-test.properties");

        InputStream regInfo = MediatorMain.class.getClassLoader().getResourceAsStream("mediator-registration-info.json");
        RegistrationConfig regConfig = null;
        if (regInfo != null) {
            regConfig = new RegistrationConfig(regInfo);
        }

        testConfig.setRegistrationConfig(regConfig);

        InputStream stream = getClass().getClassLoader().getResourceAsStream("error-messages.json");
        if (stream != null) {
            errorMessageResource = new JSONObject(IOUtils.toString(stream));
        }
    }

    /**
     * Runs cleanup after class execution.
     */
    @After
    public void after() {
        TestingUtils.clearRootContext(system, testConfig.getName());
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    /**
     * Method for initiating actors, creating requests and sending request to the actor.
     *
     * @param system     the actor system used to initialize the destination actor
     * @param testConfig the configuration used
     * @param sender     the sending actor
     * @param payload    the payload
     * @param type       class type of the destination orchestrator
     * @param path       to send the request
     */
    public void createActorAndSendRequest(ActorSystem system, MediatorConfig testConfig, ActorRef sender, String payload, Class<?> type, String path) {
        final ActorRef orchestratorActor = system.actorOf(Props.create(type, testConfig));
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/json");
        MediatorHTTPRequest POST_Request = new MediatorHTTPRequest(
                sender,
                sender,
                "unit-test",
                "POST",
                "http",
                null,
                null,
                path,
                payload,
                headers,
                Collections.<Pair<String, String>>emptyList()
        );

        orchestratorActor.tell(POST_Request, sender);
    }
}
