/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016-2018 LINAGORA
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

package org.linagora.linshare.webservice.delegationv2.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestCreationDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestGroupDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.ContactDto;
import org.linagora.linshare.core.facade.webservice.user.UploadRequestGroupFacade;
import org.linagora.linshare.webservice.delegationv2.UploadRequestGroupRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Api(value = "/rest/delegation/v2/{actorUuid}/upload_request_groups", description = "group requests API")
@Path("/{actorUuid}/upload_request_groups")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class UploadRequestGroupRestServiceImpl implements UploadRequestGroupRestService {

	private final UploadRequestGroupFacade uploadRequestGroupFacade;

	public UploadRequestGroupRestServiceImpl(final UploadRequestGroupFacade uploadRequestGroupFacade) {
		super();
		this.uploadRequestGroupFacade = uploadRequestGroupFacade;
	}

	@GET
	@Path("/")
	@ApiOperation(value = "Find a list of upload request group.", response = UploadRequestGroupDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Authentication failed."),
	@ApiResponse(code = 401, message = "Unauthorized.") })
	@Override
	public List<UploadRequestGroupDto> findAll(
			@ApiParam(value = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@ApiParam(value = "Values t filter upload resquets by status", required = false) @QueryParam("filter") List<UploadRequestStatus> statusList)
			throws BusinessException {
		return uploadRequestGroupFacade.findAll(actorUuid, statusList);
	}

	@GET
	@Path("/{uuid}")
	@ApiOperation(value = "Find an upload request group.", response = UploadRequestGroupDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Authentication failed."),
			@ApiResponse(code = 401, message = "Unauthorized."), @ApiResponse(code = 404, message = "Not found.") })
	@Override
	public UploadRequestGroupDto find(
			@ApiParam(value = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@ApiParam(value = "Upload request group uuid.", required = true) @PathParam(value = "uuid") String uuid)
			throws BusinessException {
		return uploadRequestGroupFacade.find(actorUuid, uuid);
	}
	
	@POST
	@Path("/")
	@ApiOperation(value = "Create an upload request.", response = UploadRequestDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Authentication failed.") })
	@Override
	public List<UploadRequestDto> create(
			@ApiParam(value = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@ApiParam(value = "Upload request.", required = true) UploadRequestCreationDto uploadRequestCreationDto,
			@ApiParam(value = "Group mode.", required = true) @QueryParam(value = "groupMode") Boolean groupMode) {
		List<UploadRequestDto> dto = uploadRequestGroupFacade.create(actorUuid, uploadRequestCreationDto, groupMode);
		return dto;
	}

	@PUT
	@Path("/{uuid}/update/{status}")
	@ApiOperation(value = "Update an upload request group.", response = UploadRequestDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Authentication failed."),
			@ApiResponse(code = 401, message = "Unauthorized."), @ApiResponse(code = 404, message = "Not found.") })
	@Override
	public UploadRequestGroupDto updateStatus(
			@ApiParam(value = "The actor (user) uuid.", required = true)
				@PathParam("actorUuid") String actorUuid,
			@ApiParam(value = "Upload request uuid.", required = true)
				@PathParam(value = "uuid") String requestUuid,
			@ApiParam(value = "Upload request status.", required = true)
				@PathParam("status") UploadRequestStatus status,
			@ApiParam(value = "If the owner wants to copy all documents and the upload request is in archived status", required = false)
				@QueryParam("copy") @DefaultValue("false") boolean copy) throws BusinessException {
		return uploadRequestGroupFacade.updateStatus(actorUuid, requestUuid, status, copy);
	}

	@PUT
	@Path("/")
	@ApiOperation(value = "update an upload request group", response = UploadRequestDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Authentication failed.") })
	@Override
	public UploadRequestGroupDto update(
			@ApiParam(value = "The actor (user) uuid.", required = true) @PathParam("actorUuid") String actorUuid,
			@ApiParam(value = "Upload request group", required = true) UploadRequestGroupDto uploadRequestGroupDto) {
		return uploadRequestGroupFacade.update(actorUuid, uploadRequestGroupDto);
	}

	@POST
	@Path("/{groupUuid}/recipients")
	@ApiOperation(value = "Add new recipient to upload request group.", response = UploadRequestDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Authentication failed.") })
	@Override
	public UploadRequestGroupDto addRecipient(
			@ApiParam(value = "The actor (user) uuid.", required = true) 
				@PathParam("actorUuid") String actorUuid,
			@ApiParam(value = "Upload request group uuid", required = true)
				@PathParam(value = "groupUuid") String groupUuid,
			@ApiParam(value = "List of new recipients", required = true) List<ContactDto> recipientEmail) {
		return uploadRequestGroupFacade.addRecipients(actorUuid, groupUuid, recipientEmail);
	}
}