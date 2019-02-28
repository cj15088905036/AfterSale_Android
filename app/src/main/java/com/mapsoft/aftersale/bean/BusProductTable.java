package com.mapsoft.aftersale.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/1/12.
 * 公交数据库保存信息表
 */

public class BusProductTable extends DataSupport {
    private int id;
    private String repair_code;//维修人员ID
    private String repair_name;//维修人员名称
    private String type_code;//订单类型+订单编号
    private String product_code;//产品序列号
    private String product_name;//产品名称
    private String veh_code;//车牌号
    private String fault_cause;//故障原因
    private String maint_result;//维修结果
    private String material_cost;//材料费
    private String maintenance_cost;//维修费
    private String input_time;//录入数据库时间
    private String product_state;//状态(保存/提交)
    private String fault_remark;//故障产品信息备注

    public String getFault_remark() {
        return fault_remark;
    }

    public void setFault_remark(String fault_remark) {
        this.fault_remark = fault_remark;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRepair_code() {
        return repair_code;
    }

    public void setRepair_code(String repair_code) {
        this.repair_code = repair_code;
    }

    public String getRepair_name() {
        return repair_name;
    }

    public void setRepair_name(String repair_name) {
        this.repair_name = repair_name;
    }

    public String getType_code() {
        return type_code;
    }

    public void setType_code(String type_code) {
        this.type_code = type_code;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getVeh_code() {
        return veh_code;
    }

    public void setVeh_code(String veh_code) {
        this.veh_code = veh_code;
    }

    public String getFault_cause() {
        return fault_cause;
    }

    public void setFault_cause(String fault_cause) {
        this.fault_cause = fault_cause;
    }

    public String getMaint_result() {
        return maint_result;
    }

    public void setMaint_result(String maint_result) {
        this.maint_result = maint_result;
    }

    public String getMaterial_cost() {
        return material_cost;
    }

    public void setMaterial_cost(String material_cost) {
        this.material_cost = material_cost;
    }

    public String getMaintenance_cost() {
        return maintenance_cost;
    }

    public void setMaintenance_cost(String maintenance_cost) {
        this.maintenance_cost = maintenance_cost;
    }

    public String getInput_time() {
        return input_time;
    }

    public void setInput_time(String input_time) {
        this.input_time = input_time;
    }

    public String getProduct_state() {
        return product_state;
    }

    public void setProduct_state(String product_state) {
        this.product_state = product_state;
    }

}
