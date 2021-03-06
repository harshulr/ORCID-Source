/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.listener.clients;

import static org.orcid.utils.solr.entities.SolrConstants.ORCID;
import static org.orcid.utils.solr.entities.SolrConstants.PROFILE_LAST_MODIFIED_DATE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.ExternalIdReference;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingTitle;
import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.LastModifiedDate;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidDeprecated;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OtherName;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Subtitle;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.TranslatedTitle;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.utils.NullUtils;
import org.orcid.utils.solr.entities.OrcidSolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.stereotype.Component;

@Component
public class SolrIndexUpdater {

    Logger LOG = LoggerFactory.getLogger(SolrIndexUpdater.class);

    @Value("${org.orcid.core.indexPublicProfile}")
    private boolean indexPublicProfile;

    @Value("${org.orcid.persistence.messaging.solr_indexing.enabled}")
    private boolean isSolrIndexingEnalbed;
        
    @Resource(name = "solrServer")
    private SolrServer solrServer;

    public void updateSolrIndex(OrcidProfile profile) {
        LOG.info("Updating " + profile.getOrcidIdentifier().getPath() + " in SOLR index. indexPublicProfile = " + indexPublicProfile);
        if(!isSolrIndexingEnalbed) {
            LOG.info("Solr indexing is disabled");
            return;
        }
        // Check if the profile is locked
        if (profile.isLocked()) {
            profile.downgradeToOrcidIdentifierOnly();
        }

        OrcidSolrDocument profileIndexDocument = new OrcidSolrDocument();
        profileIndexDocument.setOrcid(profile.getOrcidIdentifier().getPath());

        OrcidDeprecated orcidDeprecated = profile.getOrcidDeprecated();
        if (orcidDeprecated != null) {
            profileIndexDocument.setPrimaryRecord(orcidDeprecated.getPrimaryRecord() != null ? orcidDeprecated.getPrimaryRecord().getOrcidIdentifier().getPath() : null);
        }

        OrcidBio orcidBio = profile.getOrcidBio();
        if (orcidBio != null) {
            PersonalDetails personalDetails = orcidBio.getPersonalDetails();
            boolean persistPersonalDetails = personalDetails != null;
            if (persistPersonalDetails) {
                profileIndexDocument.setFamilyName(personalDetails.getFamilyName() != null ? personalDetails.getFamilyName().getContent() : null);
                profileIndexDocument.setGivenNames(personalDetails.getGivenNames() != null ? personalDetails.getGivenNames().getContent() : null);
                profileIndexDocument.setCreditName(personalDetails.getCreditName() != null ? personalDetails.getCreditName().getContent() : null);
                List<OtherName> otherNames = personalDetails.getOtherNames() != null ? personalDetails.getOtherNames().getOtherName() : null;
                if (otherNames != null && !otherNames.isEmpty()) {
                    List<String> names = new ArrayList<String>();
                    for (OtherName otherName : otherNames) {
                        names.add(otherName.getContent());
                    }
                    profileIndexDocument.setOtherNames(names);
                }
            }

            ContactDetails contactDetails = orcidBio.getContactDetails();
            if (contactDetails != null) {
                for (Email email : contactDetails.getEmail()) {
                    profileIndexDocument.addEmailAddress(email.getValue());
                }
            }

            ExternalIdentifiers externalIdentifiers = orcidBio.getExternalIdentifiers();
            if (externalIdentifiers != null) {
                List<String> extIdOrcids = new ArrayList<String>();
                List<String> extIdRefs = new ArrayList<String>();
                List<String> extIdOrcidsAndRefs = new ArrayList<String>();
                for (ExternalIdentifier externalIdentifier : externalIdentifiers.getExternalIdentifier()) {
                    Source source = externalIdentifier.getSource();
                    String sourcePath = null;
                    if (source != null) {
                        sourcePath = source.retrieveSourcePath();
                        if (sourcePath != null) {
                            extIdOrcids.add(sourcePath);
                        }
                    }
                    ExternalIdReference externalIdReference = externalIdentifier.getExternalIdReference();
                    if (externalIdReference != null) {
                        extIdRefs.add(externalIdReference.getContent());
                    }
                    if (NullUtils.noneNull(sourcePath, externalIdReference)) {
                        extIdOrcidsAndRefs.add(sourcePath + "=" + externalIdReference.getContent());
                    }
                }
                if (!extIdOrcids.isEmpty()) {
                    profileIndexDocument.setExternalIdSources(extIdOrcids);
                }
                if (!extIdRefs.isEmpty()) {
                    profileIndexDocument.setExternalIdReferences(extIdRefs);
                }
                if (!extIdOrcidsAndRefs.isEmpty()) {
                    profileIndexDocument.setExternalIdSourcesAndReferences(extIdOrcidsAndRefs);
                }
            }

            OrcidActivities orcidActivities = profile.getOrcidActivities();
            if (orcidActivities != null) {
                if (orcidBio != null && orcidBio.getKeywords() != null) {
                    List<Keyword> keyWords = orcidBio.getKeywords().getKeyword();
                    if (keyWords != null && keyWords.size() > 0) {
                        List<String> keywordValues = new ArrayList<String>();
                        for (Keyword keyword : keyWords) {
                            keywordValues.add(keyword.getContent());
                        }
                        profileIndexDocument.setKeywords(keywordValues);
                    }
                }
            }

            List<OrcidWork> orcidWorks = profile.retrieveOrcidWorks() != null ? profile.retrieveOrcidWorks().getOrcidWork() : null;
            if (orcidWorks != null) {
                List<String> workTitles = new ArrayList<String>();
                Map<WorkExternalIdentifierType, List<String>> allExternalIdentifiers = new HashMap<WorkExternalIdentifierType, List<String>>();
                for (OrcidWork orcidWork : orcidWorks) {

                    if (orcidWork.getWorkExternalIdentifiers() != null) {

                        for (WorkExternalIdentifier workExternalIdentifier : orcidWork.getWorkExternalIdentifiers().getWorkExternalIdentifier()) {

                            /**
                             * Creates a map that contains all different
                             * external identifiers for the current work
                             */
                            boolean nullSafeCheckForWorkExternalIdentifier = workExternalIdentifier.getWorkExternalIdentifierId() != null
                                    && !StringUtils.isBlank(workExternalIdentifier.getWorkExternalIdentifierId().getContent());

                            if (nullSafeCheckForWorkExternalIdentifier) {
                                WorkExternalIdentifierType type = workExternalIdentifier.getWorkExternalIdentifierType();
                                if (!allExternalIdentifiers.containsKey(type)) {
                                    List<String> content = new ArrayList<String>();
                                    content.add(workExternalIdentifier.getWorkExternalIdentifierId().getContent());
                                    allExternalIdentifiers.put(type, content);
                                } else {
                                    allExternalIdentifiers.get(type).add(workExternalIdentifier.getWorkExternalIdentifierId().getContent());
                                }
                            }

                        }
                    }

                    if (orcidWork.getWorkTitle() != null) {
                        Title workMainTitle = orcidWork.getWorkTitle().getTitle();
                        Subtitle worksubTitle = orcidWork.getWorkTitle().getSubtitle();
                        TranslatedTitle translatedTitle = orcidWork.getWorkTitle().getTranslatedTitle();
                        if (workMainTitle != null && !StringUtils.isBlank(workMainTitle.getContent())) {
                            workTitles.add(workMainTitle.getContent());
                        }

                        if (worksubTitle != null && !StringUtils.isBlank(worksubTitle.getContent())) {
                            workTitles.add(worksubTitle.getContent());
                        }

                        if (translatedTitle != null && !StringUtils.isBlank(translatedTitle.getContent())) {
                            workTitles.add(translatedTitle.getContent());
                        }
                    }
                }

                profileIndexDocument.setWorkTitles(workTitles);

                // Set the list of external identifiers to the document list
                addExternalIdentifiersToIndexDocument(profileIndexDocument, allExternalIdentifiers);
            }

            List<Funding> orcidFundings = profile.retrieveFundings() != null ? profile.retrieveFundings().getFundings() : null;
            if (orcidFundings != null) {
                List<String> fundingTitle = new ArrayList<String>();
                for (Funding orcidFunding : orcidFundings) {
                    FundingTitle title = orcidFunding.getTitle();
                    if (title != null) {
                        if (title.getTitle() != null && !StringUtils.isBlank(title.getTitle().getContent())) {
                            fundingTitle.add(title.getTitle().getContent());
                        }

                        if (title.getTranslatedTitle() != null && StringUtils.isBlank(title.getTranslatedTitle().getContent())) {
                            fundingTitle.add(title.getTranslatedTitle().getContent());
                        }
                    }

                }

                profileIndexDocument.setFundingTitles(fundingTitle);
            }

        }

        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        orcidMessage.setOrcidProfile(profile);
        OrcidHistory orcidHistory = profile.getOrcidHistory();
        if (orcidHistory != null) {
            LastModifiedDate lastModifiedDate = orcidHistory.getLastModifiedDate();
            if (lastModifiedDate != null) {
                profileIndexDocument.setProfileLastModifiedDate(lastModifiedDate.getValue().toGregorianCalendar().getTime());
            }
            SubmissionDate submissionDate = orcidHistory.getSubmissionDate();
            if (submissionDate != null) {
                profileIndexDocument.setProfileSubmissionDate(submissionDate.getValue().toGregorianCalendar().getTime());
            }
        }
        if (indexPublicProfile) {
            profileIndexDocument.setPublicProfileMessage(orcidMessage.toXmlString());
        }
        this.persist(profileIndexDocument);
    }

    /**
     * Fill all the different external identifiers in the profile index
     * document.
     * 
     * @param profileIndexDocument
     *            The document that will be indexed by solr
     * @param externalIdentifiers
     *            The list of external identifiers
     */
    private void addExternalIdentifiersToIndexDocument(OrcidSolrDocument profileIndexDocument, Map<WorkExternalIdentifierType, List<String>> externalIdentifiers) {
        if (profileIndexDocument.getArxiv() == null)
            profileIndexDocument.setArxiv(new ArrayList<String>());
        if (profileIndexDocument.getAsin() == null)
            profileIndexDocument.setAsin(new ArrayList<String>());
        if (profileIndexDocument.getAsintld() == null)
            profileIndexDocument.setAsintld(new ArrayList<String>());
        if (profileIndexDocument.getBibcode() == null)
            profileIndexDocument.setBibcode(new ArrayList<String>());
        if (profileIndexDocument.getDigitalObjectIds() == null)
            profileIndexDocument.setDigitalObjectIds(new ArrayList<String>());
        if (profileIndexDocument.getEid() == null)
            profileIndexDocument.setEid(new ArrayList<String>());
        if (profileIndexDocument.getIsbn() == null)
            profileIndexDocument.setIsbn(new ArrayList<String>());
        if (profileIndexDocument.getIssn() == null)
            profileIndexDocument.setIssn(new ArrayList<String>());
        if (profileIndexDocument.getJfm() == null)
            profileIndexDocument.setJfm(new ArrayList<String>());
        if (profileIndexDocument.getJstor() == null)
            profileIndexDocument.setJstor(new ArrayList<String>());
        if (profileIndexDocument.getLccn() == null)
            profileIndexDocument.setLccn(new ArrayList<String>());
        if (profileIndexDocument.getMr() == null)
            profileIndexDocument.setMr(new ArrayList<String>());
        if (profileIndexDocument.getOclc() == null)
            profileIndexDocument.setOclc(new ArrayList<String>());
        if (profileIndexDocument.getOl() == null)
            profileIndexDocument.setOl(new ArrayList<String>());
        if (profileIndexDocument.getOsti() == null)
            profileIndexDocument.setOsti(new ArrayList<String>());
        if (profileIndexDocument.getOtherIdentifierType() == null)
            profileIndexDocument.setOtherIdentifierType(new ArrayList<String>());
        if (profileIndexDocument.getPmc() == null)
            profileIndexDocument.setPmc(new ArrayList<String>());
        if (profileIndexDocument.getPmid() == null)
            profileIndexDocument.setPmid(new ArrayList<String>());
        if (profileIndexDocument.getRfc() == null)
            profileIndexDocument.setRfc(new ArrayList<String>());
        if (profileIndexDocument.getSsrn() == null)
            profileIndexDocument.setSsrn(new ArrayList<String>());
        if (profileIndexDocument.getZbl() == null)
            profileIndexDocument.setZbl(new ArrayList<String>());

        Iterator<Entry<WorkExternalIdentifierType, List<String>>> it = externalIdentifiers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<WorkExternalIdentifierType, List<String>> entry = (Map.Entry<WorkExternalIdentifierType, List<String>>) it.next();
            if (entry.getKey() != null && entry.getValue() != null && !entry.getValue().isEmpty()) {
                switch (entry.getKey()) {
                case AGR:
                    profileIndexDocument.setAgr(entry.getValue());
                    break;
                case ARXIV:
                    profileIndexDocument.setArxiv(entry.getValue());
                    break;
                case ASIN:
                    profileIndexDocument.setAsin(entry.getValue());
                    break;
                case ASIN_TLD:
                    profileIndexDocument.setAsintld(entry.getValue());
                    break;
                case BIBCODE:
                    profileIndexDocument.setBibcode(entry.getValue());
                    break;
                case CBA:
                    profileIndexDocument.setCba(entry.getValue());
                    break;
                case CIT:
                    profileIndexDocument.setCit(entry.getValue());
                    break;
                case CTX:
                    profileIndexDocument.setCtx(entry.getValue());
                    break;
                case DOI:
                    profileIndexDocument.setDigitalObjectIds(entry.getValue());
                    break;
                case EID:
                    profileIndexDocument.setEid(entry.getValue());
                    break;
                case ETHOS:
                    profileIndexDocument.setEthos(entry.getValue());
                    break;
                case HANDLE:
                    profileIndexDocument.setHandle(entry.getValue());
                    break;
                case HIR:
                    profileIndexDocument.setHir(entry.getValue());
                    break;
                case ISBN:
                    profileIndexDocument.setIsbn(entry.getValue());
                    break;
                case ISSN:
                    profileIndexDocument.setIssn(entry.getValue());
                    break;
                case JFM:
                    profileIndexDocument.setJfm(entry.getValue());
                    break;
                case JSTOR:
                    profileIndexDocument.setJstor(entry.getValue());
                    break;
                case LCCN:
                    profileIndexDocument.setLccn(entry.getValue());
                    break;
                case MR:
                    profileIndexDocument.setMr(entry.getValue());
                    break;
                case OCLC:
                    profileIndexDocument.setOclc(entry.getValue());
                    break;
                case OL:
                    profileIndexDocument.setOl(entry.getValue());
                    break;
                case OSTI:
                    profileIndexDocument.setOsti(entry.getValue());
                    break;
                case OTHER_ID:
                    profileIndexDocument.setOtherIdentifierType(entry.getValue());
                    break;
                case PAT:
                    profileIndexDocument.setPat(entry.getValue());
                    break;
                case PMC:
                    profileIndexDocument.setPmc(entry.getValue());
                    break;
                case PMID:
                    profileIndexDocument.setPmid(entry.getValue());
                    break;
                case RFC:
                    profileIndexDocument.setRfc(entry.getValue());
                    break;
                case SOURCE_WORK_ID:
                    profileIndexDocument.setSourceWorkId(entry.getValue());
                    break;
                case SSRN:
                    profileIndexDocument.setSsrn(entry.getValue());
                    break;
                case URI:
                    profileIndexDocument.setUri(entry.getValue());
                    break;
                case URN:
                    profileIndexDocument.setUrn(entry.getValue());
                    break;
                case WOSUID:
                    profileIndexDocument.setWosuid(entry.getValue());
                case ZBL:
                    profileIndexDocument.setZbl(entry.getValue());
                    break;
                }
            }
        }
    }

    // TODO: how does solr dao handle transactions/concurrency?
    public void persist(OrcidSolrDocument orcidSolrDocument) {
        try {
            solrServer.addBean(orcidSolrDocument);
            solrServer.commit();
        } catch (SolrServerException se) {
            throw new NonTransientDataAccessResourceException("Error persisting to SOLR Server", se);
        } catch (IOException ioe) {
            throw new NonTransientDataAccessResourceException("IOException when persisting to SOLR", ioe);
        }

    }

    // TODO: make this cache?
    public Date retrieveLastModified(String orcid) {
        SolrQuery query = new SolrQuery();
        query.setQuery(ORCID + ":\"" + orcid + "\"");
        query.setFields(PROFILE_LAST_MODIFIED_DATE);
        try {
            QueryResponse response = solrServer.query(query);
            List<SolrDocument> results = response.getResults();
            if (results.isEmpty()) {
                return null;
            } else {
                return (Date) results.get(0).getFieldValue(PROFILE_LAST_MODIFIED_DATE);
            }

        } catch (SolrServerException e) {
            throw new NonTransientDataAccessResourceException("Error retrieving last modified date from SOLR Server", e);
        }
    }

}
