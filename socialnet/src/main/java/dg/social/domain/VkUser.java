package dg.social.domain;

/**
 *
 * Domain object - VK user (human).
 * Created by vinnypuhh on 24.12.16.
 */
// todo: implement "Builder" pattern
public class VkUser {

    private long   id;        // user identity
    private String firstName; // user first name
    private String lastName;  // user last name

    private long   cytyId;    // used in search
    private long   countryId; // used in search

    private String about;
    private String activities;
    private String books;

    /***/
    public VkUser(long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    public String getBooks() {
        return books;
    }

    public void setBooks(String books) {
        this.books = books;
    }
}
