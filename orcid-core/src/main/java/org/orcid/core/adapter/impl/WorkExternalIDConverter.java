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
package org.orcid.core.adapter.impl;

import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.common_rc1.Url;
import org.orcid.jaxb.model.record_rc1.Relationship;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifier;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierId;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record_rc2.ExternalID;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class WorkExternalIDConverter extends BidirectionalConverter<ExternalID, String> {

    /** Uses rc1 as intermediary form
     * 
     */
    @Override
    public ExternalID convertFrom(String externalIdentifiersAsString, Type<ExternalID> arg1) {        
        WorkExternalIdentifier id = JsonUtils.readObjectFromJsonString(externalIdentifiersAsString, WorkExternalIdentifier.class);        
        ExternalID result = new ExternalID();
        result.setType(id.getWorkExternalIdentifierType().value());
        if (id.getRelationship() !=null)
            result.setRelationship(org.orcid.jaxb.model.record_rc2.Relationship.fromValue(id.getRelationship().value()));
        if (id.getUrl() != null)
            result.setUrl(new org.orcid.jaxb.model.common_rc2.Url(id.getUrl().getValue()));
        result.setValue(id.getWorkExternalIdentifierId().getContent());
        return result;
    }

    /** Currently transforms into rc1 format
     * TODO: make local class to use JSONUtils on.
     * 
     */
    @Override
    public String convertTo(ExternalID externalID, Type<String> arg1) {
        //tranform to rc1 WorkExternalIdentifier
        WorkExternalIdentifier id = new WorkExternalIdentifier();
        try{
            id.setWorkExternalIdentifierType(WorkExternalIdentifierType.fromValue(externalID.getType()));            
        }catch(IllegalArgumentException e){
            id.setWorkExternalIdentifierType(WorkExternalIdentifierType.OTHER_ID); 
        }
        id.setWorkExternalIdentifierId(new WorkExternalIdentifierId(externalID.getValue()));
        if (externalID.getUrl()!=null)
            id.setUrl(new Url(externalID.getUrl().getValue()));
        if (externalID.getRelationship() != null)
            try{
                id.setRelationship(Relationship.fromValue(externalID.getRelationship().value()));
            }catch (IllegalArgumentException e){
            }
        return JsonUtils.convertToJsonString(id);
    }

}