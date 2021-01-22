package tz.go.moh.him.ffars.mediator.e9.domain;

import com.google.gson.annotations.SerializedName;
import tz.go.moh.him.mediator.core.exceptions.ArgumentNullException;
import tz.go.moh.him.mediator.core.utils.StringUtils;

import java.util.List;

public class FundAllocation {
    @SerializedName("UID")
    private String uid;

    @SerializedName("ApplyDate")
    private String applyDate;

    @SerializedName("Items")
    private List<Item> items;

    public FundAllocation(String uid, String applyDate, List<Item> items) {
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
        @SerializedName("FacilityCode")
        private String facilityCode;

        @SerializedName("FacilityName")
        private String facilityName;

        @SerializedName("FacilityType")
        private String facilityType;

        @SerializedName("Operational")
        private boolean operational;

        @SerializedName("FacilityAllocation")
        private long facilityAllocation;

        @SerializedName("Description")
        private String description;

        @SerializedName("CurrentBalance")
        private long currentBalance;

        public Item(String facilityCode, String facilityName, String facilityType, boolean operational, long facilityAllocation, String description, long currentBalance) {
            if (StringUtils.isNullOrEmpty(facilityCode)) {
                throw new ArgumentNullException("Facility Code - Value cannot be null");
            }

            if (StringUtils.isNullOrEmpty(facilityName)) {
                throw new ArgumentNullException("Facility Name - Value cannot be null");
            }

            if (StringUtils.isNullOrEmpty(facilityType)) {
                throw new ArgumentNullException("Facility Type - Value cannot be null");
            }

            this.facilityCode = facilityCode;
            this.facilityName = facilityName;
            this.facilityType = facilityType;
            this.operational = operational;
            this.facilityAllocation = facilityAllocation;
            this.description = description;
            this.currentBalance = currentBalance;
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

        public long getFacilityAllocation() {
            return facilityAllocation;
        }

        public String getDescription() {
            return description;
        }

        public long getCurrentBalance() {
            return currentBalance;
        }
    }
}
