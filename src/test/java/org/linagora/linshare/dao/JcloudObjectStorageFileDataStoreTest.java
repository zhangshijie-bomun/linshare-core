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
package org.linagora.linshare.dao;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.dao.impl.JcloudObjectStorageFileDataStoreImpl;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:springContext-test.xml",
		"classpath:OPTIONAL-springContext-jcloud.xml" })
//@TestPropertySource("/linshare-storage-swift.properties")
public class JcloudObjectStorageFileDataStoreTest extends AbstractJUnit4SpringContextTests {


	@Autowired
	protected JcloudObjectStorageFileDataStoreImpl jcloudFileDataStore;

	@Value( "${linshare.documents.storage.providers}" )
	private String supportedProviders;

	@Value( "${linshare.documents.storage.mode}" )
	private String provider;

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		logger.info("supportedProviders : " + supportedProviders);
		logger.info("Testing provider : " + provider);
		logger.info("fileDataStore : " + jcloudFileDataStore.toString());
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testUpload() throws BusinessException, URISyntaxException, IOException {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		String fileName = "CAS.bin.export";
		Path path = Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
		File file = path.toFile();
		FileMetaData metaData = new FileMetaData(FileMetaDataKind.DATA, "application/octet-stream", file.length(),
				fileName);
		FileMetaData data = jcloudFileDataStore.add(file, metaData);
		Assert.assertNotNull(data);
		logger.info("FileMetaData : " + data.toString());
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
