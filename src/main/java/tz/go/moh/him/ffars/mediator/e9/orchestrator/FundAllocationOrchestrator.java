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
    /**
     * Represents an Error Messages Definition Resource Object defined in <a href="file:../resources/error-messages.json">/resources/error-messages.json</a>.
     */
    protected JSONObject fundAllocationErrorMessageResource;

    public FundAllocationOrchestrator(MediatorConfig config) {
        super(config);
        fundAllocationErrorMessageResource = errorMessageResource.getJSONObject("FACILITY_FUND_ALLOCATION_ERROR_MESSAGES");
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
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, fundAllocationErrorMessageResource.getString("ERROR_FACILITY_CODE_IS_BLANK"), null));

        if (StringUtils.isBlank(item.getFacilityName()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(fundAllocationErrorMessageResource.getString("ERROR_FACILITY_NAME_IS_BLANK"), item.getFacilityCode()), null));

        if (StringUtils.isBlank(item.getFacilityType()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(fundAllocationErrorMessageResource.getString("ERROR_FACILITY_TYPE_IS_BLANK"), item.getFacilityCode()), null));


        return resultDetailsList;
    }

    @Override
    protected FundAllocation convertMessageBodyToPojoList(String msg) {
        return new Gson().fromJson(msg, FundAllocation.class);
    }

    @Override
    protected boolean validateData(Object object) {
        FundAllocation fundAllocation = (FundAllocation) object;

        validateFundAllocation(fundAllocation);

        for (FundAllocation.Item item : fundAllocation.getItems()) {
            ErrorMessage fundAllocationItemErrorMessage = new ErrorMessage();
            fundAllocationItemErrorMessage.setSource(new Gson().toJson(fundAllocation));

            List<ResultDetail> fundAllcoationItemResultDetailsList = new ArrayList<>();
            fundAllcoationItemResultDetailsList.addAll(validateRequiredFields(item));

            try {
                if (!DateValidatorUtils.isValidPastDate(fundAllocation.getApplyDate(), "dd-mm-yyyy")) {
                    fundAllcoationItemResultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(fundAllocationErrorMessageResource.getString("ERROR_DATE_DEATH_OCCURRED_IS_NOT_A_VALID_PAST_DATE"), fundAllocation.getUid()), null));
                }
            } catch (ParseException e) {
                fundAllcoationItemResultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(fundAllocationErrorMessageResource.getString("ERROR_DATE_DEATH_OCCURRED_INVALID_FORMAT"), fundAllocation.getUid()), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)));
            }

            //TODO implement additional data validations checks
            if (fundAllcoationItemResultDetailsList.size() != 0) {
                //Adding the validation results to the Error message object
                fundAllocationItemErrorMessage.setResultsDetails(fundAllcoationItemResultDetailsList);
                errorMessages.add(fundAllocationItemErrorMessage);
            }
        }
        return errorMessages.size() == 0;
    }


    /**
     * Validate Fund Allocation uuid and apply date
     *
     * @param fundAllocation to be validated
     */
    private void validateFundAllocation(FundAllocation fundAllocation) {
        ErrorMessage errorMessage = new ErrorMessage();
        List<ResultDetail> resultDetailsList = new ArrayList<>();

        errorMessage.setSource(new Gson().toJson(fundAllocation));

        if (StringUtils.isBlank(fundAllocation.getUid())) {
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, fundAllocationErrorMessageResource.getString("ERROR_UUID_IS_BLANK"), null));
        }

        if (StringUtils.isBlank(fundAllocation.getApplyDate())) {
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, fundAllocationErrorMessageResource.getString("ERROR_APPLY_DATE_IS_BLANK"), null));
        }


        if (resultDetailsList.size() != 0) {
            //Adding the validation results to the Error message object
            errorMessage.setResultsDetails(resultDetailsList);
            errorMessages.add(errorMessage);
        }
    }

}
