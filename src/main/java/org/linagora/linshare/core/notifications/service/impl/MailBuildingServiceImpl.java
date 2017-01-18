/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.notifications.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.MailActivationBusinessService;
import org.linagora.linshare.core.business.service.MailConfigBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.MailActivation;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.entities.MailFooter;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.UploadProposition;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainer;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.Recipient;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.config.LinShareStringTemplateResolver;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.dto.ContextMetadata;
import org.linagora.linshare.core.notifications.emails.impl.AnonymousShareEntryDownloadEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.EmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.NewGuestEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.NewSharingEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.ResetGuestPasswordEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.SharingAcknowledgementEmailBuilder;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.utils.DocumentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class MailBuildingServiceImpl implements MailBuildingService {

	private final static Logger logger = LoggerFactory
			.getLogger(MailBuildingServiceImpl.class);

	private final TemplateEngine templateEngine;

	private final Map<MailContentType, EmailBuilder> emailBuilders;

	private final boolean displayLogo;

	private final boolean insertLicenceTerm;

	private final DomainBusinessService domainBusinessService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final MailActivationBusinessService mailActivationBusinessService;

	private static final String LINSHARE_LOGO = "<img src='cid:image.part.1@linshare.org' /><br/><br/>";

	private final Map<Language, String> downloaded = Maps.newHashMap();

	private final Map<Language, String> notDownloaded = Maps.newHashMap();

	private final Map<Language, String> shareWith = Maps.newHashMap();

	private final Map<Language, String> anonymouslySharedWith = Maps.newHashMap();

	private final String  receivedSharesUrlSuffix;

	private final String  documentsUrlSuffix;

	private class FileRepresentation {

		private String name;

		public FileRepresentation(UploadRequestEntry entry) {
			super();
			this.name = entry.getName();
		}

		@Override
		public String toString() {
			return name;
		}
	}

	@SuppressWarnings("unused")
	private class Document {
		protected String uuid;
		protected String name;

		public Document(DocumentEntry de) {
			super();
			this.uuid = de.getUuid();
			this.name = de.getName();
		}

		public Document(String name) {
			super();
			this.name = name;
		}

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	@SuppressWarnings("unused")
	private class Share {
		protected String uuid;
		protected String name;
		protected boolean downloaded;
		protected Date creationDate;
		protected Date expirationDate;

		public Share(ShareEntry se) {
			super();
			this.uuid = se.getUuid();
			this.name = se.getName();
			this.downloaded = se.getDownloaded() > 0;
			this.creationDate = se.getCreationDate().getTime();
			this.expirationDate = se.getExpirationDate().getTime();
		}

		public Share(AnonymousShareEntry se) {
			super();
			this.uuid = se.getUuid();
			this.name = se.getName();
			this.downloaded = se.getDownloaded() > 0;
			this.creationDate = se.getCreationDate().getTime();
			this.expirationDate = se.getExpirationDate().getTime();
		}

		public Share(String name) {
			super();
			this.name = name;
		}

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isDownloaded() {
			return downloaded;
		}

		public void setDownloaded(boolean downloaded) {
			this.downloaded = downloaded;
		}

		public Date getCreationDate() {
			return creationDate;
		}

		public void setCreationDate(Date creationDate) {
			this.creationDate = creationDate;
		}

		public Date getExpirationDate() {
			return expirationDate;
		}

		public void setExpirationDate(Date expirationDate) {
			this.expirationDate = expirationDate;
		}
	}

	@SuppressWarnings("unused")
	private class ContactRepresentation {
		private String mail;
		private String firstName;
		private String lastName;

		public ContactRepresentation(Contact c) {
			this.mail = StringUtils.trimToNull(c.getMail());
			this.firstName = null;
			this.lastName = null;
		}

		public ContactRepresentation(User user) {
			this.mail = StringUtils.trimToNull(user.getMail());
			this.firstName = StringUtils.trimToNull(user.getFirstName());
			this.lastName = StringUtils.trimToNull(user.getLastName());
		}

		public String getContactRepresentation() {
			return getContactRepresentation(false);
		}

		public String getContactRepresentation(boolean includeMail) {
			if (this.firstName == null || this.lastName == null)
				return this.mail;
			StringBuilder res = new StringBuilder();
			res.append(firstName);
			res.append(" ");
			res.append(lastName);
			if (includeMail) {
				res.append(" (");
				res.append(mail);
				res.append(")");
			}
			return res.toString();
		}

		
		public String getMail() {
			return mail;
		}

		public void setMail(String mail) {
			this.mail = mail;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
	}

	/**
	 * XXX HACK
	 * 
	 * Helper using LinkedHashMap to chain the Key/Value substitution
	 * in mail templates.
	 * 
	 * @author nbertrand
	 */
	private class MailContainerBuilder {

		@SuppressWarnings("serial")
		private class KeyValueChain extends LinkedHashMap<String, String> {
			public KeyValueChain add(String key, String value) {
				logger.debug("Adding K/V pair: [" + key + ", " + value
						+ "]");
				super.put(key, StringUtils.defaultString(value));
				return this;
			}

			public String build(String input) {
				logger.debug("Building mail template.");
				logger.debug("\tinput: " + input);
				String ret = input;

				for (Map.Entry<String, String> e : entrySet()) {
					ret = StringUtils.replace(ret, "${" + e.getKey() + "}",
							e.getValue());
				}
				logger.debug("\tret: " + ret);
				return ret;
			}
		}

		private KeyValueChain subjectChain;
		private KeyValueChain greetingsChain;
		private KeyValueChain bodyChain;
		private KeyValueChain footerChain;
		private KeyValueChain layoutChain;

		public MailContainerBuilder() {
			super();
			subjectChain = new KeyValueChain();
			greetingsChain = new KeyValueChain();
			bodyChain = new KeyValueChain();
			footerChain = new KeyValueChain();
			layoutChain = new KeyValueChain();
		}

		public KeyValueChain getSubjectChain() {
			return subjectChain;
		}

		public KeyValueChain getGreetingsChain() {
			return greetingsChain;
		}

		public KeyValueChain getBodyChain() {
			return bodyChain;
		}

		public KeyValueChain getFooterChain() {
			return footerChain;
		}

		public KeyValueChain getLayoutChain() {
			return layoutChain;
		}
	}

	public MailBuildingServiceImpl(
			boolean displayLogo,
			final MailConfigBusinessService mailConfigBusinessService,
			final DomainBusinessService domainBusinessService,
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final MailActivationBusinessService mailActivationBusinessService,
			boolean insertLicenceTerm,
			String receivedSharesUrlSuffix,
			String documentsUrlSuffix,
			boolean templatingStrictMode,
			boolean templatingSubjectPrefix
			) throws Exception {
		this.displayLogo = displayLogo;
		this.domainBusinessService = domainBusinessService;
		this.insertLicenceTerm = insertLicenceTerm;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.mailActivationBusinessService = mailActivationBusinessService;
		this.downloaded.put(Language.ENGLISH, "DOWNLOADED");
		this.notDownloaded.put(Language.ENGLISH, "NOT DOWNLOADED");
		this.shareWith.put(Language.ENGLISH, "Shared with");
		this.anonymouslySharedWith.put(Language.ENGLISH, "Anonymously shared with");
		this.downloaded.put(Language.FRENCH, "TÉLÉCHARGÉ");
		this.notDownloaded.put(Language.FRENCH, "NON TÉLÉCHARGÉ");
		this.shareWith.put(Language.FRENCH, "Partagé avec");
		this.anonymouslySharedWith.put(Language.FRENCH, "Partagé anonymement avec");
		this.receivedSharesUrlSuffix = receivedSharesUrlSuffix;
		this.documentsUrlSuffix = documentsUrlSuffix;

		this.templateEngine = new TemplateEngine();
		LinShareStringTemplateResolver templateResolver = new LinShareStringTemplateResolver(insertLicenceTerm, templatingSubjectPrefix);
		if (templatingStrictMode) {
			templateResolver.setTemplateMode(TemplateMode.XML);
		}
		templateEngine.setTemplateResolver(templateResolver);

		emailBuilders = Maps.newHashMap();
		emailBuilders.put(MailContentType.NEW_SHARING, new NewSharingEmailBuilder());
		emailBuilders.put(MailContentType.NEW_GUEST, new NewGuestEmailBuilder());
		emailBuilders.put(MailContentType.ANONYMOUS_DOWNLOAD, new AnonymousShareEntryDownloadEmailBuilder());
		emailBuilders.put(MailContentType.RESET_PASSWORD, new ResetGuestPasswordEmailBuilder());
		emailBuilders.put(MailContentType.SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER, new SharingAcknowledgementEmailBuilder());
		initMailBuilders();
	}

	private void initMailBuilders() {
		Collection<EmailBuilder> values = emailBuilders.values();
		for (EmailBuilder emailBuilder : values) {
			emailBuilder.setTemplateEngine(templateEngine);
			emailBuilder.setInsertLicenceTerm(insertLicenceTerm);
			emailBuilder.setMailActivationBusinessService(mailActivationBusinessService);
			emailBuilder.setFunctionalityReadOnlyService(functionalityReadOnlyService);
			emailBuilder.setDomainBusinessService(domainBusinessService);
			emailBuilder.setDocumentsUrlSuffix(documentsUrlSuffix);
			emailBuilder.setReceivedSharesUrlSuffix(receivedSharesUrlSuffix);
		}
	}

	@Override
	public MailContainerWithRecipient build(EmailContext context) throws BusinessException {
		Validate.notNull(context, "Email context can't be null");
		MailContentType type = context.getType();
		EmailBuilder builder = emailBuilders.get(type);
		Validate.notNull(builder, "Missing email builder!");
		return builder.build(context);
	}

	@Override
	public MailContainerWithRecipient fakeBuild(MailContentType type, MailConfig cfg, Language language) throws BusinessException {
		Validate.notNull(type, "MailContentType can't be null");
		if (cfg != null) {
			cfg = this.domainBusinessService.getUniqueRootDomain().getCurrentMailConfiguration();
		}
		if (language == null) {
			language = Language.FRENCH;
		}
		EmailBuilder builder = emailBuilders.get(type);
		cfg.findContent(language, type);
		return builder.fakeBuild(cfg, language);
	}

	@Override
	public ContextMetadata getAvailableVariables(MailContentType type) {
		Validate.notNull(type, "MailContentType can't be null");
		EmailBuilder builder = emailBuilders.get(type);
		return builder.getAvailableVariables();
	}

	private String formatCreationDate(Account account, Entry entry) {
		Locale locale = account.getJavaExternalMailLocale();
		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, locale);
		return formatter.format(entry.getCreationDate().getTime());
	}

	private String formatDeletionDate(Account account) {
		Locale locale = account.getJavaExternalMailLocale();
		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, locale);
		return formatter.format(new Date());
	}

	private String formatCreationDate(Account account, UploadRequest uploadRequest) {
		Locale locale = account.getJavaExternalMailLocale();
		DateFormat formatter = DateFormat.getDateInstance(DateFormat.FULL, locale);
		return formatter.format(uploadRequest.getCreationDate().getTime());
	}

	private String formatActivationDate(Account account, UploadRequest uploadRequest) {
		Locale locale = account.getJavaExternalMailLocale();
		DateFormat formatter = DateFormat.getDateInstance(DateFormat.FULL, locale);
		return formatter.format(uploadRequest.getActivationDate().getTime());
	}

	private String formatExpirationDate(Account account,
			UploadRequest uploadRequest) {
		if (uploadRequest.getExpiryDate() != null) {
			Locale locale = account.getJavaExternalMailLocale();
			DateFormat formatter = DateFormat.getDateInstance(DateFormat.FULL,
					locale);
			return formatter.format(uploadRequest.getExpiryDate().getTime());
		}
		return "";
	}

	@Override
	public MailContainerWithRecipient buildSharedDocUpdated(
			Entry shareEntry, String oldDocName, long size) throws BusinessException {
		/*
		 * XXX ugly
		 */
		User sender = (User) shareEntry.getEntryOwner();
		String actorRepresentation = new ContactRepresentation(sender)
				.getContactRepresentation();
		String url, firstName, lastName, mimeType, fileName, recipient; // ugly
		Language locale;
		if (shareEntry instanceof AnonymousShareEntry) {
			AnonymousShareEntry e = (AnonymousShareEntry) shareEntry;
			if (isDisable(e.getAnonymousUrl().getContact(), sender, MailActivationType.SHARED_DOC_UPDATED)) {
				return null;
			}
			url = e.getAnonymousUrl()
					.getFullUrl(getLinShareUrlForAContactRecipient(sender));
			recipient = e.getAnonymousUrl().getContact().getMail();
			locale = sender.getExternalMailLocale();
			firstName = "";
			lastName = recipient;
			mimeType = e.getDocumentEntry().getType();
			fileName = e.getDocumentEntry().getName();
		} else {
			ShareEntry e = (ShareEntry) shareEntry;
			if (isDisable(e.getRecipient(), MailActivationType.SHARED_DOC_UPDATED)) {
				return null;
			}
			url = getLinShareUrlForAUserRecipient(
					e.getRecipient());
			recipient = e.getRecipient().getMail();
			locale = e.getRecipient().getExternalMailLocale();
			firstName = e.getRecipient().getFirstName();
			lastName = e.getRecipient().getLastName();
			mimeType = e.getDocumentEntry().getType();
			fileName = e.getDocumentEntry().getName();
		}

		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				locale);
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", actorRepresentation);
		builder.getGreetingsChain()
				.add("firstName", firstName)
				.add("lastName", lastName);
		builder.getBodyChain()
				.add("firstName", sender.getFirstName())
				.add("lastName", sender.getLastName())
				.add("fileName", fileName)
				.add("fileSize", DocumentUtils.humanReadableByteCount(size, false, locale))
				.add("fileOldName", oldDocName)
				.add("mimeType", mimeType)
				.add("url", url)
				.add("urlparam", "");
		container.setRecipient(recipient);
		container.setFrom(getFromMailAddress(sender));
		container.setReplyTo(sender.getMail());

		return buildMailContainer(cfg, container, null,
				MailContentType.SHARED_DOC_UPDATED, builder);
	}

	@Override
	public MailContainerWithRecipient buildSharedDocDeleted(Account actor,
			Entry shareEntry) throws BusinessException {
		/*
		 * XXX very very ugly
		 */
		User sender = (User) shareEntry.getEntryOwner();
		String actorRepresentation = new ContactRepresentation(sender)
				.getContactRepresentation();
		String firstName, lastName, fileName, recipient; // ugly
		Language locale;
		if (shareEntry instanceof AnonymousShareEntry) {
			AnonymousShareEntry e = (AnonymousShareEntry) shareEntry;
			recipient = e.getAnonymousUrl().getContact().getMail();
			if (isDisable(e.getAnonymousUrl().getContact(), sender,
					MailActivationType.SHARED_DOC_DELETED)) {
				return null;
			}
			locale = sender.getExternalMailLocale();
			firstName = "";
			lastName = recipient;
			fileName = e.getDocumentEntry().getName();
		} else {
			ShareEntry e = (ShareEntry) shareEntry;
			recipient = e.getRecipient().getMail();
			if (isDisable(e.getRecipient(), MailActivationType.SHARED_DOC_DELETED)) {
				return null;
			}
			locale = e.getRecipient().getExternalMailLocale();
			firstName = e.getRecipient().getFirstName();
			lastName = e.getRecipient().getLastName();
			fileName = e.getDocumentEntry().getName();
		}

		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				locale);
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", actorRepresentation);
		builder.getGreetingsChain()
				.add("firstName", firstName)
				.add("lastName", lastName);
		builder.getBodyChain()
				.add("firstName", sender.getFirstName())
				.add("lastName", sender.getLastName())
				.add("documentName", fileName);
		container.setRecipient(recipient);
		container.setFrom(getFromMailAddress(sender));
		container.setReplyTo(sender.getMail());
		return buildMailContainer(cfg, container, null,
				MailContentType.SHARED_DOC_DELETED, builder);
	}

	@Override
	public MailContainerWithRecipient buildSharedDocUpcomingOutdated(
			Entry shareEntry, Integer days) throws BusinessException {
		/*
		 * XXX very very ugly
		 */
		User sender = (User) shareEntry.getEntryOwner();
		String actorRepresentation = new ContactRepresentation(sender)
				.getContactRepresentation();
		String url, firstName, lastName, fileName, recipient; // ugly
		Language locale;
		if (shareEntry instanceof AnonymousShareEntry) {
			AnonymousShareEntry e = (AnonymousShareEntry) shareEntry;
			recipient = e.getAnonymousUrl().getContact().getMail();
			if (isDisable(e.getAnonymousUrl().getContact(), sender,
					MailActivationType.SHARED_DOC_UPCOMING_OUTDATED)) {
				return null;
			}
			url = e.getAnonymousUrl()
					.getFullUrl(getLinShareUrlForAContactRecipient(sender));
			locale = sender.getExternalMailLocale();
			firstName = "";
			lastName = recipient;
			fileName = e.getDocumentEntry().getName();
		} else {
			ShareEntry e = (ShareEntry) shareEntry;
			if (isDisable(e.getRecipient(), MailActivationType.SHARED_DOC_UPCOMING_OUTDATED)) {
				return null;
			}
			url = getLinShareUrlForAUserRecipient(
					e.getRecipient());
			recipient = e.getRecipient().getMail();
			locale = e.getRecipient().getExternalMailLocale();
			firstName = e.getRecipient().getFirstName();
			lastName = e.getRecipient().getLastName();
			fileName = e.getDocumentEntry().getName();
		}

		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				locale);
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", actorRepresentation);
		builder.getGreetingsChain()
				.add("firstName", firstName)
				.add("lastName", lastName);
		builder.getBodyChain()
				.add("firstName", sender.getFirstName())
				.add("lastName", sender.getLastName())
				.add("documentName", fileName)
				.add("nbDays", days.toString())
				.add("url", url)
				.add("urlparam", "");
		container.setRecipient(recipient);
		container.setFrom(getFromMailAddress(sender));
		container.setReplyTo(sender.getMail());

		return buildMailContainer(cfg, container, null,
				MailContentType.SHARED_DOC_UPCOMING_OUTDATED, builder);
	}

	// TODO : To be used : method is not called.
	@Override
	public MailContainerWithRecipient buildDocUpcomingOutdated(
			DocumentEntry document, Integer days) throws BusinessException {
		User owner = (User) document.getEntryOwner();
		if (isDisable(owner, MailActivationType.DOC_UPCOMING_OUTDATED)) {
			return null;
		}
		String actorRepresentation = new ContactRepresentation(owner)
				.getContactRepresentation();
		String url = getLinShareUrlForAUserRecipient(owner);

		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				owner.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", actorRepresentation);
		builder.getGreetingsChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName());
		builder.getBodyChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName())
				.add("documentName", document.getName())
				.add("nbDays", days.toString())
				.add("url", url)
				.add("urlparam", "");
		container.setRecipient(owner.getMail());
		container.setFrom(getFromMailAddress(owner));

		return buildMailContainer(cfg, container, null,
				MailContentType.DOC_UPCOMING_OUTDATED, builder);
	}


	@Override
	public MailContainerWithRecipient buildNoDocumentHasBeenDownloadedAcknowledgement(
			ShareEntryGroup shareEntryGroup) throws BusinessException {
		if (isDisable(shareEntryGroup.getOwner(),
				MailActivationType.UNDOWNLOADED_SHARED_DOCUMENTS_ALERT)) {
			return null;
		}
		User owner = (User) shareEntryGroup.getOwner();
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				owner.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();
		Language userLocale = owner.getExternalMailLocale();
		DateFormat df = DateFormat.getDateInstance(DateFormat.FULL,
				Locale.forLanguageTag(userLocale.getTapestryLocale()));
		String creationDate = df.format(shareEntryGroup.getCreationDate());
		String expirationDate = "";
		if (shareEntryGroup.getExpirationDate() != null) {
			expirationDate = df.format(shareEntryGroup.getExpirationDate());
		}
		StringBuffer shareInfoBuffer = new StringBuffer();
		Map<DocumentEntry, List<Entry>> tmpDocuments = shareEntryGroup
				.getTmpDocuments();
		for (Map.Entry<DocumentEntry, List<Entry>> tmpDocument : tmpDocuments
				.entrySet()) {
			DocumentEntry documentEntry = tmpDocument.getKey();
			boolean oneShareWasDownloaded = oneShareWasDownloaded(documentEntry, shareEntryGroup);
			boolean allSharesWereDownloaded = allSharesWereDownloaded(documentEntry, shareEntryGroup);
			shareInfoBuffer
					.append("<tr style='height: 10px;' ></tr>")
					.append("<tr style='border-bottom: 1px solid black;'>")
					.append("<td style='margin-bottom: 5px; padding-bottom: 3px; padding-top: 10px;'>")
					.append("<a style='text-decoration: none; font-size: 13px;' href='")
					.append(getOwnerDocumentLink(owner, documentEntry))
					.append("'>")
					.append(documentEntry.getName())
					.append("</a> :")
					.append("</td>")
					.append("<td style='padding: 5px 0px; width: 130px;'>")
					.append(flagDocumentDownloadedOrNotForUSDA(userLocale, oneShareWasDownloaded, allSharesWereDownloaded, false))
					.append("</td>")
					.append("</tr>");
			for (Entry entry : tmpDocuments.get(documentEntry)) {
				shareInfoBuffer.append(flagShareRowOrNotForUSDA(entry, userLocale, oneShareWasDownloaded, allSharesWereDownloaded));
			}
				shareInfoBuffer.append("<tr style='height: 5px;' ></tr>");
		}
		builder.getSubjectChain().add("subject", shareEntryGroup.getSubject())
				.add("date", creationDate);
		builder.getGreetingsChain().add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName());
		builder.getBodyChain().add("creationDate", creationDate)
				.add("expirationDate", expirationDate)
				.add("shareInfo", shareInfoBuffer.toString());
		container.setSubject(shareEntryGroup.getSubject());
		container.setRecipient(owner.getMail());
		container.setFrom(getFromMailAddress((User) owner));

		return buildMailContainer(cfg, container, "",
				MailContentType.UNDOWNLOADED_SHARED_DOCUMENT_ALERT, builder);
	}

	private boolean allSharesWereDownloaded(DocumentEntry documentEntry,
			ShareEntryGroup shareEntryGroup) {
		Map<DocumentEntry, Boolean> map = shareEntryGroup
				.getTmpAllSharesWereNotDownloaded();
		boolean allSharesWereDownloaded = false;
		if (map.get(documentEntry) == null) {
			allSharesWereDownloaded = true;
		}
		return allSharesWereDownloaded;
	}

	private Boolean oneShareWasDownloaded(DocumentEntry documentEntry,
			ShareEntryGroup shareEntryGroup) {
		Boolean wasDownloaded = false;
		Map<DocumentEntry, Boolean> map = shareEntryGroup
				.getTmpDocumentsWereDownloaded();
		if (map.size() > 0) {
			wasDownloaded = map.get(documentEntry);
			if (wasDownloaded == null) {
				wasDownloaded = false;
			}
		}
		return wasDownloaded;
	}

	private String flagShareRowOrNotForUSDA(Entry entry, Language userLocale, boolean oneShareWasDownloaded, boolean allSharesWereDownloaded) {
		StringBuffer sb = new StringBuffer();
		sb.append("<tr>");
		sb.append("<td style='padding: 5px 40px 5px 10px;'>");
		if (entry instanceof ShareEntry) {
			ShareEntry share = (ShareEntry) entry;
			boolean shareDownloaded = share.getDownloaded() > 0;
			sb.append(shareWith.get(userLocale))
			.append(" : <b>")
			.append(share.getRecipient().getFullName())
			.append("</b>  (")
			.append(share.getRecipient().getMail() + ")")
			.append("</td>")
			.append("<td>")
			.append(flagShareDownloadedOrNotForUSDA(oneShareWasDownloaded, shareDownloaded, userLocale, allSharesWereDownloaded));
		} else {
			AnonymousShareEntry aShare = (AnonymousShareEntry) entry;
			boolean shareDownloaded = aShare.getDownloaded() > 0;
			sb.append(anonymouslySharedWith.get(userLocale))
			.append(" : <b>")
			.append(aShare.getAnonymousUrl().getContact().getMail())
			.append("</b>")
			.append("</td>")
			.append("<td>")
			.append(flagShareDownloadedOrNotForUSDA(oneShareWasDownloaded, shareDownloaded, userLocale, allSharesWereDownloaded));
		}
		sb.append("</td>");
		sb.append("</tr>");
		return sb.toString();
	}

	private String flagShareDownloadedOrNotForUSDA(
			boolean oneShareWasDownloaded, boolean shareDownloaded,
			Language userLocale, boolean allSharesWereDownloaded) {
		String res = "";
		if (!allSharesWereDownloaded) {
			// check if there is at least one download
			if (oneShareWasDownloaded) {
				if (shareDownloaded) {
					res = downloadedFlag(userLocale);
				} else {
					res = unDownloadedFlag(userLocale, true);
				}
			}
		}
		return res;
	}

	private String flagDocumentDownloadedOrNotForUSDA(Language userLocale,
			boolean oneShareWasDownloaded, boolean allSharesWereDownloaded,
			boolean warning) {
		String res = "";
		if (allSharesWereDownloaded) {
			res = downloadedFlag(userLocale);
		} else {
			if (!oneShareWasDownloaded) {
				// No download at all.
				res = unDownloadedFlag(userLocale, false);
			}
		}
		return res;
	}



	private String unDownloadedFlag(Language userLocale, boolean warning) {
		StringBuffer sb = new StringBuffer();
		sb.append("<h5 ")
			.append("style='")
			.append(" display: inline-block; color: white;")
			.append(" padding: 3px 0px;")
			.append(" margin: 0px; width: 100%; text-align: center;");
			if (warning) {
				// Orange
				sb.append(" background-color: #FF8C00;");
				sb.append("' >");
				sb.append(notDownloaded.get(userLocale));
			} else {
				// Red
				sb.append(" background-color: #EE0037;");
				sb.append("' >");
				sb.append(notDownloaded.get(userLocale));
			}
		sb.append("</h5>");
		return sb.toString();
	}

	private String downloadedFlag(Language userLocale) {
		StringBuffer sb = new StringBuffer();
		sb.append("<h5 ")
			.append("style='")
			.append(" display: inline-block; color: white;")
			.append(" padding: 3px 0px;")
			.append(" background-color: #00A114;")
			.append(" margin: 0px; width: 100%; text-align: center;")
			.append("' >")
			.append(downloaded.get(userLocale))
			.append("</h5>");
		return sb.toString();
	}

	private MailContainerWithRecipient buildNewAnonymousSharing(User sender, MailContainer input,
			AnonymousUrl anonUrl, MailContentType mailContentType) throws BusinessException {
		if (isDisable(anonUrl.getContact(), sender, MailActivationType.NEW_SHARING)) {
			return null;
		}
		String actorRepresentation = new ContactRepresentation(sender)
				.getContactRepresentation();
		String url = anonUrl
				.getFullUrl(getLinShareUrlForAContactRecipient(sender));
		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				sender.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();
		StringBuffer names = new StringBuffer();
		for (String n : anonUrl.getDocumentNames()) {
			names.append("<li>" + n + "</li>");
		}
		builder.getSubjectChain()
				.add("actorSubject", input.getSubject())
				.add("actorRepresentation", actorRepresentation);
		builder.getGreetingsChain()
				.add("firstName", "")
				.add("lastName", anonUrl.getContact().getMail());
		builder.getBodyChain()
				.add("firstName", sender.getFirstName())
				.add("lastName", sender.getLastName())
				.add("number", "" + anonUrl.getDocumentNames().size())
				.add("documentNames", names.toString())
				.add("password", anonUrl.getTemporaryPlainTextPassword())
				.add("jwsEncryptUrl", getJwsEncryptUrlString(getLinShareUrlForAContactRecipient(sender)))
				.add("url", url)
				.add("urlparam", "");
		container.setSubject(input.getSubject());
		container.setRecipient(anonUrl.getContact());
		container.setFrom(getFromMailAddress(sender));
		container.setReplyTo(sender.getMail());

		return buildMailContainer(cfg, container, input.getPersonalMessage(),
				mailContentType, builder);
	}

	@Override
	public MailContainerWithRecipient buildNewSharing(User sender,
			MailContainer input, AnonymousUrl anonUrl) throws BusinessException {
		return buildNewAnonymousSharing(sender, input, anonUrl,
				MailContentType.NEW_SHARING);
	}

	@Override
	public MailContainerWithRecipient buildNewSharingProtected(User sender,
			MailContainer input, AnonymousUrl anonUrl) throws BusinessException {
		return buildNewAnonymousSharing(sender, input, anonUrl,
				MailContentType.NEW_SHARING_PROTECTED);
	}

	@Override
	public MailContainerWithRecipient buildNewSharingCyphered(
			User sender, MailContainer input, AnonymousUrl anonUrl)
			throws BusinessException {
		return buildNewAnonymousSharing(sender, input, anonUrl,
				MailContentType.NEW_SHARING_CYPHERED);
	}

	@Override
	public MailContainerWithRecipient buildNewSharingCypheredProtected(
			User sender, MailContainer input, AnonymousUrl anonUrl)
			throws BusinessException {
		return buildNewAnonymousSharing(sender, input, anonUrl,
				MailContentType.NEW_SHARING_CYPHERED_PROTECTED);
	}

	@Override
	public MailContainerWithRecipient buildNewSharingCyphered(User sender,
			MailContainer input, User recipient,
			Set<ShareEntry> shares) throws BusinessException {
		if (isDisable(recipient, MailActivationType.NEW_SHARING)) {
			return null;
		}
		String actorRepresentation = new ContactRepresentation(sender)
				.getContactRepresentation();
		String url = getLinShareUrlForAUserRecipient(recipient);

		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				recipient.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		StringBuffer names = new StringBuffer();
		long shareSize = 0;
		for (ShareEntry share : shares) {
			if (recipient.getLsUuid().equals(share.getRecipient().getLsUuid())) {
				shareSize += 1;
				names.append("<li><a href='"
						+ getReceivedSharedFileDownloadLink(recipient, share) + "'>"
						+ share.getName() + "</a></li>");
			}
		}

		builder.getSubjectChain()
				.add("actorSubject", input.getSubject())
				.add("actorRepresentation", actorRepresentation);
		builder.getGreetingsChain()
				.add("firstName", recipient.getFirstName())
				.add("lastName", recipient.getLastName());
		builder.getBodyChain()
				.add("firstName", sender.getFirstName())
				.add("lastName", sender.getLastName())
				.add("number", "" + shareSize)
				.add("documentNames", names.toString())
				.add("jwsEncryptUrl", getJwsEncryptUrlString(url))
				.add("url", url)
				.add("urlparam", "");
		container.setSubject(input.getSubject());
		container.setRecipient(recipient);
		container.setFrom(getFromMailAddress(sender));
		container.setReplyTo(sender.getMail());

		return buildMailContainer(cfg, container,
				input.getPersonalMessage(),
				MailContentType.NEW_SHARING_CYPHERED, builder);
	}

	@Override
	public MailContainerWithRecipient buildCreateUploadProposition(User recipient, UploadProposition proposition)
			throws BusinessException {
		if (isDisable(recipient, MailActivationType.UPLOAD_PROPOSITION_CREATED)) {
			return null;
		}
		MailConfig cfg = recipient.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				recipient.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", proposition.getMail())
				.add("subject", proposition.getSubject());
		builder.getGreetingsChain()
				.add("firstName", recipient.getFirstName())
				.add("lastName", recipient.getLastName());
		builder.getBodyChain()
				.add("subject", proposition.getSubject())
				.add("body", proposition.getBody())
				.add("firstName", proposition.getFirstName())
				.add("lastName", proposition.getLastName())
				.add("mail", proposition.getMail())
				.add("uploadPropositionUrl", getUploadPropositionUrl(recipient));
		container.setRecipient(recipient.getMail());
		container.setFrom(getFromMailAddress(recipient));

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_PROPOSITION_CREATED, builder);
	}

	@Override
	public MailContainerWithRecipient buildRejectUploadProposition(User sender, UploadProposition proposition)
			throws BusinessException {
		if (isDisable(sender, MailActivationType.UPLOAD_PROPOSITION_REJECTED)) {
			return null;
		}
		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				sender.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", new ContactRepresentation(sender).getContactRepresentation())
				.add("subject", proposition.getSubject());
		builder.getGreetingsChain()
				.add("firstName", proposition.getFirstName())
				.add("lastName", proposition.getLastName());
		builder.getBodyChain()
				.add("subject", proposition.getSubject())
				.add("body", proposition.getBody())
				.add("firstName", sender.getFirstName())
				.add("lastName", sender.getLastName())
				.add("mail", proposition.getMail());

		container.setRecipient(proposition.getMail());
		container.setFrom(getFromMailAddress(sender));

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_PROPOSITION_REJECTED, builder);
	}

	// Update
	@Override
	public MailContainerWithRecipient buildUpdateUploadRequest(User owner, UploadRequestUrl request)
			throws BusinessException {
		if (isDisable(request.getContact(), owner, MailActivationType.UPLOAD_REQUEST_UPDATED)) {
			return null;
		}
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", new ContactRepresentation(owner).getContactRepresentation())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", request.getContact().getMail())
				.add("lastName", "");
		builder.getBodyChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody());
		container.setRecipient(request.getContact());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_UPDATED, builder);
	}

	@Override
	public MailContainerWithRecipient buildActivateUploadRequest(User owner, UploadRequestUrl request)
			throws BusinessException {
		if (isDisable(request.getContact(), owner, MailActivationType.UPLOAD_REQUEST_ACTIVATED)) {
			return null;
		}
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", new ContactRepresentation(owner).getContactRepresentation())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", request.getContact().getMail())
				.add("lastName", "");
		//  Why first name and last name ?
		builder.getBodyChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody())
				.add("url", request.getFullUrl(getLinShareUploadRequestUrl(owner)))
				.add("expirationDate", formatExpirationDate(owner, request.getUploadRequest()))
				.add("ownerFirstName", owner.getFirstName())
				.add("ownerLastName", owner.getLastName())
				.add("ownerMail", owner.getMail())
				.add("maxFileCount", request.getUploadRequest().getMaxFileCount().toString())
				.add("password", request.getTemporaryPlainTextPassword());
		container.setRecipient(request.getContact());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);
		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_ACTIVATED, builder);
	}

	private String getFromMailAddress(User owner) {
		String fromMail = functionalityReadOnlyService
				.getDomainMailFunctionality(owner.getDomain()).getValue();
		return fromMail;
	}

	// TODO : to be used ?
	@Override
	public MailContainerWithRecipient buildFilterUploadRequest(User owner, UploadRequestUrl request)
			throws BusinessException {
		if (isDisable(request.getContact(), owner, MailActivationType.UPLOAD_REQUEST_AUTO_FILTER)) {
			return null;
		}
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName());
		builder.getBodyChain()
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody());
		container.setRecipient(request.getContact());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_AUTO_FILTER, builder);
	}

	@Override
	public MailContainerWithRecipient buildCreateUploadRequest(User owner, UploadRequestUrl request)
			throws BusinessException {
		if (isDisable(request.getContact(), owner, MailActivationType.UPLOAD_REQUEST_CREATED)) {
			return null;
		}
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", new ContactRepresentation(owner).getContactRepresentation())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", request.getContact().getMail())
				.add("lastName", "");
		builder.getBodyChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody())
				.add("expirationDate", formatExpirationDate(owner, request.getUploadRequest()))
				.add("ownerFirstName", owner.getFirstName())
				.add("ownerLastName", owner.getLastName())
				.add("ownerMail", owner.getMail())
				.add("maxFileCount", request.getUploadRequest().getMaxFileCount().toString())
				.add("activationDate", formatActivationDate(owner, request.getUploadRequest()));
		container.setRecipient(request.getContact());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);

		return buildMailContainer(cfg, container, null,
				MailContentType.UPLOAD_REQUEST_CREATED, builder);
	}

	@Override
	public MailContainerWithRecipient buildAckUploadRequest(User owner, UploadRequestUrl request, UploadRequestEntry entry)
			throws BusinessException {
		if (isDisable(owner, MailActivationType.UPLOAD_REQUEST_ACKNOWLEDGEMENT)) {
			return null;
		}
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				owner.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		String contact = request.getContact().getMail();
		builder.getSubjectChain()
				.add("actorRepresentation", contact)
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName());
		builder.getBodyChain()
				.add("firstName", contact)
				.add("lastName", "")
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody())
				.add("fileSize", DocumentUtils.humanReadableByteCount(entry.getDocumentEntry().getSize(), false, owner.getExternalMailLocale()))
				.add("fileName", entry.getDocumentEntry().getName())
				.add("depositDate", formatCreationDate(owner, entry));
		container.setRecipient(owner.getMail());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(contact);
		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_ACKNOWLEDGMENT, builder);
	}

	@Override
	public MailContainerWithRecipient buildAckDeleteFileUploadRequest(User owner, UploadRequestUrl request, UploadRequestEntry entry)
			throws BusinessException {
		if (isDisable(owner, MailActivationType.UPLOAD_REQUEST_FILE_DELETED_BY_SENDER)) {
			return null;
		}
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		String contact = request.getContact().getMail();
		builder.getSubjectChain()
				.add("actorRepresentation", contact)
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName());
		builder.getBodyChain()
				.add("firstName", contact)
				.add("lastName", "")
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody())
				.add("fileSize", DocumentUtils.humanReadableByteCount(entry.getSize(), false, owner.getExternalMailLocale()))
				.add("fileName", entry.getName())
				.add("deleteDate", formatDeletionDate(owner));
		container.setRecipient(owner.getMail());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(contact);
		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_FILE_DELETED_BY_SENDER, builder);
	}

	@Override
	public MailContainerWithRecipient buildRemindUploadRequest(User owner, UploadRequestUrl request)
			throws BusinessException {
		if (isDisable(request.getContact(), owner, MailActivationType.UPLOAD_REQUEST_REMINDER)) {
			return null;
		}
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", new ContactRepresentation(owner).getContactRepresentation())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", request.getContact().getMail())
				.add("lastName", "");
		builder.getBodyChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody())
				.add("url", request.getFullUrl(getLinShareUploadRequestUrl(owner)))
				.add("password", request.getTemporaryPlainTextPassword());
		container.setRecipient(request.getContact());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_REMINDER, builder);
	}

	@Override
	public MailContainerWithRecipient buildUploadRequestBeforeExpiryWarnOwner(User owner, UploadRequest request)
			throws BusinessException {
		if (isDisable(owner, MailActivationType.UPLOAD_REQUEST_WARN_OWNER_BEFORE_EXPIRY)) {
			return null;
		}
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("subject", request.getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName());
		builder.getBodyChain()
				.add("subject", request.getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequestGroup().getBody())
				.add("expirationDate", formatExpirationDate(owner, request))
				.add("creationDate", formatCreationDate(owner, request));
		for (UploadRequestUrl uru : request.getUploadRequestURLs()) {
			builder.getBodyChain().add(
					"recipientMail",
					uru.getContact().getMail()
			)
			.add("files", getFileNames(uru));
		}
		container.setRecipient(owner.getMail());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);
		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_WARN_OWNER_BEFORE_EXPIRY, builder);
	}

	@Override
	public MailContainerWithRecipient buildUploadRequestBeforeExpiryWarnRecipient(User owner, UploadRequestUrl request)
			throws BusinessException {
		if (isDisable(request.getContact(), owner, MailActivationType.UPLOAD_REQUEST_WARN_RECIPIENT_BEFORE_EXPIRY)) {
			return null;
		}
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();
		builder.getSubjectChain()
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", request.getContact().getMail())
				.add("lastName", "");
		builder.getBodyChain()
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody())
				.add("files", getFileNames(request))
				.add("ownerFirstName",owner.getFirstName())
				.add("ownerLastName",owner.getLastName())
				.add("ownerMail",owner.getMail())
				.add("ownerRepresentation", new ContactRepresentation(owner).getContactRepresentation())
				.add("expirationDate", formatExpirationDate(owner, request.getUploadRequest()))
				.add("creationDate", formatCreationDate(owner, request.getUploadRequest()))
				.add("url", request.getFullUrl(getLinShareUploadRequestUrl(owner)));
		container.setRecipient(request.getContact());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);
		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_WARN_RECIPIENT_BEFORE_EXPIRY, builder);
	}

	@Override
	public MailContainerWithRecipient buildUploadRequestExpiryWarnOwner(User owner, UploadRequest request)
			throws BusinessException {
		if (isDisable(owner, MailActivationType.UPLOAD_REQUEST_WARN_OWNER_EXPIRY)) {
			return null;
		}
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("subject", request.getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName());
		builder.getBodyChain()
				.add("subject", request.getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequestGroup().getBody())
				.add("expirationDate", formatExpirationDate(owner, request))
				.add("creationDate", formatCreationDate(owner, request));
		for (UploadRequestUrl uru : request.getUploadRequestURLs()) {
			builder.getBodyChain().add(
					"recipientMail",
					uru.getContact().getMail()
			)
			.add("files", getFileNames(uru));
		}
		container.setRecipient(owner.getMail());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_WARN_OWNER_EXPIRY, builder);
	}

	@Override
	public MailContainerWithRecipient buildUploadRequestExpiryWarnRecipient(User owner, UploadRequestUrl request)
			throws BusinessException {
		if (isDisable(request.getContact(), owner, MailActivationType.UPLOAD_REQUEST_WARN_RECIPIENT_EXPIRY)) {
			return null;
		}
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", request.getContact().getMail())
				.add("lastName", "");
		builder.getBodyChain()
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody())
				.add("files", getFileNames(request))
				.add("ownerFirstName",owner.getFirstName())
				.add("ownerLastName",owner.getLastName())
				.add("ownerMail",owner.getMail())
				.add("ownerRepresentation", new ContactRepresentation(owner).getContactRepresentation())
				.add("expirationDate", formatExpirationDate(owner, request.getUploadRequest()))
				.add("creationDate", formatCreationDate(owner, request.getUploadRequest()))
				.add("url", request.getFullUrl(getLinShareUploadRequestUrl(owner)));
		container.setRecipient(request.getContact());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_WARN_RECIPIENT_EXPIRY, builder);
	}

	@Override
	public MailContainerWithRecipient buildCloseUploadRequestByRecipient(User owner, UploadRequestUrl request)
			throws BusinessException {
		if (isDisable(owner, MailActivationType.UPLOAD_REQUEST_CLOSED_BY_RECIPIENT)) {
			return null;
		}
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", request.getContact().getMail())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName());
		builder.getBodyChain()
				.add("firstName", request.getContact().getMail())
				.add("lastName", "")
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody())
				.add("files", getFileNames(request));
		container.setRecipient(owner);
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(request.getContact());

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_CLOSED_BY_RECIPIENT, builder);
	}

	private String getFileNames(
			UploadRequestUrl requestUrl) {
		ImmutableSet<FileRepresentation> files = FluentIterable
				.from(requestUrl.getUploadRequestEntries())
				.transform(new Function<UploadRequestEntry, FileRepresentation>() {
					@Override
					public FileRepresentation apply(UploadRequestEntry ure) {
						return new FileRepresentation(ure);
					}})
					.toSet();
		if (files.size() > 0) {
			return files.toString();
		}
		return " - ";
	}

	// TODO : to be use.
	@Override
	public MailContainerWithRecipient buildCloseUploadRequestByOwner(User owner, UploadRequestUrl request)
			throws BusinessException {
		if (isDisable(request.getContact(), owner, MailActivationType.UPLOAD_REQUEST_CLOSED_BY_OWNER)) {
			return null;
		}
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", new ContactRepresentation(owner).getContactRepresentation())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", request.getContact().getMail())
				.add("lastName", "");
		builder.getBodyChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody())
				.add("files", getFileNames(request));
		container.setRecipient(request.getContact());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_CLOSED_BY_OWNER, builder);
	}

	@Override
	public MailContainerWithRecipient buildDeleteUploadRequestByOwner(User owner, UploadRequestUrl request)
			throws BusinessException {
		if (isDisable(request.getContact(), owner, MailActivationType.UPLOAD_REQUEST_DELETED_BY_OWNER)) {
			return null;
		}
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", new ContactRepresentation(owner).getContactRepresentation())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", request.getContact().getMail())
				.add("lastName", "");
		builder.getBodyChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody());
		container.setRecipient(request.getContact());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_DELETED_BY_OWNER, builder);
	}

	// TODO : to be use
	@Override
	public MailContainerWithRecipient buildErrorUploadRequestNoSpaceLeft(User owner, UploadRequestUrl request)
			throws BusinessException {
		if (isDisable(owner, MailActivationType.UPLOAD_REQUEST_NO_SPACE_LEFT)) {
			return null;
		}
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", request.getContact().getMail())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName());
		builder.getBodyChain()
				.add("firstName", request.getContact().getMail())
				.add("lastName", "")
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody());
		container.setRecipient(owner.getMail());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(request.getContact());

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_NO_SPACE_LEFT, builder);
	}

	/*
	 * Helpers
	 */

	private String getLinShareUploadRequestUrl(Account sender) {
		return functionalityReadOnlyService
				.getUploadRequestFunctionality(sender.getDomain())
				.getValue();
	}

	private String getUploadPropositionUrl(Account recipient) {
		String baseUrl = getLinShareUrlForAUserRecipient(recipient);
		StringBuffer uploadPropositionUrl = new StringBuffer();
		uploadPropositionUrl.append(baseUrl);
		if (!baseUrl.endsWith("/")) {
			uploadPropositionUrl.append('/');
		}
		uploadPropositionUrl.append("uploadrequest/proposition");
		return uploadPropositionUrl.toString();
	}

	private String getLinShareUrlForAUserRecipient(Account recipient) {
		String value = functionalityReadOnlyService
				.getCustomNotificationUrlFunctionality(recipient.getDomain())
				.getValue();
		if (!value.endsWith("/")) {
			return value + "/";
		}
		return value;
	}

	private String getLinShareUrlForAContactRecipient(Account sender) {
		StringValueFunctionality notificationUrl = functionalityReadOnlyService
				.getAnonymousURLNotificationUrl(sender.getDomain());
		return notificationUrl.getValue();
	}

	private String getReceivedSharedFileDownloadLink(User recipient, ShareEntry share) {
		StringBuilder sb = new StringBuilder();
		sb.append(getLinShareUrlForAUserRecipient(recipient));
		Formatter formatter = new Formatter(sb);
		formatter.format(receivedSharesUrlSuffix, share.getUuid());
		formatter.close();
		return sb.toString();
	}

	private String getOwnerDocumentLink(User owner, DocumentEntry doc) {
		StringBuilder sb = new StringBuilder();
		sb.append(getLinShareUrlForAUserRecipient(owner));
		Formatter formatter = new Formatter(sb);
		formatter.format(documentsUrlSuffix, doc.getUuid());
		formatter.close();
		return sb.toString();
	}

	private String getJwsEncryptUrlString(String rootUrl) {
		String jwsEncryptUrlString = "";
		StringBuffer jwsEncryptUrl = new StringBuffer();

		jwsEncryptUrl.append(rootUrl);
		if (!rootUrl.endsWith("/")) {
			jwsEncryptUrl.append('/');
		}
		jwsEncryptUrl.append("localDecrypt");
		jwsEncryptUrlString = jwsEncryptUrl.toString();

		return jwsEncryptUrlString;
	}

	/*
	 * MAIL CONTAINER BUILDER SECTION
	 */

	private String formatPersonalMessage(String pm, Language lang) {
		if (StringUtils.isBlank(pm))
			return "";
		return "<p>" + pm.replace("\n", "<br/>") + "</p><hr/><br/>";
	}

	private String formatFooter(String footer, Language lang) {
        if (insertLicenceTerm) {
    		if (lang.equals(Language.FRENCH)) {
				footer += "<br/>Vous utilisez la version libre et gratuite de <a href=\"http://www.linshare.org/\" title=\"LinShare\"><strong>LinShare</strong></a>™, développée par Linagora © 2009–2015. Contribuez à la R&D du produit en souscrivant à une offre entreprise.<br/>";
			} else {
				footer += "<br/>You are using the Open Source and free version of <a href=\"http://www.linshare.org/\" title=\"LinShare\"><strong>LinShare</strong></a>™, powered by Linagora © 2009–2015. Contribute to Linshare R&D by subscribing to an Enterprise offer.<br/>";
    		}
        }
        return footer;
	}

	

	private MailContainerWithRecipient buildMailContainer(MailConfig cfg,
			final MailContainerWithRecipient input, String pm,
			MailContentType type, MailContainerBuilder builder)
			throws BusinessException {
		Language lang = input.getLanguage();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				input);
		MailContent mailContent = cfg.findContent(lang, type);
		String subjectTemplate = mailContent.getSubject();
		String body = mailContent.getBody();
		MailFooter f = cfg.findFooter(lang);
		String footer = formatFooter(f.getFooter(), lang);
		String layout = cfg.getMailLayoutHtml().getLayout();

		logger.debug("Building mail content: " + type);
		pm = formatPersonalMessage(pm, lang);
		String subject = builder.getSubjectChain().build(subjectTemplate);
		body = builder.getBodyChain().build(body);
		footer = builder.getFooterChain().build(footer);
		layout = builder.getLayoutChain()
				.add("image", displayLogo ? LINSHARE_LOGO : "")
				.add("personalMessage", pm)
				.add("body", body)
				.add("footer", footer)
				.add("mailSubject", subject)
				.build(layout);

		container.setSubject(subject);
		container.setContentHTML(layout);
		container.setContentTXT(container.getContentHTML());
		// Message IDs from Web service API (ex Plugin Thunderbird)
		container.setInReplyTo(input.getInReplyTo());
		container.setReferences(input.getReferences());
		return container;
	}

	private boolean isDisable(Contact contact, Account sender, MailActivationType type) {
		AbstractDomain recipientDomain = domainBusinessService.findGuestDomain(sender.getDomain());
		// guest domain could be inexistent into the database.
		if (recipientDomain == null) {
			recipientDomain = sender.getDomain();
		}
		MailActivation mailActivation = mailActivationBusinessService
				.findForInternalUsage(recipientDomain, type);
		boolean enable = mailActivation.isEnable();
		return !enable;
	}

	private boolean isDisable(Account recipient, MailActivationType type) {
		MailActivation mailActivation = mailActivationBusinessService
				.findForInternalUsage(recipient.getDomain(), type);
		boolean enable = mailActivation.isEnable();
		return !enable;
	}

}