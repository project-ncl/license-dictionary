package api;

import javax.validation.constraints.NotBlank;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 8/31/17
 */
public class License {
    private Integer id;
    @NotBlank
    private String name;
    private String abbreviation;
    @NotBlank
    private String url;

    private String textUrl;

    private String content;    // mstodo how to trigger download?
    private Set<String> nameAliases = new TreeSet<>();
    private Set<String> urlAliases = new TreeSet<>();

    private LicenseStatus status = LicenseStatus.AWAITING_APPROVAL;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<String> getNameAliases() {
        return nameAliases;
    }

    public void setNameAliases(Set<String> nameAliases) {
        this.nameAliases = nameAliases;
    }

    public Set<String> getUrlAliases() {
        return urlAliases;
    }

    public void setUrlAliases(Set<String> urlAliases) {
        this.urlAliases = urlAliases;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LicenseStatus getStatus() {
        return status;
    }

    public void setStatus(LicenseStatus status) {
        this.status = status;
    }

    public String getTextUrl() {
        return textUrl;
    }

    public void setTextUrl(String textUrl) {
        this.textUrl = textUrl;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        License license = (License) o;

        if (id != null ? !id.equals(license.id) : license.id != null) return false;
        if (name != null ? !name.equals(license.name) : license.name != null) return false;
        if (abbreviation != null ? !abbreviation.equals(license.abbreviation) : license.abbreviation != null)
            return false;
        if (url != null ? !url.equals(license.url) : license.url != null) return false;
        if (textUrl != null ? !textUrl.equals(license.textUrl) : license.textUrl != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (abbreviation != null ? abbreviation.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (textUrl != null ? textUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "License{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", url='" + url + '\'' +
                ", textUrl='" + textUrl + '\'' +
                '}';
    }
}
