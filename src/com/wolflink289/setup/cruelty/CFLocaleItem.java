package com.wolflink289.setup.cruelty;

import com.wolflink289.setup.cruelty.gui.WXListItem;

public class CFLocaleItem extends WXListItem {
	
	public final String locale;
	
	public CFLocaleItem(String locale) {
		super(locale, locale);
		this.locale = locale;
		
		// Set Localized Info
		String[] info = CFLocale.info(locale);
		set(info[0], info[1]);
	}
	
	@Override
	public String toString() {
		return locale;
	}
}
