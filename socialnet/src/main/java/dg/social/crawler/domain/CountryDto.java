package dg.social.crawler.domain;

import dg.social.crawler.CommonDefaults.SocialNetwork;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Country domain object.
 * Objects with different in any of fields, treated as different instances.
 * Created by gusevdm on 2/17/2017.
 */

// todo: unit tests!!!

@Entity
@Table(name = "COUNTRIES")
public class CountryDto extends AbstractEntity {

    @NotNull @Column(name = "COUNTRY_NAME", nullable = false)
    private String        countryName;

    public CountryDto() {}

    public CountryDto(long id, long externalId, String countryName, SocialNetwork socialNetwork) {
        super(id, externalId, socialNetwork);

        if (StringUtils.isBlank(countryName)) { // fail-fast
            throw new IllegalArgumentException("Can't proceed with empty country name!");
        }

        this.countryName = countryName;
    }

    public String getCountryName() {
        return countryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CountryDto that = (CountryDto) o;

        return countryName.equals(that.countryName);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + countryName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("countryName", countryName)
                .toString();
    }

}
