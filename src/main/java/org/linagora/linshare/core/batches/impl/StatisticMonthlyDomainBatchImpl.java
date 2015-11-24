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

package org.linagora.linshare.core.batches.impl;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.linagora.linshare.core.business.service.BatchHistoryBusinessService;
import org.linagora.linshare.core.business.service.DomainMonthlyStatBusinessService;
import org.linagora.linshare.core.business.service.DomainWeeklyStatBusinessService;
import org.linagora.linshare.core.domain.constants.BatchType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.BatchHistory;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.job.quartz.DomainBatchResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AbstractDomainService;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class StatisticMonthlyDomainBatchImpl extends GenericBatchImpl {

	private final DomainWeeklyStatBusinessService domainWeeklyStatBusinessService;
	private final DomainMonthlyStatBusinessService domainMonthlyStatBusinessService;
	private final AbstractDomainService abstractDomainService;
	private final BatchHistoryBusinessService batchHistoryBusinessService;

	private static final String BATCH_HISTORY_UUID = "BatchHistoryUuid";

	public StatisticMonthlyDomainBatchImpl(final DomainWeeklyStatBusinessService domainWeeklyStatBusinessService,
			final DomainMonthlyStatBusinessService domainMonthlyStatBusinessService,
			final AbstractDomainService abstractDomainService, AccountRepository<Account> accountRepository,
			BatchHistoryBusinessService batchHistoryBusinessService) {
		super(accountRepository);
		this.domainMonthlyStatBusinessService = domainMonthlyStatBusinessService;
		this.domainWeeklyStatBusinessService = domainWeeklyStatBusinessService;
		this.abstractDomainService = abstractDomainService;
		this.batchHistoryBusinessService = batchHistoryBusinessService;
	}

	@Override
	public List<String> getAll() {
		logger.info("MonthlyDomainBatchImpl job starting");
		List<String> domains = domainWeeklyStatBusinessService
				.findIdentifierDomainBetweenTwoDates(getFirstDayOfLastMonth(), getLastDayOfLastMonth());
		logger.info(domains.size() + " domain(s) have been found in DomainWeeklyStat table.");
//		BatchHistory batchHistory = new BatchHistory(BatchType.MONTHLY_DOMAIN_BATCH);
//		batchHistory = batchHistoryBusinessService.create(batchHistory);
//		Map<String, List<String>> res = Maps.newHashMap();
//		res.put(INPUT_LIST, domains);
//		res.put(BATCH_HISTORY_UUID, Lists.newArrayList(batchHistory.getUuid()));
		return domains;
	}

	@Override
	public Context execute(String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		AbstractDomain resource = abstractDomainService.findById(identifier);
		Context context = new DomainBatchResultContext(resource);
		try {
			logInfo(total, position, " processing domain : " + resource.getDescription());
			domainMonthlyStatBusinessService.create(resource, getFirstDayOfLastMonth(), getLastDayOfLastMonth());
		} catch (BusinessException businessException) {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.add(GregorianCalendar.MONTH, -1);
			logError(total, position,
					"Error while trying to create a DomainMonthlyStat for domain" + resource.getDescription()
							+ " in the month "
							+ calendar.getDisplayName(GregorianCalendar.MONTH, GregorianCalendar.LONG, Locale.US));
			logger.info(
					"Error occurred while creating a monthly statistics for domain " + resource.getDescription()
							+ " in the month "
							+ calendar.getDisplayName(GregorianCalendar.MONTH, GregorianCalendar.LONG, Locale.US),
					businessException);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to create a DomainMonthlyStat.");
			exception.setBusinessException(businessException);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(Context context, long total, long position) {
		DomainBatchResultContext domainContext = (DomainBatchResultContext) context;
		AbstractDomain domain = domainContext.getResource();
		logInfo(total, position,
				"the MonthlyDomainStat for " + domain.getDescription() + "has been successfully created.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position) {
		DomainBatchResultContext domainContext = (DomainBatchResultContext) exception.getContext();
		AbstractDomain domain = domainContext.getResource();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.MONTH, -1);
		logError(total, position,
				"creating MonthlyDomainStatistic has failed for domain" + domain.getDescription() + " in the month "
						+ calendar.getDisplayName(GregorianCalendar.MONTH, GregorianCalendar.LONG, Locale.US));
		logger.error("Error occured while creating MonthlyDomainStatistic for domain " + domain.getDescription()
				+ " in the month " + calendar.getDisplayName(GregorianCalendar.MONTH, GregorianCalendar.LONG, Locale.US)
				+ ". BatchBudinessException ", exception);
	}

	@Override
	public void terminate(List<String> all, long errors, long unhandled_errors, long total,
			long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " MonthlyDomainStatistic for domain(s) have bean created.");
		if (errors > 0) {
			logger.info(errors + "  MonthlyDomainStatistic for domain(s) failed to be created");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors + " MonthlyDomainStatistic failed to be created (unhandled error.)");
		}
//		BatchHistory batchHistory = batchHistoryBusinessService.findByUuid(context.get(BATCH_HISTORY_UUID).get(0));
//		if (batchHistory != null) {
//			batchHistory.setStatus("terminated");
//			batchHistory.setErrors(errors);
//			batchHistory.setUnhandledErrors(unhandled_errors);
//			batchHistory = batchHistoryBusinessService.update(batchHistory);
//		}
		logger.info("MonthlyDomainBatchImpl job terminated");
	}

	private Date getLastDayOfLastMonth() {
		GregorianCalendar dateCalendar = new GregorianCalendar();
		dateCalendar.add(GregorianCalendar.MONTH, -1);
		int nbDay = dateCalendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		dateCalendar.set(GregorianCalendar.DATE, nbDay);
		dateCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		dateCalendar.set(GregorianCalendar.MINUTE, 0);
		dateCalendar.set(GregorianCalendar.SECOND, 0);
		return dateCalendar.getTime();
	}

	private Date getFirstDayOfLastMonth() {
		GregorianCalendar dateCalendar = new GregorianCalendar();
		dateCalendar.add(GregorianCalendar.MONTH, -1);
		dateCalendar.set(GregorianCalendar.DATE, 1);
		dateCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		dateCalendar.set(GregorianCalendar.MINUTE, 0);
		dateCalendar.set(GregorianCalendar.SECOND, 0);
		return dateCalendar.getTime();
	}

	private Date getFirstDayOfMonth() {
		GregorianCalendar dateCalendar = new GregorianCalendar();
		dateCalendar.set(GregorianCalendar.DATE, 1);
		dateCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		dateCalendar.set(GregorianCalendar.MINUTE, 0);
		dateCalendar.set(GregorianCalendar.SECOND, 0);
		return dateCalendar.getTime();
	}

	@Override
	public boolean needToRun() {
		return !batchHistoryBusinessService.exist(getFirstDayOfMonth(), new Date(), BatchType.MONTHLY_DOMAIN_BATCH);
	}
}
