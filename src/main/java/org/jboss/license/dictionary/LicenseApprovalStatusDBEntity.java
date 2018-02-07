package org.jboss.license.dictionary;

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

@Entity
@Table(name = "license_approval_status")
@ToString
@EqualsAndHashCode
public class LicenseApprovalStatusDBEntity {

    public static final String SEQUENCE_NAME = "license_approval_status_id_seq";

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

    @OneToMany(mappedBy = "licenseApprovalStatus")
    @Getter
    @Setter
    private Set<LicenseDBEntity> licenses;

    public LicenseApprovalStatusDBEntity() {
        this.licenses = new HashSet<LicenseDBEntity>();
    }

    public void addLicense(LicenseDBEntity license) {
        this.licenses.add(license);
    }

}
