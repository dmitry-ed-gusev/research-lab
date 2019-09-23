package com.msd.bdp.ditoolcore.transformation;

import java.util.List;

public class RulePack {

    private List<Pack> rulePacks;

    public List<Pack> getRulePacks() {
        return rulePacks;
    }

    public void setRulePacks(List<Pack> rulePacks) {
        this.rulePacks = rulePacks;
    }


    public static class Pack {
        private String rulePackName;

        public List<Rule> rules;

        public String getRulePackName() {
            return rulePackName;
        }

        public void setRulePackName(String rulePackName) {
            this.rulePackName = rulePackName;
        }

        public List<Rule> getRules() {
            return rules;
        }

        public void setRules(List<Rule> rules) {
            this.rules = rules;
        }
    }

    public static class Rule {

        private String source;

        private String target;

        private String sourceTransformation;

        private String targetTransformation;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getSourceTransformation() {
            return sourceTransformation;
        }

        public void setSourceTransformation(String sourceTransformation) {
            this.sourceTransformation = sourceTransformation;
        }

        public String getTargetTransformation() {
            return targetTransformation;
        }

        public void setTargetTransformation(String targetTransformation) {
            this.targetTransformation = targetTransformation;
        }
    }
}




