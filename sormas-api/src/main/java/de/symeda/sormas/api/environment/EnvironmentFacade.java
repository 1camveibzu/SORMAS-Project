package de.symeda.sormas.api.environment;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.CoreFacade;

@Remote
public interface EnvironmentFacade extends CoreFacade<EnvironmentDto, EnvironmentIndexDto, EnvironmentReferenceDto, EnvironmentCriteria> {

	List<EnvironmentDto> getAllEnvironmentsAfter(Date date);
}
