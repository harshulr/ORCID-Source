/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.jpa.entities;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.orcid.jaxb.model.message.CurrencyCode;
import org.orcid.jaxb.model.message.FundingType;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.utils.NullUtils;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@Entity
@Table(name = "org_funding_relation")
public class OrgFundingRelation extends BaseEntity<Long> implements Comparable<OrgAffiliationRelationEntity>, ProfileAware {

	private static final long serialVersionUID = 1L;
	
	private Long id;
    private OrgEntity org;
    private ProfileEntity profile;
    private String title;
    private String description;
    private FundingType type;
    private CurrencyCode currencyCode;
    private long amount;
    private String url;
    private String contributorsJson;
	private StartDateEntity startDate;
    private EndDateEntity endDate;
    private Visibility visibility;
    private ProfileEntity source;
	
    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "org_funding_relation_seq")
    @SequenceGenerator(name = "org_funding_relation_seq", sequenceName = "org_funding_relation_seq")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.DETACH, CascadeType.REFRESH })
    @JoinColumn(name = "org_id", nullable = false)
	public OrgEntity getOrg() {
		return org;
	}

	public void setOrg(OrgEntity org) {
		this.org = org;
	}

	@Override
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orcid", nullable = false, updatable = false, insertable = true)
	public ProfileEntity getProfile() {
		return profile;
	}

	public void setProfile(ProfileEntity profile) {
		this.profile = profile;
	}

	@JoinColumn(name = "title", nullable = false)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@JoinColumn(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JoinColumn(name = "type", nullable = false)
	public FundingType getType() {
		return type;
	}

	public void setType(FundingType type) {
		this.type = type;
	}

	@JoinColumn(name = "currency", nullable = false)
	public CurrencyCode getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(CurrencyCode currencyCode) {
		this.currencyCode = currencyCode;
	}

	@JoinColumn(name = "amount", nullable = false)
	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	@JoinColumn(name = "url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@JoinColumn(name = "contributors_json")
	public String getContributorsJson() {
		return contributorsJson;
	}

	public void setContributorsJson(String contributorsJson) {
		this.contributorsJson = contributorsJson;
	}
	
	public StartDateEntity getStartDate() {
		return startDate;
	}

	public void setStartDate(StartDateEntity startDate) {
		this.startDate = startDate;
	}

	public EndDateEntity getEndDate() {
		return endDate;
	}

	public void setEndDate(EndDateEntity endDate) {
		this.endDate = endDate;
	}

	@Basic
    @Enumerated(EnumType.STRING)
	public Visibility getVisibility() {
		return visibility;
	}

	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}

	@ManyToOne
    @JoinColumn(name = "source_id")
	public ProfileEntity getSource() {
		return source;
	}

	public void setSource(ProfileEntity source) {
		this.source = source;
	}

	@Override
	public int compareTo(OrgAffiliationRelationEntity other) {
		if (other == null) {
            return 1;
        }
        int compareEnds = compareEnds(endDate, other.getEndDate());
        if (compareEnds != 0) {
            return compareEnds;
        }
        int compareStarts = compareStarts(startDate, other.getStartDate());
        if (compareStarts != 0) {
            return compareStarts;
        }
        return compareNames(org.getName(), other.getOrg().getName());
	}

	private int compareEnds(FuzzyDateEntity endDate, FuzzyDateEntity otherEndDate) {
        if (NullUtils.anyNull(endDate, otherEndDate)) {
            return -NullUtils.compareNulls(endDate, otherEndDate);
        }
        return -endDate.compareTo(otherEndDate);
    }

    private int compareStarts(FuzzyDateEntity startDate, FuzzyDateEntity otherStartDate) {
        if (NullUtils.anyNull(startDate, otherStartDate)) {
            return NullUtils.compareNulls(startDate, otherStartDate);
        }
        return -startDate.compareTo(otherStartDate);
    }

    private int compareNames(String name, String otherName) {
        if (NullUtils.anyNull(name, otherName)) {
            return NullUtils.compareNulls(name, otherName);
        }
        return name.compareTo(otherName);
    }
}
