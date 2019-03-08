/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples;

import java.util.function.BiConsumer;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.CaseInfoLayout;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class SampleDataView extends AbstractSampleView {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	public static final String EDIT_LOC = "edit";
	public static final String CASE_LOC = "case";
	public static final String PATHOGEN_TESTS_LOC = "pathogenTests";
	public static final String ADDITIONAL_TESTS_LOC = "additionalTests";

	public SampleDataView() {
		super(VIEW_NAME);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		setHeightUndefined();

		String htmlLayout = LayoutUtil.fluidRow(LayoutUtil.fluidColumnLoc(8, 0, 12, 0, EDIT_LOC),
				LayoutUtil.fluidColumnLoc(4, 0, 6, 0, CASE_LOC),
				LayoutUtil.fluidColumnLoc(4, 0, 6, 0, PATHOGEN_TESTS_LOC),
				LayoutUtil.fluidColumnLoc(4, 0, 6, 0, ADDITIONAL_TESTS_LOC));

		VerticalLayout container = new VerticalLayout();
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		CustomLayout layout = new CustomLayout();
		layout.addStyleName(CssStyles.ROOT_COMPONENT);
		layout.setTemplateContents(htmlLayout);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeightUndefined();
		container.addComponent(layout);

		SampleDto sampleDto = FacadeProvider.getSampleFacade().getSampleByUuid(getSampleRef().getUuid());

		CommitDiscardWrapperComponent<SampleEditForm> editComponent = ControllerProvider.getSampleController()
				.getSampleEditComponent(getSampleRef().getUuid());
		editComponent.setMargin(false);
		editComponent.setWidth(100, Unit.PERCENTAGE);
		editComponent.getWrappedComponent().setWidth(100, Unit.PERCENTAGE);
		editComponent.addStyleName(CssStyles.MAIN_COMPONENT);
		layout.addComponent(editComponent, EDIT_LOC);

		BiConsumer<PathogenTestResultType, Runnable> pathogenTestChangedCallback = new BiConsumer<PathogenTestResultType, Runnable>() {
			@Override
			public void accept(PathogenTestResultType pathogenTestResult, Runnable saveCallback) {
				SampleDto componentSample = editComponent.getWrappedComponent().getValue();
				if (pathogenTestResult != componentSample.getPathogenTestResult()) {
					ControllerProvider.getSampleController().showChangePathogenTestResultWindow(editComponent, componentSample.getUuid(), pathogenTestResult, saveCallback);
				} else {
					saveCallback.run();
				}
			}
		};

		CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(sampleDto.getAssociatedCase().getUuid());
		CaseInfoLayout caseInfoLayout = new CaseInfoLayout(caseDto);
		caseInfoLayout.addStyleName(CssStyles.SIDE_COMPONENT);
		layout.addComponent(caseInfoLayout, CASE_LOC);

		if (Boolean.TRUE.equals(sampleDto.getPathogenTestingRequested())) {
			PathogenTestListComponent pathogenTestList = new PathogenTestListComponent(getSampleRef(), pathogenTestChangedCallback);
			pathogenTestList.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addComponent(pathogenTestList, PATHOGEN_TESTS_LOC);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.ADDITIONAL_TEST_VIEW) &&
				Boolean.TRUE.equals(sampleDto.getAdditionalTestingRequested())) {
			AdditionalTestListComponent additionalTestList = new AdditionalTestListComponent(getSampleRef().getUuid());
			additionalTestList.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addComponent(additionalTestList, ADDITIONAL_TESTS_LOC);
		}
	}
}
