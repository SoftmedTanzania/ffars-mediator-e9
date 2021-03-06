package tz.go.moh.him.ffars.mediator.e9.orchestration;

import akka.testkit.JavaTestKit;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.testing.MockLauncher;
import org.openhim.mediator.engine.testing.TestingUtils;
import tz.go.moh.him.ffars.mediator.e9.mock.MockDestination;
import tz.go.moh.him.ffars.mediator.e9.orchestrator.FundAllocationOrchestrator;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Contains tests for the {@link FundAllocationOrchestrator} class.
 */
public class FundAllocationOrchestratorTest extends BaseTest {
    /**
     * Represents an Error Messages Definition Resource Object defined in <a href="file:../resources/error-messages.json">/resources/error-messages.json</a>.
     */
    protected JSONObject fundAllocationErrorMessageResource;

    /**
     * Runs initialization before each class execution.
     */
    @Override
    public void before() throws Exception {
        super.before();

        fundAllocationErrorMessageResource = errorMessageResource.getJSONObject("FACILITY_FUND_ALLOCATION_ERROR_MESSAGES");
        List<MockLauncher.ActorToLaunch> toLaunch = new LinkedList<>();
        toLaunch.add(new MockLauncher.ActorToLaunch("http-connector", MockDestination.class));
        TestingUtils.launchActors(system, testConfig.getName(), toLaunch);
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
     * Tests the mediator.
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testMediatorHTTPRequest() throws Exception {
        assertNotNull(testConfig);
        new JavaTestKit(system) {{
            InputStream stream = FundAllocationOrchestratorTest.class.getClassLoader().getResourceAsStream("facility-funds-allocation-request.json");

            assertNotNull(stream);


            createActorAndSendRequest(system, testConfig, getRef(), IOUtils.toString(stream), FundAllocationOrchestrator.class, "/funds_allocations");

            final Object[] out =
                    new ReceiveWhile<Object>(Object.class, duration("1 second")) {
                        @Override
                        protected Object match(Object msg) throws Exception {
                            if (msg instanceof FinishRequest) {
                                return msg;
                            }
                            throw noMatch();
                        }
                    }.get();

            boolean foundResponse = false;

            for (Object o : out) {
                if (o instanceof FinishRequest) {
                    foundResponse = true;
                    break;
                }
            }

            assertTrue("Must send FinishRequest", foundResponse);
        }};
    }

    /**
     * Tests the mediator with payload with invalid payload
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void testInValidPayload() throws Exception {
        assertNotNull(testConfig);

        new JavaTestKit(system) {{
            InputStream stream = FundAllocationOrchestratorTest.class.getClassLoader().getResourceAsStream("facility-funds-allocation-invalid-request.json");

            assertNotNull(stream);

            createActorAndSendRequest(system, testConfig, getRef(), IOUtils.toString(stream), FundAllocationOrchestrator.class, "/funds_allocations");

            final Object[] out =
                    new ReceiveWhile<Object>(Object.class, duration("1 second")) {
                        @Override
                        protected Object match(Object msg) throws Exception {
                            if (msg instanceof FinishRequest) {
                                return msg;
                            }
                            throw noMatch();
                        }
                    }.get();

            int responseStatus = 0;
            String responseMessage = "";

            for (Object o : out) {
                if (o instanceof FinishRequest) {
                    responseStatus = ((FinishRequest) o).getResponseStatus();
                    responseMessage = ((FinishRequest) o).getResponse();
                    break;
                }
            }

            assertEquals(400, responseStatus);
            assertTrue(responseMessage.contains(fundAllocationErrorMessageResource.getString("ERROR_APPLY_DATE_IS_BLANK")));
            assertTrue(responseMessage.contains(String.format(fundAllocationErrorMessageResource.getString("ERROR_APPLY_DATE_IS_BLANK"), "")));
            assertTrue(responseMessage.contains(String.format(fundAllocationErrorMessageResource.getString("ERROR_FACILITY_NAME_IS_BLANK"), "")));
            assertTrue(responseMessage.contains(String.format(fundAllocationErrorMessageResource.getString("ERROR_FACILITY_CODE_IS_BLANK"), "")));
            assertTrue(responseMessage.contains(String.format(fundAllocationErrorMessageResource.getString("ERROR_FACILITY_TYPE_IS_BLANK"), "")));
        }};
    }


}
