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
package org.orcid.listener;

import java.util.Map;

import org.orcid.utils.listener.LastModifiedMessage;
import org.orcid.utils.listener.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/** This class forms the basis of the message drive data dump and SOLR index updater.
 * It is intended to be a stand alone application but can sit alongside other modules in dev, QA and sandbox.
 * 
 * @author tom
 *
 */
@Component
public class LastUpdatedListener {
    
    Logger LOG = LoggerFactory.getLogger(LastUpdatedListener.class);
        
    @JmsListener(destination=MessageConstants.Queues.UPDATED_ORCIDS)
    public void processMessage(final Map<String,String> map) {
        LOG.debug("Recieved last updated message");
        map.forEach((k,v)->LOG.debug(k+"->"+v));   
        
        //alternative
        LastModifiedMessage m = new LastModifiedMessage(map);
        LOG.debug(m.getOrcid());
    }
}