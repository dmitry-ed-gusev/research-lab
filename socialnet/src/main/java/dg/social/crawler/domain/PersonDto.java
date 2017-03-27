package dg.social.crawler.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.util.Set;

import static dg.social.crawler.SCrawlerDefaults.SocialNetwork;

/**
 * Domain object - one person user (human).
 * Created by vinnypuhh on 24.12.16.
 */

@Entity
@Table(name = "PEOPLE")
public class PersonDto extends AbstractEntity {

    @Column(name = "FIRST_NAME")
    private String firstName;   // user first name
    @Column (name = "LAST_NAME")
    private String lastName;    // user last name
    @Column (name = "DISPLAY_NAME")
    private String displayName; // display/main name
    @Column (name = "NATIVE_NAME")
    private String nativeName;  // native name

    @ElementCollection
    @CollectionTable(name = "PEOPLE_NAMES", joinColumns = @JoinColumn(name = "PERSON_ID"))
    @Column(name = "NAME")
    private Set<String> namesList; // list of names (variations)

    @Column
    private String      city;
    @ElementCollection
    @CollectionTable(name="PEOPLE_CITIES", joinColumns = @JoinColumn(name = "PERSON_ID"))
    @Column(name = "CITY_NAME")
    private Set<String> citiesList; // list of possible cities

    @Column
    private String      country;
    @ElementCollection
    @CollectionTable(name="PEOPLE_COUNTRIES", joinColumns = @JoinColumn(name = "PERSON_ID"))
    @Column(name = "COUNTRY_NAME")
    private Set<String> countriesList; // list of possible countries

    @Transient
    private String maidenName;
    @Transient
    private String about;
    @Transient
    private String birthDay;
    @Transient
    private String books;
    @Transient
    private String games;
    @Transient
    private String interests;
    @Transient
    private String movies;
    @Transient
    private String music;
    @Transient
    private String nickname;
    @Transient
    private String quotes;
    @Transient
    private String screenName;
    @Transient
    private String site;
    @Transient
    private String status;
    @Transient
    private String tv;
    @Transient
    private String homeTown;

    /***/
    public PersonDto() {}

    /***/
    public PersonDto(long id, long externalId, SocialNetwork socialNetwork) {
        super(id, externalId, socialNetwork);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getNativeName() {
        return nativeName;
    }

    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }

    public Set<String> getNamesList() {
        return namesList;
    }

    public void setNamesList(Set<String> namesList) {
        this.namesList = namesList;
    }

    public String getMaidenName() {
        return maidenName;
    }

    public void setMaidenName(String maidenName) {
        this.maidenName = maidenName;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getBooks() {
        return books;
    }

    public void setBooks(String books) {
        this.books = books;
    }

    public String getGames() {
        return games;
    }

    public void setGames(String games) {
        this.games = games;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getMovies() {
        return movies;
    }

    public void setMovies(String movies) {
        this.movies = movies;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getQuotes() {
        return quotes;
    }

    public void setQuotes(String quotes) {
        this.quotes = quotes;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTv() {
        return tv;
    }

    public void setTv(String tv) {
        this.tv = tv;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Set<String> getCitiesList() {
        return citiesList;
    }

    public void setCitiesList(Set<String> citiesList) {
        this.citiesList = citiesList;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Set<String> getCountriesList() {
        return countriesList;
    }

    public void setCountriesList(Set<String> countriesList) {
        this.countriesList = countriesList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("displayName", displayName)
                .append("nativeName", nativeName)
                .append("namesList", namesList)
                .append("city", this.city)
                .append("cities", this.citiesList)
                .append("country", this.country)
                .append("countries", this.countriesList)
                .append("maidenName", maidenName)
                .append("about", about)
                .append("birthDay", birthDay)
                .append("books", books)
                .append("games", games)
                .append("interests", interests)
                .append("movies", movies)
                .append("music", music)
                .append("nickname", nickname)
                .append("quotes", quotes)
                .append("screenName", screenName)
                .append("site", site)
                .append("status", status)
                .append("tv", tv)
                .append("homeTown", homeTown)
                .toString();
    }

}
