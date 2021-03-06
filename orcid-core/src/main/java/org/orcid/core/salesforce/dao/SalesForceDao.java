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
package org.orcid.core.salesforce.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.orcid.core.salesforce.model.Consortium;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;

/**
 * 
 * @author Will Simpson
 *
 */
public interface SalesForceDao {

    static final String MAIN_CONTACT_ROLE = "Main Contact";

    static final String TECH_LEAD_ROLE = "Tech Lead";

    List<Member> retrieveMembers();

    List<Member> retrieveConsortia();

    Consortium retrieveConsortium(String consortiumId);

    MemberDetails retrieveDetails(String memberId, String consortiumLeadId);

    Map<String, List<Contact>> retrieveContactsByOpportunityId(Collection<String> opportunityIds);

    /**
     * @return The sales force object id, if valid.
     * @throws IllegalArgumentException
     *             if the sales force object id is not the correct format, or
     *             could contain something malicious.
     */
    String validateSalesForceId(String memberId);

}