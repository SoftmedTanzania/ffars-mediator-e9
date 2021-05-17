package tz.go.moh.him.ffars.mediator.e9.domain;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import tz.go.moh.him.mediator.core.exceptions.ArgumentNullException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Contains tests for the {@link FundAllocation} class.
 */
public class FundAllocationTest {

    /**
     * Tests the deserialization of an FundAllocation.
     */
    @Test
    public void testDeserializeFundAllocation() throws IOException {
        InputStream stream = FundAllocationTest.class.getClassLoader().getResourceAsStream("facility-funds-allocation-request.json");

        Assert.assertNotNull(stream);

        String data = IOUtils.toString(stream);

        Assert.assertNotNull(data);

        Gson gson = new Gson();

        FundAllocation fundAllocation = gson.fromJson(data, FundAllocation.class);
        FundAllocation.Item item = fundAllocation.getItems().get(0);

        Assert.assertEquals("1621259720", fundAllocation.getUid());
        Assert.assertEquals("2020-07-10", fundAllocation.getApplyDate());
        Assert.assertEquals("DR510004", item.getFacilityCode());
        Assert.assertEquals("DR510004", item.getFacilityCode());
        Assert.assertEquals("Vijibweni Hospital Kigamboni", item.getFacilityName());
        Assert.assertEquals("District Hospitals", item.getFacilityType());
        Assert.assertEquals("Advance Payment", item.getDescription());
        Assert.assertTrue(item.isOperational());
        Assert.assertEquals(7579500.00, item.getFacilityAllocation(), 1);
        Assert.assertEquals(14606648.61, item.getCurrentBalance(),1);

    }

    /**
     * Tests the serialization of an FundAllocation.
     */
    @Test
    public void testSerializeFundAllocation() {
        FundAllocation.Item item = new FundAllocation.Item("109089890", "Mahida", "DISP", true, 40000, "desc", 30000,"BRHQ-00000330");
        FundAllocation fundAllocation = new FundAllocation("123412341234", "12-02-2019", Arrays.asList(item));

        Gson gson = new Gson();

        String actual = gson.toJson(fundAllocation);

        Assert.assertTrue(actual.contains("123412341234"));
        Assert.assertTrue(actual.contains("12-02-2019"));
        Assert.assertTrue(actual.contains("109089890"));
        Assert.assertTrue(actual.contains("Mahida"));
        Assert.assertTrue(actual.contains("DISP"));
        Assert.assertTrue(actual.contains("40000"));
        Assert.assertTrue(actual.contains("desc"));
        Assert.assertTrue(actual.contains("30000"));
        Assert.assertTrue(actual.contains("BRHQ-00000330"));
    }

    /**
     * Tests the exception when instantiating FundAllocation.
     */
    @Test(expected = ArgumentNullException.class)
    public void testSerializeFundAllocationExceptionStatus() {
        new FundAllocation("123456789", null, null);
    }

    /**
     * Tests the exception when instantiating FundAllocation.
     */
    @Test(expected = ArgumentNullException.class)
    public void testSerializeFundAllocationExceptionUid() {
        new FundAllocation(null, "Success", null);
    }

    /**
     * Tests the exception when instantiating FundAllocation.
     */
    @Test(expected = ArgumentNullException.class)
    public void testSerializeFundAllocationExceptionItems() {
        new FundAllocation("12231", "Success", null);
    }
}