package dg.social.crawler.domain;

import javax.validation.constraints.NotNull;
import dg.social.crawler.CommonDefaults.SocialNetwork;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * Country domain object.
 * Objects with different in any of fields, treated as different instances.
 * Created by gusevdm on 2/17/2017.
 */

// todo: unit tests!!!

@Entity
@Table(name = "COUNTRIES")
public class CountryDto extends AbstractEntity {

    @NotNull @Column(name = "EXTERNAL_ID", nullable = false)
    private long          externalId;
    @NotNull @Column(name = "COUNTRY_NAME", nullable = false)
    private String        countryName;
    @NotNull @Enumerated(EnumType.STRING) @Column(name = "NETWORK_TYPE", nullable = false)
    private SocialNetwork networkType;

    public CountryDto() {}

    public CountryDto(long id, long externalId, String countryName, SocialNetwork networkType) {
        super(id);

        if (StringUtils.isBlank(countryName) || networkType == null) { // fail-fast
            throw new IllegalArgumentException(
                    String.format("Invalid country name [%s] or social net type [%s]!",
                            countryName, networkType));
        }

        this.externalId  = externalId;
        this.countryName = countryName;
        this.networkType = networkType;
    }

    public long getExternalId() {
        return externalId;
    }

    public String getCountryName() {
        return countryName;
    }

    public SocialNetwork getNetworkType() {
        return networkType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CountryDto that = (CountryDto) o;

        if (externalId != that.externalId) return false;
        if (!countryName.equals(that.countryName)) return false;
        return networkType == that.networkType;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (externalId ^ (externalId >>> 32));
        result = 31 * result + countryName.hashCode();
        result = 31 * result + networkType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("externalId", externalId)
                .append("countryName", countryName)
                .append("networkType", networkType)
                .toString();
    }

}
