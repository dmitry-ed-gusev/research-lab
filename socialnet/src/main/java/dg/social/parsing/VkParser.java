package dg.social.parsing;

import dg.social.domain.VkUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.List;

/**
 * Parse results of executing VK HTTP methods.
 * Created by vinnypuhh on 25.12.16.
 */

public final class VkParser {

    private static final Log LOG = LogFactory.getLog(VkParser.class);

    private static final JSONParser JSON_PARSER = new JSONParser();
    private static final String JSON_RESPONSE_KEY = "response";

    private VkParser() {}

    /***/
    public static List<VkUser> parseUsers(String jsonSearchResult) throws ParseException {
        LOG.debug("VkParser.parseUsers() working.");

        if (StringUtils.isBlank(jsonSearchResult)) { // fail-fast
            throw new IllegalArgumentException("Can't parse empty search results!");
        }

        // parsing JSON with search results
        JSONObject jsonObject = (JSONObject) JSON_PARSER.parse(jsonSearchResult);
        JSONObject response   = (JSONObject) jsonObject.get("response");
        JSONArray items       = (JSONArray)  response.get("items");


        System.out.println("=> " + response.get("count"));
        System.out.println("=> " + response.get("items"));
        System.out.println("** " + items.size());
        return null;
    }

    /***/
    public static void main(String[] args) throws ParseException {
        LOG.info("VkParser starting.");

        String result = "{\"response\":{\"count\":71148,\"items\":[{\"id\":16821412,\"first_name\":\"Антон\",\"last_name\":\"Гусев\"},{\"id\":18112,\"first_name\":\"Виктор\",\"last_name\":\"Гусев\"},{\"id\":92777407,\"first_name\":\"Святослав\",\"last_name\":\"Гусев\"},{\"id\":4517431,\"first_name\":\"Денис\",\"last_name\":\"Гусев\"},{\"id\":7072850,\"first_name\":\"Максим\",\"last_name\":\"Гусев\"},{\"id\":51252103,\"first_name\":\"Пётр\",\"last_name\":\"Гусев\"},{\"id\":9332725,\"first_name\":\"Тимофей\",\"last_name\":\"Гусев\"},{\"id\":53152018,\"first_name\":\"Владимир\",\"last_name\":\"Гусев\"},{\"id\":2788909,\"first_name\":\"Александр\",\"last_name\":\"Гусев\"},{\"id\":91356514,\"first_name\":\"Максим\",\"last_name\":\"Гусев\"},{\"id\":125520962,\"first_name\":\"Сергей\",\"last_name\":\"Гусев\"},{\"id\":194142007,\"first_name\":\"Иван\",\"last_name\":\"Гусев\"},{\"id\":189175467,\"first_name\":\"Родион\",\"last_name\":\"Гусев\"},{\"id\":78650067,\"first_name\":\"Дмитрий\",\"last_name\":\"Гусев\"},{\"id\":121986755,\"first_name\":\"Антон\",\"last_name\":\"Гусев\"},{\"id\":123130300,\"first_name\":\"Никита\",\"last_name\":\"Гусев\"},{\"id\":1736209,\"first_name\":\"Дмитрий\",\"last_name\":\"Гусев\"},{\"id\":153905046,\"first_name\":\"Дмитрий\",\"last_name\":\"Гусев\"},{\"id\":216322428,\"first_name\":\"Сергей\",\"last_name\":\"Гусев\"},{\"id\":101449995,\"first_name\":\"Вячеслав\",\"last_name\":\"Гусев\"},{\"id\":5351419,\"first_name\":\"Владимир\",\"last_name\":\"Гусев\"},{\"id\":147562203,\"first_name\":\"Григорий\",\"last_name\":\"Гусев\"},{\"id\":301380172,\"first_name\":\"Александр\",\"last_name\":\"Гусев\"},{\"id\":113691632,\"first_name\":\"Андрей\",\"last_name\":\"Гусев\"},{\"id\":76276196,\"first_name\":\"Артем\",\"last_name\":\"Гусев\"},{\"id\":36666084,\"first_name\":\"Дмитрий\",\"last_name\":\"Гусев\"},{\"id\":138610661,\"first_name\":\"Кристиан\",\"last_name\":\"Гусев\"},{\"id\":37868115,\"first_name\":\"Андрей\",\"last_name\":\"Гусев\"},{\"id\":19939204,\"first_name\":\"Александр\",\"last_name\":\"Гусев\"},{\"id\":122670462,\"first_name\":\"Никита\",\"last_name\":\"Гусев\"},{\"id\":30699103,\"first_name\":\"Артем\",\"last_name\":\"Гусев\"},{\"id\":147894776,\"first_name\":\"Никита\",\"last_name\":\"Гусев\"},{\"id\":488809,\"first_name\":\"Сергей\",\"last_name\":\"Гусев\"},{\"id\":29246718,\"first_name\":\"Руслан\",\"last_name\":\"Гусев\"},{\"id\":255740653,\"first_name\":\"Андрей\",\"last_name\":\"Гусев\"},{\"id\":160761762,\"first_name\":\"Илья\",\"last_name\":\"Гусев\"},{\"id\":106796833,\"first_name\":\"Андрей\",\"last_name\":\"Гусев\"},{\"id\":354733604,\"first_name\":\"Кирилл\",\"last_name\":\"Гусев\"},{\"id\":256164439,\"first_name\":\"Никита\",\"last_name\":\"Гусев\"},{\"id\":45248695,\"first_name\":\"Александр\",\"last_name\":\"Гусев\"},{\"id\":25835962,\"first_name\":\"Денис\",\"last_name\":\"Гусев\"},{\"id\":24420125,\"first_name\":\"Александр\",\"last_name\":\"Гусев\"},{\"id\":89441811,\"first_name\":\"Дмитрий\",\"last_name\":\"Гусев\"},{\"id\":137670120,\"first_name\":\"Юра\",\"last_name\":\"Гусев\"},{\"id\":9178785,\"first_name\":\"Артур\",\"last_name\":\"Гусев\"},{\"id\":139401279,\"first_name\":\"Серёга\",\"last_name\":\"Гусев\"},{\"id\":65202131,\"first_name\":\"Леха\",\"last_name\":\"Гусев\"},{\"id\":8628562,\"first_name\":\"Григорий\",\"last_name\":\"Гусев\"},{\"id\":252301933,\"first_name\":\"Даниил\",\"last_name\":\"Гусев\"},{\"id\":142044884,\"first_name\":\"Владимир\",\"last_name\":\"Гусев\"},{\"id\":27726164,\"first_name\":\"Владимир\",\"last_name\":\"Гусев\"},{\"id\":42435855,\"first_name\":\"Сергей\",\"last_name\":\"Гусев\"},{\"id\":265612173,\"first_name\":\"Виталий\",\"last_name\":\"Гусев\"},{\"id\":10973542,\"first_name\":\"Владимир\",\"last_name\":\"Гусев\"},{\"id\":32139129,\"first_name\":\"Евгений\",\"last_name\":\"Гусев\"},{\"id\":3036957,\"first_name\":\"Олег\",\"last_name\":\"Гусев\"},{\"id\":54986442,\"first_name\":\"Иван\",\"last_name\":\"Гусев\"},{\"id\":36008697,\"first_name\":\"Сергей\",\"last_name\":\"Гусев\"},{\"id\":23843944,\"first_name\":\"Илья\",\"last_name\":\"Гусев\"},{\"id\":6579,\"first_name\":\"Константин\",\"last_name\":\"Гусев\"},{\"id\":151650621,\"first_name\":\"Александр\",\"last_name\":\"Гусев\"},{\"id\":52931556,\"first_name\":\"Николай\",\"last_name\":\"Гусев\"},{\"id\":29854380,\"first_name\":\"Максим\",\"last_name\":\"Гусев\"},{\"id\":9821645,\"first_name\":\"Александр\",\"last_name\":\"Гусев\"},{\"id\":135651074,\"first_name\":\"Илья\",\"last_name\":\"Гусев\"},{\"id\":71761121,\"first_name\":\"Вован\",\"last_name\":\"Гусев\"},{\"id\":27455213,\"first_name\":\"Михаил\",\"last_name\":\"Гусев\"},{\"id\":22404308,\"first_name\":\"Денис\",\"last_name\":\"Гусев\"},{\"id\":14078911,\"first_name\":\"Роман\",\"last_name\":\"Гусев\"},{\"id\":63102648,\"first_name\":\"Владимир\",\"last_name\":\"Гусев\"},{\"id\":14418522,\"first_name\":\"Дима\",\"last_name\":\"Гусев\"},{\"id\":41109423,\"first_name\":\"Аарон\",\"last_name\":\"Гусев\"},{\"id\":34148126,\"first_name\":\"Алексей\",\"last_name\":\"Гусев\"},{\"id\":27405246,\"first_name\":\"Ринат\",\"last_name\":\"Гусев\"},{\"id\":19433183,\"first_name\":\"Виктор\",\"last_name\":\"Гусев\"},{\"id\":231143106,\"first_name\":\"Максим\",\"last_name\":\"Гусев\"},{\"id\":189931224,\"first_name\":\"Кирилл\",\"last_name\":\"Гусев\"},{\"id\":164280453,\"first_name\":\"Егор\",\"last_name\":\"Гусев\"},{\"id\":154566290,\"first_name\":\"Дмитрий\",\"last_name\":\"Гусев\"},{\"id\":152702356,\"first_name\":\"Александр\",\"last_name\":\"Гусев\"},{\"id\":121555811,\"first_name\":\"Илья\",\"last_name\":\"Гусев\"},{\"id\":118468694,\"first_name\":\"Вова\",\"last_name\":\"Гусев\"},{\"id\":103481965,\"first_name\":\"Леша\",\"last_name\":\"Гусев\"},{\"id\":38983265,\"first_name\":\"Владислав\",\"last_name\":\"Гусев\"},{\"id\":32221865,\"first_name\":\"Алексей\",\"last_name\":\"Гусев\"},{\"id\":138373324,\"first_name\":\"Алексей\",\"last_name\":\"Гусев\"},{\"id\":131628719,\"first_name\":\"Алексей\",\"last_name\":\"Гусев\"},{\"id\":126644173,\"first_name\":\"Дмитрий\",\"last_name\":\"Гусев\"},{\"id\":84164283,\"first_name\":\"Владислав\",\"last_name\":\"Гусев\"},{\"id\":76470536,\"first_name\":\"Александр\",\"last_name\":\"Гусев\"},{\"id\":45850976,\"first_name\":\"Максим\",\"last_name\":\"Гусев\"},{\"id\":20735325,\"first_name\":\"Алексей\",\"last_name\":\"Гусев\"},{\"id\":11759684,\"first_name\":\"Владимир\",\"last_name\":\"Гусев\"},{\"id\":164767254,\"first_name\":\"Никита\",\"last_name\":\"Гусев\"},{\"id\":39277496,\"first_name\":\"Илья\",\"last_name\":\"Гусев\"},{\"id\":30312522,\"first_name\":\"Слава\",\"last_name\":\"Гусев\"},{\"id\":8384360,\"first_name\":\"Александр\",\"last_name\":\"Гусев\"},{\"id\":4824096,\"first_name\":\"Павел\",\"last_name\":\"Гусев\"},{\"id\":3674494,\"first_name\":\"Кирилл\",\"last_name\":\"Гусев\"},{\"id\":109846449,\"first_name\":\"Игорь\",\"last_name\":\"Гусев\"}]}}";

        List<VkUser> list = VkParser.parseUsers(result);

        System.out.println("-> " + list);

    }

}
