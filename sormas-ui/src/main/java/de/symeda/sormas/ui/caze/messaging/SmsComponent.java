package de.symeda.sormas.ui.caze.messaging;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.CustomField;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;

public class SmsComponent extends CustomField<String> {

	private long missingPhoneNumbers;

	private TextArea smsTextArea;

	public SmsComponent(long missingPhoneNumbers) {
		this.missingPhoneNumbers = missingPhoneNumbers;
	}

	@Override
	protected Component initContent() {
		final VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSpacing(false);
		mainLayout.setSizeUndefined();
		mainLayout.setWidth(100, Unit.PERCENTAGE);

		if (missingPhoneNumbers > 0) {
			mainLayout.addComponent(
				new Label(
					VaadinIcons.INFO_CIRCLE.getHtml() + " "
						+ String.format(I18nProperties.getString(Strings.numberOfMissingPhoneNumbers), missingPhoneNumbers),
					ContentMode.HTML));
			mainLayout.addComponent(new Label());
		}

		mainLayout.addComponent(new Label(I18nProperties.getString(Strings.enterSMS)));

		smsTextArea = new TextArea();
		smsTextArea.setWidth(100, Unit.PERCENTAGE);
		smsTextArea.setRows(4);
		mainLayout.addComponent(smsTextArea);

		final HorizontalLayout charactersLayout = new HorizontalLayout();
		charactersLayout.setSpacing(false);
		charactersLayout.addComponent(new Label(I18nProperties.getString(Strings.characters)));
		charactersLayout.addComponent(new Label(" "));
		final Label characterLeftLabel = new Label("0");
		charactersLayout.addComponent(characterLeftLabel);
		charactersLayout.addComponent(new Label("/160"));
		mainLayout.addComponent(charactersLayout);
		mainLayout.setComponentAlignment(charactersLayout, Alignment.BOTTOM_RIGHT);

		final HorizontalLayout nrOfMessagesLayout = new HorizontalLayout();
		nrOfMessagesLayout.setSpacing(false);
		nrOfMessagesLayout.setMargin(false);
		nrOfMessagesLayout.addComponent(new Label(I18nProperties.getString(Strings.numberOfMessages)));
		nrOfMessagesLayout.addComponent(new Label(" "));
		final Label nrOfMessagesLabel = new Label("1");
		nrOfMessagesLayout.addComponent(nrOfMessagesLabel);

		mainLayout.addComponent(nrOfMessagesLayout);
		mainLayout.setComponentAlignment(nrOfMessagesLayout, Alignment.BOTTOM_RIGHT);

		smsTextArea.addValueChangeListener(e -> {
			final int nrOfCharacters = e.getValue().length();
			characterLeftLabel.setValue(String.valueOf(nrOfCharacters));
			nrOfMessagesLabel.setValue(String.valueOf(1 + nrOfCharacters / 160));
		});

		return mainLayout;
	}

	@Override
	public Class<? extends String> getType() {
		return String.class;
	}

	@Override
	public String getValue() {
		return smsTextArea.getValue();
	}
}
