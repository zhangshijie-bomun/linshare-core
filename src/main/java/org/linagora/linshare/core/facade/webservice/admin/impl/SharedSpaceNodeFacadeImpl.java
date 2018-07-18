/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.SharedSpaceNodeFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;

import com.google.common.base.Strings;

public class SharedSpaceNodeFacadeImpl extends AdminGenericFacadeImpl implements SharedSpaceNodeFacade {

	private final SharedSpaceNodeService ssNodeService;

	public SharedSpaceNodeFacadeImpl(AccountService accountService, SharedSpaceNodeService ssNodeService) {
		super(accountService);
		this.ssNodeService = ssNodeService;
	}

	@Override
	public SharedSpaceNode find(String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "The shared space node uuid to find must bes set.");
		Account authUser = checkAuthentication(Role.SUPERADMIN);
		return ssNodeService.find(authUser, authUser, uuid);
	}

	@Override
	public SharedSpaceNode delete(SharedSpaceNode node, String uuid) throws BusinessException {
		Account authUser = checkAuthentication(Role.SUPERADMIN);
		if (!Strings.isNullOrEmpty(uuid)) {
			node = ssNodeService.find(authUser, authUser, uuid);
		} else {
			Validate.notNull(node, "The shared space node to delete must be set.");
			Validate.notEmpty(node.getUuid(), "The shared space node uuid to delete must be set.");
		}
		return ssNodeService.delete(authUser, authUser, node);
	}

	@Override
	public SharedSpaceNode update(SharedSpaceNode node, String uuid) throws BusinessException {
		Validate.notNull(node, "Missing required shared space node to update.");
		Account authUser = checkAuthentication(Role.SUPERADMIN);
		if (!Strings.isNullOrEmpty(uuid)) {
			node.setUuid(uuid);
		} else {
			Validate.notEmpty(node.getUuid(), "The shared space node uuid to update must be set.");
		}
		return ssNodeService.update(authUser, authUser, node);
	}

}
