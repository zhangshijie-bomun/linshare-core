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
package org.linagora.linShare.view.tapestry.pages.administration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linShare.core.Facade.AbstractDomainFacade;
import org.linagora.linShare.core.Facade.FunctionalityFacade;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.constants.FileSizeUnit;
import org.linagora.linShare.core.domain.constants.FunctionalityNames;
import org.linagora.linShare.core.domain.constants.TimeUnit;
import org.linagora.linShare.core.domain.entities.ShareExpiryRule;
import org.linagora.linShare.core.domain.vo.FunctionalityVo;
import org.linagora.linShare.core.domain.vo.IntegerValueFunctionalityVo;
import org.linagora.linShare.core.domain.vo.SizeValueFunctionalityVo;
import org.linagora.linShare.core.domain.vo.StringValueFunctionalityVo;
import org.linagora.linShare.core.domain.vo.TimeValueBooleanFunctionalityVo;
import org.linagora.linShare.core.domain.vo.TimeValueFunctionalityVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Parameters {

	private static Logger logger = LoggerFactory.getLogger(Parameters.class);

    @SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */

	@Inject
	private Messages messages;
    @Inject
    private AbstractDomainFacade abstractDomainFacade;
    
    @Inject
    private FunctionalityFacade functionalityFacade;
    
    @Inject
    private UserFacade userFacade;
    
    @SessionState
    private UserVo loginUser;
    
	@Persist
	@Property
	private String selectedDomain;
	
	@Persist
	@Property
	private List<String> domains;
	@Property
	@Persist
	private boolean superadmin;
	
	@Property
	@Persist
	private boolean admin;

	@Property
	private boolean noDomain;
	
	
				// Functionalities
	
	//	FILESIZE_MAX 
	@Persist
	@Property
	private SizeValueFunctionalityVo userMaxFileSizeFunctionality;
//	@Persist
	@Property
	private boolean showUserMaxFileSize;
//	@Persist
	@Property
	private Integer userMaxFileSize;
//	@Persist
	@Property
	private FileSizeUnit userMaxFileSizeUnit;
	
	
	//	QUOTA_GLOBAL
	@Persist
	@Property
	private SizeValueFunctionalityVo globalQuotaFunctionality;
	@Property
	private boolean showGlobalQuota = false;
	@Property
	private Integer globalQuota;
	@Property
	private FileSizeUnit globalQuotaUnit;
	
	
	//	QUOTA_USER
	@Persist
	@Property
	private SizeValueFunctionalityVo userQuotaFunctionality;
	@Property
	private boolean showUserQuota = false;
	@Property
	private Integer userQuota;
	@Property
	private FileSizeUnit userQuotaUnit;
	
	
	//	ACCOUNT_EXPIRATION
	@Persist
	@Property
	private TimeValueFunctionalityVo guestAccountExpiryTimeFunctionality;
	@Property
	private boolean showGuestAccountExpiryTime = false;
	@Property
    private Integer guestAccountExpiryTime;
	@Property
	private TimeUnit guestAccountExpiryUnit;

	
	//	FILE_EXPIRATION
	@Persist
	@Property
	private TimeValueFunctionalityVo defaultFileExpiryTimeFunctionality;
	@Property
	private boolean showDefaultFileExpiryTime = false;
	@Property
    private TimeUnit defaultFileExpiryUnit;
    @Property
    private Integer defaultFileExpiryTime;

	
    //	SHARE_EXPIRATION
	@Persist
	@Property
	private TimeValueBooleanFunctionalityVo defaultShareExpiryTimeFunctionality;
	@Property
	private boolean showDefaultShareExpiryTime = false;
	@Property
	private boolean deleteDocWithShareExpiryTime = false;
	@Property
    private TimeUnit defaultShareExpiryUnit;
    @Property
    private Integer defaultShareExpiryTime;
    
    @SuppressWarnings("unused")
	@Property
    private ShareExpiryRule shareExpiryRule;
    @Persist
    @Property
    private List<ShareExpiryRule> shareExpiryRules;
    
	
	
    //	TIME_STAMPING
	@Persist
	@Property
	private StringValueFunctionalityVo timeStampingFunctionality;

	@Property
	private boolean showTimeStamping = false;
	@Property
	private String timeStampingUrl;
		
	 
	//	CUSTOM_LOGO
	@Persist
	@Property
	private StringValueFunctionalityVo customLogoFunctionality;
	@Property
	private boolean showCustomLogo = false;
	@Property
	private String customLogo;

	
	//	COMPLETION
	@Persist
	@Property
	private IntegerValueFunctionalityVo completionFunctionality;
	@Property
	private boolean showCompletion= false;
	@Property
	private Integer autoCompleteThreshold;
	
	
	
	@Property
	@Persist
	private List<FunctionalityVo> functionalities;
	
	
	@InjectComponent
	private Form parameterForm;
	
	@Property
	@Persist
	private FunctionalityVo functionalityRow;

    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
    @SetupRender
    public void init() throws BusinessException {
    }   
    

	public Object onActivate(String domainIdentifier) throws BusinessException {
		logger.debug("domainIdentifier:" + domainIdentifier);
		selectedDomain = domainIdentifier;
		
		domains = abstractDomainFacade.getAllDomainIdentifiers(loginUser);
		if(!domains.contains(selectedDomain)) {
			shareSessionObjects.addError(messages.get("pages.error.badAuth.message"));
			return org.linagora.linShare.view.tapestry.pages.administration.Index.class;
    	}
		
		
		
		functionalities = functionalityFacade.getAllParameters(domainIdentifier);
		
		Collections.sort(functionalities);
		
		for (FunctionalityVo functionality : functionalities) {
			if(functionality.getIdentifier().equals(FunctionalityNames.FILESIZE_MAX)) {
				userMaxFileSizeFunctionality = (SizeValueFunctionalityVo)functionality; 
				showUserMaxFileSize = true;
				userMaxFileSize = userMaxFileSizeFunctionality.getSize();
				userMaxFileSizeUnit = userMaxFileSizeFunctionality.getUnit();
				
				
			} else if(functionality.getIdentifier().equals(FunctionalityNames.QUOTA_GLOBAL)) {
				globalQuotaFunctionality = (SizeValueFunctionalityVo)functionality;
				showGlobalQuota = true;
				globalQuota = globalQuotaFunctionality.getSize();
				globalQuotaUnit = globalQuotaFunctionality.getUnit();
				
			} else if(functionality.getIdentifier().equals(FunctionalityNames.QUOTA_USER)) {
				userQuotaFunctionality = (SizeValueFunctionalityVo)functionality;
				showUserQuota = true;
				userQuota = userQuotaFunctionality.getSize();
				userQuotaUnit = userQuotaFunctionality.getUnit();
				
			} else if(functionality.getIdentifier().equals(FunctionalityNames.ACCOUNT_EXPIRATION)) {
				guestAccountExpiryTimeFunctionality = (TimeValueFunctionalityVo)functionality;
				showGuestAccountExpiryTime = true;
				guestAccountExpiryTime = guestAccountExpiryTimeFunctionality.getTime();
				guestAccountExpiryUnit = guestAccountExpiryTimeFunctionality.getUnit();
				
			} else if(functionality.getIdentifier().equals(FunctionalityNames.FILE_EXPIRATION)) {
				defaultFileExpiryTimeFunctionality = (TimeValueFunctionalityVo)functionality;
				showDefaultFileExpiryTime = true;
				defaultFileExpiryTime = defaultFileExpiryTimeFunctionality.getTime();
				defaultFileExpiryUnit = defaultFileExpiryTimeFunctionality.getUnit();
				
			} else if(functionality.getIdentifier().equals(FunctionalityNames.SHARE_EXPIRATION)) {
				defaultShareExpiryTimeFunctionality = (TimeValueBooleanFunctionalityVo)functionality;
				showDefaultShareExpiryTime = true;
				defaultShareExpiryUnit = defaultShareExpiryTimeFunctionality.getUnit();
				defaultShareExpiryTime = defaultShareExpiryTimeFunctionality.getTime();
				deleteDocWithShareExpiryTime = defaultShareExpiryTimeFunctionality.isBool();
				
				shareExpiryRules = abstractDomainFacade.getShareExpiryRules(domainIdentifier);
		        if (shareExpiryRules == null) {
		            shareExpiryRules = new ArrayList<ShareExpiryRule>();
		        }
				
			} else if(functionality.getIdentifier().equals(FunctionalityNames.TIME_STAMPING)) {
				timeStampingFunctionality = (StringValueFunctionalityVo)functionality;
				showTimeStamping = true;
				timeStampingUrl = timeStampingFunctionality.getValue();
				
			} else if(functionality.getIdentifier().equals(FunctionalityNames.CUSTOM_LOGO)) {
				customLogoFunctionality = (StringValueFunctionalityVo) functionality;
				showCustomLogo = true;
				customLogo = customLogoFunctionality.getValue();
				
			} else if(functionality.getIdentifier().equals(FunctionalityNames.COMPLETION)) {
				completionFunctionality = (IntegerValueFunctionalityVo) functionality;
				showCompletion = true;
				autoCompleteThreshold = completionFunctionality.getValue();
				
			} else {
				logger.error("Unknown Functionality Form for : " + functionality.getIdentifier());
			}
		}
		return null;
	}
	
	public Object onSuccessFromParameterForm() throws BusinessException {
		logger.debug("onSuccessFromParameterForm");
		if(userMaxFileSizeFunctionality != null ) {
			userMaxFileSizeFunctionality.setSize(userMaxFileSize );
			userMaxFileSizeFunctionality.setUnit(userMaxFileSizeUnit);
		}
		if(globalQuotaFunctionality != null ){
			globalQuotaFunctionality.setSize(globalQuota);
			globalQuotaFunctionality.setUnit(globalQuotaUnit);
		}
		if(userQuotaFunctionality != null ){
			userQuotaFunctionality.setSize(userQuota);
			userQuotaFunctionality.setUnit(userQuotaUnit);
		}
		if(guestAccountExpiryTimeFunctionality != null ){
			guestAccountExpiryTimeFunctionality.setTime(guestAccountExpiryTime);
			guestAccountExpiryTimeFunctionality.setUnit(guestAccountExpiryUnit);
		}
		if(defaultFileExpiryTimeFunctionality != null ){
			defaultFileExpiryTimeFunctionality.setTime(defaultFileExpiryTime);
			defaultFileExpiryTimeFunctionality.setUnit(defaultFileExpiryUnit);
		}
		if(defaultShareExpiryTimeFunctionality != null ){
			defaultShareExpiryTimeFunctionality.setUnit(defaultShareExpiryUnit);
			defaultShareExpiryTimeFunctionality.setTime(defaultShareExpiryTime);
			defaultShareExpiryTimeFunctionality.setBool(deleteDocWithShareExpiryTime);
			
			abstractDomainFacade.updateShareExpiryRules(loginUser, selectedDomain, shareExpiryRules);
		}
		if(timeStampingFunctionality != null ){
			timeStampingFunctionality.setValue(timeStampingUrl);
		}
		if(customLogoFunctionality != null ){
			customLogoFunctionality.setValue(customLogo);
		}
		
		if(completionFunctionality != null ){
			completionFunctionality.setValue(autoCompleteThreshold);
		}

		logger.debug("functionalities.size : " + functionalities.size());
		for (FunctionalityVo f : functionalities ) {
			logger.debug(f.toString());
		}
		functionalityFacade.updateParameters(loginUser, functionalities);
		return org.linagora.linShare.view.tapestry.pages.administration.Index.class;
	}
	
	
	public boolean getNeedAccountParameterTitle() {
		if(showUserMaxFileSize || showGlobalQuota || showUserQuota || showGuestAccountExpiryTime) {
			return true;
		}
		return false;
	}
	
	public boolean getNeedOtherTitle() {
		if(showCustomLogo || showTimeStamping || showCompletion) {
			return true;
		}
		return false;
	}
	
	
	 public ValueEncoder<ShareExpiryRule> getShareExpiryRuleEncoder() {
    	return new ValueEncoder<ShareExpiryRule>() {
			public String toClient(ShareExpiryRule value) {
                return ""+shareExpiryRules.indexOf(value);
			}

			public ShareExpiryRule toValue(String clientValue) {
				int key = Integer.parseInt(clientValue);
                if (shareExpiryRules.size() > key) {
                    return shareExpiryRules.get(key);
                } else {
                    return null;
                }
			}
    	};
    }

    public Object onAddRow() {
        ShareExpiryRule expiryRule = new ShareExpiryRule();
        shareExpiryRules.add(expiryRule);
        return expiryRule;
    }

    public void onRemoveRow(ShareExpiryRule expiryRule) {
        shareExpiryRules.remove(expiryRule);
    }
    
	    
	
    Object onException(Throwable cause) {
    	shareSessionObjects.addError(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }
    
    
    public Object onActionFromCancel() {
		functionalities = null;
		return org.linagora.linShare.view.tapestry.pages.administration.Index.class;
	}
//    
//    public void onValidateFormFromAdministrationForm() {
//        boolean activeEnciph = securedStorageDisallowed ? false : activeEncipherment;
//    	
//    	//just validate JCE
//    	if(activeEnciph==true||activeSignature==true){
//    		if(!parameterFacade.checkPlatformEncryptSupportedAlgo()){
//    			administrationForm.recordError(messages.get("pages.administration.index.jce.error"));
//    		}
//    	}
//    }
}
