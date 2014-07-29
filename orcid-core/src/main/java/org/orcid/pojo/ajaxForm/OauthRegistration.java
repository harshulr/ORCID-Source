package org.orcid.pojo.ajaxForm;


public class OauthRegistration extends Registration {       
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    Text clientId;
    Text redirectUri;
    Text scope;
    Text responseType;
    boolean approved=false;

    public OauthRegistration() {
        super();
        this.clientId = new Text();
        this.redirectUri = new Text();
        this.scope = new Text();
        this.responseType = new Text();
    }
    
    public OauthRegistration(Registration reg) {
        this.setErrors(reg.getErrors());
        this.setSendChangeNotifications(reg.getSendChangeNotifications());
        this.setSendOrcidNews(reg.getSendOrcidNews());
        this.setTermsOfUse(reg.getTermsOfUse());
        this.setActivitiesVisibilityDefault(reg.getActivitiesVisibilityDefault());
        this.setPassword(reg.getPassword());
        this.setPasswordConfirm(reg.getPasswordConfirm());
        this.setEmail(reg.getEmail());
        this.setEmailConfirm(reg.getEmailConfirm());        
        this.setGivenNames(reg.getGivenNames());
        this.setFamilyNames(reg.getFamilyNames());        
        this.setCreationType(reg.getCreationType());        
        this.setReferredBy(reg.getReferredBy());     
        this.clientId = new Text();
        this.redirectUri = new Text();
        this.scope = new Text();
        this.responseType = new Text();
    }
    
    public Text getClientId() {
        return clientId;
    }
    public void setClientId(Text clientId) {
        this.clientId = clientId;
    }
    public Text getRedirectUri() {
        return redirectUri;
    }
    public void setRedirectUri(Text redirectUri) {
        this.redirectUri = redirectUri;
    }
    public Text getScope() {
        return scope;
    }
    public void setScope(Text scope) {
        this.scope = scope;
    }
    public Text getResponseType() {
        return responseType;
    }
    public void setResponseType(Text responseType) {
        this.responseType = responseType;
    }
    public boolean getApproved() {
        return approved;
    }
    public void setApproved(boolean approved) {
        this.approved = approved;
    }        
}
