package dg.social.crawler.networks.telescope;

import dg.social.crawler.domain.EducationProfileValue;
import org.junit.Test;

import java.util.Set;

/**
 * Unit tests for TelescopeParser class.
 * Created by gusevdm on 3/31/2017.
 */

public class TelescopeParserTest {

    String education =
            "[{'faculty': 'Master Of Business Administration', 'institution': 'Pace University', 'graduationYear': '1996', " +
                    "'department': 'Information Systems', 'degree': 'MBA in IT', 'startYear': None, 'institutionUrl': None}, " +
                    "{'faculty': 'Applied Mathematics And Computer Science', 'institution': 'Belarusian State University', 'graduationYear': '1986', " +
                    "'department': None, 'degree': 'BS in CS', 'startYear': None, 'institutionUrl': None}]";

    @Test
    public void test() {
        String oneEducation = "[{'faculty': 'Master Of Business Administration', 'institution': 'Pace University', " +
                "'graduationYear': '1996', 'department': 'Information Systems', 'degree': 'MBA in IT', 'startYear': None, " +
                "'institutionUrl': None}]";

        Set<EducationProfileValue> educations = TelescopeParser.parseEducationString(oneEducation);

        System.out.println(educations);
    }

}
