package tz.go.moh.him.ffars.mediator.e9.domain;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import tz.go.moh.him.mediator.core.exceptions.ArgumentNullException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Contains tests for the {@link FundAllocation} class.
 */
public class FundAllocationTest {

    /**
     * Tests the deserialization of an FundAllocation.
     */
    @Test
    public void testDeserializeFundAllocation() {
        InputStream stream = FundAllocationTest.class.getClassLoader().getResourceAsStream("facility-funds-allocation-request.json");

        Assert.assertNotNull(stream);

        String data;

        try {
            data = IOUtils.toString(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Assert.assertNotNull(data);

        Gson gson = new Gson();

        FundAllocation fundAllocation = gson.fromJson(data, FundAllocation.class);
        FundAllocation.Item item = fundAllocation.getItems().get(0);

        Assert.assertEquals("4590", fundAllocation.getUid());
        Assert.assertEquals("12-02-2019", fundAllocation.getApplyDate());
        Assert.assertEquals("109089890", item.getFacilityCode());
        Assert.assertEquals("Mahida", item.getFacilityName());
        Assert.assertEquals("DISP", item.getFacilityType());
        Assert.assertEquals("Mapokezi ya fedha kutoka MOF kwa awamu ya kwanza", item.getDescription());
        Assert.assertTrue(item.isOperational());
        Assert.assertEquals(30000, item.getFacilityAllocation());
        Assert.assertEquals(20000, item.getCurrentBalance());

    }

    /**
     * Tests the serialization of an FundAllocation.
     */
    @Test
    public void testSerializeFundAllocation() {

        FundAllocation fundAllocation = new FundAllocation("123412341234", "12-02-2019",null);

        Gson gson = new Gson();

        String actual = gson.toJson(fundAllocation);

        Assert.assertTrue(actual.contains("123412341234"));
        Assert.assertTrue(actual.contains("12-02-2019"));
    }

    /**
     * Tests the exception when instantiating FundAllocation.
     */
    @Test(expected = ArgumentNullException.class)
    public void testSerializeAckExceptionStatus() {
        new FundAllocation("123456789", null,null);
    }

    /**
     * Tests the exception when instantiating FundAllocation.
     */
    @Test(expected = ArgumentNullException.class)
    public void testSerializeAckExceptionTransactionIdNumber() {
        new FundAllocation(null, "Success",null);
    }
}