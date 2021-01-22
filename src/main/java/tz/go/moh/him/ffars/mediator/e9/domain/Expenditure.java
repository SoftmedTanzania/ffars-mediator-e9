package tz.go.moh.him.ffars.mediator.e9.domain;

import com.google.gson.annotations.SerializedName;
import tz.go.moh.him.mediator.core.exceptions.ArgumentNullException;
import tz.go.moh.him.mediator.core.utils.StringUtils;

import java.util.List;

public class Expenditure {
    @SerializedName("UID")
    private String uid;

    @SerializedName("ApplyDate")
    private String applyDate;

    @SerializedName("Items")
    private List<Item> items;

    public Expenditure(String uid, String applyDate, List<Item> items) {
        if (StringUtils.isNullOrEmpty(uid)) {
            throw new ArgumentNullException("uuid - Value cannot be null");
        }

        if (StringUtils.isNullOrEmpty(applyDate)) {
            throw new ArgumentNullException("Apply Date - Value cannot be null");
        }

        if (items == null || items.isEmpty()) {
            throw new ArgumentNullException("Items - Value cannot be null");
        }

        this.uid = uid;
        this.applyDate = applyDate;
        this.items = items;
    }

    public String getUid() {
        return uid;
    }

    public String getApplyDate() {
        return applyDate;
    }

    public List<Item> getItems() {
        return items;
    }

    public static class Item {
        @SerializedName("OrderDate")
        private String orderDate;

        @SerializedName("InvoiceNumber")
        private String invoiceNumber;

        @SerializedName("FacilityCode")
        private String facilityCode;

        @SerializedName("FacilityName")
        private String facilityName;

        @SerializedName("FacilityType")
        private String facilityType;

        @SerializedName("Operational")
        private boolean operational;

        @SerializedName("TotalCost")
        private long totalCost;

        @SerializedName("InKInd")
        private long inKInd;

        @SerializedName("OtherSource")
        private long otherSource;

        @SerializedName("Description")
        private String description;

        @SerializedName("CurrentBalance")
        private long CurrentBalance;

        public Item(String orderDate, String invoiceNumber, String facilityCode, String facilityName, String facilityType, boolean operational, long totalCost, long inKInd, long otherSource, String description, long currentBalance) {
            if (StringUtils.isNullOrEmpty(orderDate)) {
                throw new ArgumentNullException("Order Date - Value cannot be null");
            }

            if (StringUtils.isNullOrEmpty(invoiceNumber)) {
                throw new ArgumentNullException("Invoice Number - Value cannot be null");
            }

            if (StringUtils.isNullOrEmpty(facilityCode)) {
                throw new ArgumentNullException("Facility Code - Value cannot be null");
            }

            if (StringUtils.isNullOrEmpty(facilityName)) {
                throw new ArgumentNullException("Facility Name - Value cannot be null");
            }

            if (StringUtils.isNullOrEmpty(facilityType)) {
                throw new ArgumentNullException("Facility Type - Value cannot be null");
            }

            this.orderDate = orderDate;
            this.invoiceNumber = invoiceNumber;
            this.facilityCode = facilityCode;
            this.facilityName = facilityName;
            this.facilityType = facilityType;
            this.operational = operational;
            this.totalCost = totalCost;
            this.inKInd = inKInd;
            this.otherSource = otherSource;
            this.description = description;
            CurrentBalance = currentBalance;
        }

        public String getOrderDate() {
            return orderDate;
        }

        public String getInvoiceNumber() {
            return invoiceNumber;
        }

        public String getFacilityCode() {
            return facilityCode;
        }

        public String getFacilityName() {
            return facilityName;
        }

        public String getFacilityType() {
            return facilityType;
        }

        public boolean isOperational() {
            return operational;
        }

        public long getTotalCost() {
            return totalCost;
        }

        public long getInKInd() {
            return inKInd;
        }

        public long getOtherSource() {
            return otherSource;
        }

        public String getDescription() {
            return description;
        }

        public long getCurrentBalance() {
            return CurrentBalance;
        }
    }
}
