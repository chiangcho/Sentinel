package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * sentinel客户端加载的类型为ParamFlowRule，因此保存到nacos的规则属性需要ParamFlowRule一致
 * 同时需要记录id，app，ip，port等信息，因此定义该po类用于记录这些信息，nacos中使用该po结构
 */
public class ParamFlowRulePO extends ParamFlowRule {
    protected Long id;

    protected String app;
    protected String ip;
    protected Integer port;
    private Date gmtCreate;
    private Date gmtModified;

    public ParamFlowRulePO() {
    }

    public ParamFlowRulePO(ParamFlowRuleEntity rule) {
        this.setId(rule.getId());
        this.setApp(rule.getApp());
        this.setIp(rule.getIp());
        this.setPort(rule.getPort());
        this.setGmtCreate(rule.getGmtCreate());
        this.setGmtModified(rule.getGmtModified());
        BeanUtils.copyProperties(rule.getRule(),this);
    }

    public ParamFlowRuleEntity toEntity() {
        ParamFlowRuleEntity tmp = new ParamFlowRuleEntity();
        tmp.setId(this.getId());
        tmp.setApp(this.getApp());
        tmp.setIp(this.getIp());
        tmp.setPort(this.getPort());
        tmp.setGmtCreate(this.getGmtCreate());
        tmp.setGmtModified(this.getGmtModified());
        ParamFlowRule rule = new ParamFlowRule();
        BeanUtils.copyProperties(this, rule);
        tmp.setRule(rule);
        return tmp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

}
