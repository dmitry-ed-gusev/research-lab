package dg.social.crawler.networks.vk;

import dg.social.crawler.domain.CountryDto;
import dg.social.crawler.domain.PersonDto;
import dg.social.crawler.networks.ParserInterface;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

import static dg.social.crawler.SCrawlerDefaults.SocialNetwork.VK;

/**
 * Parse results of executing VK HTTP methods.
 * Created by vinnypuhh on 25.12.16.
 */

public final class VkParser implements ParserInterface {

    private static final Log LOG = LogFactory.getLog(VkParser.class);

    private static final JSONParser JSON_PARSER       = new JSONParser();
    private static final String     JSON_RESPONSE_KEY = "response";
    private static final String     JSON_ITEMS_KEY    = "items";

    private VkParser() {}

    /***/
    public static List<PersonDto> parseUsers(String jsonSearchResult) throws ParseException {
        LOG.debug("VkParser.parseUsers() working.");

        if (StringUtils.isBlank(jsonSearchResult)) { // fail-fast
            throw new IllegalArgumentException("Can't parse empty search results!");
        }

        List<PersonDto> usersList = new ArrayList<>(); // resulting list of users

        // parsing JSON with search results
        JSONObject jsonObject = (JSONObject) JSON_PARSER.parse(jsonSearchResult);
        JSONObject response   = (JSONObject) jsonObject.get(JSON_RESPONSE_KEY);
        JSONArray  items      = (JSONArray)  response.get(JSON_ITEMS_KEY);
        LOG.debug(String.format("Response contains [%s] items, total [%s] items.", items.size(), response.get("count")));

        // iterate over found items and create list of users
        PersonDto user;
        JSONObject item;
        for (Object object : items) {

            //System.out.println("item -> " + object); // <- too much output

            item = (JSONObject) object;
            user = new PersonDto(0, (Long) item.get("id"), VK);

            // additional fields
            user.setFirstName(String.valueOf(item.get("first_name")));
            user.setLastName(String.valueOf(item.get("last_name")));
            user.setAbout(String.valueOf(item.get("about")));
            user.setBirthDay(String.valueOf(item.get("bdate")));
            user.setBooks(String.valueOf(item.get("books")));
            user.setGames(String.valueOf(item.get("games")));
            user.setInterests(String.valueOf(item.get("interests")));
            user.setMaidenName(String.valueOf(item.get("maiden_name")));
            user.setMovies(String.valueOf(item.get("movies")));
            user.setMusic(String.valueOf(item.get("music")));
            user.setNickname(String.valueOf(item.get("nickname")));
            user.setQuotes(String.valueOf(item.get("quotes")));
            user.setScreenName(String.valueOf(item.get("screenName")));
            user.setSite(String.valueOf(item.get("site")));
            user.setStatus(String.valueOf(item.get("status")));
            user.setTv(String.valueOf(item.get("tv")));
            user.setHomeTown(String.valueOf(item.get("home_town")));

            // add found/parsed user to resulting list
            usersList.add(user);
        }

        return usersList;
    }

    /***/
    public static List<CountryDto> parseCountries(String jsonSearchResult) throws ParseException {
        LOG.debug("VkParser.parseCountries() working.");

        if (StringUtils.isBlank(jsonSearchResult)) { // fail-fast
            throw new IllegalArgumentException("Can't parse empty search results!");
        }

        List<CountryDto> countriesList = new ArrayList<>(); // resulting list of users

        // parsing JSON with search results
        JSONObject jsonObject = (JSONObject) JSON_PARSER.parse(jsonSearchResult);
        JSONObject response   = (JSONObject) jsonObject.get(JSON_RESPONSE_KEY);
        JSONArray  items      = (JSONArray)  response.get(JSON_ITEMS_KEY);
        LOG.debug(String.format("Response contains [%s] items, total [%s] items.", items.size(), response.get("count")));

        // iterate over found items and create list of users
        JSONObject item;
        for (Object object : items) {
            //System.out.println("item -> " + object); // <- too much output
            item = (JSONObject) object;
            countriesList.add(new CountryDto(0, (long) item.get("id"), (String) item.get("title"), VK));
        }

        return countriesList;
    }

}
