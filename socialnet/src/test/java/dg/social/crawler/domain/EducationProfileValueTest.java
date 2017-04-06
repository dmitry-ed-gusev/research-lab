package dg.social.crawler.domain;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for VALUE domain object {@link EducationProfileValue}
 * Created by gusevdm on 3/31/2017.
 */

// todo: test hashCode()
public class EducationProfileValueTest {

    private static EducationProfileValue profile1;
    private static EducationProfileValue profile2eq1;
    private static EducationProfileValue profile3noteq1;

    @BeforeClass
    public static void beforeAll() {
        profile1 = new EducationProfileValue();
        profile1.setFaculty("faculty");
        profile1.setUniversityUrl("university");
        profile1.setStartYear("2000");
        profile1.setGraduationYear("2010");
        profile1.setDepartment("department");
        profile1.setDegree("degree");
        profile1.setUniversityUrl("url");

        profile2eq1 = new EducationProfileValue();
        profile2eq1.setFaculty("faculty");
        profile2eq1.setUniversityUrl("university");
        profile2eq1.setStartYear("2000");
        profile2eq1.setGraduationYear("2010");
        profile2eq1.setDepartment("department");
        profile2eq1.setDegree("degree");
        profile2eq1.setUniversityUrl("url");

        profile3noteq1 = new EducationProfileValue();
        profile3noteq1.setFaculty("faculty zzz");
        profile3noteq1.setUniversityUrl("university");
        profile3noteq1.setStartYear("2000");
        profile3noteq1.setGraduationYear("2010");
        profile3noteq1.setDepartment("department xxxx");
        profile3noteq1.setDegree("degree");
        profile3noteq1.setUniversityUrl(null);
    }
    
    
    @Test
    public void testEqualityWithNull() {
        assertFalse("Shouldn't be equals!", new EducationProfileValue().equals(null));
    }

    @Test
    public void testEqualityWithEmpty() {
        assertTrue("Should be equals!", new EducationProfileValue().equals(new EducationProfileValue()));
    }

    @Test
    public void testEqualityWithEqual() {
        assertEquals("Should be equals!", profile1, profile2eq1);
    }

    @Test
    public void testEqualityWithNotEqual() {
        assertNotEquals("Shouldn't be equal!", profile1, profile3noteq1);
    }

}
