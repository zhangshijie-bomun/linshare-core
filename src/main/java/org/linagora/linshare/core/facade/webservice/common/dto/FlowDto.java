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
package org.linagora.linshare.core.facade.webservice.common.dto;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "Flowjs")
@ApiModel(value = "FlowjsDto", description = "Response Object for Flowjs file uploads")
public class FlowDto {

	@ApiModelProperty(value = "position number of the chunk corresponding to the dto response")
	protected long chunkNumber;

	@ApiModelProperty(value = "Filename")
	protected String fileName;

	@ApiModelProperty(value = "the document created and stored after all chunks have been uploaded successfully")
	protected EntryDto entry;

	@ApiModelProperty(value = "a boolean to indicate whether this is the last chunk uploaded")
	protected boolean isLastChunk;

	@ApiModelProperty(value = "Flag to indicate if the chunk has been uploaded successfully")
	protected boolean chunkUploadSuccess;

	@ApiModelProperty(value = "True if async upload is enabled")
	protected Boolean isAsync;

	@ApiModelProperty(value = "Uuid of the asynchronous task, generated by the server and given back to FlowUploader")
	protected String asyncTaskUuid;

	@ApiModelProperty(value = "Error message")
	protected String errorMessage;

	@ApiModelProperty(value = "Optional error code")
	protected Integer errCode;

	@ApiModelProperty(value = "The delay between every request to ask if upload is complete.")
	protected Integer frequence;

	public FlowDto() {
		super();
		this.isLastChunk = false;
	}

	public FlowDto(long chunkNumber) {
		super();
		this.chunkNumber = chunkNumber;
		this.isLastChunk = false;
	}

	public long getChunkNumber() {
		return chunkNumber;
	}

	public void setChunkNumber(long chunkNumber) {
		this.chunkNumber = chunkNumber;
	}

	public String getFilename() {
		return fileName;
	}

	public void setFilename(String filename) {
		this.fileName = filename;
	}

	public EntryDto getEntry() {
		return entry;
	}

	public void setEntry(EntryDto entry) {
		this.entry = entry;
	}

	public boolean isLastChunk() {
		return isLastChunk;
	}

	public void setLastChunk(boolean isLastChunk) {
		this.isLastChunk = isLastChunk;
	}

	public boolean isChunkUploadSuccess() {
		return chunkUploadSuccess;
	}

	public void setChunkUploadSuccess(boolean chunkUploadSuccess) {
		this.chunkUploadSuccess = chunkUploadSuccess;
	}

	public Boolean getIsAsync() {
		return isAsync;
	}

	public void setIsAsync(Boolean isAsync) {
		this.isAsync = isAsync;
	}

	public String getAsyncTaskUuid() {
		return asyncTaskUuid;
	}

	public void setAsyncTaskUuid(String asyncTaskUuid) {
		this.asyncTaskUuid = asyncTaskUuid;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Integer getFrequence() {
		return frequence;
	}

	public void setFrequence(Integer frequence) {
		this.frequence = frequence;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Integer getErrCode() {
		return errCode;
	}

	public void setErrCode(Integer errCode) {
		this.errCode = errCode;
	}

	public void completeAsyncTransfert(AsyncTaskDto asyncTask) {
		this.isAsync = true;
		this.asyncTaskUuid = asyncTask.getUuid();
		setFilename(asyncTask.getFileName());
		this.setLastChunk(true);
		this.setChunkUploadSuccess(true);
	}

	public void completeTransfert(EntryDto uploadedEntry) {
		this.setEntry(uploadedEntry);
		this.setLastChunk(true);
		this.setChunkUploadSuccess(true);
	}
}
