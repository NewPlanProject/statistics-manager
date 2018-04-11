package org.citic.iiot.diagnosis.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.citic.iiot.app.core.util.CodeUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yxw on 2017/7/17.
 */
@Data
public class MaintainByIdOutVO  implements Serializable {

    private static final long serialVersionUID = -1L;
    private String title;
    private String type;
    private String deviceid;
    private String devicename;
    private String detail;
    private String faultid;
    private String faultname;
    private String userid;       //修改人
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;    //修改时间
    private Integer numbers = 500;

    public Integer getNumbers() {
        if(CodeUtil.isNotNullEmpty(detail)){
            numbers = 500 - (detail.length() > 500 ? 500 : detail.length());
        }
        return numbers;
    }
}
