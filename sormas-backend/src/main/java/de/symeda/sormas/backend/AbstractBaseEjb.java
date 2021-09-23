package de.symeda.sormas.backend;

import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;

// todo should we use BaseAdoService?
public abstract class AbstractBaseEjb<DTO extends AbstractDomainObject, SRV extends AdoServiceWithUserFilter<DTO>> {

	protected SRV service;

	protected AbstractBaseEjb() {
	}

	protected AbstractBaseEjb(SRV service) {
		this.service = service;
	}

	// todo cannot be filled right now as we are missing ArchivableAbstractDomainObject
	// with this abstract class e.g., ImmunizationFacadeEjb could be wired up to this as well
	public abstract void archive(String uuid);

	public abstract void dearchive(String uuid);
}
