package org.jboss.license.dictionary.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "LicenseDeterminationType")
@Table(name = "license_determination_type")
@ToString
@EqualsAndHashCode
public class LicenseDeterminationType {

    public static final String SEQUENCE_NAME = "license_determination_type_id_seq";

    @Id
    @GeneratedValue(generator = SEQUENCE_NAME)
    @GenericGenerator(name = SEQUENCE_NAME, strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = SEQUENCE_NAME), @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "10") })
    @Getter
    private Integer id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100)
    @Getter
    @Setter
    private String name;

    @Column(name = "description")
    @Getter
    @Setter
    private String description;

    @OneToMany(mappedBy = "licenseDeterminationType")
    @Getter
    @Setter
    private Set<ProjectVersionLicenseCheck> projectVersionLicenseChecks;

    public LicenseDeterminationType() {
        this.projectVersionLicenseChecks = new HashSet<ProjectVersionLicenseCheck>();
    }

    public void addProjectVersionLicenseCheck(ProjectVersionLicenseCheck projectVersionLicenseCheck) {
        this.projectVersionLicenseChecks.add(projectVersionLicenseCheck);
    }

}
