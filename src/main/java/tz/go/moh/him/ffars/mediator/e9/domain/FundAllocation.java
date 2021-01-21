package tz.go.moh.him.ffars.mediator.e9.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FundAllocation {
    @SerializedName("UID")
    private String uid;

    @SerializedName("ApplyDate")
    private String applyDate;

    @SerializedName("Items")
    private List<Item> items;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(String applyDate) {
        this.applyDate = applyDate;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
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

        public String getFacilityCode() {
            return facilityCode;
        }

        public void setFacilityCode(String facilityCode) {
            this.facilityCode = facilityCode;
        }

        public String getFacilityName() {
            return facilityName;
        }

        public void setFacilityName(String facilityName) {
            this.facilityName = facilityName;
        }

        public String getFacilityType() {
            return facilityType;
        }

        public void setFacilityType(String facilityType) {
            this.facilityType = facilityType;
        }

        public boolean isOperational() {
            return operational;
        }

        public void setOperational(boolean operational) {
            this.operational = operational;
        }

        public long getFacilityAllocation() {
            return facilityAllocation;
        }

        public void setFacilityAllocation(long facilityAllocation) {
            this.facilityAllocation = facilityAllocation;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public long getCurrentBalance() {
            return currentBalance;
        }

        public void setCurrentBalance(long currentBalance) {
            this.currentBalance = currentBalance;
        }
    }
}
