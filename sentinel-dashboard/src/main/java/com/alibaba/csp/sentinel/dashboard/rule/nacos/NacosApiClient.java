package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import com.alibaba.csp.sentinel.dashboard.datasource.RuleConfigTypeEnum;
import com.alibaba.csp.sentinel.dashboard.datasource.ds.nacos.NacosProperties;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.AbstractpersistentRuleApiClient;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Jiajiangnan
 * @E-mail jiajiangnan.office@foxmail.com
 * @Date 2020/8/31
 * @Version 1.0
 */
public class NacosApiClient<T> extends AbstractpersistentRuleApiClient<T> {

    @Autowired
    private NacosProperties nacosProperties;
    @Autowired
    private ConfigService configService;

    private String getRuleConfigId(String appName, RuleConfigTypeEnum ruleFix) {
        appName = StringUtils.isBlank(appName) ? "Sentinel" : appName;
        return String.format("%s-%s", appName, ruleFix.getValue());
    }

    @Override
    public List<T> fetch(String app, RuleConfigTypeEnum configType) throws Exception {
        String ruleName = this.getRuleConfigId(app, configType);
        String rulesJson = configService.getConfig(ruleName, nacosProperties.getGroupId(), 3000);
        if (StringUtil.isEmpty(rulesJson)) {
            return (List<T>) new ArrayList();
        }
        List list = JSON.parseArray(rulesJson, configType.getClazz());

        //如果是param-flow,则处理方式与其他类型不同，需要将ParamFlowRulePO转换为Entity
        if(configType.equals(RuleConfigTypeEnum.PARAM_FLOW)) {
            List<ParamFlowRuleEntity> paramFlowRuleEntitys = new ArrayList<>();
            for(Object obj :list) {
                ParamFlowRulePO po = (ParamFlowRulePO) obj;
                paramFlowRuleEntitys.add(po.toEntity());
            }
            list = paramFlowRuleEntitys;
        }
        return list;
    }

    @Override
    public void publish(String app, RuleConfigTypeEnum configType, List<T> rules) throws Exception {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }
        String ruleName = this.getRuleConfigId(app, configType);
        String groupId = nacosProperties.getGroupId();
        //如果是param-flow,则处理方式与其他类型不同，需要将Entity转换为ParamFlowRulePO
        if(configType.equals(RuleConfigTypeEnum.PARAM_FLOW)) {
            List<ParamFlowRule> paramFlowRules = new ArrayList<>();
            for (T rule :rules) {
                ParamFlowRulePO po = new ParamFlowRulePO((ParamFlowRuleEntity) rule);
                paramFlowRules.add(po);
            }
            rules = (List<T>) paramFlowRules;
        }
        String rulesJson = JSON.toJSONString(rules,true);
        configService.publishConfig(ruleName, groupId, rulesJson);
    }

}
