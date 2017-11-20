package org.jboss.license.dictionary.license;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jboss.license.dictionary.api.LicenseStatus;

import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.Set;

/**
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 8/31/17
 */
@Entity
@Getter
@ToString(exclude = {"content", "nameAliases", "urlAliases"})
@EqualsAndHashCode
public class LicenseEntity {
    @Id
    @GeneratedValue
    private Integer id;
    @Setter
    private String name;
    @Setter
    private String abbreviation;
    @Setter
    private String url;
    @Setter
    @Enumerated(EnumType.STRING)
    private LicenseStatus status;
    @ElementCollection
    @Setter
    private Set<String> nameAliases;
    @ElementCollection
    @Setter
    private Set<String> urlAliases;
    @Setter
    private String textUrl;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Setter
    private String content;
    @Setter
    private String fedoraAbbrevation;
    @Setter
    private String fedoraName;
    @Setter
    private String spdxName;
    @Setter
    private String spdxAbbreviation;
    @Setter
    private String spdxUrl;
}
