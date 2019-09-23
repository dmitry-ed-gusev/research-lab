package com.msd.bdp.ditoolcore.transformation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.msd.bdp.DiToolException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetTransRulePack {

    private static final YAMLFactory YAML_FACTORY = new YAMLFactory();
    private static String YML_CONFIG = "rulePack.yml";

    public List<RulePack.Rule> getTransformationRules(RuleEnum e) throws IOException {

        ObjectMapper mapper = new ObjectMapper(YAML_FACTORY);
        RulePack pack = mapper.readValue(new File(YML_CONFIG), RulePack.class);

        for (RulePack.Pack rulePack : pack.getRulePacks()) {
            if (rulePack.getRulePackName().equals(e.getText())) {
                return rulePack.getRules();
            }

        }

        throw new DiToolException(String.format("There is no rule pack for $s", e.getText()));
    }

    //TODO should be deleted
    public void createConfig() throws IOException {


        RulePack.Rule rule = new RulePack.Rule();
        rule.setSource("sourceBLA");
        rule.setTarget("targetBla");
        rule.setSourceTransformation("BLA");
        rule.setTargetTransformation("da");

        RulePack.Pack pack = new RulePack.Pack();
        pack.setRulePackName("vav");
        pack.setRules(Collections.singletonList(rule));

        RulePack r = new RulePack();
        List<RulePack.Pack> packs = new ArrayList<>();
        packs.add(pack);
        packs.add(pack);
        r.setRulePacks(packs);

        File ymlFile = new File(YML_CONFIG);
        String yamlConfig = makeConfig(r);
        FileUtils.write(ymlFile, yamlConfig, Charset.defaultCharset());

    }

    private String makeConfig(RulePack t) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper(YAML_FACTORY);
        return mapper.writeValueAsString(t);

    }

}
