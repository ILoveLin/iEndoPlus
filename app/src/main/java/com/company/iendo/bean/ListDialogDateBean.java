package com.company.iendo.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/12/29 15:34
 * desc：dialog字典数据----
 * 1,按'DictName'进行分组
 * 2,按照分组中找出 'ParentId=0' 的项,作为每个分组的类别
 *
 *
 *
 * 每个数字对应--相对于的Dialog数据
 *
 * //字典项的顺序
 * #define DICT_NAME_ID_NO                         -1   //没有字典项
 * #define DICT_NAME_ID_NAME                        1   //姓名
 * #define DICT_NAME_ID_AGE                         2   //年龄
 * #define DICT_NAME_ID_INPATIENTID                 3   //住院号
 * #define DICT_NAME_ID_BEDID                       4   //病床号
 * #define DICT_NAME_ID_OCCUPATIOR                  5   //职业
 * #define DICT_NAME_ID_FEETYPE                     6   //收费类型
 * #define DICT_NAME_ID_WARDID                      7   //病区号
 * #define DICT_NAME_ID_EXAMNINGPHYSICIAN           8   //送检医生
 * #define DICT_NAME_ID_DEPARTMENT                  9   //科室
 * #define DICT_NAME_ID_DEVICE                      10  //设备
 * #define DICT_NAME_ID_CHIEFCOMPLAINT              11  //主诉
 * #define DICT_NAME_ID_CLINICALDIAGNOSIS           12  //临床诊断
 * #define DICT_NAME_ID_CHECKCONTENT                13  //镜检所见
 * #define DICT_NAME_ID_CHECKDIAGNOSIS              14  //镜检诊断
 * #define DICT_NAME_ID_BIOPSY                      15  //活检
 * #define DICT_NAME_ID_CTOLOGY                     16  //细胞学
 * #define DICT_NAME_ID_TEST                        17  //试验
 * #define DICT_NAME_ID_PATHOLOGY                   18  //病理学
 * #define DICT_NAME_ID_ADVICE                      19  //建议
 * #define DICT_NAME_ID_EXAMDOC                     20  //检查医生
 * #define DICT_NAME_ID_PATIENTNO                   21  //病历号
 * #define DICT_NAME_ID_INSUREANCEID                22  //医保号
 * #define DICT_NAME_ID_RACE                        23  //民族
 * #define DICT_NAME_ID_NATIVEPLACE                 24  //籍贯
 * #define DICT_NAME_ID_OUTPATIENTID                25  //门诊号
 * #define DICT_NAME_ID_TEL                         26  //电话
 * #define DICT_NAME_ID_ADDRESS                     27  //住址
 * #define DICT_NAME_ID_MEDHISTORY                  28  //病史
 * #define DICT_NAME_ID_FAMILYHISTORY               29  //家族史
 * #define DICT_NAME_ID_ASSISTANT                   30  //助手
 * #define DICT_NAME_ID_INSTRUMENTPHYSICAN          31  //器械师
 * #define DICT_NAME_ID_SURGEON                     32  //手术医生
 * #define DICT_NAME_ID_SCRUBNURSE                  33  //洗手护士
 * #define DICT_NAME_ID_PEROPERATIVEIAGNOSIS        34  //术前诊断
 * #define DICT_NAME_ID_OPERATIONNAME               35  //手术名称
 * #define DICT_NAME_ID_OPERATIONTIME               36  //手术时间
 * #define DICT_NAME_ID_ANESTHETICTYPE              37  //麻醉方法
 * #define DICT_NAME_ID_ANESTHETIST                 38  //麻醉师
 * #define DICT_NAME_ID_AURGEONDESCRIPTION          39  //手术过程
 * #define DICT_NAME_ID_OPERATEDDIAGNOSIS           40  //术后诊断
 * #define DICT_NAME_ID_INTERN                      41  //实习医生
 * #define DICT_NAME_ID_OTHERS                      42  //其他
 * #define DICT_NAME_ID_AWAIT1                      43  //待定1
 * #define DICT_NAME_ID_AWAIT2                      44  //待定2
 * #define DICT_NAME_ID_AWAIT3                      45  //待定3
 * #define DICT_NAME_ID_SEX                         100  //性别
 * #define DICT_NAME_ID_MARRIED                     101  //婚否
 * #define DICT_NAME_ID_PARTS                       200  //部位
 * #define DICT_NAME_ID_LESION                      201  //病变
 *
 *
 *
 */
public class ListDialogDateBean {

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
        @SerializedName("listDicts")
        private List<ListDictsDTO> listDicts;

        public List<ListDictsDTO> getListDicts() {
            return listDicts;
        }

        public void setListDicts(List<ListDictsDTO> listDicts) {
            this.listDicts = listDicts;
        }

        public static class ListDictsDTO {
            @SerializedName("ID")
            private String ID;
            @SerializedName("ParentId")
            private String ParentId;
            @SerializedName("DictName")
            private String DictName;
            @SerializedName("DictItem")
            private String DictItem;
            @SerializedName("EndoType")
            private int EndoType;

            public String getID() {
                return ID;
            }

            public void setID(String ID) {
                this.ID = ID;
            }

            public String getParentId() {
                return ParentId;
            }

            public void setParentId(String ParentId) {
                this.ParentId = ParentId;
            }

            public String getDictName() {
                return DictName;
            }

            public void setDictName(String DictName) {
                this.DictName = DictName;
            }

            public String getDictItem() {
                return DictItem;
            }

            public void setDictItem(String DictItem) {
                this.DictItem = DictItem;
            }

            public int getEndoType() {
                return EndoType;
            }

            public void setEndoType(int EndoType) {
                this.EndoType = EndoType;
            }

            @Override
            public String toString() {
                return "ListDictsDTO{" +
                        "ID='" + ID + '\'' +
                        ", ParentId='" + ParentId + '\'' +
                        ", DictName='" + DictName + '\'' +
                        ", DictItem='" + DictItem + '\'' +
                        ", EndoType=" + EndoType +
                        '}';
            }
        }
    }

    @Override
    public String toString() {
        return "ListDialogDateBean{" +
                "data=" + data +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
