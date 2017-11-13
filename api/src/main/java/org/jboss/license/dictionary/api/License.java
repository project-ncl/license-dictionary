package org.jboss.license.dictionary.api;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
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
}
