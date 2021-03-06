<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->

<script type="text/ng-template" id="delete-peer-review-modal">
	<div class="lightbox-container">
		<div class="ie7fix-inner">
			<div class="row">
				<div class="col-md-12 col-xs-12 col-sm-12">
					<h3><@orcid.msg 'manage.deletePeerReview.pleaseConfirm' /></h3>
					{{fixedTitle}}<br />
    				<div class="btn btn-danger" ng-click="deleteByPutCode(deletePutCode, deleteGroup)">
			    		<@orcid.msg 'freemarker.btnDelete' />
    				</div>
    				<a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>
    			</div>
			</div>
		</div>
	</div>
</script>