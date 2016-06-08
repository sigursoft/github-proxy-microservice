package bel.kozik.github.domain;

import static java.lang.String.format;

/**
 * Repository POJO.
 * Created by Anton Kozik on 25.05.16.
 */
public class Repository {

    private String fullName;

    private String description;

    private String cloneUri;

    private int stars;

    private String createdAt;

    @Deprecated
    public Repository() {
        // for JSON generation
    }

    public Repository(String fullName, String description, String cloneUri, int stars, String createdAt) {
        this.fullName = fullName;
        this.description = description;
        this.cloneUri = cloneUri;
        this.stars = stars;
        this.createdAt = createdAt;
    }

    public String toJSON() {
        return format("{" +
                        "\"fullName\" : \"%s\"," +
                        "\"description\" : \"%s\"," +
                        "\"cloneUri\" : \"%s\"," +
                        "\"stars\" : %d," +
                        "\"createdAt\" : \"%s\"" +
                        "}", fullName, description, cloneUri, stars, createdAt);
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCloneUri(String cloneUri) {
        this.cloneUri = cloneUri;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDescription() {
        return description;
    }

    public String getCloneUri() {
        return cloneUri;
    }

    public int getStars() {
        return stars;
    }

    public String getCreatedAt() {
        return createdAt;
    }

}
