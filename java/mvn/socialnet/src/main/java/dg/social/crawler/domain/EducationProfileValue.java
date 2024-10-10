package dg.social.crawler.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Domain VALUE object - one education profile (faculty/university/etc.).
 * Completely depends on (its lifecycle is managed by) PersonDto ENTITY object.
 * Created by gusevdm on 3/29/2017.
 */

@Embeddable
public class EducationProfileValue {

    @Column (name = "FACULTY")
    private String faculty;
    @Column (name = "UNIVERSITY")
    private String university;
    @Column (name = "START_YEAR")
    private String startYear;
    @Column (name = "GRADUATION_YEAR")
    private String graduationYear;
    @Column (name = "DEPARTMENT")
    private String department;
    @Column (name = "DEGREE")
    private String degree;
    @Column (name = "UNIVERSITY_URL")
    private String universityUrl;

    /***/
    public EducationProfileValue() {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EducationProfileValue that = (EducationProfileValue) o;

        if (faculty != null ? !faculty.equals(that.faculty) : that.faculty != null) return false;
        if (university != null ? !university.equals(that.university) : that.university != null) return false;
        if (startYear != null ? !startYear.equals(that.startYear) : that.startYear != null) return false;
        if (graduationYear != null ? !graduationYear.equals(that.graduationYear) : that.graduationYear != null)
            return false;
        if (department != null ? !department.equals(that.department) : that.department != null) return false;
        if (degree != null ? !degree.equals(that.degree) : that.degree != null) return false;
        return universityUrl != null ? universityUrl.equals(that.universityUrl) : that.universityUrl == null;
    }

    @Override
    public int hashCode() {
        int result = faculty != null ? faculty.hashCode() : 0;
        result = 31 * result + (university != null ? university.hashCode() : 0);
        result = 31 * result + (startYear != null ? startYear.hashCode() : 0);
        result = 31 * result + (graduationYear != null ? graduationYear.hashCode() : 0);
        result = 31 * result + (department != null ? department.hashCode() : 0);
        result = 31 * result + (degree != null ? degree.hashCode() : 0);
        result = 31 * result + (universityUrl != null ? universityUrl.hashCode() : 0);
        return result;
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
