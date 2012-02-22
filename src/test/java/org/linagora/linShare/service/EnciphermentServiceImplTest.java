package org.linagora.linShare.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linShare.core.dao.FileSystemDao;
import org.linagora.linShare.core.domain.constants.LinShareTestConstants;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.Signature;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.objects.FileInfo;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.AbstractDomainRepository;
import org.linagora.linShare.core.repository.DocumentRepository;
import org.linagora.linShare.core.repository.DomainPolicyRepository;
import org.linagora.linShare.core.repository.FunctionalityRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.EnciphermentService;
import org.linagora.linShare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;


@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-startopends.xml",
		"classpath:springContext-jackRabbit.xml",
		"classpath:springContext-test.xml"
		})
public class EnciphermentServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests{

	private static Logger logger = LoggerFactory.getLogger(EnciphermentServiceImplTest.class);
	
	@Autowired
	private FunctionalityRepository functionalityRepository;
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;
	
	@Autowired
	private DomainPolicyRepository domainPolicyRepository;
	
	
	@Autowired
	private EnciphermentService enciphermentService;
	
	@Autowired
	private DocumentRepository documentRepository;
	
	@Autowired
	private FileSystemDao fileRepository;
	
	@Qualifier("userRepository")
	@Autowired
	private UserRepository<User> userRepository;
	
	@Autowired
	private UserService userService;
	
	private InputStream inputStream;
	private String inputStreamUuid;
	private User Jane;
	private Document aDocument;
	
	private LoadingServiceTestDatas datas;
	
	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		
		datas = new LoadingServiceTestDatas(functionalityRepository,abstractDomainRepository,domainPolicyRepository,userRepository,userService);
		datas.loadUsers();
		
		Jane = datas.getUser2();
		
		inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("linShare-default.properties");
		inputStreamUuid = fileRepository.insertFile(Jane.getLogin(), inputStream, 10000, "linShare-default.properties", "text/plain");
				
		FileInfo inputStreamInfo = fileRepository.getFileInfoByUUID(inputStreamUuid);
		
		Calendar lastModifiedLin = inputStreamInfo.getLastModified();
		Calendar exp=inputStreamInfo.getLastModified();
		exp.add(Calendar.HOUR, 4);
		
		aDocument = new Document(inputStreamUuid,inputStreamInfo.getName(),inputStreamInfo.getMimeType(),lastModifiedLin,exp, Jane,false,false,false,new Long(10000));
		List<Signature> signatures = new ArrayList<Signature>();
		aDocument.setSignatures(signatures);
		
		try {
			documentRepository.create(aDocument);
			Jane.addDocument(aDocument);
			userRepository.update(Jane);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (BusinessException e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		
		
		logger.debug(LinShareTestConstants.END_SETUP);
	}
	
	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		
		logger.debug("aDocument.getIdentifier : " + aDocument.getIdentifier());
		printDocs(Jane);
		documentRepository.delete(aDocument);
//		Jane.deleteDocument(aDocument);
		Jane.getDocuments().clear();
		userRepository.update(Jane);
		fileRepository.removeFileByUUID(aDocument.getIdentifier());
		datas.deleteUsers();
		
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}
	
	@Test
	public void testChangeDocumentExtension() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		String documentName = new String("foobar.txt");
		String documentNameEncipher = new String("foobar.txt.aes");
		
		String documentNameChanged = enciphermentService.changeDocumentExtension(documentName);

		Assert.assertTrue(documentNameEncipher.equalsIgnoreCase(documentNameChanged));
		
		String documentNameChanged2 = enciphermentService.changeDocumentExtension(documentNameChanged);
		
		Assert.assertTrue(documentName.equalsIgnoreCase(documentNameChanged2));
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testIsDocumentEncrypted() throws UnsupportedEncodingException, GeneralSecurityException, BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Calendar expirationDate = Calendar.getInstance();
		
		// Add 2 years from the actual date
		expirationDate.add(Calendar.YEAR, 2);
		
		DocumentVo doc = new DocumentVo(aDocument.getIdentifier(),"doc","",Calendar.getInstance(),expirationDate,"doc","user1@linpki.org",false,false,false,(long) 10);

		Assert.assertFalse(enciphermentService.isDocumentEncrypted(doc));
		
		aDocument.setEncrypted(true);
		
		Assert.assertTrue(enciphermentService.isDocumentEncrypted(doc));
		
		logger.debug(LinShareTestConstants.END_TEST);

	}
	
	@Test
	public void testEncryptDocument() throws BusinessException, IOException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Calendar expirationDate = Calendar.getInstance();
		// Add 2 years from the actual date
		expirationDate.add(Calendar.YEAR, -2);
		
		logger.debug("inputStreamUuid : " + inputStreamUuid);
		logger.debug("aDocument.getIdentifier : " + aDocument.getIdentifier());

		DocumentVo docVo = new DocumentVo(aDocument.getIdentifier(),"doc","",Calendar.getInstance(),expirationDate,"doc",Jane.getLogin(),false,false,false,(long) 10);
		
		UserVo userVo = new UserVo(Jane);
		printDocs(Jane);
		
		Document encryptedDoc = enciphermentService.encryptDocument(docVo, userVo, "password");
		logger.debug("encryptedDoc.getIdentifier : " + encryptedDoc.getIdentifier());
		logger.debug("aDocument.getIdentifier : " + aDocument.getIdentifier());
		logger.debug("inputStreamUuid : " + inputStreamUuid);
		
		printDocs(Jane);		
		
		aDocument = encryptedDoc;
		
		Assert.assertTrue(aDocument.getEncrypted());
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	private void printDocs(User user) {
		logger.debug("begin : " + user.getLogin());
		for (Document doc : user.getDocuments()) {
			logger.debug("doc : " + doc.getIdentifier());
			
		}
		logger.debug("end");
	}
	
	@Test
	public void testDecryptDocument() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Calendar expirationDate = Calendar.getInstance();
		// Add 2 years from the actual date
		expirationDate.add(Calendar.YEAR, -2);

		DocumentVo doc = new DocumentVo(aDocument.getIdentifier(),"doc","",Calendar.getInstance(),expirationDate,"doc",Jane.getLogin(),false,false,false,(long) 10);

		UserVo userVo = new UserVo(Jane);
		
		Document encryptedDoc = enciphermentService.encryptDocument(doc, userVo, "password");
		Assert.assertTrue(aDocument.getEncrypted());
		
		
		// Instantiate new DocumentVo encrypted
		doc = new DocumentVo(aDocument.getIdentifier(),"doc","",Calendar.getInstance(),expirationDate,"doc",Jane.getLogin(),false,false,false,(long) 10);
		
		Document decryptedDoc = enciphermentService.decryptDocument(doc, userVo, "password");
		Assert.assertFalse(aDocument.getEncrypted());
		
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
}
