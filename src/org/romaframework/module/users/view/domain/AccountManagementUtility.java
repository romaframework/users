package org.romaframework.module.users.view.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.romaframework.core.flow.ObjectContext;
import org.romaframework.module.admin.domain.CustomizableEntry;
import org.romaframework.module.users.UsersModule;
import org.romaframework.module.users.domain.BaseAccount;

/**
 * Utility class for the management of account and password through Spring injection Check of the password expiration period Check
 * for matching between password and regular expression Check of the account expiration period Check for previous password
 * 
 * @author F.Cuda
 * 
 */
public class AccountManagementUtility {

	public static final String	NAME_KEY					= "oldPw.";
	public static final long		DAY_MILLISECONDS	= 86400000;					// 1000 * 60 * 60 * 24

	/**
	 * Check if the password period has gone off
	 * 
	 * @return
	 */
	public static boolean isPasswordExpired(BaseAccount account) {
		Integer period = ObjectContext.getInstance().getComponent(UsersModule.class).getPasswordPeriod();
		if (period != null) {
			// current Date minus dateSignedOn in day
			if (Math.round(((new Date()).getTime() - account.getLastModifiedPassword().getTime()) / DAY_MILLISECONDS) > period)
				return true;
		}
		return false;
	}

	/**
	 * Check if the account period has gone off
	 * 
	 * @param account
	 * @return
	 */
	public static boolean isAccountExpired(BaseAccount account) {
		Integer period = ObjectContext.getInstance().getComponent(UsersModule.class).getAccountPeriod();
		if (period != null && account.getSignedOn() != null) {
			// current Date minus dateSignedOn in day
			if (Math.round(((new Date()).getTime() - account.getSignedOn().getTime()) / DAY_MILLISECONDS) > period)
				return true;
		}
		return false;
	}

	/**
	 * Methods allow you to match new password with regular expression setted inside BaseAccount
	 * 
	 * @param account
	 * @param password
	 * @return
	 */
	public static boolean isPasswordMathedRegExpression(String password) {
		List<String> regExpression = ObjectContext.getInstance().getComponent(UsersModule.class).getPasswordMatches();
		if (regExpression != null) {
			for (String curReg : regExpression)
				if (!curReg.equals("")) {
					if (!password.matches(curReg))
						return false;
				}
		}
		return true;
	}

	/**
	 * Discover if the password have been used before
	 * 
	 * @param account
	 * @param password
	 * @return
	 */
	public static boolean isPasswordUnused(BaseAccount account, String password) {
		Integer passwordMaxNumber = ObjectContext.getInstance().getComponent(UsersModule.class).getPasswordMaxNumber();

		if (passwordMaxNumber != null) {
			List<CustomizableEntry> entries = new ArrayList<CustomizableEntry>();
			Set<String> keyConfiguration = account.getConfiguration().keySet();
			for (String string : keyConfiguration) {
				if (string.startsWith(AccountManagementUtility.NAME_KEY)) {
					CustomizableEntry entry = account.getConfiguration().get(string);
					entries.add(entry);
				}
			}
			Collections.sort(entries, new Comparator<CustomizableEntry>() {
				@Override
				public int compare(CustomizableEntry o1, CustomizableEntry o2) {
					return o1.getDescription().compareTo(o2.getDescription());
				}
			});
			if (entries.size() > 0) {
				for (CustomizableEntry entry : entries) {
					if (entry.getValue().equals(password)) {
						return false;
					}
				}
				CustomizableEntry entry = new CustomizableEntry(AccountManagementUtility.NAME_KEY + password, password);
				entry.setDescription("0");
				account.getConfiguration().put(AccountManagementUtility.NAME_KEY + password, entry);
				for (CustomizableEntry curEntry : entries) {
					Integer curPos = new Integer(curEntry.getDescription());
					curPos++;
					if (curPos < passwordMaxNumber) {
						curEntry.setDescription(curPos.toString());
					} else {
						account.getConfiguration().remove(curEntry.getKey());
					}
				}
			} else {
				CustomizableEntry entry = new CustomizableEntry(AccountManagementUtility.NAME_KEY + password, password);
				entry.setDescription("0");
				account.getConfiguration().put(AccountManagementUtility.NAME_KEY + password, entry);
				return true;
			}
		}
		return true;
	}
}
