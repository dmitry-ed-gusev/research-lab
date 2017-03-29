package dg.social.crawler.domain;

import dg.social.crawler.SCrawlerDefaults;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Entity;
import javax.persistence.Table;

import static dg.social.crawler.SCrawlerDefaults.SocialNetwork;

/**
 * Domain object - one education profile (faculty/university/etc.)
 * Created by gusevdm on 3/29/2017.
 */

@Entity
@Table (name = "EDUCATION_PROFILES")
public class EducationDto extends AbstractEntity {

    private String faculty;
    private String university;
    private String startYear;
    private String graduationYear;
    private String department;
    private String degree;
    private String universityUrl;

    /***/
    public EducationDto() {
    }

    /***/
    public EducationDto(long id, long externalId, SocialNetwork socialNetwork) {
        super(id, externalId, socialNetwork);
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public String getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(String graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getUniversityUrl() {
        return universityUrl;
    }

    public void setUniversityUrl(String universityUrl) {
        this.universityUrl = universityUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("faculty", faculty)
                .append("university", university)
                .append("startYear", startYear)
                .append("graduationYear", graduationYear)
                .append("department", department)
                .append("degree", degree)
                .append("universityUrl", universityUrl)
                .toString();
    }

}
