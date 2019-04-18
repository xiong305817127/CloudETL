package com.ys.idatrix.quality.analysis.dto;

import org.pentaho.di.core.util.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName NodeDictDataDto
 * @Description 字典数据实体类
 * @Author ouyang
 * @Date 2018/10/15 9:38
 * @Version 1.0
 */
@Table(catalog="idatrix.analysis.node.dictdata.tableName",name="tbl_nodeDictData")
public class NodeDictDataDto {

    //唯一标识
    @Id
    @GeneratedValue
    private Long id;

    //字典ID
    private String dictId;

    //标准值
    private String stdVal1;

    //参考值1
    private String simVal2;

    //参考值2
    private String simVal3;

    //参考值3
    private String simVal4;

    //参考值4
    private String simVal5;

    //参考值5
    private String simVal6;

    //参考值6
    private String simVal7;

    //参考值7
    private String simVal8;

    //参考值8
    private String simVal9;

    //参考值9
    private String simVal10;
    
    

    /**
	 * 
	 */
	public NodeDictDataDto() {
		super();
	}

	/**
	 * @param id
	 * @param dictId
	 * @param stdVal1
	 * @param simVal2
	 * @param simVal3
	 * @param simVal4
	 * @param simVal5
	 * @param simVal6
	 * @param simVal7
	 * @param simVal8
	 * @param simVal9
	 * @param simVal10
	 */
	public NodeDictDataDto(Long id, String dictId, String stdVal1, String simVal2, String simVal3, String simVal4,
			String simVal5, String simVal6, String simVal7, String simVal8, String simVal9, String simVal10) {
		super();
		this.id = id;
		this.dictId = dictId;
		this.stdVal1 = stdVal1;
		this.simVal2 = simVal2;
		this.simVal3 = simVal3;
		this.simVal4 = simVal4;
		this.simVal5 = simVal5;
		this.simVal6 = simVal6;
		this.simVal7 = simVal7;
		this.simVal8 = simVal8;
		this.simVal9 = simVal9;
		this.simVal10 = simVal10;
	}

	//设置参考值
    public void setValueArr(String valueArr) {
        if(valueArr != null){
            String[] values = valueArr.split(",");
            int i = 1;
            //循环设置参考值
            for (String value : values) {
                if(i==1){
                    simVal2 = value;
                } else if(i==2) {
                    simVal3 = value;
                } else if(i==3) {
                    simVal4 = value;
                } else if(i==4) {
                    simVal5 = value;
                } else if(i==5) {
                    simVal6 = value;
                } else if(i==6) {
                    simVal7 = value;
                } else if(i==7) {
                    simVal8 = value;
                } else if(i==8) {
                    simVal9 = value;
                } else if(i==9) {
                    simVal10 = value;
                }
                i++;
            }
        }
    }

    //获取参考值
    public String[] getReferences() {
        List<String> list = new ArrayList<>();
        if(!Utils.isEmpty(simVal2)) {
            list.add(simVal2);
        }
        if(!Utils.isEmpty(simVal3)) {
            list.add(simVal3);
        }
        if(!Utils.isEmpty(simVal4)) {
            list.add(simVal4);
        }
        if(!Utils.isEmpty(simVal5)) {
            list.add(simVal5);
        }
        if(!Utils.isEmpty(simVal6)) {
            list.add(simVal6);
        }
        if(!Utils.isEmpty(simVal7)) {
            list.add(simVal7);
        }
        if(!Utils.isEmpty(simVal8)) {
            list.add(simVal8);
        }
        if(!Utils.isEmpty(simVal9)) {
            list.add(simVal9);
        }
        if(!Utils.isEmpty(simVal10)) {
            list.add(simVal10);
        }
        return list.toArray( new String[] {});
    }

    //获取标准值和参考值
    public String[] getAllValue(){
        List<String> list = new ArrayList<>();
        if(!Utils.isEmpty(stdVal1)){
            list.add(stdVal1);
        }
        if(!Utils.isEmpty(simVal2)) {
            list.add(simVal2);
        }
        if(!Utils.isEmpty(simVal3)) {
            list.add(simVal3);
        }
        if(!Utils.isEmpty(simVal4)) {
            list.add(simVal4);
        }
        if(!Utils.isEmpty(simVal5)) {
            list.add(simVal5);
        }
        if(!Utils.isEmpty(simVal6)) {
            list.add(simVal6);
        }
        if(!Utils.isEmpty(simVal7)) {
            list.add(simVal7);
        }
        if(!Utils.isEmpty(simVal8)) {
            list.add(simVal8);
        }
        if(!Utils.isEmpty(simVal9)) {
            list.add(simVal9);
        }
        if(!Utils.isEmpty(simVal10)) {
            list.add(simVal10);
        }
        return list.toArray( new String[] {});
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDictId() {
        return dictId;
    }

    public void setDictId(String dictId) {
        this.dictId = dictId;
    }

    public String getStdVal1() {
        return stdVal1;
    }

    public void setStdVal1(String stdVal1) {
        this.stdVal1 = stdVal1;
    }

    public String getSimVal2() {
        return simVal2;
    }

    public void setSimVal2(String simVal2) {
        this.simVal2 = simVal2;
    }

    public String getSimVal3() {
        return simVal3;
    }

    public void setSimVal3(String simVal3) {
        this.simVal3 = simVal3;
    }

    public String getSimVal4() {
        return simVal4;
    }

    public void setSimVal4(String simVal4) {
        this.simVal4 = simVal4;
    }

    public String getSimVal5() {
        return simVal5;
    }

    public void setSimVal5(String simVal5) {
        this.simVal5 = simVal5;
    }

    public String getSimVal6() {
        return simVal6;
    }

    public void setSimVal6(String simVal6) {
        this.simVal6 = simVal6;
    }

    public String getSimVal7() {
        return simVal7;
    }

    public void setSimVal7(String simVal7) {
        this.simVal7 = simVal7;
    }

    public String getSimVal8() {
        return simVal8;
    }

    public void setSimVal8(String simVal8) {
        this.simVal8 = simVal8;
    }

    public String getSimVal9() {
        return simVal9;
    }

    public void setSimVal9(String simVal9) {
        this.simVal9 = simVal9;
    }

    public String getSimVal10() {
        return simVal10;
    }

    public void setSimVal10(String simVal10) {
        this.simVal10 = simVal10;
    }
}
