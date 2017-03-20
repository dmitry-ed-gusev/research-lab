package dg.social.crawler;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Config class for SCrawler.
 * Created by gusevdm on 2/28/2017.
 */

@Component
public class SCrawlerConfig {

    @Value("${crawler.search.string}")
    private String  searchString;
    @Value("${crawler.telescope.csv}")
    private String  telescopeCsv;
    @Value("${crawler.output.file}")
    private String  outputFile;
    @Value("${crawler.output.force}")
    private boolean outputForce;

    public SCrawlerConfig() {
    }

    public String getSearchString() {
        return searchString;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public boolean isOutputForce() {
        return outputForce;
    }

    public String getTelescopeCsv() {
        return telescopeCsv;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("searchString", searchString)
                .append("telescopeCsv", telescopeCsv)
                .append("outputFile", outputFile)
                .append("outputForce", outputForce)
                .toString();
    }

}
