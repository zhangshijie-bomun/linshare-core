/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.core.facade.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.MailingListContactVo;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.MailingListFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.MailingListService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.view.tapestry.services.impl.MailCompletionService;
import org.linagora.linshare.view.tapestry.services.impl.MailingListCompletionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailingListFacadeImpl implements MailingListFacade {

	Logger logger = LoggerFactory.getLogger(MailingListFacadeImpl.class);
	private final MailingListService mailingListService;
	private final UserService userService;
	private final AbstractDomainService abstractDomainService;
	private final AccountService accountService;

	public MailingListFacadeImpl(MailingListService mailingListService, UserService userService,
			AbstractDomainService abstractDomainService, AccountService accountService) {
		super();
		this.mailingListService = mailingListService;
		this.userService = userService;
		this.abstractDomainService = abstractDomainService;
		this.accountService = accountService;
	}

	@Override
	public MailingListVo retrieveList(String uuid) {
		MailingList mailingList = mailingListService.retrieveList(uuid);
		if (mailingList != null) {
			return new MailingListVo(mailingList);
		} else {
			return null;
		}
	}

	@Override
	public MailingListVo createList(MailingListVo mailingListVo) throws BusinessException {
		MailingList mailingList = new MailingList(mailingListVo);
		String ownerMail = mailingListVo.getOwner().getMail();
		String ownerDomainId = mailingListVo.getOwner().getDomainIdentifier();
		User actor = userService.findOrCreateUser(ownerMail, ownerDomainId);
		mailingList.setOwner(actor);
		mailingList.setMails(null);
		AbstractDomain domain = abstractDomainService.retrieveDomain(mailingListVo.getDomainId());
		mailingList.setDomain(domain);
		mailingListService.createList(mailingList);
		return mailingListVo;
	}

	@Override
	public List<MailingListVo> findAllList() {
		return ListToListVo(mailingListService.findAllList());
	}

	@Override
	public List<MailingListVo> findAllListByUser(UserVo actorVo) throws BusinessException {
		User actor = userService.findOrCreateUser(actorVo.getMail(), actorVo.getDomainIdentifier());
		return ListToListVo(mailingListService.findAllListByUser(actor));
	}

	@Override
	public void deleteList(UserVo actorVo, String uuid) throws BusinessException {
		User actor = userService.findOrCreateUser(actorVo.getMail(), actorVo.getDomainIdentifier());
		mailingListService.deleteList(actor, uuid);
	}

	@Override
	public void updateList(UserVo actorVo, MailingListVo mailingListVo) throws BusinessException {
		MailingList mailingList = new MailingList(mailingListVo);
		User actor = (User) userService.findOrCreateUser(actorVo.getMail(), actorVo.getDomainIdentifier());
		String ownerMail = mailingListVo.getOwner().getMail();
		String ownerDomainId = mailingListVo.getOwner().getDomainIdentifier();
		User owner = userService.findOrCreateUser(ownerMail, ownerDomainId);
		mailingList.setOwner(owner);
		AbstractDomain domain = abstractDomainService.retrieveDomain(owner.getDomainId());
		mailingList.setDomain(domain);
		mailingListService.updateList(actor, mailingList);
	}

	@Override
	public void updateContact(MailingListVo listVo, MailingListContactVo contactToUpdate) throws BusinessException {
		MailingList list = mailingListService.retrieveList(listVo.getUuid());
		MailingListContact contact = mailingListService.retrieveContact(list, contactToUpdate.getMail());
		contact.setDisplay(contactToUpdate.getDisplay());
		mailingListService.updateContact(list, contact);
	}

	@Override
	public void deleteContact(MailingListVo listVo, String mail) throws BusinessException {
		MailingList list = mailingListService.retrieveList(listVo.getUuid());
		mailingListService.deleteContact(list, mail);
	}

	@Override
	public MailingListContactVo retrieveContact(MailingListVo list, String mail) throws BusinessException {
		MailingList mailingList = mailingListService.retrieveList(list.getUuid());
		return new MailingListContactVo(mailingListService.retrieveContact(mailingList, mail));
	}

	@Override
	public String checkUniqueIdentifier(UserVo user, String value) throws BusinessException {
		List<MailingListVo> list = findAllMyList(user);
		int i = 1;
		String copy = value;

		for (MailingListVo mailingListVo : list) {
			while (mailingListVo.getIdentifier().equals(copy)) {
				copy = value + i;
				i++;
			}
		}
		return copy;
	}

	@Override
	public List<MailingListVo> findAllMyList(UserVo user) throws BusinessException {
		User actor = userService.findOrCreateUser(user.getMail(), user.getDomainIdentifier());
		return ListToListVo(mailingListService.findAllListByOwner(actor));
	}

	@Override
	public List<MailingListVo> getListFromQuickShare(UserVo user, String mailingLists) {
		List<MailingListVo> finalList = new ArrayList<MailingListVo>();
		if (mailingLists != null) {
			String[] recipients = mailingLists.split(",");
			if (mailingLists.startsWith("\"") && mailingLists.endsWith(")")) {
				for (String oneMailingList : recipients) {
					int index1 = oneMailingList.indexOf("(");
					String owner = oneMailingList.substring(index1 + 1, oneMailingList.length() - 1);

					int index2 = oneMailingList.indexOf(" ");
					String identifier = oneMailingList.substring(1, index2 - 1);

					if (owner.equals("Me")) {
						owner = user.getFullName();
					}
					MailingListVo mailingListVo = retrieveMailingListByOwnerAndIdentifier(identifier, owner);
					finalList.add(mailingListVo);
				}
			}
		} else {
			finalList = new ArrayList<MailingListVo>();
		}
		return finalList;
	}

	private MailingListVo retrieveMailingListByOwnerAndIdentifier(String identifier, String ownerUuid) {
		User actor = (User) userService.findByLsUuid(ownerUuid);
		return new MailingListVo(mailingListService.findListByIdentifier(actor, identifier));
	}

	@Override
	public List<String> completionsForSearchList(UserVo loginUser, String input, String criteriaOnSearch)
			throws BusinessException {
		List<MailingListVo> searchResults = performSearch(input, loginUser);
		List<String> elements = new ArrayList<String>();
		
		for (MailingListVo mailingListVo : searchResults) {
			elements.add(MailingListCompletionService.formatLabelForUser(loginUser,mailingListVo, false));
		}
		/*	if (!(mailingListVo.getOwner().equals(loginUser))) {
				completeName = "\"" + mailingListVo.getIdentifier() + "\" (" + mailingListVo.getOwner().getFullName()
						+ ")";
			} else {
				completeName = "\"" + mailingListVo.getIdentifier() + "\" (Me)";
			}

			if (criteriaOnSearch.equals("public") && mailingListVo.isPublic() == true) {
				elements.add(completeName);
			} else if (criteriaOnSearch.equals("private") && mailingListVo.isPublic() == false) {
				elements.add(completeName);
			} else if (criteriaOnSearch.equals("all")) {
				elements.add(completeName);
			}
		}*/
		return elements;
	}

	@Override
	public List<MailingListVo> setListFromSearch(UserVo loginUser, String targetLists, String criteriaOnSearch)
			throws BusinessException {
		List<MailingListVo> lists = new ArrayList<MailingListVo>();
		if (targetLists != null) {
			if (targetLists.startsWith("\"") && targetLists.endsWith(")")) {
				lists = this.getListFromQuickShare(loginUser, targetLists);
			} else {
				if (targetLists.equals("*")) {
					if (loginUser.isSuperAdmin()) {
						lists = this.findAllList();
					} else {
						lists = this.findAllListByUser(loginUser);
					}
				} else {
					lists = performSearch(targetLists, loginUser);
				}
			}
			if (criteriaOnSearch.equals("public") || criteriaOnSearch.equals("private")) {
				List<MailingListVo> finalList = new ArrayList<MailingListVo>(lists);
				lists.clear();

				for (MailingListVo mailingListVo : finalList) {
					if (criteriaOnSearch.equals("private") && mailingListVo.isPublic() == false) {
						lists.add(mailingListVo);
					} else if (criteriaOnSearch.equals("public") && mailingListVo.isPublic() == true) {
						lists.add(mailingListVo);
					}
				}
			}
		} else {
			return new ArrayList<MailingListVo>();
		}
		return lists;
	}

	private List<MailingListVo> performSearch(String input, UserVo loginUser) throws BusinessException {
		List<MailingListVo> list = new ArrayList<MailingListVo>();
		List<MailingListVo> finalList = new ArrayList<MailingListVo>();
		if (loginUser.isSuperAdmin()) {
			list = this.findAllList();
		} else {
			list = this.findAllListByUser(loginUser);
		}

		for (MailingListVo mailingListVo : list) {
			if (mailingListVo.getIdentifier().indexOf(input) != -1) {
				finalList.add(mailingListVo);
			}
		}
		return finalList;
	}

	public boolean checkUserIsContact(List<MailingListContactVo> contacts, String mail) {

		for (MailingListContactVo contact : contacts) {
			if (contact.getMail().equals(mail)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> completionOnUsers(UserVo actorVo, String pattern) throws BusinessException {
		User actor = userService.findByLsUuid(actorVo.getLogin());
		List<String> ret = new ArrayList<String>();

		List<User> userSet = performSearchUser(actor, pattern);

		for (User user : userSet) {
			if (!(user.equals(actor))) {
				String completeName = MailCompletionService.formatLabel(new UserVo(user), false);
				if (!ret.contains(completeName)) {
					ret.add(completeName);
				}
			}
		}
		return ret;
	}

	private List<User> performSearchUser(User loginUser, String input) throws BusinessException {
		String firstName_ = null;
		String lastName_ = null;

		if (input != null && input.length() > 0) {
			StringTokenizer stringTokenizer = new StringTokenizer(input, " ");
			if (stringTokenizer.hasMoreTokens()) {
				firstName_ = stringTokenizer.nextToken();
				if (stringTokenizer.hasMoreTokens()) {
					lastName_ = stringTokenizer.nextToken();
				}
			}
		}

		Set<User> userSet = new HashSet<User>();

		if (input != null) {
			userSet.addAll(userService.searchUser(input.trim(), null, null, null, loginUser));
		} else {
			userSet.addAll(userService.searchUser(null, firstName_, lastName_, null, loginUser));
		}

		return new ArrayList<User>(userSet);
	}

	@Override
	public List<UserVo> searchAmongUsers(UserVo userVo, String input) throws BusinessException {
		List<User> results = new ArrayList<User>();
		List<UserVo> finalResults = new ArrayList<UserVo>();
		User owner = (User) accountService.findByLsUuid(userVo.getLogin());
		if (input != null) {
			if (input.startsWith("\"") && input.endsWith(">")) {
				UserVo tmp = MailCompletionService.getUserFromDisplay(input);
				results = userService.searchUser(tmp.getMail(), tmp.getFirstName(), tmp.getLastName(), null, owner);
			} else {
				results = performSearchUser(owner, input);
			}

			for (User currentUser : results) {
				if (!(currentUser.equals(owner))) {
					finalResults.add(new UserVo(currentUser));
				}
			}
		}
		return finalResults;
	}

	@Override
	public void addUserToList(MailingListVo mailingListVo, String domain, String mail) throws BusinessException {
		User actor = (User) userService.findOrCreateUser(mailingListVo.getOwner().getMail(), mailingListVo.getOwner()
				.getDomainIdentifier());
		User selectedUser = userService.findOrCreateUser(mail, domain);
		if (selectedUser != null) {
			String display = MailCompletionService.formatLabel(selectedUser.getMail(), selectedUser.getFirstName(),
					selectedUser.getLastName(), false);
			MailingListContactVo newContact = new MailingListContactVo(mail, display);
			MailingList list = mailingListService.retrieveList(mailingListVo.getUuid());
			list.getMailingListContact().add(new MailingListContact(newContact));
			mailingListService.updateList(actor, list);
		}
	}

	@Override
	public void addNewContactToList(MailingListVo mailingListVo, MailingListContactVo contactVo)
			throws BusinessException {
		MailingList list = mailingListService.retrieveList(mailingListVo.getUuid());
		User actor = (User) userService.findOrCreateUser(mailingListVo.getOwner().getMail(), mailingListVo.getOwner()
				.getDomainIdentifier());
		MailingListContact contact = new MailingListContact(contactVo);
		list.getMailingListContact().add(contact);
		mailingListService.updateList(actor, list);
	}

	@Override
	public void setNewOwner(MailingListVo mailingListVo, String input) throws BusinessException {

		UserVo selectedUser = MailCompletionService.getUserFromDisplay(input);
		User user = userService.findUnkownUserInDB(selectedUser.getMail());
		mailingListVo.setOwner(new UserVo(user));
		AbstractDomain domain = abstractDomainService.retrieveDomain(user.getDomainId());
		mailingListVo.setDomainId(domain.getIdentifier());

	}

	@Override
	public void refreshList(List<MailingListVo> list) {
		List<MailingListVo> refreshList = new ArrayList<MailingListVo>(list);
		list.clear();
		for (MailingListVo mailingListVo : refreshList) {
			if (retrieveList(mailingListVo.getUuid()) != null) {
				list.add(retrieveList(mailingListVo.getUuid()));
			}
		}
	}

	private List<MailingListVo> ListToListVo(List<MailingList> list) {
		List<MailingListVo> listVo = new ArrayList<MailingListVo>();
		if (list != null) {
			for (MailingList currentList : list) {
				listVo.add(new MailingListVo(currentList));
			}
		}
		return listVo;
	}

	@Override
	public boolean getListIsDeletable(UserVo actorVo, MailingListVo listVo) throws BusinessException {
		MailingList list = mailingListService.retrieveList(listVo.getUuid());
		User actor = (User) userService.findOrCreateUser(actorVo.getMail(), actorVo.getDomainIdentifier());
		if (list.getOwner().equals(actor) || actorVo.isSuperAdmin()) {
			return true;
		}
		return false;
	}

}
