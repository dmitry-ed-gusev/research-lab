package dg.social.crawler.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import static dg.social.crawler.CommonDefaults.SocialNetwork;

/**
 * Domain object - one person user (human).
 * Created by vinnypuhh on 24.12.16.
 */

@Entity
@Table(name = "PEOPLE")
public class PersonDto extends AbstractEntity {

    @Transient
    private String firstName; // user first name
    @Transient
    private String lastName;  // user last name
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("firstName", firstName)
                .append("lastName", lastName)
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
