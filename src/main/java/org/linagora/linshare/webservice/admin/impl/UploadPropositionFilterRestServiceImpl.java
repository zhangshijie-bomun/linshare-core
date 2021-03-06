/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
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

package org.linagora.linshare.webservice.admin.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.UploadPropositionFilterFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadPropositionFilterDto;
import org.linagora.linshare.mongo.entities.UploadPropositionFilter;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.UploadPropositionFilterRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/*
 * TODO:
 * - swagger documentation
 */
@Path("/upload_proposition_filters")
@Api(value = "/rest/admin/upload_proposition_filters", description = "Upload proposition filters API")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class UploadPropositionFilterRestServiceImpl extends WebserviceBase
		implements UploadPropositionFilterRestService {

	private UploadPropositionFilterFacade uploadPropositionFilterFacade;

	public UploadPropositionFilterRestServiceImpl(
			UploadPropositionFilterFacade uploadPropositionFilterFacade) {
		super();
		this.uploadPropositionFilterFacade = uploadPropositionFilterFacade;
	}

	@Path("/domains/{domainUuid}")
	@GET
	@ApiOperation(value = "Find all filters.", response = UploadPropositionFilter.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't super admin."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error.") })
	@Override
	public List<UploadPropositionFilter> findAll(
			@ApiParam(value = "Domain uuid", required = true)
				@PathParam("domainUuid") String domainUuid)
			throws BusinessException {
		return uploadPropositionFilterFacade.findAll(domainUuid);
	}

	@Path("/{uuid}/domains/{domainUuid}")
	@GET
	@ApiOperation(value = "Find a filter", response = UploadPropositionFilterDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't super admin."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error.") })
	@Override
	public UploadPropositionFilter find(
			@ApiParam(value = "Filter uuid", required = true)
				@PathParam("uuid") String uuid,
			@ApiParam(value = "Domain uuid", required = true)
				@PathParam("domainUuid") String domainUuid)
			throws BusinessException {
		return uploadPropositionFilterFacade.find(uuid, domainUuid);
	}

	@Path("/{uuid}/domains/{domainUuid}")
	@HEAD
	@ApiOperation(value = "Find a filter")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't super admin."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error.") })
	@Override
	public void head(
			@ApiParam(value = "Filter uuid", required = true)
				@PathParam("uuid") String uuid,
			@ApiParam(value = "Domain uuid", required = true)
				@PathParam("domainUuid") String domainUuid)
			throws BusinessException {
		uploadPropositionFilterFacade.find(uuid, domainUuid);
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Create a filter.", response = UploadPropositionFilter.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't super admin."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error.") })
	@Override
	public UploadPropositionFilter create(
			@ApiParam(value = "Payload to create a upload proposition filter", required = true) UploadPropositionFilter uploadPropositionFilter)
			throws BusinessException {
		return uploadPropositionFilterFacade.create(uploadPropositionFilter);
	}

	@Path("/{uuid : .*}")
	@PUT
	@ApiOperation(value = "Update a filter.", response = UploadPropositionFilter.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't super admin."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error.") })
	@Override
	public UploadPropositionFilter update(
			@ApiParam(value = "Payload to update an upload proposition filter", required = true) UploadPropositionFilter uploadPropositionFilter,
			@ApiParam(value = "Filter uuid, if null uploadPropositionFilter.uuid is used.", required = false)
					@PathParam("uuid") String uuid)
			throws BusinessException {
		return uploadPropositionFilterFacade.update(uploadPropositionFilter, uuid);
	}

	@Path("/{uuid : .*}")
	@DELETE
	@ApiOperation(value = "Delete a filter.", response = UploadPropositionFilterDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't super admin."),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error.") })
	@Override
	public UploadPropositionFilter delete(
			@ApiParam(value = "Payload to delete an upload proposition filter", required = true) UploadPropositionFilter uploadPropositionFilter,
			@ApiParam(value = "Filter uuid, if null uploadPropositionFilter.uuid is used.", required = false)
					@PathParam("uuid") String uuid)
			throws BusinessException {
		return uploadPropositionFilterFacade.delete(uploadPropositionFilter, uuid);
	}
}
