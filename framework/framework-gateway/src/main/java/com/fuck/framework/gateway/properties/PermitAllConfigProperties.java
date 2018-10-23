package com.fuck.framework.gateway.properties;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * DESCRIPTION:
 * security放行url pattern
 * @author zouyan
 * @create 2017-12-21 11:41
 * Created by fuck~ on 2017/12/21.
 */
@Component("permitAllConfigProperties")
@ConfigurationProperties(prefix="custom.permitall")
public class PermitAllConfigProperties {

    private List<String> patterns;

    private String[] antPatterns = new String[]{};

    public List<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    public String[] getAntPatterns() {
        return antPatterns;
    }

    private static List<Pattern> permitallUrlPattern = new ArrayList<Pattern>();

    @PostConstruct
    public void init() {
        if (CollectionUtils.isNotEmpty(patterns)) {
            antPatterns = new String[patterns.size()];
            antPatterns = patterns.toArray(antPatterns);
        }
        for (int i = 0; i < patterns.size(); i++) {
            String currentUrl = patterns.get(i).replaceAll("\\*\\*", "(.*?)");
            Pattern currentPattern = Pattern.compile(currentUrl, Pattern.CASE_INSENSITIVE);
            permitallUrlPattern.add(currentPattern);
        }
    }

    public boolean isPermitAllUrl(String url) {
        for (Pattern pattern : permitallUrlPattern) {
            if (pattern.matcher(url).find()) {
                return true;
            }
        }
        return false;
    }

}
