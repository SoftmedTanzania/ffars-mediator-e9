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
import tz.go.moh.him.ffars.mediator.e9.orchestrator.ExpenditureOrchestrator;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ExpenditureOrchestratorTest extends BaseTest {

    protected JSONObject expenditureErrorMessageResource;

    @Override
    public void before() throws Exception {
        super.before();

        expenditureErrorMessageResource = errorMessageResource.getJSONObject("EXPENDITURE_ERROR_MESSAGES");
        List<MockLauncher.ActorToLaunch> toLaunch = new LinkedList<>();
        toLaunch.add(new MockLauncher.ActorToLaunch("http-connector", MockDestination.class));
        TestingUtils.launchActors(system, testConfig.getName(), toLaunch);
    }

    @After
    public void after() {
        TestingUtils.clearRootContext(system, testConfig.getName());
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testMediatorHTTPRequest() throws Exception {
        assertNotNull(testConfig);
        new JavaTestKit(system) {{
            InputStream stream = ExpenditureOrchestratorTest.class.getClassLoader().getResourceAsStream("expenditure-request.json");

            assertNotNull(stream);


            createActorAndSendRequest(system, testConfig, getRef(), IOUtils.toString(stream), ExpenditureOrchestrator.class, "/expenditure");

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

    @Test
    public void testInValidPayload() throws Exception {
        assertNotNull(testConfig);

        new JavaTestKit(system) {{
            InputStream stream = ExpenditureOrchestratorTest.class.getClassLoader().getResourceAsStream("expenditure-invalid-request.json");

            assertNotNull(stream);

            createActorAndSendRequest(system, testConfig, getRef(), IOUtils.toString(stream), ExpenditureOrchestrator.class, "/expenditure");

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
            assertTrue(responseMessage.contains(expenditureErrorMessageResource.getString("ERROR_APPLY_DATE_IS_BLANK")));
            assertTrue(responseMessage.contains(String.format(expenditureErrorMessageResource.getString("ERROR_APPLY_DATE_IS_BLANK"), "")));
            assertTrue(responseMessage.contains(String.format(expenditureErrorMessageResource.getString("ERROR_INVOICE_NUMBER_IS_BLANK"), "")));
            assertTrue(responseMessage.contains(String.format(expenditureErrorMessageResource.getString("ERROR_ORDER_DATE_IS_BLANK"), "")));
            assertTrue(responseMessage.contains(String.format(expenditureErrorMessageResource.getString("ERROR_FACILITY_NAME_IS_BLANK"), "")));
            assertTrue(responseMessage.contains(String.format(expenditureErrorMessageResource.getString("ERROR_FACILITY_CODE_IS_BLANK"), "")));
            assertTrue(responseMessage.contains(String.format(expenditureErrorMessageResource.getString("ERROR_FACILITY_TYPE_IS_BLANK"), "")));
        }};
    }


}
