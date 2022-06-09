package de.symeda.sormas.backend.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.api.report.AggregateReportDto;
import de.symeda.sormas.api.report.AggregateReportFacade;
import de.symeda.sormas.api.report.AggregateReportGroupingLevel;
import de.symeda.sormas.api.report.AggregatedCaseCountDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AgeGroupUtils;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.FacilityService;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryService;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import org.apache.commons.lang3.StringUtils;

@Stateless(name = "AggregateReportFacade")
@RolesAllowed(UserRight._AGGREGATE_REPORT_VIEW)
public class AggregateReportFacadeEjb implements AggregateReportFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private AggregateReportService service;
	@EJB
	private UserService userService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private PointOfEntryService pointOfEntryService;
	@EJB
	private DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;

	@Override
	public List<AggregateReportDto> getAllAggregateReportsAfter(Date date) {

		return service.getAllAfter(date).stream().map(r -> toDto(r)).collect(Collectors.toList());
	}

	@Override
	public List<AggregateReportDto> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(r -> toDto(r)).collect(Collectors.toList());
	}

	@Override
	@RolesAllowed(UserRight._AGGREGATE_REPORT_EDIT)
	public AggregateReportDto saveAggregateReport(@Valid AggregateReportDto dto) {

		AgeGroupUtils.validateAgeGroup(dto.getAgeGroup());
		AggregateReport report = fromDto(dto, true);
		service.ensurePersisted(report);
		return toDto(report);
	}

	@Override
	public List<String> getAllUuids() {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return service.getAllUuids();
	}

	public static AggregateReportDto toDto(AggregateReport source) {

		if (source == null) {
			return null;
		}

		AggregateReportDto target = new AggregateReportDto();
		DtoHelper.fillDto(target, source);

		target.setDisease(source.getDisease());
		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setYear(source.getYear());
		target.setEpiWeek(source.getEpiWeek());
		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setHealthFacility(FacilityFacadeEjb.toReferenceDto(source.getHealthFacility()));
		target.setPointOfEntry(PointOfEntryFacadeEjb.toReferenceDto(source.getPointOfEntry()));
		target.setNewCases(source.getNewCases());
		target.setLabConfirmations(source.getLabConfirmations());
		target.setDeaths(source.getDeaths());
		target.setAgeGroup(source.getAgeGroup());

		return target;
	}

	@Override
	public List<AggregatedCaseCountDto> getIndexList(AggregateReportCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AggregatedCaseCountDto> cq = cb.createQuery(AggregatedCaseCountDto.class);
		Root<AggregateReport> root = cq.from(AggregateReport.class);

		Predicate filter = service.createUserFilter(cb, cq, root);
		if (criteria != null) {
			Predicate criteriaFilter = service.createCriteriaFilter(criteria, cb, cq, root);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		List<Selection<?>> selectionList = new ArrayList<>(
			Arrays.asList(
				root.get(AggregateReport.DISEASE),
				cb.sum(root.get(AggregateReport.NEW_CASES)),
				cb.sum(root.get(AggregateReport.LAB_CONFIRMATIONS)),
				cb.sum(root.get(AggregateReport.DEATHS)),
				root.get(AggregateReport.EPI_WEEK),
				root.get(AggregateReport.AGE_GROUP)));

		List<Expression<?>> expressions = new ArrayList<>(
			Arrays.asList(root.get(AggregateReport.DISEASE), root.get(AggregateReport.EPI_WEEK), root.get(AggregateReport.AGE_GROUP)));

		AggregateReportGroupingLevel groupingLevel = null;

		if (criteria != null && criteria.getAggregateReportGroupingLevel() != null) {
			groupingLevel = criteria.getAggregateReportGroupingLevel();

			if (shouldIncludeRegion(groupingLevel)) {
				Join<AggregateReport, Region> regionJoin = root.join(AggregateReport.REGION, JoinType.LEFT);
				List<Path<Object>> regionPath = Arrays.asList(regionJoin.get(Region.NAME), regionJoin.get(Region.ID));
				expressions.addAll(regionPath);
				selectionList.addAll(regionPath);
			}

			if (shouldIncludeDistrict(groupingLevel)) {
				Join<AggregateReport, District> districtJoin = root.join(AggregateReport.DISTRICT, JoinType.LEFT);
				List<Path<Object>> districtPath = Arrays.asList(districtJoin.get(District.NAME), districtJoin.get(District.ID));
				expressions.addAll(districtPath);
				selectionList.addAll(districtPath);
			}

			if (shouldIncludeHealthFacility(groupingLevel)) {
				Join<AggregateReport, Facility> facilityJoin = root.join(AggregateReport.HEALTH_FACILITY, JoinType.LEFT);
				List<Path<Object>> facilityPath = Arrays.asList(facilityJoin.get(Facility.NAME), facilityJoin.get(Facility.ID));
				expressions.addAll(facilityPath);
				selectionList.addAll(facilityPath);
			}

			if (shouldIncludePointOfEntry(groupingLevel)) {
				Join<AggregateReport, PointOfEntry> pointOfEntryJoin = root.join(AggregateReport.POINT_OF_ENTRY, JoinType.LEFT);
				List<Path<Object>> pointOfEntryPath = Arrays.asList(pointOfEntryJoin.get(PointOfEntry.ID), pointOfEntryJoin.get(PointOfEntry.NAME));
				expressions.addAll(pointOfEntryPath);
				selectionList.addAll(pointOfEntryPath);
			}
		}

		cq.multiselect(selectionList);

		cq.groupBy(expressions);

		List<AggregatedCaseCountDto> resultList = em.createQuery(cq).getResultList();
		Map<Disease, AggregatedCaseCountDto> reportSet = new HashMap<>();

		for (AggregatedCaseCountDto result : resultList) {
			reportSet.put(result.getDisease(), result);
		}

		if (criteria != null && criteria.getShowZeroRowsForGrouping()) {
			for (Disease disease : diseaseConfigurationFacade.getAllDiseases(true, false, false)) {
				if (!reportSet.containsKey(disease)) {
					resultList.add(new AggregatedCaseCountDto(disease, 0L, 0L, 0L, 0, ""));
				}
			}
		}

		resultList.sort(
			Comparator.comparing(AggregatedCaseCountDto::getDisease, Comparator.nullsFirst(Comparator.comparing(Disease::toString)))
				.thenComparing(AggregatedCaseCountDto::getRegionName, Comparator.nullsFirst(Comparator.naturalOrder()))
				.thenComparing(AggregatedCaseCountDto::getDistrictName, Comparator.nullsFirst(Comparator.naturalOrder()))
				.thenComparing(AggregatedCaseCountDto::getHealthFacilityName, Comparator.nullsFirst(Comparator.naturalOrder()))
				.thenComparing(AggregatedCaseCountDto::getPointOfEntryName, Comparator.nullsFirst(Comparator.naturalOrder()))
				.thenComparing(AggregatedCaseCountDto::getEpiWeek, Comparator.nullsFirst(Comparator.naturalOrder()))
				.thenComparing(
					r -> r.getAgeGroup() != null
						? r.getAgeGroup().split("_")[0].replaceAll("[^a-zA-Z]", StringUtils.EMPTY).toUpperCase()
						: StringUtils.EMPTY)
				.thenComparing(
					r -> r.getAgeGroup() != null ? Integer.parseInt(r.getAgeGroup().split("_")[0].replaceAll("[^0-9]", StringUtils.EMPTY)) : 0));
		return resultList;
	}

	private boolean shouldIncludeRegion(AggregateReportGroupingLevel groupingLevel) {
		return AggregateReportGroupingLevel.REGION.equals(groupingLevel)
			|| AggregateReportGroupingLevel.DISTRICT.equals(groupingLevel)
			|| AggregateReportGroupingLevel.HEALTH_FACILITY.equals(groupingLevel)
			|| AggregateReportGroupingLevel.POINT_OF_ENTRY.equals(groupingLevel);
	}

	private boolean shouldIncludeDistrict(AggregateReportGroupingLevel groupingLevel) {
		return AggregateReportGroupingLevel.DISTRICT.equals(groupingLevel)
			|| AggregateReportGroupingLevel.HEALTH_FACILITY.equals(groupingLevel)
			|| AggregateReportGroupingLevel.POINT_OF_ENTRY.equals(groupingLevel);
	}

	private boolean shouldIncludeHealthFacility(AggregateReportGroupingLevel groupingLevel) {
		return AggregateReportGroupingLevel.HEALTH_FACILITY.equals(groupingLevel);
	}

	private boolean shouldIncludePointOfEntry(AggregateReportGroupingLevel groupingLevel) {
		return AggregateReportGroupingLevel.POINT_OF_ENTRY.equals(groupingLevel);
	}

	@Override
	public List<AggregateReportDto> getList(AggregateReportCriteria criteria) {

		User user = userService.getCurrentUser();
		return service.findBy(criteria, user).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	public AggregateReport fromDto(@NotNull AggregateReportDto source, boolean checkChangeDate) {

		AggregateReport target = DtoHelper.fillOrBuildEntity(source, service.getByUuid(source.getUuid()), AggregateReport::new, checkChangeDate);

		target.setDisease(source.getDisease());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setYear(source.getYear());
		target.setEpiWeek(source.getEpiWeek());
		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setHealthFacility(facilityService.getByReferenceDto(source.getHealthFacility()));
		target.setPointOfEntry(pointOfEntryService.getByReferenceDto(source.getPointOfEntry()));
		target.setNewCases(source.getNewCases());
		target.setLabConfirmations(source.getLabConfirmations());
		target.setDeaths(source.getDeaths());
		target.setAgeGroup(source.getAgeGroup());

		return target;
	}

	@Override
	@RolesAllowed(UserRight._AGGREGATE_REPORT_EDIT)
	public void deleteReport(String reportUuid) {

		if (!userService.hasRight(UserRight.AGGREGATE_REPORT_EDIT)) {
			throw new UnsupportedOperationException("User " + userService.getCurrentUser().getUuid() + " is not allowed to edit aggregate reports.");
		}

		AggregateReport aggregateReport = service.getByUuid(reportUuid);
		service.deletePermanent(aggregateReport);
	}

	@Override
	public long countWithCriteria(AggregateReportCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AggregateReport> root = cq.from(AggregateReport.class);

		Predicate filter = service.createUserFilter(cb, cq, root);
		if (criteria != null) {
			Predicate criteriaFilter = service.createCriteriaFilter(criteria, cb, cq, root);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(root));

		return em.createQuery(cq).getSingleResult();
	}

	@LocalBean
	@Stateless
	public static class AggregateReportFacadeEjbLocal extends AggregateReportFacadeEjb {

	}
}
