package tz.go.moh.him.ffars.mediator.e9.orchestrator;

import com.google.gson.Gson;
import org.codehaus.plexus.util.StringUtils;
import org.json.JSONObject;
import org.openhim.mediator.engine.MediatorConfig;
import tz.go.moh.him.ffars.mediator.e9.domain.Expenditure;
import tz.go.moh.him.mediator.core.domain.ErrorMessage;
import tz.go.moh.him.mediator.core.domain.ResultDetail;
import tz.go.moh.him.mediator.core.validator.DateValidatorUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ExpenditureOrchestrator extends BaseOrchestrator {
    /**
     * Represents an Error Messages Definition Resource Object defined in <a href="file:../resources/error-messages.json">/resources/error-messages.json</a>.
     */
    protected JSONObject expenditureErrorMessageResource;

    public ExpenditureOrchestrator(MediatorConfig config) {
        super(config);
        expenditureErrorMessageResource = errorMessageResource.getJSONObject("EXPENDITURE_ERROR_MESSAGES");
    }

    /**
     * Validate Expenditure Item Required Fields
     *
     * @param item to be validated
     * @return array list of validation results details incase of failed validations
     */
    public List<ResultDetail> validateRequiredFields(Expenditure.Item item) {
        List<ResultDetail> resultDetailsList = new ArrayList<>();
        if (StringUtils.isBlank(item.getInvoiceNumber()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, expenditureErrorMessageResource.getString("ERROR_INVOICE_NUMBER_IS_BLANK"), null));

        if (StringUtils.isBlank(item.getOrderDate()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(expenditureErrorMessageResource.getString("ERROR_ORDER_DATE_IS_BLANK"), item.getInvoiceNumber()), null));

        if (StringUtils.isBlank(item.getFacilityName()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(expenditureErrorMessageResource.getString("ERROR_FACILITY_NAME_IS_BLANK"), item.getFacilityName()), null));

        if (StringUtils.isBlank(item.getFacilityCode()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(expenditureErrorMessageResource.getString("ERROR_FACILITY_CODE_IS_BLANK"), item.getInvoiceNumber()), null));

        if (StringUtils.isBlank(item.getFacilityType()))
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(expenditureErrorMessageResource.getString("ERROR_FACILITY_TYPE_IS_BLANK"), item.getInvoiceNumber()), null));


        return resultDetailsList;
    }

    @Override
    protected Expenditure convertMessageBodyToPojoList(String msg) {
        return new Gson().fromJson(msg, Expenditure.class);
    }

    @Override
    protected boolean validateData(Object object) {
        Expenditure expenditure = (Expenditure) object;

        validateExpenditure(expenditure);

        for (Expenditure.Item item : expenditure.getItems()) {
            ErrorMessage itemErrorMessage = new ErrorMessage();
            List<ResultDetail> itemResultDetailsList = new ArrayList<>();

            itemErrorMessage.setSource(new Gson().toJson(item));

            itemResultDetailsList.addAll(validateRequiredFields(item));

            try {
                if (!DateValidatorUtils.isValidPastDate(item.getOrderDate(), "dd-mm-yyyy")) {
                    itemResultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(expenditureErrorMessageResource.getString("ERROR_ORDER_DATE_IS_NOT_A_VALID_PAST_DATE"), expenditure.getUid()), null));
                }
            } catch (ParseException e) {
                itemResultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, expenditureErrorMessageResource.getString("ERROR_ORDER_DATE_INVALID_FORMAT"), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)));
            }


            //TODO implement additional data validations checks
            if (itemResultDetailsList.size() != 0) {
                //Adding the validation results to the Error message object
                itemErrorMessage.setResultsDetails(itemResultDetailsList);
                errorMessages.add(itemErrorMessage);
            }
        }
        return errorMessages.size() == 0;
    }

    /**
     * Validate Expenditure uuid and apply date
     *
     * @param expenditure to be validated
     */
    private void validateExpenditure(Expenditure expenditure) {
        ErrorMessage errorMessage = new ErrorMessage();
        List<ResultDetail> resultDetailsList = new ArrayList<>();

        errorMessage.setSource(new Gson().toJson(expenditure));

        if (StringUtils.isBlank(expenditure.getApplyDate())) {
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, expenditureErrorMessageResource.getString("ERROR_APPLY_DATE_IS_BLANK"), null));
        }

        if (StringUtils.isBlank(expenditure.getUid())) {
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, expenditureErrorMessageResource.getString("ERROR_UUID_IS_BLANK"), null));
        }

        try {
            if (!DateValidatorUtils.isValidPastDate(expenditure.getApplyDate(), "dd-mm-yyyy")) {
                resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, String.format(expenditureErrorMessageResource.getString("ERROR_APPLY_DATE_IS_NOT_A_VALID_PAST_DATE"), expenditure.getUid()), null));
            }
        } catch (ParseException e) {
            resultDetailsList.add(new ResultDetail(ResultDetail.ResultsDetailsType.ERROR, expenditureErrorMessageResource.getString("ERROR_APPLY_DATE_INVALID_FORMAT"), tz.go.moh.him.mediator.core.utils.StringUtils.writeStackTraceToString(e)));
        }

        if (resultDetailsList.size() != 0) {
            //Adding the validation results to the Error message object
            errorMessage.setResultsDetails(resultDetailsList);
            errorMessages.add(errorMessage);
        }
    }


}
