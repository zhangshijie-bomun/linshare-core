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
package org.linagora.linshare.core.service.impl;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.EnsembleQuotaBusinessService;
import org.linagora.linshare.core.domain.constants.EnsembleType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.EnsembleQuota;
import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.QuotaResourceAccessControl;
import org.linagora.linshare.core.service.EnsembleQuotaService;

public class EnsembleQuotaServiceImpl extends GenericServiceImpl<Account, Quota> implements EnsembleQuotaService {

	private EnsembleQuotaBusinessService ensembleQuotaBusinessService;

	public EnsembleQuotaServiceImpl(QuotaResourceAccessControl rac,
			EnsembleQuotaBusinessService ensembleQuotaBusinessService) {
		super(rac);
		this.ensembleQuotaBusinessService = ensembleQuotaBusinessService;
	}

	@Override
	public EnsembleQuota create(Account actor, AbstractDomain domain, EnsembleQuota entity) {
		Validate.notNull(actor, "Acctor must be set.");
		Validate.notNull(domain, "Domain must be set.");
		Validate.notNull(entity, "Entity must be set.");
		checkCreatePermission(actor, null, EnsembleQuota.class, BusinessErrorCode.QUOTA_UNAUTHORIZED, null, domain);
		return ensembleQuotaBusinessService.create(entity);
	}

	@Override
	public EnsembleQuota update(Account actor, AbstractDomain domain, EnsembleQuota entity) {
		Validate.notNull(actor, "Acctor must be set.");
		Validate.notNull(domain, "Domain must be set.");
		Validate.notNull(entity, "Entity must be set.");
		checkUpdatePermission(actor, null, EnsembleQuota.class, BusinessErrorCode.QUOTA_UNAUTHORIZED, null, domain);
		EnsembleQuota ensembleQuota = ensembleQuotaBusinessService.find(entity.getDomain(), entity.getEnsembleType());
		ensembleQuota.setFileSizeMax(entity.getFileSizeMax());
		ensembleQuota.setQuota(entity.getQuota());
		ensembleQuota.setQuotaWarning(entity.getQuotaWarning());
		return ensembleQuotaBusinessService.update(ensembleQuota);
	}

	@Override
	public EnsembleQuota find(Account actor, AbstractDomain domain, EnsembleType ensembleType) {
		Validate.notNull(actor, "Acctor must be set.");
		Validate.notNull(domain, "Domain must be set.");
		Validate.notNull(ensembleType, "EnsembleType must be set.");
		checkReadPermission(actor, null, EnsembleQuota.class, BusinessErrorCode.QUOTA_UNAUTHORIZED, null, domain);
		EnsembleQuota ensembleQuota = ensembleQuotaBusinessService.find(domain, ensembleType);
		if(ensembleQuota == null){
			throw new BusinessException(BusinessErrorCode.ENSEMBLE_QUOTA_NOT_FOUND, "Can not found ensemble " + ensembleType.toString() + " quota of the domain "+domain.getUuid());
		}
		return ensembleQuota;
	}
}