/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.repository.hibernate;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.Assert;

import org.junit.Test;
import org.linagora.linShare.core.domain.LogAction;
import org.linagora.linShare.core.domain.entities.FileLogEntry;
import org.linagora.linShare.core.domain.entities.LogEntry;
import org.linagora.linShare.core.domain.entities.ShareLogEntry;
import org.linagora.linShare.core.domain.entities.UserLogEntry;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.LogEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class LogEntryRepositoryImplTest extends AbstractJUnit4SpringContextTests {

	
	@Autowired
	private LogEntryRepository logEntryRepository;
	
	private final Calendar actionDate = new GregorianCalendar();
	private final String actorMail = "testActorMail";
	private final String actorFirstname = "testActorFirstName";
	private final String actorLastname = "testActorLastName";
	private final String fileName = "testFileName";
	private final Long fileSize = 10L;	
	private final String fileType = "testExt";
	private final String targetMail = "testTargetMail";
	private final String targetFirstname = "testTargetFirstName";
	private final String targetLastname= "testTargetLastName";
	

	@Test
	public void testExistFileLogEntry() throws BusinessException{
		LogEntry testFileLogEntry = new FileLogEntry(actionDate, actorMail, actorFirstname, actorLastname, LogAction.FILE_UPLOAD,
				"test description", fileName, fileSize, fileType);
		
		logEntryRepository.create(testFileLogEntry);
		
		Assert.assertTrue(logEntryRepository.findByUser(actorMail) != null);
		Assert.assertTrue(logEntryRepository.findByUser(actorMail).size() == 1);
		
		LogEntry tmpLogEntry = logEntryRepository.findByUser(actorMail).get(0);
		
		Assert.assertTrue(tmpLogEntry instanceof FileLogEntry);
		Assert.assertTrue(tmpLogEntry.getActorFirstname().equals(actorFirstname));
		Assert.assertTrue(((FileLogEntry)tmpLogEntry).getLogAction().equals(LogAction.FILE_UPLOAD));

		logEntryRepository.delete(tmpLogEntry);
	}
	
	
	@Test
	public void testExistUserLogEntry() throws BusinessException{
		LogEntry testFileLogEntry = new UserLogEntry(actionDate, actorMail, actorFirstname, actorLastname, LogAction.USER_CREATE,
				"test description", targetMail, targetFirstname, targetLastname, null);
		
		logEntryRepository.create(testFileLogEntry);
		
		Assert.assertTrue(logEntryRepository.findByUser(actorMail) != null);
		Assert.assertTrue(logEntryRepository.findByUser(actorMail).size() == 1);
		
		LogEntry tmpLogEntry = logEntryRepository.findByUser(actorMail).get(0);
		
		Assert.assertTrue(tmpLogEntry instanceof UserLogEntry);
		Assert.assertTrue(tmpLogEntry.getActorFirstname().equals(actorFirstname));
		Assert.assertTrue(((UserLogEntry)tmpLogEntry).getLogAction().equals(LogAction.USER_CREATE));
		logEntryRepository.delete(tmpLogEntry);
	}
	
	@Test
	public void testExistShareLogEntry() throws BusinessException{
		LogEntry testFileLogEntry = new ShareLogEntry(actionDate, actorMail, actorFirstname, actorLastname, LogAction.FILE_SHARE,
				"test description", fileName, fileSize, fileType, targetMail, targetFirstname, targetLastname, null);
		
		logEntryRepository.create(testFileLogEntry);
		
		Assert.assertTrue(logEntryRepository.findByUser(actorMail) != null);
		Assert.assertTrue(logEntryRepository.findByUser(actorMail).size() == 1);
		
		LogEntry tmpLogEntry = logEntryRepository.findByUser(actorMail).get(0);
		
		Assert.assertTrue(tmpLogEntry instanceof ShareLogEntry);
		Assert.assertTrue(tmpLogEntry.getActorFirstname().equals(actorFirstname));
		Assert.assertTrue(((ShareLogEntry)tmpLogEntry).getLogAction().equals(LogAction.FILE_SHARE));
		logEntryRepository.delete(tmpLogEntry);
	}
}
