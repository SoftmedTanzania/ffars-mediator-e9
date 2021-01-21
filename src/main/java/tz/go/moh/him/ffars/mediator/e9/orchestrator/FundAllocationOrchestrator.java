package tz.go.moh.him.ffars.mediator.e9.orchestrator;

import com.google.gson.Gson;
import org.codehaus.plexus.util.StringUtils;
import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import tz.go.moh.him.ffars.mediator.e9.domain.FundAllocation;
import tz.go.moh.him.mediator.core.domain.ErrorMessage;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.validator.DateValidatorUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class FundAllocationOrchestrator extends BaseOrchestrator {
    protected JSONObject dailyDeathCountErrorMessageResource;

    public FundAllocationOrchestrator(MediatorConfig config) {
        super(config);
        dailyDeathCountErrorMessageResource = errorMessageResource.getJSONObject("FACILITY_FUND_ALLOCATION_ERROR_MESSAGES");
    }

    /**
     * Validate Fund Allocation Required Fields
     *
     * @param item to be validated
     * @return array list of validation results details incase of failed validations
     */
    public List<ResultDetail> validateRequiredFields(FundAllocation.Item item) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();
        if (StringUtils.isBlank(item.getFacilityCode()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, dailyDeathCountErrorMessageResource.getString("ERROR_DOB_IS_BLANK"), null));

        if (StringUtils.isBlank(item.getFacilityName()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(dailyDeathCountErrorMessageResource.getString("ERROR_MESSAGE_TYPE_IS_BLANK"), item.getFacilityName()), null));

        if (StringUtils.isBlank(item.getFacilityType()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(dailyDeathCountErrorMessageResource.getString("ERROR_DATE_DEATH_OCCURRED_IS_BLANK"), item.getFacilityType()), null));


        return resultDetailsList;
    }

    @Override
    protected FundAllocation convertMessageBodyToPojoList(String msg) {
        return new Gson().fromJson(msg, FundAllocation.class);
    }

    @Override
    protected boolean validateData(Object object) {
        FundAllocation fundAllocation = (FundAllocation) object;


        for (FundAllocation.Item item : fundAllocation.getItems()) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setSource(new Gson().toJson(fundAllocation));

            List<ResultDetail> resultDetailsList = new ArrayList<>();
            resultDetailsList.addAll(validateRequiredFields(item));

            try {
                if (!DateValidatorUtils.isValidPastDate(fundAllocation.getApplyDate(), "dd-mm-yyyy")) {
                    resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(dailyDeathCountErrorMessageResource.getString("ERROR_DATE_DEATH_OCCURRED_IS_NOT_A_VALID_PAST_DATE"), fundAllocation.getUid()), null));
                }
            } catch (ParseException e) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(dailyDeathCountErrorMessageResource.getString("ERROR_DATE_DEATH_OCCURRED_INVALID_FORMAT"), fundAllocation.getUid()), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)));
            }

            //TODO implement additional data validations checks
            if (resultDetailsList.size() != 0) {
                //Adding the validation results to the Error message object
                errorMessage.setResultsDetails(resultDetailsList);
                errorMessages.add(errorMessage);
            }
        }
        return errorMessages.size() == 0;
    }

}
