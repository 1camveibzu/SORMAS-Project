package de.symeda.sormas.ui.utils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.shared.ui.combobox.FilteringMode;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.CustomField;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Month;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.DateHelper;

public abstract class AbstractForm<T> extends CustomField<T> {

	private static final long serialVersionUID = -3816438104870246080L;

	private static final Logger logger = LoggerFactory.getLogger(AbstractForm.class);

	protected final String propertyI18nPrefix;
	private final SormasBeanFieldGroup<T> fieldGroup;
	private Class<T> type;
	private List<Field<?>> customFields = new ArrayList<>();

	protected AbstractForm(Class<T> type, String propertyI18nPrefix, SormasFieldGroupFieldFactory fieldFactory, boolean addFields) {

		this.type = type;
		this.propertyI18nPrefix = propertyI18nPrefix;

		fieldGroup = new SormasBeanFieldGroup<>(type);

		fieldGroup.setFieldFactory(fieldFactory);
		setHeightUndefined();

		if (addFields) {
			addFields();
		}
	}

	protected abstract String createHtmlLayout();

	protected abstract void addFields();

	@Override
	public Class<? extends T> getType() {
		return type;
	}

	@Override
	protected CustomLayout getContent() {
		return (CustomLayout) super.getContent();
	}

	@Override
	public CustomLayout initContent() {

		String htmlLayout = createHtmlLayout();
		CustomLayout layout = new CustomLayout();
		layout.setTemplateContents(htmlLayout);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeightUndefined();

		return layout;
	}

	public BeanFieldGroup<T> getFieldGroup() {
		return this.fieldGroup;
	}

	public List<Field<?>> getCustomFields() {
		return customFields;
	}

	protected void addFields(String... properties) {
		for (String property : properties) {
			addField(property);
		}
	}

	protected void addFields(List<String> properties) {
		for (String property : properties) {
			addField(property);
		}
	}

	public void addFields(FieldConfiguration... properties) {
		addFields(getContent(), properties);
	}

	public void addFields(CustomLayout layout, String... properties) {
		for (String property : properties) {
			addField(layout, property);
		}
	}

	public void addFields(CustomLayout layout, FieldConfiguration... properties) {
		for (FieldConfiguration property : properties) {
			addField(layout, property);
		}
	}

	@SuppressWarnings("rawtypes")
	protected <F extends Field> F addField(FieldConfiguration configuration) {
		return addField(getContent(), configuration);
	}

	@SuppressWarnings({
		"rawtypes",
		"unchecked" })
	protected <F extends Field> F addField(FieldConfiguration configuration, Class<F> fieldType) {
		Field field = addField(getContent(), configuration.getPropertyId(), fieldType);

		applyFieldConfiguration(configuration, field);

		return (F) field;
	}

	@SuppressWarnings({
		"unchecked",
		"rawtypes" })
	protected <F extends Field> F addField(CustomLayout layout, FieldConfiguration configuration) {
		Field field = addField(layout, configuration.getPropertyId());

		applyFieldConfiguration(configuration, field);

		return (F) field;
	}

	@SuppressWarnings({
		"rawtypes",
		"unchecked" })
	protected <F extends Field> F addField(CustomLayout layout, Class<F> fieldType, FieldConfiguration configuration) {
		Field field = addField(layout, configuration.getPropertyId(), fieldType);

		applyFieldConfiguration(configuration, field);

		return (F) field;
	}

	@SuppressWarnings("rawtypes")
	protected void applyFieldConfiguration(FieldConfiguration configuration, Field field) {
		if (configuration.getWidth() != null) {
			field.setWidth(configuration.getWidth(), configuration.getWidthUnit());
		}

		if (configuration.getCaption() != null) {
			field.setCaption(configuration.getCaption());
		}

		if (configuration.getDescription() != null && field instanceof AbstractField) {
			((AbstractField) field).setDescription(configuration.getDescription());
		}

		if (configuration.getStyle() != null) {
			CssStyles.style(field, configuration.getStyle());
		}
	}

	@SuppressWarnings("rawtypes")
	protected <F extends Field> void addFields(Class<F> fieldType, String... properties) {
		for (String property : properties) {
			addField(property, fieldType);
		}
	}

	protected <F extends Field> void addFieldsWithCss(Class<F> fieldType, List<String> properties, String... cssClasses) {
		for (String property : properties) {
			F field = addField(property, fieldType);
			CssStyles.style(field, cssClasses);
		}
	}

	@SuppressWarnings("rawtypes")
	protected <F extends Field> void addFields(CustomLayout layout, Class<F> fieldType, String... properties) {
		for (String property : properties) {
			addField(layout, property, fieldType);
		}
	}

	@SuppressWarnings({
		"unchecked",
		"rawtypes" })
	protected <F extends Field> F addField(String propertyId) {
		return (F) addField(propertyId, Field.class);
	}

	@SuppressWarnings({
		"unchecked",
		"rawtypes" })
	protected <F extends Field> F addField(CustomLayout layout, String propertyId) {
		return (F) addField(layout, propertyId, Field.class);
	}

	@SuppressWarnings("rawtypes")
	protected <F extends Field> F addField(String propertyId, Class<F> fieldType) {
		return addField(getContent(), propertyId, fieldType);
	}

	protected <F extends Field> F addField(String propertyId, F field) {
		return addField(getContent(), propertyId, field);
	}

	@SuppressWarnings("rawtypes")
	protected <F extends Field> F addField(CustomLayout layout, String propertyId, Class<F> fieldType) {
		F field = createField(propertyId, fieldType);
		return addFieldToLayout(layout, propertyId, field);
	}

	protected <F extends Field> F addField(CustomLayout layout, String propertyId, F field) {
		getFieldGroup().bind(field, propertyId);

		return addFieldToLayout(layout, propertyId, field);
	}

	protected <F extends Field> F addFieldToLayout(CustomLayout layout, String propertyId, F field) {
		formatField(field, propertyId);
		field.setId(propertyId);
		layout.addComponent(field, propertyId);
		addDefaultAdditionalValidators(field, null);
		setDefaultAdditionalParameters(field);

		return field;
	}

	protected <T extends Field> T createField(String propertyId, Class<T> fieldType) {
		return getFieldGroup().buildAndBind(propertyId, (Object) propertyId, fieldType);
	}

	protected <F extends Field<?>> F addCustomField(FieldConfiguration fieldConfiguration, Class<?> dataType, Class<F> fieldType) {
		F field = addCustomField(fieldConfiguration.getPropertyId(), dataType, fieldType);

		field.setCaption(fieldConfiguration.getCaption());
		field.setStyleName(fieldConfiguration.getStyle());

		return field;
	}

	@SuppressWarnings("rawtypes")
	protected <F extends Field> F addField(String propertyId, Class<F> fieldType, FieldWrapper<F> fieldWrapper) {
		return addField(getContent(), propertyId, fieldType, fieldWrapper);
	}

	@SuppressWarnings("rawtypes")
	protected <F extends Field> F addField(CustomLayout layout, String propertyId, Class<F> fieldType, FieldWrapper<F> fieldWrapper) {
		F field = getFieldGroup().buildAndBind(propertyId, (Object) propertyId, fieldType);
		formatField(field, propertyId);
		field.setId(propertyId);
		// Add validators before wrapping field, so the wrapper can access validators
		addDefaultAdditionalValidators(field, null);
		setDefaultAdditionalParameters(field);
		layout.addComponent(fieldWrapper.wrap(field, Captions.numberOfCharacters), propertyId);
		return field;
	}

	@SuppressWarnings("rawtypes")
	protected <F extends Field> F addCustomField(String fieldId, Class<?> dataType, Class<F> fieldType) {
		F field = getFieldGroup().getFieldFactory().createField(dataType, fieldType);
		field.setId(fieldId);
		formatField(field, fieldId);
		addDefaultAdditionalValidators(field, dataType);
		setDefaultAdditionalParameters(field);
		getContent().addComponent(field, fieldId);
		customFields.add(field);
		return field;
	}

	@SuppressWarnings("rawtypes")
	protected <F extends Field> F addCustomField(String fieldId, String customCaption, Class<?> dataType, Class<F> fieldType) {
		F field = getFieldGroup().getFieldFactory().createField(dataType, fieldType);
		field.setId(fieldId);
		formatField(field, fieldId, customCaption);
		addDefaultAdditionalValidators(field, dataType);
		setDefaultAdditionalParameters(field);
		getContent().addComponent(field, fieldId);
		customFields.add(field);
		return field;
	}

	protected <F extends Field> void addCustomField(F field, String fieldId, String customCaption) {
		field.setId(fieldId);
		formatField(field, fieldId, customCaption);
		addDefaultAdditionalValidators(field, field.getType());
		setDefaultAdditionalParameters(field);
		getContent().addComponent(field, fieldId);
		customFields.add(field);
	}

	protected <F extends Field> F addCustomField(String fieldId, Class<?> dataType, Class<F> fieldType, String customCaption) {
		F field = getFieldGroup().getFieldFactory().createField(dataType, fieldType);
		field.setId(fieldId);
		formatField(field, fieldId, customCaption);

		addDefaultAdditionalValidators(field, dataType);
		setDefaultAdditionalParameters(field);
		getContent().addComponent(field, fieldId);
		customFields.add(field);
		return field;
	}

	@SuppressWarnings("rawtypes")
	protected <F extends Field> void formatField(F field, String propertyId) {
	}

	@SuppressWarnings("rawtypes")
	protected <F extends Field> void formatField(F field, String propertyId, String customCaption) {
	}

	@SuppressWarnings("rawtypes")
	/**
	 * @param allowedDaysInFuture
	 *            How many days in the future the value of this field can be or
	 *            -1 for no restriction at all
	 */
	protected <F extends Field> F addDateField(String propertyId, Class<F> fieldType, int allowedDaysInFuture) {
		F field = createField(propertyId, fieldType);
		formatField(field, propertyId);
		field.setId(propertyId);
		getContent().addComponent(field, propertyId);
		addFutureDateValidator(field, allowedDaysInFuture);
		return field;
	}

	public void addBirthDateFields(CustomLayout moreFiltersContainer, String yearPropertyId, String monthPropertyId, String dayPropertyId) {
		final ComboBox birthDateYYYY = addField(moreFiltersContainer, yearPropertyId, ComboBox.class);
		birthDateYYYY.setInputPrompt(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE_YYYY));
		birthDateYYYY.setWidth(140, Sizeable.Unit.PIXELS);
		birthDateYYYY.addItems(DateHelper.getYearsToNow());
		birthDateYYYY.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ID_TOSTRING);
		final ComboBox birthDateMM = addField(moreFiltersContainer, monthPropertyId, ComboBox.class);
		birthDateMM.setInputPrompt(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE_MM));
		birthDateMM.setWidth(140, Sizeable.Unit.PIXELS);
		birthDateMM.addItems(DateHelper.getMonthsInYear());
		DateHelper.getMonthsInYear().forEach(month -> birthDateMM.setItemCaption(month, Month.values()[month - 1].toString()));
		final ComboBox birthDateDD = addField(moreFiltersContainer, dayPropertyId, ComboBox.class);
		birthDateDD.setInputPrompt(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE_DD));
		birthDateDD.setWidth(140, Sizeable.Unit.PIXELS);
	}

	@SuppressWarnings("rawtypes")
	/**
	 * @param fieldDataType
	 *            - must be specified if the current FieldGroup does not know about the field
	 */
	protected <F extends Field> F addDefaultAdditionalValidators(F field, Class<?> fieldDataType) {
		addLengthValidator(field, fieldDataType);
		addMinMaxValidator(field, fieldDataType);
		addFutureDateValidator(field, 0);
		return field;
	}

	@SuppressWarnings("rawtypes")
	/**
	 * Add additional Parameters to fields, like setting the filteringMode for ComboBoxes
	 *
	 * @param field
	 */
	protected <F extends Field> F setDefaultAdditionalParameters(F field) {
		if (field instanceof ComboBox) {
			((ComboBox) field).setFilteringMode(FilteringMode.CONTAINS);
		}
		return field;
	}

	/**
	 * @param fieldDataType
	 *            - must be specified if the current FieldGroup does not know about the field
	 */
	private <F extends Field<?>> void addLengthValidator(F field, Class<?> fieldDataType) {
		final Class<?> typeOfFieldData;
		if (fieldDataType != null) {
			typeOfFieldData = fieldDataType;
		} else {
			typeOfFieldData = fieldGroup.getPropertyType(fieldGroup.getPropertyId(field));
		}

		if (typeOfFieldData.equals(String.class)) {
			Integer maxLength = getPropertyMaxLength(field.getId());
			if (maxLength != null) {
				field.addValidator(new MaxLengthValidator(maxLength));
			} else {
				final Class<?> fieldType = field.getClass();
				if (fieldType.isAssignableFrom(TextArea.class) || fieldType.isAssignableFrom(com.vaadin.v7.ui.TextArea.class)) {
					field.addValidator(new MaxLengthValidator(SormasFieldGroupFieldFactory.TEXT_AREA_MAX_LENGTH));
				} else if (fieldType.isAssignableFrom(TextField.class) || fieldType.isAssignableFrom(com.vaadin.v7.ui.TextField.class)) {
					field.addValidator(new MaxLengthValidator(SormasFieldGroupFieldFactory.TEXT_FIELD_MAX_LENGTH));
				}
			}
		}

	}

	private Integer getPropertyMaxLength(String propertyId) {
		Size sizeAnnotation = getPropertyAnnotation(propertyId, Size.class);
		return sizeAnnotation != null ? sizeAnnotation.max() : null;
	}

	private <T extends Annotation> T getPropertyAnnotation(String propertyId, Class<T> annotationType) {
		try {
			java.lang.reflect.Field field = type.getDeclaredField(propertyId);

			if (!field.isAnnotationPresent(annotationType)) {
				return null;
			}

			return field.getAnnotation(annotationType);
		} catch (NoSuchFieldException e) {
			return null;
		}
	}

	private <F extends Field<?>> void addMinMaxValidator(F field, Class<?> fieldDataType) {
		Max maxAnnotation = getPropertyAnnotation(field.getId(), Max.class);
		Long maxValue = maxAnnotation != null ? maxAnnotation.value() : null;

		Min minAnnotation = getPropertyAnnotation(field.getId(), Min.class);
		Long minValue = minAnnotation != null ? minAnnotation.value() : null;

		if (minValue != null || maxValue != null) {
			String validationMessageTag;
			Map<String, Object> validationMessageArgs = new HashMap<>();
			if (minValue == null) {
				validationMessageTag = Validations.numberTooBig;
				validationMessageArgs.put("value", maxValue);
			} else if (maxValue == null) {
				validationMessageTag = Validations.numberTooSmall;
				validationMessageArgs.put("value", minValue);
			} else {
				validationMessageTag = Validations.numberNotInRange;
				validationMessageArgs.put("min", minValue);
				validationMessageArgs.put("max", maxValue);
			}

			field.addValidator(
				new NumberValidator(I18nProperties.getValidationError(validationMessageTag, validationMessageArgs), minValue, maxValue));
		}
	}

	@SuppressWarnings("rawtypes")
	protected <F extends Field> F addFutureDateValidator(F field, int amountOfDays) {
		if (amountOfDays < 0) {
			return field;
		}

		if (DateField.class.isAssignableFrom(field.getClass()) || DateTimeField.class.isAssignableFrom(field.getClass())) {
			field.addValidator(new FutureDateValidator(field, amountOfDays, field.getCaption()));
		}

		return field;
	}

	public <T extends Field<?>> T getField(String fieldOrPropertyId) {
		Field<?> field = getFieldGroup().getField(fieldOrPropertyId);
		if (field == null) {
			// try to get the field from the layout
			Component component = getContent().getComponent(fieldOrPropertyId);
			if (component instanceof Field<?>) {
				field = (Field<?>) component;
			}
		}
		return (T) field;
	}

	@Override
	protected T getInternalValue() {
		BeanItem<T> beanItem = getFieldGroup().getItemDataSource();
		if (beanItem == null) {
			return null;
		} else {
			return beanItem.getBean();
		}
	}

	@Override
	protected void setInternalValue(T newValue) {
		super.setInternalValue(newValue);
		BeanFieldGroup<T> fieldGroup = getFieldGroup();
		fieldGroup.setItemDataSource(newValue);
	}

	protected String getPropertyI18nPrefix() {
		return propertyI18nPrefix;
	}

	protected boolean isConfiguredServer(String countryCode) {
		return FacadeProvider.getConfigFacade().isConfiguredCountry(countryCode);
	}

	private static class SormasBeanFieldGroup<T> extends BeanFieldGroup<T> {

		private static final long serialVersionUID = 7271384537227612872L;

		public SormasBeanFieldGroup(Class<T> beanType) {
			super(beanType);
		}

		@Override
		public Class<?> getPropertyType(Object propertyId) {
			return super.getPropertyType(propertyId);
		}

		@Override
		protected void configureField(Field<?> field) {

			field.setBuffered(isBuffered());
			if (!isEnabled()) {
				field.setEnabled(false);
			}

			if (field.getPropertyDataSource().isReadOnly() || isReadOnly()) {
				field.setReadOnly(true);
			}
		}
	}
}
