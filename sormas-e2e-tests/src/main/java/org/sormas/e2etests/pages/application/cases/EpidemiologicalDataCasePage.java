/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.sormas.e2etests.pages.application.cases;

import org.openqa.selenium.By;

public class EpidemiologicalDataCasePage {
  public static final By EXPOSURE_DETAILS_KNOWN_OPTIONS =
      By.cssSelector("#exposureDetailsKnown .v-select-option");
  public static final By EXPOSURE_DETAILS_NEW_ENTRY_BUTTON = By.cssSelector("#actionNewEntry");
  public static final By START_OF_EXPOSURE_INPUT = By.cssSelector(".v-window #startDate input");
  public static final By END_OF_EXPOSURE_INPUT = By.cssSelector(".v-window #endDate input");
  public static final By EXPOSURE_DESCRIPTION_INPUT = By.cssSelector(".v-window #description");
  public static final By TYPE_OF_ACTIVITY_COMBOBOX = By.cssSelector(".v-window #exposureType div");
  public static final By EXPOSURE_DETAILS_ROLE_COMBOBOX =
      By.cssSelector(".v-window div#exposureRole div");
  public static final By ACTIVITY_AS_CASE_OPTIONS =
      By.cssSelector(".v-select-optiongroup #activityAsCaseDetailsKnown");
  public static final By RISK_AREA_OPTIONS = By.cssSelector(".v-window #riskArea .v-select-option");
  public static final By INDOORS_OPTIONS = By.cssSelector(".v-window #indoors .v-select-option");
  public static final By OUTDOORS_OPTIONS = By.cssSelector(".v-window #outdoors .v-select-option");
  public static final By WEARING_MASK_OPTIONS =
      By.cssSelector(".v-window #wearingMask .v-select-option");
  public static final By WEARING_PPE_OPTIONS =
      By.cssSelector(".v-window #wearingPpe .v-select-option");
  public static final By OTHER_PROTECTIVE_MEASURES_OPTIONS =
      By.cssSelector(".v-window #otherProtectiveMeasures .v-select-option");
  public static final By SHORT_DISTANCE_OPTIONS =
      By.cssSelector(".v-window #shortDistance .v-select-option");
  public static final By LONG_FACE_TO_FACE_CONTACT_OPTIONS =
      By.cssSelector(".v-window #longFaceToFaceContact .v-select-option");
  public static final By ANIMAL_MARKET_OPTIONS =
      By.cssSelector(".v-window #animalMarket .v-select-option");
  public static final By PERCUTANEOUS_OPTIONS =
      By.cssSelector(".v-window #percutaneous .v-select-option");
  public static final By CONTACT_TO_BODY_FLUIDS_OPTONS =
      By.cssSelector(".v-window #contactToBodyFluids .v-select-option");
  public static final By HANDLING_SAMPLES_OPTIONS =
      By.cssSelector(".v-window #handlingSamples .v-select-option");
  public static final By TYPE_OF_PLACE_COMBOBOX = By.cssSelector(".v-window #typeOfPlace div");
  public static final By CONTINENT_COMBOBOX = By.cssSelector(".v-window #continent div");
  public static final By SUBCONTINENT_COMBOBOX = By.cssSelector(".v-window #subcontinent div");
  public static final By COUNTRY_COMBOBOX = By.cssSelector(".v-window #country div");
  public static final By DISTRICT_COMBOBOX = By.cssSelector(".v-window #district div");
  public static final By COMMUNITY_COMBOBOX = By.cssSelector(".v-window #community div");
  public static final By STREET_INPUT = By.cssSelector(".v-window input#street");
  public static final By HOUSE_NUMBER_INPUT = By.cssSelector(".v-window input#houseNumber");
  public static final By ADDITIONAL_INFORMATION_INPUT =
      By.cssSelector(".v-window input#additionalInformation");
  public static final By POSTAL_CODE_INPUT = By.cssSelector(".v-window input#postalCode");
  public static final By CITY_INPUT = By.cssSelector(".v-window input#city");
  public static final By DONE_BUTTON = By.cssSelector(".v-window #commit");
  public static final By DISCARD_BUTTON = By.cssSelector(".v-window #discard");
  public static final By RESIDING_DETAILS_KNOWN_OPTIONS =
      By.cssSelector("#highTransmissionRiskArea .v-select-option");
  public static final By RESIDING_OR_WORKING_DETAILS_KNOWN_OPTIONS =
      By.cssSelector("#highTransmissionRiskArea .v-select-option");
  public static final By RESIDING_OR_TRAVELING_DETAILS_KNOWN_OPTIONS =
      By.cssSelector("#largeOutbreaksArea .v-select-option");
  public static final By CONTACT_WITH_SOURCE_CASE_KNOWN =
      By.cssSelector("#contactWithSourceCaseKnown .v-select-option");
  public static final By NEW_CONTACT_BUTTON = By.id("contactNewContact");
  public static final By ACTIVITY_DETAILS_KNOWN_OPTIONS =
      By.cssSelector("#activityAsCaseDetailsKnown .v-select-option");
  public static final By ACTIVITY_DETAILS_NEW_ENTRY_BUTTON =
      By.cssSelector("#activitiesAsCase #actionNewEntry");
  public static final By ACTIVITY_START_OF_ACTIVITY_INPUT =
      By.cssSelector(".v-window #startDate .v-textfield.v-datefield-textfield");
  public static final By ACTIVITY_END_OF_ACTIVITY_INPUT =
      By.cssSelector(".v-window #endDate .v-textfield.v-datefield-textfield");
  public static final By ACTIVITY_DESCRIPTION = By.cssSelector(".v-window #description");
  public static final By ACTIVITY_TYPE_OF_ACTIVITY_COMBOBOX =
      By.cssSelector(".v-window #activityAsCaseType div");
  public static final By ACTIVITY_CONTINENT_COMBOBOX = By.cssSelector(".v-window #continent div");
  public static final By ACTIVITY_SUBCONTINENT_COMBOBOX =
      By.cssSelector(".v-window #subcontinent div");
  public static final By ACTIVITY_COUNTRY_COMBOBOX = By.cssSelector(".v-window #country div");
  public static final By ACTIVITY_DONE_BUTTON = By.cssSelector(".v-window #commit");
  public static final By ACTIVITY_DISCARD_BUTTON = By.cssSelector(".v-window #discard");
  public static final By SAVE_BUTTON_EPIDEMIOLOGICAL_DATA = By.id("commit");
  public static final By OPEN_SAVED_EXPOSURE_BUTTON =
      By.xpath("//div[contains(@id, 'de.symeda.sormas.api.exposure')]");
  public static final By OPEN_SAVED_ACTIVITY_BUTTON =
      By.xpath("//div[contains(@id, 'de.symeda.sormas.api.activityascase')]");
}
