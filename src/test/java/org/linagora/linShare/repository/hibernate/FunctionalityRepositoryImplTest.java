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

import java.util.List;

import junit.framework.Assert;

import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.linagora.linShare.core.domain.constants.FileSizeUnit;
import org.linagora.linShare.core.domain.constants.FunctionalityNames;
import org.linagora.linShare.core.domain.constants.LinShareConstants;
import org.linagora.linShare.core.domain.constants.Policies;
import org.linagora.linShare.core.domain.constants.TimeUnit;
import org.linagora.linShare.core.domain.constants.UnitType;
import org.linagora.linShare.core.domain.entities.AbstractDomain;
import org.linagora.linShare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linShare.core.domain.entities.DomainPolicy;
import org.linagora.linShare.core.domain.entities.FileSizeUnitClass;
import org.linagora.linShare.core.domain.entities.Functionality;
import org.linagora.linShare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linShare.core.domain.entities.Policy;
import org.linagora.linShare.core.domain.entities.RootDomain;
import org.linagora.linShare.core.domain.entities.StringValueFunctionality;
import org.linagora.linShare.core.domain.entities.TimeUnitClass;
import org.linagora.linShare.core.domain.entities.UnitRangeFunctionality;
import org.linagora.linShare.core.domain.entities.UnitValueFunctionality;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.AbstractDomainRepository;
import org.linagora.linShare.core.repository.DomainPolicyRepository;
import org.linagora.linShare.core.repository.FunctionalityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations={"classpath:springContext-test.xml", 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml"})
public class FunctionalityRepositoryImplTest extends AbstractJUnit4SpringContextTests {

	
	@Autowired
	private FunctionalityRepository functionalityRepository;
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;
	
	@Autowired
	private DomainPolicyRepository domainPolicyRepository;
	
	
	static private String ID_FONC_1="TEST_TIME_STAMPING";
	static private String ID_FONC_2="TEST_QUOTA_U";
	static private String ID_FONC_3="TEST_EXPIRATION";
	private static String rootDomaineName = "Domain0";
	private static String domainePolicyName0 = "TestAccessPolicy0";
	private static String domainePolicyName1 = "TestAccessPolicy1";
	
	private AbstractDomain currentDomain;
	private DomainPolicy defaultPolicy;
	
	
	
	@Before
	@Transactional (propagation=Propagation.REQUIRED)
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		defaultPolicy = new DomainPolicy(domainePolicyName0, new DomainAccessPolicy());
		logger.debug("Current DomainPolicy : " + defaultPolicy.toString());
		domainPolicyRepository.create(defaultPolicy);
		
		currentDomain= new RootDomain(rootDomaineName,"My root domain");
		currentDomain.setPolicy(defaultPolicy);
		abstractDomainRepository.create(currentDomain);
		logger.debug("Current AbstractDomain object: " + currentDomain.toString());

		logger.debug("End setUp");
	}

	@After
	@Transactional (propagation=Propagation.SUPPORTS)
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		abstractDomainRepository.delete(currentDomain);
		domainPolicyRepository.delete(defaultPolicy);
		logger.debug("End tearDown");
	}
	
	@Test
	public void testCreateStringValueFunctionality() throws BusinessException{
		
		String value = "http://server/service";
		Functionality fonc = new StringValueFunctionality(ID_FONC_1,
						false,
						new Policy(Policies.ALLOWED, true),
						new Policy(Policies.ALLOWED, true),
						currentDomain,
						value);
		
		functionalityRepository.create(fonc);

		logger.debug("Current object: " + fonc.toString());

		StringValueFunctionality entityFonc = (StringValueFunctionality)functionalityRepository.findById(currentDomain,ID_FONC_1);
		Assert.assertTrue(entityFonc.getActivationPolicy().getStatus());
		Assert.assertTrue(entityFonc.getConfigurationPolicy().getStatus());
		Assert.assertEquals(rootDomaineName,entityFonc.getDomain().getIdentifier());
		Assert.assertEquals(value,entityFonc.getValue());
		functionalityRepository.delete(fonc);
	}
	
	@Test
	public void testCreateFileSizeValueFunctionality() throws BusinessException{
		
		Integer value = 1024;
		Functionality fonc = new UnitValueFunctionality(ID_FONC_2,
				false,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				currentDomain,
				value,
				new FileSizeUnitClass(FileSizeUnit.GIGA)
				);
		
		functionalityRepository.create(fonc);
		
		logger.debug("Current object: " + fonc.toString());
		
		
		List<Functionality> a =functionalityRepository.findAll();
		logger.debug("a.size() : " + a.size());
		
		UnitValueFunctionality entityFonc = (UnitValueFunctionality)functionalityRepository.findById(currentDomain,ID_FONC_2);
		Assert.assertFalse(entityFonc.getActivationPolicy().getStatus());
		Assert.assertFalse(entityFonc.getConfigurationPolicy().getStatus());
		Assert.assertEquals(rootDomaineName,entityFonc.getDomain().getIdentifier());
		Assert.assertEquals(value,entityFonc.getValue());
		Assert.assertEquals(UnitType.SIZE, entityFonc.getUnit().getUnitType());
		Assert.assertEquals(FileSizeUnit.GIGA, entityFonc.getUnit().getUnitValue());
	}
	
	@Test
	public void testCreateTimeValueFunctionality() throws BusinessException{
		
		Integer value = 1024;
		Functionality fonc = new UnitValueFunctionality(ID_FONC_2,
				false,
				new Policy(Policies.FORBIDDEN, true),
				new Policy(Policies.MANDATORY, false),
				currentDomain,
				value,
				new TimeUnitClass(TimeUnit.WEEK)
				);
		
		functionalityRepository.create(fonc);
		
		logger.debug("Current object: " + fonc.toString());
		
		UnitValueFunctionality entityFonc = (UnitValueFunctionality)functionalityRepository.findById(currentDomain,ID_FONC_2);
		Assert.assertFalse(entityFonc.getActivationPolicy().getStatus());
		Assert.assertTrue(entityFonc.getConfigurationPolicy().getStatus());
		Assert.assertEquals(rootDomaineName,entityFonc.getDomain().getIdentifier());
		Assert.assertEquals(value,entityFonc.getValue());
		Assert.assertEquals(UnitType.TIME, entityFonc.getUnit().getUnitType());
		Assert.assertEquals(TimeUnit.WEEK, entityFonc.getUnit().getUnitValue());
	}
	
	
	
	@Test
	public void testCreateTwoUnitValueFunctionality() throws BusinessException{
		
		Integer value = 1024;
		Functionality fonc = new UnitValueFunctionality(ID_FONC_2,
				false,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				currentDomain,
				value,
				new FileSizeUnitClass(FileSizeUnit.GIGA)
				);
		
		functionalityRepository.create(fonc);
		
		logger.debug("Current object: " + fonc.toString());
		
		UnitValueFunctionality entityFonc = (UnitValueFunctionality)functionalityRepository.findById(currentDomain,ID_FONC_2);
		Assert.assertFalse(entityFonc.getActivationPolicy().getStatus());
		Assert.assertFalse(entityFonc.getConfigurationPolicy().getStatus());
		Assert.assertEquals(rootDomaineName,entityFonc.getDomain().getIdentifier());
		Assert.assertEquals(value,entityFonc.getValue());
		Assert.assertEquals(UnitType.SIZE, entityFonc.getUnit().getUnitType());
		Assert.assertEquals(FileSizeUnit.GIGA, entityFonc.getUnit().getUnitValue());
	
		
		
		Integer value2 = 256;
		Functionality fonc2 = new UnitValueFunctionality(ID_FONC_3,
				false,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				currentDomain,
				value2,
				new TimeUnitClass(TimeUnit.WEEK)
				);
		
		functionalityRepository.create(fonc2);
		
		logger.debug("Current object: " + fonc2.toString());
		
		UnitValueFunctionality entityFonc2 = (UnitValueFunctionality)functionalityRepository.findById(currentDomain,ID_FONC_3);
		Assert.assertFalse(entityFonc2.getActivationPolicy().getStatus());
		Assert.assertFalse(entityFonc2.getConfigurationPolicy().getStatus());
		Assert.assertEquals(rootDomaineName,entityFonc2.getDomain().getIdentifier());
		Assert.assertEquals(value2,entityFonc2.getValue());
		Assert.assertEquals(UnitType.TIME, entityFonc2.getUnit().getUnitType());
		Assert.assertEquals(TimeUnit.WEEK, entityFonc2.getUnit().getUnitValue());
	}
	
	
	@Test
	public void testCreateFileSizeRangeValueFunctionality() throws BusinessException{
		
		Integer min = 5;
		Integer max = 10;
		
		Functionality fonc = new UnitRangeFunctionality(ID_FONC_1,
						false,
						new Policy(Policies.ALLOWED, true),
						new Policy(Policies.ALLOWED, true),
						currentDomain,
						min,
						max,
						new FileSizeUnitClass(FileSizeUnit.GIGA),
						new FileSizeUnitClass(FileSizeUnit.GIGA));
		
		functionalityRepository.create(fonc);

		logger.debug("Current object: " + fonc.toString());

		UnitRangeFunctionality entityFonc = (UnitRangeFunctionality)functionalityRepository.findById(currentDomain,ID_FONC_1);
		Assert.assertTrue(entityFonc.getActivationPolicy().getStatus());
		Assert.assertTrue(entityFonc.getConfigurationPolicy().getStatus());
		Assert.assertEquals(rootDomaineName,entityFonc.getDomain().getIdentifier());
		
		Assert.assertEquals(FileSizeUnit.GIGA,entityFonc.getMaxUnit().getUnitValue());
		Assert.assertEquals(min,entityFonc.getMin());
		Assert.assertEquals(max,entityFonc.getMax());
		
		functionalityRepository.delete(fonc);
	}
	
	@Test
	public void testCreateTimeRangeValueFunctionality() throws BusinessException{
		
		Integer min = 5;
		Integer max = 10;
		
		Functionality fonc = new UnitRangeFunctionality(ID_FONC_1,
				false,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true),
				currentDomain,
				min,
				max,
				new TimeUnitClass(TimeUnit.WEEK),
				new TimeUnitClass(TimeUnit.MONTH));
		
		functionalityRepository.create(fonc);
		
		logger.debug("Current object: " + fonc.toString());
		
		UnitRangeFunctionality entityFonc = (UnitRangeFunctionality)functionalityRepository.findById(currentDomain,ID_FONC_1);
		Assert.assertTrue(entityFonc.getActivationPolicy().getStatus());
		Assert.assertTrue(entityFonc.getConfigurationPolicy().getStatus());
		Assert.assertEquals(rootDomaineName,entityFonc.getDomain().getIdentifier());
		
		Assert.assertEquals(UnitType.TIME,entityFonc.getMinUnit().getUnitType());
		Assert.assertEquals(TimeUnit.WEEK,entityFonc.getMinUnit().getUnitValue());
		Assert.assertEquals(UnitType.TIME,entityFonc.getMaxUnit().getUnitType());
		Assert.assertEquals(TimeUnit.MONTH,entityFonc.getMaxUnit().getUnitValue());
		Assert.assertEquals(min,entityFonc.getMin());
		Assert.assertEquals(max,entityFonc.getMax());
		
		functionalityRepository.delete(fonc);
	}
	
	
	@Test
	public void testCreateTwoStringValueFunctionality() throws BusinessException{
		
		String value = "http://server/service";
		Functionality fonc = new StringValueFunctionality(ID_FONC_1,
						false,
						new Policy(Policies.ALLOWED, true),
						new Policy(Policies.ALLOWED, true),
						currentDomain,
						value);
		
		functionalityRepository.create(fonc);

		logger.debug("Current object: " + fonc.toString());

		StringValueFunctionality entityFonc = (StringValueFunctionality)functionalityRepository.findById(currentDomain, ID_FONC_1);
		Assert.assertTrue(entityFonc.getActivationPolicy().getStatus());
		Assert.assertTrue(entityFonc.getConfigurationPolicy().getStatus());
		Assert.assertEquals(rootDomaineName,entityFonc.getDomain().getIdentifier());
		Assert.assertEquals(value,entityFonc.getValue());
		
		
		
		
		String rootDomaineName2=rootDomaineName+"-0";
		AbstractDomain currentDomain2= new RootDomain(rootDomaineName2,"My root domain");
		
		DomainPolicy policy = new DomainPolicy(domainePolicyName1, new DomainAccessPolicy());
		domainPolicyRepository.create(policy);
		currentDomain2.setPolicy(policy);
		
		
		abstractDomainRepository.create(currentDomain2);
		logger.debug("Current AbstractDomain object: " + currentDomain2.toString());
		
		String value2 = "http://server/service";
		Functionality fonc2 = new StringValueFunctionality(ID_FONC_1,
						false,
						new Policy(Policies.ALLOWED, true),
						new Policy(Policies.ALLOWED, true),
						currentDomain2,
						value2);
		
		functionalityRepository.create(fonc2);

		logger.debug("Current object: " + fonc2.toString());

		StringValueFunctionality entityFonc2 = (StringValueFunctionality)functionalityRepository.findById(currentDomain2, ID_FONC_1);
		Assert.assertTrue(entityFonc2.getActivationPolicy().getStatus());
		Assert.assertTrue(entityFonc2.getConfigurationPolicy().getStatus());
		Assert.assertEquals(rootDomaineName2,entityFonc2.getDomain().getIdentifier());
		Assert.assertEquals(value2,entityFonc2.getValue());
		
		functionalityRepository.delete(fonc);
		functionalityRepository.delete(fonc2);
		abstractDomainRepository.delete(currentDomain2);
		domainPolicyRepository.delete(policy);
	}
	
	
	@Ignore
	@Test(expected=DataIntegrityViolationException.class)
	public void testUnicityOfFunctionality() throws BusinessException{

		String value = "http://server/service";
		Functionality fonc = new StringValueFunctionality(ID_FONC_1,
						false,
						new Policy(Policies.ALLOWED, true),
						new Policy(Policies.ALLOWED, true),
						currentDomain,
						value);
		
		functionalityRepository.create(fonc);

		logger.debug("Current object: " + fonc.toString());

		StringValueFunctionality entityFonc = (StringValueFunctionality)functionalityRepository.findById(currentDomain, ID_FONC_1);
		Assert.assertTrue(entityFonc.getActivationPolicy().getStatus());
		Assert.assertTrue(entityFonc.getConfigurationPolicy().getStatus());
		Assert.assertEquals(rootDomaineName,entityFonc.getDomain().getIdentifier());
		Assert.assertEquals(value,entityFonc.getValue());
		
		
		
		
		String value2 = "http://server/service";
		Functionality fonc2 = new StringValueFunctionality(ID_FONC_1,
						false,
						new Policy(Policies.ALLOWED, true),
						new Policy(Policies.ALLOWED, true),
						currentDomain,
						value2);
		
		// it should throw a DataIntegrityViolationException
		functionalityRepository.create(fonc2);
		functionalityRepository.delete(fonc);
	}/**/
	
	@Test
	public void testEqualFunctionality() throws BusinessException{
		
		Functionality fonc = new Functionality(ID_FONC_1,
						false,
						new Policy(Policies.ALLOWED, true),
						new Policy(Policies.ALLOWED, true),
						currentDomain);
		
		
		Functionality fonc2 = new Functionality(ID_FONC_1,
						false,
						new Policy(Policies.ALLOWED, true),
						new Policy(Policies.ALLOWED, true),
						currentDomain);
		
		Assert.assertTrue(fonc.businessEquals(fonc2));
		fonc2.setSystem(true);
		Assert.assertFalse(fonc.businessEquals(fonc2));
		
		fonc.setSystem(true);
		fonc2.setIdentifier(ID_FONC_2);
		Assert.assertFalse(fonc.businessEquals(fonc2));
	}
	
	@Test
	public void testEqualStringValueFunctionality() throws BusinessException{
		
		String value = "http://server/service";
		StringValueFunctionality fonc = new StringValueFunctionality(ID_FONC_1,
				false,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true),
				currentDomain,
				value);
		
		
		StringValueFunctionality fonc2 = new StringValueFunctionality(ID_FONC_1,
				false,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true),
				currentDomain,
				value);
		
		Assert.assertTrue(fonc.businessEquals(fonc2));
		String value2 = "http://server/service2";
		fonc2.setValue(value2);
		Assert.assertFalse(fonc.businessEquals(fonc2));
	}
	
	@Test
	public void testEqualIntegerValueFunctionality() throws BusinessException{
		
		Integer value=8;
		IntegerValueFunctionality fonc = new IntegerValueFunctionality(ID_FONC_1,
				false,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true),
				currentDomain,
				value);
		
		IntegerValueFunctionality fonc2 = new IntegerValueFunctionality(ID_FONC_1,
				false,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true),
				currentDomain,
				value);
		
		Assert.assertTrue(fonc.businessEquals(fonc2));
		fonc2.setValue(2);
		Assert.assertFalse(fonc.businessEquals(fonc2));
	}
	
	@Test
	public void testEqualUnitValueFunctionality1() throws BusinessException{
		
		Integer value = 1024;
		UnitValueFunctionality fonc = new UnitValueFunctionality(ID_FONC_2,
				false,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				currentDomain,
				value,
				new FileSizeUnitClass(FileSizeUnit.GIGA)
				);
		
		UnitValueFunctionality fonc2 = new UnitValueFunctionality(ID_FONC_2,
				false,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				currentDomain,
				value,
				new FileSizeUnitClass(FileSizeUnit.GIGA)
				);
		
		Assert.assertTrue(fonc.businessEquals(fonc2));
		fonc2.setValue(8);
		Assert.assertFalse(fonc.businessEquals(fonc2));
	}
	
	@Test
	public void testEqualUnitValueFunctionality2() throws BusinessException{
		
		Integer value = 1024;
		UnitValueFunctionality fonc = new UnitValueFunctionality(ID_FONC_2,
				false,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				currentDomain,
				value,
				new FileSizeUnitClass(FileSizeUnit.GIGA)
				);
		
		UnitValueFunctionality fonc2 = new UnitValueFunctionality(ID_FONC_2,
				false,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				currentDomain,
				value,
				new TimeUnitClass(TimeUnit.WEEK)
				);
		
		UnitValueFunctionality fonc3 = new UnitValueFunctionality(ID_FONC_2,
				false,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				currentDomain,
				value,
				new TimeUnitClass(TimeUnit.DAY)
				);
		
		Assert.assertFalse(fonc.businessEquals(fonc2));
		Assert.assertFalse(fonc2.businessEquals(fonc3));
	}
	
	@Test
	public void testEqualUnitRangeFunctionality() throws BusinessException{
		
		Integer min = 5;
		Integer max = 10;
		
		UnitRangeFunctionality fonc = new UnitRangeFunctionality(ID_FONC_1,
						false,
						new Policy(Policies.ALLOWED, true),
						new Policy(Policies.ALLOWED, true),
						currentDomain,
						min,
						max,
						new FileSizeUnitClass(FileSizeUnit.GIGA),
						new FileSizeUnitClass(FileSizeUnit.GIGA));
		
		
		UnitRangeFunctionality fonc2 = new UnitRangeFunctionality(ID_FONC_1,
						false,
						new Policy(Policies.ALLOWED, true),
						new Policy(Policies.ALLOWED, true),
						currentDomain,
						min,
						max,
						new FileSizeUnitClass(FileSizeUnit.GIGA),
						new FileSizeUnitClass(FileSizeUnit.GIGA));
		
		Assert.assertTrue(fonc.businessEquals(fonc2));
		fonc.setMin(30);
		Assert.assertFalse(fonc.businessEquals(fonc2));
		fonc2.setMin(30);
		Assert.assertTrue(fonc.businessEquals(fonc2));
		if(fonc2.getMinUnit().getUnitType().equals(UnitType.SIZE)) {
			((FileSizeUnitClass) fonc2.getMinUnit()).setUnitValue(FileSizeUnit.KILO);
		}
		Assert.assertFalse(fonc.businessEquals(fonc2));
	}

	@Test
	public void testCloneFunctionality() throws BusinessException{
		
		Functionality func = new Functionality(ID_FONC_1,
				false,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true),
				currentDomain);
		
		Functionality newFunc = (Functionality) func.clone();
		Assert.assertTrue(newFunc.businessEquals(func));
		
		func.setSystem(true);
		Assert.assertFalse(newFunc.businessEquals(func));
	}
	
	
	@Test
	public void testCloneStringFunctionality() throws BusinessException{
		AbstractDomain otherDomain= new RootDomain(rootDomaineName+"_other","My root domain");
		DomainPolicy policy = new DomainPolicy(domainePolicyName1, new DomainAccessPolicy());
		domainPolicyRepository.create(policy);
		otherDomain.setPolicy(policy);
		
		abstractDomainRepository.create(otherDomain);
		logger.debug("otherDomain object: " + otherDomain.toString());
		
		
		String value = "http://server/service";
		StringValueFunctionality func = new StringValueFunctionality(ID_FONC_1,
				false,
				new Policy(Policies.ALLOWED, true),
				new Policy(Policies.ALLOWED, true),
				currentDomain,
				value);
		
		functionalityRepository.create(func);
		logger.debug("Current object func : " + func.toString());
		
		
		Functionality newFunc = (Functionality) func.clone();
		newFunc.setDomain(otherDomain);
		
		functionalityRepository.create(newFunc);
		logger.debug("Current object newFunc : " + newFunc.toString());
		
		
		Assert.assertTrue(newFunc.businessEquals(func));
		Assert.assertNotNull(newFunc.getDomain());
		
		func.setValue("plop");
		Assert.assertFalse(newFunc.businessEquals(func));
		
		abstractDomainRepository.delete(otherDomain);
		domainPolicyRepository.delete(policy);
	}
	
	@Test
	public void testCloneUnitValueFunctionality() throws BusinessException{
		AbstractDomain otherDomain= new RootDomain(rootDomaineName+"_other","My root domain");
		DomainPolicy policy = new DomainPolicy(domainePolicyName1, new DomainAccessPolicy());
		domainPolicyRepository.create(policy);
		otherDomain.setPolicy(policy);
		
		abstractDomainRepository.create(otherDomain);
		logger.debug("otherDomain object: " + otherDomain.toString());
		
		
		Integer value = 1024;
		UnitValueFunctionality func = new UnitValueFunctionality(ID_FONC_2,
				false,
				new Policy(Policies.ALLOWED, false),
				new Policy(Policies.ALLOWED, false),
				currentDomain,
				value,
				new FileSizeUnitClass(FileSizeUnit.GIGA)
				);
		
		functionalityRepository.create(func);
		logger.debug("Current object func : " + func.toString());
		
		
		Functionality newFunc = (Functionality) func.clone();
		newFunc.setDomain(otherDomain);
		
		functionalityRepository.create(newFunc);
		logger.debug("Current object newFunc : " + newFunc.toString());
		
		
		Assert.assertTrue(newFunc.businessEquals(func));
		Assert.assertNotNull(newFunc.getDomain());
		
		func.setValue(256);
		Assert.assertFalse(newFunc.businessEquals(func));
		Assert.assertTrue(newFunc instanceof UnitValueFunctionality);
		
		
		abstractDomainRepository.delete(otherDomain);
		domainPolicyRepository.delete(policy);
	}
	
	
	@Test
	public void testCloneFileSizeRangeValueFunctionality() throws BusinessException{
		AbstractDomain otherDomain= new RootDomain(rootDomaineName+"_other","My root domain");
		DomainPolicy policy = new DomainPolicy(domainePolicyName1, new DomainAccessPolicy());
		domainPolicyRepository.create(policy);
		otherDomain.setPolicy(policy);
		
		abstractDomainRepository.create(otherDomain);
		logger.debug("otherDomain object: " + otherDomain.toString());
		
		Integer min = 5;
		Integer max = 10;
		
		UnitRangeFunctionality func = new UnitRangeFunctionality(ID_FONC_1,
						false,
						new Policy(Policies.ALLOWED, true),
						new Policy(Policies.ALLOWED, true),
						currentDomain,
						min,
						max,
						new FileSizeUnitClass(FileSizeUnit.GIGA),
						new FileSizeUnitClass(FileSizeUnit.GIGA));
		
		functionalityRepository.create(func);

		logger.debug("Current object func : " + func.toString());

	
		Functionality newFunc = (Functionality) func.clone();
		newFunc.setDomain(otherDomain);
		
		functionalityRepository.create(newFunc);
		logger.debug("Current object newFunc : " + newFunc.toString());
		
		
		Assert.assertTrue(newFunc.businessEquals(func));
		Assert.assertNotNull(newFunc.getDomain());
		
		func.setMax(42);
		Assert.assertFalse(newFunc.businessEquals(func));
		Assert.assertTrue(newFunc instanceof UnitRangeFunctionality);
		
		
		abstractDomainRepository.delete(otherDomain);
		domainPolicyRepository.delete(policy);
		
	}	
	
	
	
	
	
	
	
	
	
	
	
	
}
