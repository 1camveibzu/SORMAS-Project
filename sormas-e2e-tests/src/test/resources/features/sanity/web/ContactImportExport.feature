@UI @Sanity @Contact @ContactImportExport
Feature: Contact import and export tests

  @issue=SORDEV-10043 @env_main
  Scenario: Contact basic export test
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Contacts button from navbar
    Then I filter by Contact uuid
    And I click on the Export contact button
    Then I click on the Basic Contact Export button
    Then I check if downloaded data generated by basic contact option is correct

  @issue=SORDEV-10046 @env_main
  Scenario: Contact custom export test
    When API: I create a new person
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Then API: I create a new contact
    Then API: I check that POST call body is "OK"
    And API: I check that POST call status code is 200
    Given I log in as a Admin User
    And I click on the Contacts button from navbar
    Then I filter by Contact uuid
    And I click on the Export contact button
    Then I click on the Custom Contact Export button
    And I click on the New Export Configuration button in Custom Contact Export popup
    Then I fill Configuration Name field with "Test Configuration" in Custom Contact Export popup
    And I select specific data to export in Export Configuration for Custom Contact Export
    When I download created custom contact export file
    And I delete created custom contact export file
    Then I check if downloaded data generated by custom contact option is correct

  @issue=SORDEV-6134 @env_de
  Scenario: Test Re-add primary phone number and primary email address to import templates for Contact
    Given I log in as a Admin User
    Then I click on the Contacts button from navbar
    And I click on the More button on Contact directory page
    And I click on the Import button from Contact directory
    Then I select the "TestContact_ImportPrio_template.csv" CSV file in the file picker
    And I click on the "DATENIMPORT STARTEN" button from the Import Contact popup
    Then I click to create new person from the Contact Import popup
    And I check that an import success notification appears in the Import Contact popup
    Then I close Import Contact form
    And I filter by "TestCase ImportPrio" as a Person's full name on Contact Directory Page
    And I click APPLY BUTTON in Contact Directory Page
    And I open the last created UI Contact
    Then I open Contact Person tab
    And I check that Primary telephone is visible on Edit Contact Person Page
    And I check that Primary email address is visible on Edit Contact Person Page
