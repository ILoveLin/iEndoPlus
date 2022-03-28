package com.company.iendo.green.db.downcase;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.List;

import org.greenrobot.greendao.annotation.Generated;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/12/27 8:45
 * desc：病例数据库表
 * 因为所有用户绑定的是设备
 * 病例绑定的是用户
 * <p>
 * 所有的病例都是绑定设备的,也就是说病例表的deviceCaseID==设备表的deviceCode
 */
@Entity
public class CaseDBBean  {

    //主键
    @Id(autoincrement = true)
    private Long id;
    //病例ID(这个是绑定相对于的设备id ,比如下载病例的是会把当前设备code赋值给他确保离线模式下能查找到,当前设备下绑定的用户)
    private String deviceCaseID;    //把设备的主键id赋值给deviceID(相当于他是主键ID)
    // 本地数据库设备id
    private String Did;
    // 职业
    private String Occupatior;
    //籍贯
    private String NativePlace;
    //收费
    private String Fee;
    //主诉
    private String ChiefComplaint;
    //图片路径集合--文件夹（设备ID-病例ID）
    @Convert(columnType = String.class, converter = CaseImageConverter.class)
    private List<CaseImageListBean> ImageList;
    @Convert(columnType = String.class, converter = CaseVideoConverter.class)
    //视频路径集合--文件夹（设备ID-病例ID）
    private List<CaseVideoListBean> VideoList;
    //活检
    private String Biopsy;
    //病理学
    private String Pathology;
    //收费类型
    private String FeeType;
    // 医疗病史
    private String MedHistory;
    // 最后一个来查房的医生
    private String LastCheckUserID;
    // 年龄单位
    private String AgeUnit;
    // 建议
    private String Advice;
    // 操作员用户名
    private String UserName;
    // 创建时间
    private String record_date;
    // 图片数量
    private String imagesCount;
    // 视频数量
    private String videosCount;
    // 申请医生
    private String SubmitDoctor;
    // 民族种族
    private String Race;
    // 病例类型
    private String RecordType;
    // 更新时间
    private String update_time;
    // 患者年龄
    private String PatientAge;
    // 身份证号
    private String CardID;
    // 电话
    private String Tel;
    // 检查时间
    private String check_date;
    // 病人编号
    private String PatientNo;
    // 住院号
    private String InpatientID;
    // 病床号
    private String BedID;
    // 检查内容（镜检所见）
    private String CheckContent;
    // 初复诊 (0-初诊 1-复诊)
    private String ReturnVisit;
    // 病例编号
    private String CaseNo;
    // 细胞学
    private String Ctology;
    // 生日
    private String DOB;
    // 检查医生
    private String ExaminingPhysician;
    // 镜检诊断
    private String CheckDiagnosis;
    // 性别
    private String Sex;
    // 工作站类型
    private String EndoType;
    // 设备
    private String Device;
    // 是否还在医院住院
    private String IsInHospital;
    // 婚否
    private String Married;
    // 家族病史
    private String FamilyHistory;
    // 试验
    private String Test;
    // 临床诊断
    private String ClinicalDiagnosis;
    // 科室
    private String Department;
    // 病区号
    private String WardID;
    // 病例号
    private String CaseID;
    // 姓名
    private String Name;
    // 住址
    private String Address;
    // 社保卡号
    private String InsuranceID;
    // 其他
    //  此处上位机返回来的病例ID  病例详情界面数据====DataDTO   里面的这个字段ID=1158,
    //  用来在下载的时候来判断当前病例是否下载过,如果设备码和ID  都一样,有数据返回则更新不然新增病例
    private String Others;   //设置是否下载过的标识 ==上位机返回的ID
    // 其他01
    private String Others01;

    @Generated(hash = 688807308)
    public CaseDBBean(Long id, String deviceCaseID, String Did, String Occupatior,
                      String NativePlace, String Fee, String ChiefComplaint,
                      List<CaseImageListBean> ImageList, List<CaseVideoListBean> VideoList,
                      String Biopsy, String Pathology, String FeeType, String MedHistory,
                      String LastCheckUserID, String AgeUnit, String Advice, String UserName,
                      String record_date, String imagesCount, String videosCount,
                      String SubmitDoctor, String Race, String RecordType, String update_time,
                      String PatientAge, String CardID, String Tel, String check_date,
                      String PatientNo, String InpatientID, String BedID, String CheckContent,
                      String ReturnVisit, String CaseNo, String Ctology, String DOB,
                      String ExaminingPhysician, String CheckDiagnosis, String Sex,
                      String EndoType, String Device, String IsInHospital, String Married,
                      String FamilyHistory, String Test, String ClinicalDiagnosis,
                      String Department, String WardID, String CaseID, String Name,
                      String Address, String InsuranceID, String Others, String Others01) {
        this.id = id;
        this.deviceCaseID = deviceCaseID;
        this.Did = Did;
        this.Occupatior = Occupatior;
        this.NativePlace = NativePlace;
        this.Fee = Fee;
        this.ChiefComplaint = ChiefComplaint;
        this.ImageList = ImageList;
        this.VideoList = VideoList;
        this.Biopsy = Biopsy;
        this.Pathology = Pathology;
        this.FeeType = FeeType;
        this.MedHistory = MedHistory;
        this.LastCheckUserID = LastCheckUserID;
        this.AgeUnit = AgeUnit;
        this.Advice = Advice;
        this.UserName = UserName;
        this.record_date = record_date;
        this.imagesCount = imagesCount;
        this.videosCount = videosCount;
        this.SubmitDoctor = SubmitDoctor;
        this.Race = Race;
        this.RecordType = RecordType;
        this.update_time = update_time;
        this.PatientAge = PatientAge;
        this.CardID = CardID;
        this.Tel = Tel;
        this.check_date = check_date;
        this.PatientNo = PatientNo;
        this.InpatientID = InpatientID;
        this.BedID = BedID;
        this.CheckContent = CheckContent;
        this.ReturnVisit = ReturnVisit;
        this.CaseNo = CaseNo;
        this.Ctology = Ctology;
        this.DOB = DOB;
        this.ExaminingPhysician = ExaminingPhysician;
        this.CheckDiagnosis = CheckDiagnosis;
        this.Sex = Sex;
        this.EndoType = EndoType;
        this.Device = Device;
        this.IsInHospital = IsInHospital;
        this.Married = Married;
        this.FamilyHistory = FamilyHistory;
        this.Test = Test;
        this.ClinicalDiagnosis = ClinicalDiagnosis;
        this.Department = Department;
        this.WardID = WardID;
        this.CaseID = CaseID;
        this.Name = Name;
        this.Address = Address;
        this.InsuranceID = InsuranceID;
        this.Others = Others;
        this.Others01 = Others01;
    }

    @Generated(hash = 2018851680)
    public CaseDBBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceCaseID() {
        return this.deviceCaseID;
    }

    public void setDeviceCaseID(String deviceCaseID) {
        this.deviceCaseID = deviceCaseID;
    }

    public String getDid() {
        return this.Did;
    }

    public void setDid(String Did) {
        this.Did = Did;
    }

    public String getOccupatior() {
        return this.Occupatior;
    }

    public void setOccupatior(String Occupatior) {
        this.Occupatior = Occupatior;
    }

    public String getNativePlace() {
        return this.NativePlace;
    }

    public void setNativePlace(String NativePlace) {
        this.NativePlace = NativePlace;
    }

    public String getFee() {
        return this.Fee;
    }

    public void setFee(String Fee) {
        this.Fee = Fee;
    }

    public String getChiefComplaint() {
        return this.ChiefComplaint;
    }

    public void setChiefComplaint(String ChiefComplaint) {
        this.ChiefComplaint = ChiefComplaint;
    }

    public List<CaseImageListBean> getImageList() {
        return this.ImageList;
    }

    public void setImageList(List<CaseImageListBean> ImageList) {
        this.ImageList = ImageList;
    }

    public List<CaseVideoListBean> getVideoList() {
        return this.VideoList;
    }

    public void setVideoList(List<CaseVideoListBean> VideoList) {
        this.VideoList = VideoList;
    }

    public String getBiopsy() {
        return this.Biopsy;
    }

    public void setBiopsy(String Biopsy) {
        this.Biopsy = Biopsy;
    }

    public String getPathology() {
        return this.Pathology;
    }

    public void setPathology(String Pathology) {
        this.Pathology = Pathology;
    }

    public String getFeeType() {
        return this.FeeType;
    }

    public void setFeeType(String FeeType) {
        this.FeeType = FeeType;
    }

    public String getMedHistory() {
        return this.MedHistory;
    }

    public void setMedHistory(String MedHistory) {
        this.MedHistory = MedHistory;
    }

    public String getLastCheckUserID() {
        return this.LastCheckUserID;
    }

    public void setLastCheckUserID(String LastCheckUserID) {
        this.LastCheckUserID = LastCheckUserID;
    }

    public String getAgeUnit() {
        return this.AgeUnit;
    }

    public void setAgeUnit(String AgeUnit) {
        this.AgeUnit = AgeUnit;
    }

    public String getAdvice() {
        return this.Advice;
    }

    public void setAdvice(String Advice) {
        this.Advice = Advice;
    }

    public String getUserName() {
        return this.UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getRecord_date() {
        return this.record_date;
    }

    public void setRecord_date(String record_date) {
        this.record_date = record_date;
    }

    public String getImagesCount() {
        return this.imagesCount;
    }

    public void setImagesCount(String imagesCount) {
        this.imagesCount = imagesCount;
    }

    public String getVideosCount() {
        return this.videosCount;
    }

    public void setVideosCount(String videosCount) {
        this.videosCount = videosCount;
    }

    public String getSubmitDoctor() {
        return this.SubmitDoctor;
    }

    public void setSubmitDoctor(String SubmitDoctor) {
        this.SubmitDoctor = SubmitDoctor;
    }

    public String getRace() {
        return this.Race;
    }

    public void setRace(String Race) {
        this.Race = Race;
    }

    public String getRecordType() {
        return this.RecordType;
    }

    public void setRecordType(String RecordType) {
        this.RecordType = RecordType;
    }

    public String getUpdate_time() {
        return this.update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getPatientAge() {
        return this.PatientAge;
    }

    public void setPatientAge(String PatientAge) {
        this.PatientAge = PatientAge;
    }

    public String getCardID() {
        return this.CardID;
    }

    public void setCardID(String CardID) {
        this.CardID = CardID;
    }

    public String getTel() {
        return this.Tel;
    }

    public void setTel(String Tel) {
        this.Tel = Tel;
    }

    public String getCheck_date() {
        return this.check_date;
    }

    public void setCheck_date(String check_date) {
        this.check_date = check_date;
    }

    public String getPatientNo() {
        return this.PatientNo;
    }

    public void setPatientNo(String PatientNo) {
        this.PatientNo = PatientNo;
    }

    public String getInpatientID() {
        return this.InpatientID;
    }

    public void setInpatientID(String InpatientID) {
        this.InpatientID = InpatientID;
    }

    public String getBedID() {
        return this.BedID;
    }

    public void setBedID(String BedID) {
        this.BedID = BedID;
    }

    public String getCheckContent() {
        return this.CheckContent;
    }

    public void setCheckContent(String CheckContent) {
        this.CheckContent = CheckContent;
    }

    public String getReturnVisit() {
        return this.ReturnVisit;
    }

    public void setReturnVisit(String ReturnVisit) {
        this.ReturnVisit = ReturnVisit;
    }

    public String getCaseNo() {
        return this.CaseNo;
    }

    public void setCaseNo(String CaseNo) {
        this.CaseNo = CaseNo;
    }

    public String getCtology() {
        return this.Ctology;
    }

    public void setCtology(String Ctology) {
        this.Ctology = Ctology;
    }

    public String getDOB() {
        return this.DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getExaminingPhysician() {
        return this.ExaminingPhysician;
    }

    public void setExaminingPhysician(String ExaminingPhysician) {
        this.ExaminingPhysician = ExaminingPhysician;
    }

    public String getCheckDiagnosis() {
        return this.CheckDiagnosis;
    }

    public void setCheckDiagnosis(String CheckDiagnosis) {
        this.CheckDiagnosis = CheckDiagnosis;
    }

    public String getSex() {
        return this.Sex;
    }

    public void setSex(String Sex) {
        this.Sex = Sex;
    }

    public String getEndoType() {
        return this.EndoType;
    }

    public void setEndoType(String EndoType) {
        this.EndoType = EndoType;
    }

    public String getDevice() {
        return this.Device;
    }

    public void setDevice(String Device) {
        this.Device = Device;
    }

    public String getIsInHospital() {
        return this.IsInHospital;
    }

    public void setIsInHospital(String IsInHospital) {
        this.IsInHospital = IsInHospital;
    }

    public String getMarried() {
        return this.Married;
    }

    public void setMarried(String Married) {
        this.Married = Married;
    }

    public String getFamilyHistory() {
        return this.FamilyHistory;
    }

    public void setFamilyHistory(String FamilyHistory) {
        this.FamilyHistory = FamilyHistory;
    }

    public String getTest() {
        return this.Test;
    }

    public void setTest(String Test) {
        this.Test = Test;
    }

    public String getClinicalDiagnosis() {
        return this.ClinicalDiagnosis;
    }

    public void setClinicalDiagnosis(String ClinicalDiagnosis) {
        this.ClinicalDiagnosis = ClinicalDiagnosis;
    }

    public String getDepartment() {
        return this.Department;
    }

    public void setDepartment(String Department) {
        this.Department = Department;
    }

    public String getWardID() {
        return this.WardID;
    }

    public void setWardID(String WardID) {
        this.WardID = WardID;
    }

    public String getCaseID() {
        return this.CaseID;
    }

    public void setCaseID(String CaseID) {
        this.CaseID = CaseID;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getAddress() {
        return this.Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public String getInsuranceID() {
        return this.InsuranceID;
    }

    public void setInsuranceID(String InsuranceID) {
        this.InsuranceID = InsuranceID;
    }

    public String getOthers() {
        return this.Others;
    }

    public void setOthers(String Others) {
        this.Others = Others;
    }

    public String getOthers01() {
        return this.Others01;
    }

    public void setOthers01(String Others01) {
        this.Others01 = Others01;
    }

    @Override
    public String toString() {
        return "CaseDBBean{" +
                "id=" + id +
                ", deviceCaseID='" + deviceCaseID + '\'' +
                ", Did='" + Did + '\'' +
                ", Occupatior='" + Occupatior + '\'' +
                ", NativePlace='" + NativePlace + '\'' +
                ", Fee='" + Fee + '\'' +
                ", ChiefComplaint='" + ChiefComplaint + '\'' +
                ", ImageList=" + ImageList +
                ", VideoList=" + VideoList +
                ", Biopsy='" + Biopsy + '\'' +
                ", Pathology='" + Pathology + '\'' +
                ", FeeType='" + FeeType + '\'' +
                ", MedHistory='" + MedHistory + '\'' +
                ", LastCheckUserID='" + LastCheckUserID + '\'' +
                ", AgeUnit='" + AgeUnit + '\'' +
                ", Advice='" + Advice + '\'' +
                ", UserName='" + UserName + '\'' +
                ", record_date='" + record_date + '\'' +
                ", imagesCount='" + imagesCount + '\'' +
                ", videosCount='" + videosCount + '\'' +
                ", SubmitDoctor='" + SubmitDoctor + '\'' +
                ", Race='" + Race + '\'' +
                ", RecordType='" + RecordType + '\'' +
                ", update_time='" + update_time + '\'' +
                ", PatientAge='" + PatientAge + '\'' +
                ", CardID='" + CardID + '\'' +
                ", Tel='" + Tel + '\'' +
                ", check_date='" + check_date + '\'' +
                ", PatientNo='" + PatientNo + '\'' +
                ", InpatientID='" + InpatientID + '\'' +
                ", BedID='" + BedID + '\'' +
                ", CheckContent='" + CheckContent + '\'' +
                ", ReturnVisit='" + ReturnVisit + '\'' +
                ", CaseNo='" + CaseNo + '\'' +
                ", Ctology='" + Ctology + '\'' +
                ", DOB='" + DOB + '\'' +
                ", ExaminingPhysician='" + ExaminingPhysician + '\'' +
                ", CheckDiagnosis='" + CheckDiagnosis + '\'' +
                ", Sex='" + Sex + '\'' +
                ", EndoType='" + EndoType + '\'' +
                ", Device='" + Device + '\'' +
                ", IsInHospital='" + IsInHospital + '\'' +
                ", Married='" + Married + '\'' +
                ", FamilyHistory='" + FamilyHistory + '\'' +
                ", Test='" + Test + '\'' +
                ", ClinicalDiagnosis='" + ClinicalDiagnosis + '\'' +
                ", Department='" + Department + '\'' +
                ", WardID='" + WardID + '\'' +
                ", CaseID='" + CaseID + '\'' +
                ", Name='" + Name + '\'' +
                ", Address='" + Address + '\'' +
                ", InsuranceID='" + InsuranceID + '\'' +
                ", Others='" + Others + '\'' +
                ", Others01='" + Others01 + '\'' +
                '}';
    }
}
