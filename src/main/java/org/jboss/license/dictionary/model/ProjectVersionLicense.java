package org.jboss.license.dictionary.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "ProjectVersionLicense")
@Table(name = "project_version_license", indexes = {
        @Index(name = "idx_project_version_license_license", columnList = "license_id"),
        @Index(name = "idx_project_version_license_project_version_license_check", columnList = "project_version_license_check_id") })
@ToString
@EqualsAndHashCode
public class ProjectVersionLicense {

    public static final String SEQUENCE_NAME = "project_version_license_id_seq";

    @Id
    @GeneratedValue(generator = SEQUENCE_NAME)
    @GenericGenerator(name = SEQUENCE_NAME, strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = SEQUENCE_NAME), @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "100") })
    @Getter
    private Integer id;

    @Column(name = "scope")
    @Getter
    @Setter
    private String scope;

    @ManyToOne
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "fk_project_version_license_license"))
    @Getter
    @Setter
    private License license;

    @ManyToOne
    @JoinColumn(name = "project_version_license_check_id", nullable = false, foreignKey = @ForeignKey(name = "fk_project_version_license_project_version_license_check"))
    @Getter
    @Setter
    private ProjectVersionLicenseCheck projectVersionLicenseCheck;

    @OneToMany(mappedBy = "projectVersionLicense")
    private Set<ProjectVersionLicenseHint> projectVersionLicenseHints;

    public ProjectVersionLicense() {
        this.projectVersionLicenseHints = new HashSet<ProjectVersionLicenseHint>();
    }

    public void addProjectVersionLicenseHint(ProjectVersionLicenseHint projectVersionLicenseHint) {
        this.projectVersionLicenseHints.add(projectVersionLicenseHint);
    }

}
