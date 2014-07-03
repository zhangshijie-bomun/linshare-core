package org.linagora.linshare.webservice.uploadrequest;

import javax.ws.rs.core.Response;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.EntryDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.UploadRequestDto;

public interface UploadRequestRestService {

	Response find(String uuid, String password) throws BusinessException;

	UploadRequestDto update(UploadRequestDto dto, String password)
			throws BusinessException;

	void delete(String uuid, String password, String entryUuid)
			throws BusinessException;

	void delete(String uuid, String password, EntryDto entry)
			throws BusinessException;
}
