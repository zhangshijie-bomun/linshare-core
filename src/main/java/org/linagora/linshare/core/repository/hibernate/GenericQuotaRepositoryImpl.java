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
package org.linagora.linshare.core.repository.hibernate;

import java.util.List;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.constants.EnsembleType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.GenericQuotaRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public abstract class GenericQuotaRepositoryImpl<T extends Quota> extends AbstractRepositoryImpl<T>
		implements GenericQuotaRepository<T> {

	public GenericQuotaRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public T create(T entity) throws BusinessException {
		entity.setLastValue((long) 0);
		return super.create(entity);
	}

	@Override
	public T update(T entity, Long curentValue) throws BusinessException{
		entity.setLastValue(entity.getCurrentValue());
		entity.setCurrentValue(curentValue + entity.getCurrentValue());
		return super.update(entity);
	}

	@Override
	public T find(AbstractDomain domain, Account account, EnsembleType ensembleType) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		if (domain != null) {
			criteria.add(Restrictions.eq("domain", domain));
		}
		if (account != null) {
			criteria.add(Restrictions.eq("account", account));
		}
		if (ensembleType != null) {
			criteria.add(Restrictions.eq("ensembleType", ensembleType));
		}
		List<T> result = findByCriteria(criteria);
		if (result == null || result.isEmpty()) {
			return null;
		} else if (result.size() == 1) {
			return result.get(0);
		} else {
			throw new IllegalStateException("It must be only one quota for any entity");
		}
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(T entity) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		return criteria.add(Restrictions.eq("id", entity.getId()));
	}
}
