package com.company.iendo.bean;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/4 14:39
 * desc：病例详情
 */
public class CaseDetailBean {


    @SerializedName("data")
    private DataDTO data;
    @SerializedName("code")
    private int code;
    @SerializedName("msg")
    private String msg;

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataDTO {
        @SerializedName("check_date")
        private String check_date;
        @SerializedName("record_date")
        private String record_date;
        @SerializedName("update_time")
        private String update_time;
        @SerializedName("ID")
        private int ID;
        @SerializedName("RecordType")
        private String RecordType;
        @SerializedName("PatientID")
        private String PatientID;
        @SerializedName("Married")
        private String Married;
        @SerializedName("Name")
        private String Name;
        @SerializedName("Sex")
        private String Sex;
        @SerializedName("Tel")
        private String Tel;
        @SerializedName("Address")
        private String Address;
        @SerializedName("PatientNo")
        private String PatientNo;
        @SerializedName("CardID")
        private String CardID;
        @SerializedName("MedHistory")
        private String MedHistory;
        @SerializedName("FamilyHistory")
        private String FamilyHistory;
        @SerializedName("Race")
        private String Race;
        @SerializedName("Occupatior")
        private String Occupatior;
        @SerializedName("InsuranceID")
        private String InsuranceID;
        @SerializedName("NativePlace")
        private String NativePlace;
        @SerializedName("IsInHospital")
        private boolean IsInHospital;
        @SerializedName("LastCheckUserID")
        private int LastCheckUserID;
        @SerializedName("DOB")
        private String DOB;
        @SerializedName("PatientAge")
        private int PatientAge;
        @SerializedName("AgeUnit")
        private String AgeUnit;
        @SerializedName("CaseNo")
        private String CaseNo;
        @SerializedName("ReturnVisit")
        private boolean ReturnVisit;
        @SerializedName("BedID")
        private String BedID;
        @SerializedName("WardID")
        private String WardID;
        @SerializedName("CaseID")
        private String CaseID;
        @SerializedName("SubmitDoctor")
        private String SubmitDoctor;
        @SerializedName("Department")
        private String Department;
        @SerializedName("Device")
        private String Device;
        @SerializedName("Fee")
        private String Fee;
        @SerializedName("FeeType")
        private String FeeType;
        @SerializedName("ChiefComplaint")
        private String ChiefComplaint;
        @SerializedName("Test")
        private String Test;
        @SerializedName("Advice")
        private String Advice;
        @SerializedName("InpatientID")
        private String InpatientID;
        @SerializedName("OutpatientID")
        private String OutpatientID;
        @SerializedName("Others")
        private String Others;
        @SerializedName("Await1")
        private String Await1;
        @SerializedName("Await2")
        private String Await2;
        @SerializedName("Await3")
        private String Await3;
        @SerializedName("Await4")
        private String Await4;
        @SerializedName("Await5")
        private int Await5;
        @SerializedName("Biopsy")
        private String Biopsy;
        @SerializedName("Ctology")
        private String Ctology;
        @SerializedName("Pathology")
        private String Pathology;
        @SerializedName("CheckDate")
        private String CheckDate;
        @SerializedName("RecordDate")
        private String RecordDate;
        @SerializedName("Printed")
        private boolean Printed;
        @SerializedName("Upload")
        private boolean Upload;
        @SerializedName("Bespeak")
        private boolean Bespeak;
        @SerializedName("Images")
        private String Images;
        @SerializedName("ReportStyle")
        private String ReportStyle;
        @SerializedName("UserName")
        private String UserName;
        @SerializedName("StudyInstanceUID")
        private String StudyInstanceUID;
        @SerializedName("SeriesInstanceUID")
        private String SeriesInstanceUID;
        @SerializedName("ReportSeriesInstanceUID")
        private Object ReportSeriesInstanceUID;
        @SerializedName("ImageCount")
        private int ImageCount;
        @SerializedName("imagesCount")
        private int imagesCount;
        @SerializedName("videosCount")
        private int videosCount;
        @SerializedName("UpdateTime")
        private String UpdateTime;
        @SerializedName("EndoType")
        private int EndoType;
        @SerializedName("ExaminingPhysician")
        private String ExaminingPhysician;
        @SerializedName("ClinicalDiagnosis")
        private String ClinicalDiagnosis;
        @SerializedName("CheckContent")
        private String CheckContent;
        @SerializedName("CheckDiagnosis")
        private String CheckDiagnosis;

        public String getCheck_date() {
            return check_date;
        }

        public void setCheck_date(String check_date) {
            this.check_date = check_date;
        }

        public String getRecord_date() {
            return record_date;
        }

        public void setRecord_date(String record_date) {
            this.record_date = record_date;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getRecordType() {
            return RecordType;
        }

        public void setRecordType(String RecordType) {
            this.RecordType = RecordType;
        }

        public String getPatientID() {
            return PatientID;
        }

        public void setPatientID(String PatientID) {
            this.PatientID = PatientID;
        }

        public String getMarried() {
            return Married;
        }

        public void setMarried(String Married) {
            this.Married = Married;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getSex() {
            return Sex;
        }

        public void setSex(String Sex) {
            this.Sex = Sex;
        }

        public String getTel() {
            return Tel;
        }

        public void setTel(String Tel) {
            this.Tel = Tel;
        }

        public int getVideosCount() {
            return videosCount;
        }

        public void setVideosCount(int videosCount) {
            this.videosCount = videosCount;
        }

        public String getAddress() {
            return Address;
        }

        public void setAddress(String Address) {
            this.Address = Address;
        }

        public String getPatientNo() {
            return PatientNo;
        }

        public void setPatientNo(String PatientNo) {
            this.PatientNo = PatientNo;
        }

        public String getCardID() {
            return CardID;
        }

        public void setCardID(String CardID) {
            this.CardID = CardID;
        }

        public String getMedHistory() {
            return MedHistory;
        }

        public void setMedHistory(String MedHistory) {
            this.MedHistory = MedHistory;
        }

        public String getFamilyHistory() {
            return FamilyHistory;
        }

        public void setFamilyHistory(String FamilyHistory) {
            this.FamilyHistory = FamilyHistory;
        }

        public String getRace() {
            return Race;
        }

        public void setRace(String Race) {
            this.Race = Race;
        }

        public String getOccupatior() {
            return Occupatior;
        }

        public void setOccupatior(String Occupatior) {
            this.Occupatior = Occupatior;
        }

        public String getInsuranceID() {
            return InsuranceID;
        }

        public void setInsuranceID(String InsuranceID) {
            this.InsuranceID = InsuranceID;
        }

        public String getNativePlace() {
            return NativePlace;
        }

        public void setNativePlace(String NativePlace) {
            this.NativePlace = NativePlace;
        }

        public boolean isIsInHospital() {
            return IsInHospital;
        }

        public void setIsInHospital(boolean IsInHospital) {
            this.IsInHospital = IsInHospital;
        }

        public int getLastCheckUserID() {
            return LastCheckUserID;
        }

        public void setLastCheckUserID(int LastCheckUserID) {
            this.LastCheckUserID = LastCheckUserID;
        }

        public String getDOB() {
            return DOB;
        }

        public void setDOB(String DOB) {
            this.DOB = DOB;
        }

        public int getPatientAge() {
            return PatientAge;
        }

        public void setPatientAge(int PatientAge) {
            this.PatientAge = PatientAge;
        }

        public String getAgeUnit() {
            return AgeUnit;
        }

        public void setAgeUnit(String AgeUnit) {
            this.AgeUnit = AgeUnit;
        }

        public String getCaseNo() {
            return CaseNo;
        }

        public void setCaseNo(String CaseNo) {
            this.CaseNo = CaseNo;
        }

        public boolean isReturnVisit() {
            return ReturnVisit;
        }

        public void setReturnVisit(boolean ReturnVisit) {
            this.ReturnVisit = ReturnVisit;
        }

        public String getBedID() {
            return BedID;
        }

        public void setBedID(String BedID) {
            this.BedID = BedID;
        }

        public String getWardID() {
            return WardID;
        }

        public void setWardID(String WardID) {
            this.WardID = WardID;
        }

        public String getCaseID() {
            return CaseID;
        }

        public void setCaseID(String CaseID) {
            this.CaseID = CaseID;
        }

        public String getSubmitDoctor() {
            return SubmitDoctor;
        }

        public void setSubmitDoctor(String SubmitDoctor) {
            this.SubmitDoctor = SubmitDoctor;
        }

        public String getDepartment() {
            return Department;
        }

        public void setDepartment(String Department) {
            this.Department = Department;
        }

        public String getDevice() {
            return Device;
        }

        public void setDevice(String Device) {
            this.Device = Device;
        }

        public String getFee() {
            return Fee;
        }

        public void setFee(String Fee) {
            this.Fee = Fee;
        }

        public String getFeeType() {
            return FeeType;
        }

        public void setFeeType(String FeeType) {
            this.FeeType = FeeType;
        }

        public String getChiefComplaint() {
            return ChiefComplaint;
        }

        public void setChiefComplaint(String ChiefComplaint) {
            this.ChiefComplaint = ChiefComplaint;
        }

        public String getTest() {
            return Test;
        }

        public void setTest(String Test) {
            this.Test = Test;
        }

        public String getAdvice() {
            return Advice;
        }

        public void setAdvice(String Advice) {
            this.Advice = Advice;
        }

        public String getInpatientID() {
            return InpatientID;
        }

        public void setInpatientID(String InpatientID) {
            this.InpatientID = InpatientID;
        }

        public String getOutpatientID() {
            return OutpatientID;
        }

        public void setOutpatientID(String OutpatientID) {
            this.OutpatientID = OutpatientID;
        }

        public String getOthers() {
            return Others;
        }

        public void setOthers(String Others) {
            this.Others = Others;
        }

        public String getAwait1() {
            return Await1;
        }

        public void setAwait1(String Await1) {
            this.Await1 = Await1;
        }

        public String getAwait2() {
            return Await2;
        }

        public void setAwait2(String Await2) {
            this.Await2 = Await2;
        }

        public String getAwait3() {
            return Await3;
        }

        public void setAwait3(String Await3) {
            this.Await3 = Await3;
        }

        public String getAwait4() {
            return Await4;
        }

        public void setAwait4(String Await4) {
            this.Await4 = Await4;
        }

        public int getAwait5() {
            return Await5;
        }

        public void setAwait5(int Await5) {
            this.Await5 = Await5;
        }

        public String getBiopsy() {
            return Biopsy;
        }

        public void setBiopsy(String Biopsy) {
            this.Biopsy = Biopsy;
        }

        public String getCtology() {
            return Ctology;
        }

        public void setCtology(String Ctology) {
            this.Ctology = Ctology;
        }

        public String getPathology() {
            return Pathology;
        }

        public void setPathology(String Pathology) {
            this.Pathology = Pathology;
        }

        public String getCheckDate() {
            return CheckDate;
        }

        public void setCheckDate(String CheckDate) {
            this.CheckDate = CheckDate;
        }

        public String getRecordDate() {
            return RecordDate;
        }

        public void setRecordDate(String RecordDate) {
            this.RecordDate = RecordDate;
        }

        public boolean isPrinted() {
            return Printed;
        }

        public void setPrinted(boolean Printed) {
            this.Printed = Printed;
        }

        public boolean isUpload() {
            return Upload;
        }

        public void setUpload(boolean Upload) {
            this.Upload = Upload;
        }

        public boolean isBespeak() {
            return Bespeak;
        }

        public void setBespeak(boolean Bespeak) {
            this.Bespeak = Bespeak;
        }

        public String getImages() {
            return Images;
        }

        public void setImages(String Images) {
            this.Images = Images;
        }

        public String getReportStyle() {
            return ReportStyle;
        }

        public void setReportStyle(String ReportStyle) {
            this.ReportStyle = ReportStyle;
        }

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String UserName) {
            this.UserName = UserName;
        }

        public String getStudyInstanceUID() {
            return StudyInstanceUID;
        }

        public void setStudyInstanceUID(String StudyInstanceUID) {
            this.StudyInstanceUID = StudyInstanceUID;
        }

        public String getSeriesInstanceUID() {
            return SeriesInstanceUID;
        }

        public void setSeriesInstanceUID(String SeriesInstanceUID) {
            this.SeriesInstanceUID = SeriesInstanceUID;
        }

        public Object getReportSeriesInstanceUID() {
            return ReportSeriesInstanceUID;
        }

        public void setReportSeriesInstanceUID(Object ReportSeriesInstanceUID) {
            this.ReportSeriesInstanceUID = ReportSeriesInstanceUID;
        }

        public int getImageCount() {
            return ImageCount;
        }

        public void setImageCount(int ImageCount) {
            this.ImageCount = ImageCount;
        }

        public String getUpdateTime() {
            return UpdateTime;
        }

        public void setUpdateTime(String UpdateTime) {
            this.UpdateTime = UpdateTime;
        }

        public int getEndoType() {
            return EndoType;
        }

        public void setEndoType(int EndoType) {
            this.EndoType = EndoType;
        }

        public String getExaminingPhysician() {
            return ExaminingPhysician;
        }

        public void setExaminingPhysician(String ExaminingPhysician) {
            this.ExaminingPhysician = ExaminingPhysician;
        }

        public String getClinicalDiagnosis() {
            return ClinicalDiagnosis;
        }

        public void setClinicalDiagnosis(String ClinicalDiagnosis) {
            this.ClinicalDiagnosis = ClinicalDiagnosis;
        }

        public String getCheckContent() {
            return CheckContent;
        }

        public void setCheckContent(String CheckContent) {
            this.CheckContent = CheckContent;
        }

        public String getCheckDiagnosis() {
            return CheckDiagnosis;
        }

        public void setCheckDiagnosis(String CheckDiagnosis) {
            this.CheckDiagnosis = CheckDiagnosis;
        }

        public int getImagesCount() {
            return imagesCount;
        }

        public void setImagesCount(int imagesCount) {
            this.imagesCount = imagesCount;
        }

        @Override
        public String toString() {
            return "DataDTO{" +
                    "check_date='" + check_date + '\'' +
                    ", record_date='" + record_date + '\'' +
                    ", update_time='" + update_time + '\'' +
                    ", ID=" + ID +
                    ", RecordType='" + RecordType + '\'' +
                    ", PatientID='" + PatientID + '\'' +
                    ", Married='" + Married + '\'' +
                    ", Name='" + Name + '\'' +
                    ", Sex='" + Sex + '\'' +
                    ", Tel='" + Tel + '\'' +
                    ", Address='" + Address + '\'' +
                    ", PatientNo='" + PatientNo + '\'' +
                    ", CardID='" + CardID + '\'' +
                    ", MedHistory='" + MedHistory + '\'' +
                    ", FamilyHistory='" + FamilyHistory + '\'' +
                    ", Race='" + Race + '\'' +
                    ", Occupatior='" + Occupatior + '\'' +
                    ", InsuranceID='" + InsuranceID + '\'' +
                    ", NativePlace='" + NativePlace + '\'' +
                    ", IsInHospital=" + IsInHospital +
                    ", LastCheckUserID=" + LastCheckUserID +
                    ", DOB='" + DOB + '\'' +
                    ", PatientAge=" + PatientAge +
                    ", AgeUnit='" + AgeUnit + '\'' +
                    ", CaseNo='" + CaseNo + '\'' +
                    ", ReturnVisit=" + ReturnVisit +
                    ", BedID='" + BedID + '\'' +
                    ", WardID='" + WardID + '\'' +
                    ", CaseID='" + CaseID + '\'' +
                    ", SubmitDoctor='" + SubmitDoctor + '\'' +
                    ", Department='" + Department + '\'' +
                    ", Device='" + Device + '\'' +
                    ", Fee='" + Fee + '\'' +
                    ", FeeType='" + FeeType + '\'' +
                    ", ChiefComplaint='" + ChiefComplaint + '\'' +
                    ", Test='" + Test + '\'' +
                    ", Advice='" + Advice + '\'' +
                    ", InpatientID='" + InpatientID + '\'' +
                    ", OutpatientID='" + OutpatientID + '\'' +
                    ", Others='" + Others + '\'' +
                    ", Await1='" + Await1 + '\'' +
                    ", Await2='" + Await2 + '\'' +
                    ", Await3='" + Await3 + '\'' +
                    ", Await4='" + Await4 + '\'' +
                    ", Await5=" + Await5 +
                    ", Biopsy='" + Biopsy + '\'' +
                    ", Ctology='" + Ctology + '\'' +
                    ", Pathology='" + Pathology + '\'' +
                    ", CheckDate='" + CheckDate + '\'' +
                    ", RecordDate='" + RecordDate + '\'' +
                    ", Printed=" + Printed +
                    ", Upload=" + Upload +
                    ", Bespeak=" + Bespeak +
                    ", Images='" + Images + '\'' +
                    ", ReportStyle='" + ReportStyle + '\'' +
                    ", UserName='" + UserName + '\'' +
                    ", StudyInstanceUID='" + StudyInstanceUID + '\'' +
                    ", SeriesInstanceUID='" + SeriesInstanceUID + '\'' +
                    ", ReportSeriesInstanceUID=" + ReportSeriesInstanceUID +
                    ", ImageCount=" + ImageCount +
                    ", UpdateTime='" + UpdateTime + '\'' +
                    ", EndoType=" + EndoType +
                    ", ExaminingPhysician='" + ExaminingPhysician + '\'' +
                    ", ClinicalDiagnosis='" + ClinicalDiagnosis + '\'' +
                    ", CheckContent='" + CheckContent + '\'' +
                    ", CheckDiagnosis='" + CheckDiagnosis + '\'' +
                    '}';
        }
    }
}
