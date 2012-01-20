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
package org.linagora.linShare.service;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linShare.core.domain.entities.DomainPattern;
import org.linagora.linShare.core.domain.entities.LDAPConnection;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.service.UserProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = { 
		"classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-service-test.xml"		
		})
public class UserProviderServiceImplTest extends AbstractJUnit4SpringContextTests{
	
	private static String rootDomainName = "Domain0";
	private static String topDomainName = "Domain0.1";
	private static String subDomainName = "Domain0.1.1";
	private static String domainePolicyName0 = "TestAccessPolicy0";

	private static String baseDn = "dc=nodomain,dc=com";
	private static String identifier= "ID_LDAP_DE_TEST2";
	private static String identifierP= "ID_PARAM_DE_TEST2";
	private static String providerUrl= "ldap://10.75.113.53:389";
	private static String securityAuth= "simple";
	
	
	@Autowired
	private UserProviderService userProviderService;
	
	@Before
	@Transactional (propagation=Propagation.REQUIRED)
	public void setUp() throws Exception {
//		logger.debug("Begin setUp");
//		logger.debug("End setUp");
	}

	@After
	@Transactional (propagation=Propagation.REQUIRED)
	public void tearDown() throws Exception {
//		logger.debug("Begin tearDown");
//		logger.debug("End tearDown");
	}

	@Test
	public void testCreateLDAPConnection() {
		
		LDAPConnection ldapconnexion  = new LDAPConnection(identifier, providerUrl, securityAuth);
		try {
			userProviderService.createLDAPConnection(ldapconnexion);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail("Can't create connection.");
		}
		logger.debug("Current ldapconnexion object: " + ldapconnexion.toString());
	}
	
	
	@Test
	public void testCreateDomainPattern() {
		
		DomainPattern domainPattern = new DomainPattern(identifierP, "blabla", "getUserCommand", "getAllDomainUsersCommand", "authCommand", "searchUserCommand", "mail","firstname","lastname");
		try {
			userProviderService.createDomainPattern(domainPattern);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail("Can't create domain pattern.");
		}
		logger.debug("Current pattern object: " + domainPattern.toString());
	}
	
	@Test
	public void testCreateDeleteLDAPConnection() {
		
		LDAPConnection ldapconnexion  = new LDAPConnection(identifier +"2", providerUrl, securityAuth);
		try {
			userProviderService.createLDAPConnection(ldapconnexion);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail("Can't create connection.");
		}
		logger.debug("Current ldapconnexion object: " + ldapconnexion.toString());
		
		
		try {
			userProviderService.deleteConnection(ldapconnexion.getIdentifier());
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail("Can't delete connection.");
		}
		
	}
	
	@Test
	public void testCreateDeleteDomainPattern() {
		
		
		DomainPattern domainPattern = new DomainPattern(identifierP +"2", "blabla", "getUserCommand", "getAllDomainUsersCommand", "authCommand", "searchUserCommand", "mail","firstname","lastname");
		try {
			userProviderService.createDomainPattern(domainPattern);
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail("Can't create pattern.");
		}
		logger.debug("Current pattern object: " + domainPattern.toString());

		try {
			userProviderService.deletePattern(domainPattern.getIdentifier());
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail("Can't delete pattern.");
		}
	}
}
