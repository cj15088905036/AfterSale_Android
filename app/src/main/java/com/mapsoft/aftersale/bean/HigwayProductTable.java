package com.mapsoft.aftersale.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/2/6.
 * 公路数据库保存信息表
 */

public class HigwayProductTable extends DataSupport {
    private int id;
    private String type_code;//订单类型+订单编号
    private String order_engineer;//维修人员名称
    private String userId;//维修人员id
    private String productType;//产品类型
    private String productName;//产品名称
    private String productId;//设备序列号
    private String productSim;//SIM卡号
    private String faultCause;//故障原因
    private String maintResult;//维修结果
    private String vehCode;//车牌号
    private String installAddress;//安装地址
    private String installTime;//安装时间
    private String installRemark;//安装备注
    private String product_state;//状态(保存/提交)
    private String sql_time;//录入本地数据库时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType_code() {
        return type_code;
    }

    public void setType_code(String type_code) {
        this.type_code = type_code;
    }

    public String getOrder_engineer() {
        return order_engineer;
    }

    public void setOrder_engineer(String order_engineer) {
        this.order_engineer = order_engineer;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductSim() {
        return productSim;
    }

    public void setProductSim(String productSim) {
        this.productSim = productSim;
    }

    public String getFaultCause() {
        return faultCause;
    }

    public void setFaultCause(String faultCause) {
        this.faultCause = faultCause;
    }

    public String getMaintResult() {
        return maintResult;
    }

    public void setMaintResult(String maintResult) {
        this.maintResult = maintResult;
    }

    public String getVehCode() {
        return vehCode;
    }

    public void setVehCode(String vehCode) {
        this.vehCode = vehCode;
    }

    public String getInstallAddress() {
        return installAddress;
    }

    public void setInstallAddress(String installAddress) {
        this.installAddress = installAddress;
    }

    public String getInstallTime() {
        return installTime;
    }

    public void setInstallTime(String installTime) {
        this.installTime = installTime;
    }

    public String getInstallRemark() {
        return installRemark;
    }

    public void setInstallRemark(String installRemark) {
        this.installRemark = installRemark;
    }

    public String getProduct_state() {
        return product_state;
    }

    public void setProduct_state(String product_state) {
        this.product_state = product_state;
    }

    public String getSql_time() {
        return sql_time;
    }

    public void setSql_time(String sql_time) {
        this.sql_time = sql_time;
    }
}
