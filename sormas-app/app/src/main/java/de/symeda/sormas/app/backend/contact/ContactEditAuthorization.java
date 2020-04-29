package de.symeda.sormas.app.backend.contact;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseEditAuthorization;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;

public class ContactEditAuthorization {

    public static boolean isContactEditAllowed(Contact contact) {

        User user = ConfigProvider.getUser();
        Case caseofContact = DatabaseHelper.getCaseDao().queryUuidBasic(contact.getCaseUuid());

        if (user.getUuid().equals(contact.getReportingUser().getUuid())) {
            return true;
        }

        if (CaseEditAuthorization.isCaseEditAllowed(caseofContact)) {
            return true;
        }

        if (ConfigProvider.hasRole(UserRole.getSupervisorRoles())) {
            return contact.getRegion().equals(user.getRegion());
        }

        if (ConfigProvider.hasRole(UserRole.getOfficerRoles())) {
            return contact.getDistrict().equals(user.getDistrict());
        }

        if (ConfigProvider.hasRole(UserRole.NATIONAL_USER)) {
            return true;
        }

        return false;
    }
}
