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
 * Contains tests for the {@link Expenditure} class.
 */
public class ExpenditureTest {

    /**
     * Tests the deserialization of an Expenditure.
     */
    @Test
    public void testDeserializeExpenditure() throws IOException {
        InputStream stream = ExpenditureTest.class.getClassLoader().getResourceAsStream("expenditure-request.json");

        Assert.assertNotNull(stream);

        String data = IOUtils.toString(stream);

        Assert.assertNotNull(data);

        Gson gson = new Gson();

        Expenditure fundAllocation = gson.fromJson(data, Expenditure.class);
        Expenditure.Item item = fundAllocation.getItems().get(0);

        Assert.assertEquals("4590", fundAllocation.getUid());
        Assert.assertEquals("12-02-2019", fundAllocation.getApplyDate());
        Assert.assertEquals("12-09-2018", item.getOrderDate());
        Assert.assertEquals("INV003-2019", item.getInvoiceNumber());
        Assert.assertEquals("109089890", item.getFacilityCode());
        Assert.assertEquals("Majengo", item.getFacilityName());
        Assert.assertEquals("HC", item.getFacilityType());
        Assert.assertEquals("Any description  or empty", item.getDescription());
        Assert.assertTrue(item.isOperational());
        Assert.assertEquals(30000, item.getTotalCost());
        Assert.assertEquals(24000, item.getInKInd());
        Assert.assertEquals(6000, item.getOtherSource());
        Assert.assertEquals(20000, item.getCurrentBalance());

    }

    /**
     * Tests the serialization of an Expenditure.
     */
    @Test
    public void testSerializeExpenditure() {
        Expenditure.Item item = new Expenditure.Item("12-09-2018", "INV003-2019", "109089890", "Majengo", "HC", true, 30000, 24000, 6000, "", 20000);
        Expenditure expenditure = new Expenditure("4590", "12-02-2019", Arrays.asList(item));

        Gson gson = new Gson();

        String actual = gson.toJson(expenditure);

        Assert.assertTrue(actual.contains("4590"));
        Assert.assertTrue(actual.contains("12-02-2019"));
        Assert.assertTrue(actual.contains("12-09-2018"));
        Assert.assertTrue(actual.contains("INV003-2019"));
        Assert.assertTrue(actual.contains("109089890"));
        Assert.assertTrue(actual.contains("Majengo"));
        Assert.assertTrue(actual.contains("HC"));
        Assert.assertTrue(actual.contains("true"));
        Assert.assertTrue(actual.contains("30000"));
        Assert.assertTrue(actual.contains("24000"));
        Assert.assertTrue(actual.contains("6000"));
        Assert.assertTrue(actual.contains("20000"));
    }

    /**
     * Tests the exception when instantiating Expenditure.
     */
    @Test(expected = ArgumentNullException.class)
    public void testSerializeExpenditureExceptionStatus() {
        new Expenditure("4590", null, null);
    }

    /**
     * Tests the exception when instantiating Expenditure.
     */
    @Test(expected = ArgumentNullException.class)
    public void testSerializeExpenditureExceptionUid() {
        new Expenditure(null, "12-09-2018", null);
    }

    /**
     * Tests the exception when instantiating Expenditure.
     */
    @Test(expected = ArgumentNullException.class)
    public void testSerializeExpenditureExceptionItems() {
        new Expenditure("4590", "12-09-2018", null);
    }
}