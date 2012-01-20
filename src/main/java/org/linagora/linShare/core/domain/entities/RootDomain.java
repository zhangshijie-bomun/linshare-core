package org.linagora.linShare.core.domain.entities;

import org.linagora.linShare.core.domain.constants.DomainType;

public class RootDomain extends AbstractDomain {

	public RootDomain() {
	}

	public RootDomain(String identifier, String label) {
		super(identifier, label);
		this.defaultRole=Role.SYSTEM;
		this.defaultLocale="en";
	}

	@Override
	public DomainType getDomainType() {
		return DomainType.ROOTDOMAIN;
	}
}
