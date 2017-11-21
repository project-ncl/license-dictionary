package api;

import java.util.Arrays;

/**
 * mstodo: Header
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 11/3/17
 */
public enum LicenseStatus {
    AWAITING_APPROVAL(null),
    NOT_APPROVED("no"),
    APPROVED("yes");

    private final String mapping;

    LicenseStatus(String mapping) {
        this.mapping = mapping;
    }

    public static LicenseStatus fromJsonString(String approved) {
        if (approved == null) {
            return AWAITING_APPROVAL;
        }
        return Arrays.stream(LicenseStatus.values())
                .filter(v -> approved.equals(v.mapping))
                .findAny().orElse(AWAITING_APPROVAL);
    }

    public String getJsonMapping() {
        return mapping;
    }
}
