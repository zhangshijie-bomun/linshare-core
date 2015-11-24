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
package org.linagora.linshare.batches;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.business.service.UserDailyStatBusinessService;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.BatchHistoryBusinessService;
import org.linagora.linshare.core.business.service.DomainDailyStatBusinessService;
import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.business.service.EnsembleQuotaBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.PlatformQuotaBusinessService;
import org.linagora.linshare.core.business.service.ThreadDailyStatBusinessService;
import org.linagora.linshare.core.domain.constants.EnsembleType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UserDailyStat;
import org.linagora.linshare.core.domain.entities.DomainDailyStat;
import org.linagora.linshare.core.domain.entities.EnsembleQuota;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadDailyStat;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.service.LoadingServiceTestDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-rac.xml",
		"classpath:springContext-startopendj.xml",
		"classpath:springContext-jackRabbit-mock.xml",
		"classpath:springContext-test.xml",
		"classpath:springContext-quota-manager.xml",
		"classpath:springContext-service-miscellaneous.xml",
		"classpath:springContext-ldap.xml"})
public class DailyBatchTest extends AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;

	@Autowired
	@Qualifier("statisticDailyUserBatch")
	private GenericBatch dailyUserBatch;

	@Autowired
	@Qualifier("statisticDailyDomainBatch")
	private GenericBatch dailyDomainBatch;

	@Autowired
	private UserDailyStatBusinessService userdailyStatBusinessService;

	@Autowired
	private DomainDailyStatBusinessService domaindailyStatBusinessService;

	@Autowired
	private AccountQuotaBusinessService accountQuotaBusinessService;

	@Autowired
	private DomainQuotaBusinessService domainQuotaBusinessService;

	@Autowired
	private EnsembleQuotaBusinessService ensembleQuotaBusinessService;

	@Autowired
	private ThreadDailyStatBusinessService threadDailyStatBusinessService;


	@Autowired
	@Qualifier("statisticDailyThreadBatch")
	private GenericBatch dailyThreadBatch;
	@Autowired
	@Qualifier("userRepository")
	private UserRepository<User> userRepository;

	@Autowired
	private OperationHistoryBusinessService operationHistoryBusinessService;

	@Autowired
	private PlatformQuotaBusinessService platformQuotaBusinessService;

	@Autowired
	private BatchHistoryBusinessService batchHistoryBusinessService;

	LoadingServiceTestDatas dates;
	private User jane;

	@Before
	public void setUp (){
		this.executeSqlScript("import-tests-operationHistory.sql", false);
		this.executeSqlScript("import-tests-quota.sql", false);
		this.executeSqlScript("import-tests-stat.sql", false);
		dates = new LoadingServiceTestDatas(userRepository);
		dates.loadUsers();
		jane = dates.getUser2();
	}

	@Test
	public void test() {

		List<String> listThreadIdentifier = dailyThreadBatch.getAll();
		assertEquals(2, listThreadIdentifier.size());

		Thread thread1 = (Thread) accountRepository.findByLsUuid(listThreadIdentifier.get(0));
		Thread thread2 = (Thread) accountRepository.findByLsUuid(listThreadIdentifier.get(1));

		List<OperationHistory> listOperationHistory = operationHistoryBusinessService.find(thread1, null, null, new Date());
		assertNotEquals(0, listOperationHistory.size());

		listOperationHistory = operationHistoryBusinessService.find(thread2, null, null, new Date());
		assertNotEquals(0, listOperationHistory.size());

		dailyThreadBatch.execute(thread1.getLsUuid(), listThreadIdentifier.size(), 1);

		List<ThreadDailyStat> listThreaddailyStat = threadDailyStatBusinessService.findBetweenTwoDates(thread1, new Date(), new Date());

		assertEquals(1, listThreaddailyStat.size());
		ThreadDailyStat threadDailyStat = listThreaddailyStat.get(0);
		assertEquals(thread1, threadDailyStat.getAccount());
		assertEquals(2, (long) threadDailyStat.getOperationCount());
		assertEquals(100, (long) threadDailyStat.getActualOperationSum());
		assertEquals(1, (long) threadDailyStat.getAddOperationCount());
		assertEquals(400, (long) threadDailyStat.getAddOperationSum());
		assertEquals(1, (long) threadDailyStat.getDeleteOperationCount());
		assertEquals(-300, (long) threadDailyStat.getDeleteOperationSum());
		assertEquals(100, (long) threadDailyStat.getDiffOperationSum());

		Quota quota = accountQuotaBusinessService.find(thread1);
		assertNotNull(quota);
		assertEquals(800, (long) quota.getCurrentValue());
		assertEquals(700, (long) quota.getLastValue());
		assertEquals(1000, (long) quota.getQuota());
		assertEquals(800, (long) quota.getQuotaWarning());
		assertEquals(5, (long) quota.getFileSizeMax());

		dailyThreadBatch.execute(thread2.getLsUuid(), listThreadIdentifier.size(), 1);

		batchHistoryBusinessService.findByUuid(thread2.getLsUuid());

		listThreaddailyStat = threadDailyStatBusinessService.findBetweenTwoDates(thread2, new Date(), new Date());

		assertEquals(1, listThreaddailyStat.size());
		threadDailyStat = listThreaddailyStat.get(0);
		assertEquals(thread2, threadDailyStat.getAccount());
		assertEquals(1, (long) threadDailyStat.getOperationCount());
		assertEquals(200, (long) threadDailyStat.getActualOperationSum());
		assertEquals(1, (long) threadDailyStat.getAddOperationCount());
		assertEquals(200, (long) threadDailyStat.getAddOperationSum());
		assertEquals(0, (long) threadDailyStat.getDeleteOperationCount());
		assertEquals(0, (long) threadDailyStat.getDeleteOperationSum());
		assertEquals(200, (long) threadDailyStat.getDiffOperationSum());

		quota = accountQuotaBusinessService.find(thread2);
		assertNotNull(quota);
		assertEquals(700, (long) quota.getCurrentValue());
		assertEquals(500, (long) quota.getLastValue());
		assertEquals(1300, (long) quota.getQuota());
		assertEquals(1000, (long) quota.getQuotaWarning());
		assertEquals(6, (long) quota.getFileSizeMax());

		listOperationHistory = operationHistoryBusinessService.find(jane, null, null, new Date());
		assertNotEquals(0, listOperationHistory.size());

		dailyUserBatch.execute(jane.getLsUuid(), 10, 1);

		List<UserDailyStat> listUserdailyStat = userdailyStatBusinessService.findBetweenTwoDates(jane, new Date(), new Date());

		assertEquals(1, listUserdailyStat.size());
		UserDailyStat userDailyStat = listUserdailyStat.get(0);
		assertEquals(jane, userDailyStat.getAccount());
		assertEquals(7, (long) userDailyStat.getOperationCount());
		assertEquals(700, (long) userDailyStat.getActualOperationSum());
		assertEquals(5, (long) userDailyStat.getAddOperationCount());
		assertEquals(1200, (long) userDailyStat.getAddOperationSum());
		assertEquals(2, (long) userDailyStat.getDeleteOperationCount());
		assertEquals(-500, (long) userDailyStat.getDeleteOperationSum());
		assertEquals(700, (long) userDailyStat.getDiffOperationSum());

		quota = accountQuotaBusinessService.find(jane);
		assertNotNull(quota);
		assertEquals(1500, (long) quota.getCurrentValue());
		assertEquals(800, (long) quota.getLastValue());
		assertEquals(1600, (long) quota.getQuota());
		assertEquals(1480, (long) quota.getQuotaWarning());
		assertEquals(5, (long) quota.getFileSizeMax());

		listOperationHistory = operationHistoryBusinessService.find(jane, null, null, new Date());
		assertEquals(0, listOperationHistory.size());

		dailyDomainBatch.execute(jane.getDomain().getUuid(), 20, 1);

		EnsembleQuota ensembleQuota = ensembleQuotaBusinessService.find(jane.getDomain(), EnsembleType.USER);
		assertNotNull(ensembleQuota);
		assertEquals(2400, (long) ensembleQuota.getCurrentValue());
		assertEquals(496, (long) ensembleQuota.getLastValue());
		assertEquals(1900, (long) ensembleQuota.getQuota());
		assertEquals(1300, (long) ensembleQuota.getQuotaWarning());
		assertEquals(5, (long) ensembleQuota.getFileSizeMax());

		ensembleQuota = ensembleQuotaBusinessService.find(jane.getDomain(), EnsembleType.THREAD);
		assertNotNull(ensembleQuota);
		assertEquals(1500, (long) ensembleQuota.getCurrentValue());
		assertEquals(900, (long) ensembleQuota.getLastValue());
		assertEquals(2000, (long) ensembleQuota.getQuota());
		assertEquals(1500, (long) ensembleQuota.getQuotaWarning());
		assertEquals(5, (long) ensembleQuota.getFileSizeMax());

		List<DomainDailyStat> listDomaindailyStat = domaindailyStatBusinessService.findBetweenTwoDates(jane.getDomain(), new Date(), new Date());

		assertEquals(1, listDomaindailyStat.size());
		DomainDailyStat domainDailyStat = listDomaindailyStat.get(0);
		assertEquals(jane.getDomain(), domainDailyStat.getDomain());
		assertEquals(10, (long) domainDailyStat.getOperationCount());
		assertEquals(1000, (long) domainDailyStat.getActualOperationSum());
		assertEquals(7, (long) domainDailyStat.getAddOperationCount());
		assertEquals(1800, (long) domainDailyStat.getAddOperationSum());
		assertEquals(3, (long) domainDailyStat.getDeleteOperationCount());
		assertEquals(-800, (long) domainDailyStat.getDeleteOperationSum());
		assertEquals(1000, (long) domainDailyStat.getDiffOperationSum());

		quota = domainQuotaBusinessService.find(jane.getDomain());
		assertNotNull(quota);
		assertEquals(3900, (long) quota.getCurrentValue());
		assertEquals(1096, (long) quota.getLastValue());
		assertEquals(1900, (long) quota.getQuota());
		assertEquals(1800, (long) quota.getQuotaWarning());
		assertEquals(5, (long) quota.getFileSizeMax());

		quota = platformQuotaBusinessService.find();
		assertNotNull(quota);
		assertEquals(3900, (long) quota.getCurrentValue());
		assertEquals(1096, (long) quota.getLastValue());
		assertEquals(2300, (long) quota.getQuota());
		assertEquals(2000, (long) quota.getQuotaWarning());
		assertEquals(10, (long) quota.getFileSizeMax());
	}
}
