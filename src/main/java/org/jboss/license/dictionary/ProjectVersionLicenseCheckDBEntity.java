package org.jboss.license.dictionary;

import java.time.Instant;
import java.util.Date;
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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "ProjectVersionLicenseCheck")
@Table(name = "project_version_license_check", indexes = {
        @Index(name = "idx_project_version_license_check_license_determination_type", columnList = "project_version_id") })
@ToString
@EqualsAndHashCode
public class ProjectVersionLicenseCheckDBEntity {

    public static final String SEQUENCE_NAME = "project_version_license_check_id_seq";

    @Id
    @GeneratedValue(generator = SEQUENCE_NAME)
    @GenericGenerator(name = SEQUENCE_NAME, strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = SEQUENCE_NAME), @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "100") })
    @Getter
    private Integer id;

    @NotNull
    @Size(max = 50)
    @Column(name = "determined_by_user", length = 50)
    @Getter
    @Setter
    private String determinedByUser;

    @NotNull
    @Column(name = "determination_date", columnDefinition = "timestamp with time zone")
    @Getter
    @Setter
    private Date determinationDate;

    @Column(name = "notes")
    @Getter
    @Setter
    private String notes;

    @ManyToOne
    @JoinColumn(name = "project_version_id", nullable = false, foreignKey = @ForeignKey(name = "fk_project_version_license_check_project_version"))
    @Getter
    @Setter
    private ProjectVersionDBEntity projectVersion;

    @ManyToOne
    @JoinColumn(name = "license_determination_type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_project_version_license_check_license_determination_type"))
    @Getter
    @Setter
    private LicenseDeterminationTypeDBEntity licenseDeterminationType;

    @OneToMany(mappedBy = "projectVersionLicenseCheck")
    private Set<ProjectVersionLicenseDBEntity> projectVersionLicenses;

    public ProjectVersionLicenseCheckDBEntity() {
        this.determinationDate = Date.from(Instant.now());
        this.projectVersionLicenses = new HashSet<ProjectVersionLicenseDBEntity>();
    }

    @PrePersist
    private void initDeterminationTime() {
        this.determinationDate = Date.from(Instant.now());
    }

    public void addProjectVersionLicense(ProjectVersionLicenseDBEntity projectVersionLicense) {
        this.projectVersionLicenses.add(projectVersionLicense);
    }

}
