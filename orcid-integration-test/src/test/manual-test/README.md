# Manual Test

##Register/Verify
1. Visit https://qa.orcid.org/register
2. Create new account:
    * First name: ma_test
    * Last name: [DD][month][YYYY] (ex: 24feb2016)
    * Email: ma_test_[DD][month][YYYY]@mailinator.com (ex: ma_test_24feb2016@mailinator.com)
    * Password: generate random password using [LastPass generator](https://lastpass.com/generatepassword.php) or similar
    * Default privacy for new activities: Public
    * Email frequency: Weekly summary
3. Visit https://qa.orcid.org/signout
4. Visit http://mailinator.com and check ma_test_[DD][month][YYYY]@mailinator.com
5. Open message from support@verify.orcid.org with subject [ORCID] Thanks for creating an ORCID iD 
6. Click (or copy/paste) email verification link
7. When redirected to https://qa.orcid.org/signin, sign in using ma_test credentials created in previous steps
5. Visit https://qa.orcid.org/signout

##Sign In
1. Visit https://qa.orcid.org/signin sign in using a UnitedID account and complete steps to link it to the account created today.
2. Check that the notification to link the account to Manually Testing Credentials comes up and click Connect
5. Visit https://qa.orcid.org/signout
4. Visit https://qa.orcid.org/signin sign in using a Google account not linked to an existing ORCID record, complete steps to link it to the account created today.
5. Visit https://qa.orcid.org/account and revoke Google and UnitedID account access

##Reset password
1. Visit https://qa.orcid.org/reset-password and reset the password for ma_test_[DD][month][YYYY]@mailinator.com
2. Visit http://mailinator.com and check ma_test_[DD][month][YYYY]@mailinator.com
3. Open message from reset@notify.orcid.org with subject [ORCID] About your password reset request 
4. Click (or copy/paste) password reset link
5. Reset password with random value generated using [LastPass generator](https://lastpass.com/generatepassword.php) or similar
6. Visit https://qa.orcid.org/signin and sign in using the new password created in the previous steps
7. Visit https://qa.orcid.org/signout

##My-ORCID
1. Visit https://qa.orcid.org/my-orcid
2. Add a published name: Published Name
3. Add an also know as name: Other Name
4. Add a country: US
5. Add a keyword: keyword
6. Add a URL: https://qa.orcid.org
7. Add a biography: Bio
8. Add an education item: Institution "ORCID" (select from dropdown list)
9. Add a funding item: type "grant", title "ma fund test", funding agency "Wellcome Trust" (select from dropdown list)
10. Add a work: type: "journal article", title "ma test work", identifier type "DOI", identifier value “9999”
11. Add a second email address: ma_test_[DD][month][YYYY]_01@mailinator.com and mark as public
12. Visit public page (http://qa.orcid.org/[XXXX-XXXX-XXXX-XXXX])
	  * Verify information added in step 2-10 is visible
      * Verify ma_test_[DD][month][YYYY]@mailinator.com is not visible
      * Verify ma_test_[DD][month][YYYY]_01@mailinator.com is visible
13. Visit https://qa.orcid.org/signout

##Public API Authenticate
1. Go to https://qa.orcid.org/oauth/authorize?client_id=[public client id]&response_type=code&scope=/authenticate&redirect_uri=https://developers.google.com/oauthplayground

2. Register for a new account and grant authorization
    * First name: ma_public_test
    * Last name: [DD][month][YYYY] (ex: 24feb2016)
    * Email: ma_public_test_[DD][month][YYYY]@mailinator.com (ex: ma_test_24feb2016@mailinator.com)
    * Password: generate random password using [LastPass generator](https://lastpass.com/generatepassword.php) or similar
    * Default privacy for new activities: Private
    * Email frequency: Never

3. Exchange the authorization code: 

    ```
    curl -i -L -H 'Accept: application/json' --data 'client_id=[public client id]&client_secret=[public client secret]&grant_type=authorization_code&code=[code]&redirect_uri=https://developers.google.com/oauthplayground' 'https://qa.orcid.org/oauth/token' -k
    ```

## Public API Read/Search
1. Generate a read-public token:

    ```
    curl -i -L -H 'Accept: application/json' -d 'client_id=[public client id]' -d 'client_secret=[public client secret]' -d 'scope=/read-public' -d 'grant_type=client_credentials' 'http://pub.qa.orcid.org/oauth/token'
    ```

2. Search for the records you created:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [public token]' 'Accept: application/xml' 'https://pub.qa.orcid.org/v1.2/search/orcid-bio/?q=family-name:[DD][month][YYYY]' -k
    ```
3. Check that both records are returned

3. Read a record with 1.2:
 
    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [public token]' -X GET 'http://pub.qa.orcid.org/v1.2/[orcid id]/orcid-profile' -L -i -k
    ```
    
3. Read a record with 2.0:
 
    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [public token]' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/[orcid id]/record' -L -i -k
    ```

4. Read a record without a version: 

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [public token]' -X GET 'http://pub.qa.orcid.org/[orcid id]/orcid-profile' -L -i -k
    ```

5. Read the record without an access token: 

    ```
    curl -H 'Content-Type: application/xml' 'http://pub.qa.orcid.org/v1.2/[orcid id]/orcid-profile' -L -i -k
    ```
    
13. Visit https://qa.orcid.org/signout

##Member API 1.2 Post/Update 
1. Go to https://qa.orcid.org/oauth/authorize?client_id=[client id]&response_type=code&scope=/orcid-bio/update /orcid-works/create /orcid-works/update /affiliations/create /affiliations/update /funding/create /funding/update /orcid-profile/read-limited&redirect_uri=https://developers.google.com/oauthplayground

2. Log into the account created for testing today and grant short lived authorization

3. Exchange the authorization code:
 
    ```
    curl -i -L -H 'Accept: application/json' --data 'client_id=[client id]&client_secret=[client secret]&grant_type=authorization_code&code=[code]&redirect_uri=https://developers.google.com/oauthplayground' 'https://qa.orcid.org/oauth/token' -k
    ```

4. Post the ma test work:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [1.2 token]' -H 'Accept: application/xml' -d '@ma_work.xml' -X POST 'http://api.qa.orcid.org/v1.2/[orcid id]/orcid-works' -L -i -k
    ```

5. Update the ma test work: 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [1.2 token]' -H 'Accept: application/xml' -d '@ma_work2.xml' -X PUT 'http://api.qa.orcid.org/v1.2/[orcid id]/orcid-works' -L -i -k
    ```

6. Check that the work is updated and the manually added work is not affected

7. Post the ma test funding: 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [1.2 token]' -H 'Accept: application/xml' -d '@ma_fund.xml' -X POST 'http://api.qa.orcid.org/v1.2/[orcid id]/funding' -L -i -k
    ```

8. Update the ma test funding: 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [1.2 token]' -H 'Accept: application/xml' -d '@ma_fund2.xml' -X PUT 'http://api.qa.orcid.org/v1.2/[orcid id]/funding' -L -i -k
    ```

9. Check that the funding item is updated and the manually added funding is not affected

10. Post the ma test education: 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [1.2 token]' -H 'Accept: application/xml' -d '@ma_edu.xml' -X POST 'http://api.qa.orcid.org/v1.2/[orcid id]/affiliations' -L -i -k
    ```

11. Update the ma test education: 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [1.2 token]' -H 'Accept: application/xml' -d '@ma_edu2.xml' -X PUT 'http://api.qa.orcid.org/v1.2/[orcid id]/affiliations' -L -i -k
    ```

12. Check that the education is updated and the manually added education is not affected

14. Update the biography:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [1.2 token]' -H 'Accept: application/xml' -d '@ma_bio.xml' -X PUT 'http://api.qa.orcid.org/v1.2/[orcid id]/orcid-bio' -L -i -k
    ```
15. Check that the researcher URL, keyword, and identifier are added and the manually added biography items are not affected

15. Attempt to access the wrong record - check that this fails
    
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [1.2 token]' -H 'Accept: application/xml' -d '@ma_work.xml' -X POST 'http://api.qa.orcid.org/0000-0002-2619-0514/orcid-works' -L -i -k
    ```

16. Attempt to post to a record without a token - check that this fails

	```
	curl -H 'Content-Type: application/orcid+xml' -H 'Accept: application/xml' -d '@ma_work.xml' -X POST 'http://api.qa.orcid.org/v1.2/[orcid id]/orcid-works' -L -i -k
	```

17. Go to https://qa.orcid.org/account and revoke the permission

18. Check that the token no longer works by attempting to post a work - check that this fails
    
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [1.2 token]' -H 'Accept: application/xml' -d '@ma_work.xml' -X POST 'http://api.qa.orcid.org/v1.2/[orcid id]/orcid-works' -L -i -k
    ```


##Member API 2.0 Post/Update/Notifications
1. Log into the account created for testing today 

2. Go to https://qa.orcid.org/oauth/authorize?client_id=[client id]&response_type=code&scope=/read-limited /activities/update /orcid-bio/update&redirect_uri=https://developers.google.com/oauthplayground

3. Grant long lived authorization

4. Exchange the authorization code: 

    ```
    curl -i -L -H 'Accept: application/json' --data 'client_id=[client id]&client_secret=[client secret]&grant_type=authorization_code&code=[code]&redirect_uri=https://developers.google.com/oauthplayground' 'https://qa.orcid.org/oauth/token' -k
    ```

5. Post the ma test work 2: 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/xml' -d '@ma2_work.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/work' -L -i -k
    ```

6. Update the work with JSON: 

    ```
    curl -H 'Content-Type: application/orcid+json' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/json' -X PUT 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/work/[put-code]' -d '{
"put-code":"[put-code]",
"title": {"title": "API Test Title"},
"type": "JOURNAL_ARTICLE",
"external-ids": {
"external-id": [{
"external-id-value": "1234",
"external-id-type": "doi",
"external-id-relationship": "SELF"}]}}}' -L -i -k
    ```
    
13. Attempt to update an item without a token - check that this fails

    ```
    curl -H 'Content-Type: application/orcid+json' -H 'Accept: application/json' -X PUT 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/work/[put-code]' -d '{
"put-code":"[put-code]",
"title": {"title": "API Test Title"},
"type": "JOURNAL_ARTICLE",
"external-ids": {
"external-id": [{
"external-id-value": "1234",
"external-id-type": "doi",
"external-id-relationship": "SELF"}]}}}' -L -i -k
    ```

8. Delete the work:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/xml' -X DELETE 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/work/[put-code]' -L -i -k
    ```

9. Post an education item:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/xml' -d '@ma2_edu.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/education' -L -i -k
    ```

10. Post a funding item:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/xml' -d '@ma2_fund.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/funding' -L -i -k
    ```

11. Post a peer-review item:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/xml' -d '@ma2_peer.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/peer-review' -L -i -k
    ```
    
12. Post a keyword:
	```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/xml' -d '@ma2_keyword.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/keywords' -L -i -k
    ```

12. Post a personal external identifier:
	```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/xml' -d '@ma2_identifier.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/external-identifiers' -L -i -k
    ```

12. Attempt to post to a record without a token - check that this fails

	```
	curl -H 'Content-Type: application/orcid+xml' -H 'Accept: application/xml' -d '@ma2_work.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/work' -L -i -k
	```

14. Attempt to update an item you are not the source of - check that this fails

    ```
    curl -H 'Content-Type: application/orcid+json' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/json' -X PUT 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/work/[put-code]-3' -d '{
	"put-code":"[put-code]-3",
	"title": {"title": "API Test Title"},
	"type": "JOURNAL_ARTICLE",
	"external-ids": {
	"external-id": [{
	"external-id-value": "1234",
	"external-id-type": "doi",
	"external-id-relationship": "SELF"}]}}}' -L -i -k
    ```

15. Post a notification

	```
	curl -i -H 'Authorization: Bearer eafafe49-b5bf-41db-9fb5-ad3a6cba575b' -H 'Content-Type: application/orcid+xml' -X POST -d '@notify.xml' https://api.qa.orcid.org/v2.0_rc2/[orcid id]/notification-permission -k
	```
	
16. Go to https://qa.orcid.org/inbox 
	* Check that notification to add a work has posted
	* Check that notifications from the previous updates have posted

##Refresh tokens
1. Generate a new token with the same scopes and expiration as the 2.0 token and do not revoke the original token

```
curl -i -L -k -H 'Authorization: Bearer [2.0 token]' -d 'refresh_token=[2.0 refresh]' -d 'grant_type=refresh_token' -d 'client_id=[client id]' -d 'client_secret=[client secret]' -d 'revoke_old=false' https://qa.orcid.org/oauth/token
```

2. Use the new token to post the test work

```
curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [refreshed token]' -H 'Accept: application/xml' -d '@ma2_work.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/work' -L -i -k
```

3. Check that the original token still works by reading the record

```
curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/xml' 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/record' -L -i -k
```

4. Generate a new token with a short lifespan, only /read-limited scope and revoke the original token    

```
curl -i -L -k -H 'Authorization: Bearer [2.0 token]' -d 'refresh_token=[2.0 refresh]' -d 'grant_type=refresh_token' -d 'client_id=[client id]' -d 'client_secret=[client secret]' -d 'revoke_old=true' -d 'scope=/read-limited' -d 'expires_in=600' https://qa.orcid.org/oauth/token
```

2. Check the new token can't be used to post items by attempting to post the test work - check that this fails

```
curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [refreshed2]' -H 'Accept: application/xml' -d '@ma2_work.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/work' -L -i -k
```

2. Check the original token was revoked by attempting to post the test work - check that this fails

```
curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/xml' -d '@ma2_work.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/work' -L -i -k
```

3. Check that the new token can be used to read the record - check that this fails

```
curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [refreshed2]' -H 'Accept: application/xml' 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/record' -L -i -k
```

4. Check that you can't generate a refresh token using a revoked token - check that this fails

```
curl -i -L -k -H 'Authorization: Bearer [1.2 token]' -d 'refresh_token=[1.2 refresh]' -d 'grant_type=refresh_token' -d 'client_id=[client id]' -d 'client_secret=[client secret]' -d 'revoke_old=false' https://qa.orcid.org/oauth/token
```

##Member API 1.2 Creating/Claiming

1. Get a token to create records
	
	```
	curl -i -L -H 'Accept: application/json' -d 'client_id=[client id]' -d 'client_secret=[client secret]' -d 'scope=/orcid-profile/create' -d 'grant_type=client_credentials' 'http://api.qa.orcid.org/oauth/token'
	```
2. Post a new record with the 1.2 API
	
	```
	curl -H 'Accept: application/xml' -H 'Content-Type: application/vdn.orcid+xml' -H 'Authorization: Bearer [create token]'  'http://api.qa.orcid.org/v1.2/orcid-profile' -X POST -d '@create.xml' -L -i -k
	```
3. View the newly created record in the UI, check that no information is public

4. Read the newly created record with the API and check that no information is returned

	```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [public token]' -X GET 'http://pub.qa.orcid.org/v1.2/[new id]/orcid-profile' -L -i -k
    ```
5. Check the email inbox used when creating the record and follow the link to claim the record

6. Complete the steps to claim the record

7. Change the information added to the record to limited

7. Try to post to the record using the create token - check that this fails
	
	```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [create token]' -H 'Accept: application/xml' -d '@ma_work.xml' -X POST 'http://api.qa.orcid.org/v1.2/[new id]/orcid-works' -L -i -k
    ```
    
8. Try to read the record with the create token- limited information should not be returned
	
	```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [create token]' -H 'Accept: application/xml' 'http://api.qa.orcid.org/v1.2/[new id]/orcid-profile' -L -i -k
    ```
	

##Privacy Check
###Public Record

This record has every field set to public. Access this record via the API and UI to check that all fields are returned.

1. Visit http://qa.orcid.org/0000-0002-3874-7658

2. Check that the following fields are shown
 * Name
 * Also known as
 * County
 * Keywords
 * Websites
 * Email
 * Other iDs
 * Biography

3. Check that there is one item visible in each of the following sections
 * Education
 * Employment
 * Funding
 * Peer-Review

4. Read with Public API 1.2. Check that the record is returned.

    ```
    curl -H 'Accept: application/orcid+xml' 'http://pub.qa.orcid.org/v1.2/0000-0002-3874-7658/orcid-profile' -L -i -k
    ```

5. Read with Public API 2.0_rc2. Check that the record is returned
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer a8ac4d85-df2b-4de2-9411-1b94491f463b' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/0000-0002-3874-7658/record' -L -i -k
    ```

6. Read with Member API 1.2. Check that the record is returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer eba7892b-4f4a-4651-9c47-f0c74fae61c5' -X GET 'https://api.qa.orcid.org/v1.2/0000-0002-3874-7658/orcid-profile' -L -i -k
    ```

7. Read with Member API 2.0_rc2 record. Check that all fields are returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer eba7892b-4f4a-4651-9c47-f0c74fae61c5' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0002-3874-7658/record' -L -i -k
    ```


7. Read with Member API 2.0_rc2 person. Check that personal information fields are returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer eba7892b-4f4a-4651-9c47-f0c74fae61c5' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0002-3874-7658/person' -L -i -k
    ```


8. Read with Member API 2.0_rc2 activities. Check that affiliations, funding, peer-review and work summaries are returned
 
    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 0658713c-5b6d-4fa4-a3da-73db9c7ab16c' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0002-3874-7658/activities' -L -i -k
    ```


###Limited Record

This record has ever field set to limited, check that nothing is visible in the UI and that it can only be read on the API with a /read-limited token.

1. Visit http://qa.orcid.org/0000-0001-7325-5491

2. Check that no information is displayed other than the ORCID iD

3. Read with Public API 1.2. Check that the bio and activities section are not returned

    ```
    curl -H 'Accept: application/orcid+xml' 'http://pub.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```


4. Read record with Public API 2.0_rc2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 80e4aa5a-6ccc-44b3-83bb-3d9e315cda22' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```


5. Read activities with Public API 2.0_rc2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 80e4aa5a-6ccc-44b3-83bb-3d9e315cda22' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/activities' -L -i -k
    ```

6. Read person with Public API 2.0_rc2. Check that nothing is returned
   
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 80e4aa5a-6ccc-44b3-83bb-3d9e315cda22' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/person' -L -i -k
    ```
    
7. Read email with Public API 2.0_rc2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 80e4aa5a-6ccc-44b3-83bb-3d9e315cda22' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/email' -L -i -k
    ```


3. Read record with /read-public token API 1.2. Check that nothing is returned

    ```
    curl -H 'Accept: application/orcid+xml' -H 'Authorization: Bearer ba290a09-b757-4583-a5af-bd55d7087467' -X GET 'http://api.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```

4. Read record with /read-public token API 2.0_rc2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer ba290a09-b757-4583-a5af-bd55d7087467' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```

5. Read activities with /read-public token 2.0_rc2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer ba290a09-b757-4583-a5af-bd55d7087467' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/activities' -L -i -k
    ```

6. Read person with /read-public token 2.0_rc2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer ba290a09-b757-4583-a5af-bd55d7087467' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/person' -L -i -k
    ```

7. Read email with /read-public token API 2.0_rc2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer ba290a09-b757-4583-a5af-bd55d7087467' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/email' -L -i -k
    ```

8. Read with revoked access token  1.2. Check that an error message is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 63409312-5ef6-4051-988c-f33b0fcea09f' -X GET 'https://api.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```

8. Read record with revoked access token  2.0_rc2. Check that an error message is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 63409312-5ef6-4051-988c-f33b0fcea09f' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```

9. Read with an access token for another record 1.2. Check that an error is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 2283056e-6a4a-4c80-b3a0-beaa102161d0' -X GET 'https://api.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```

10. Read record with access token for another record 2.0_rc2. Check that an error is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 2283056e-6a4a-4c80-b3a0-beaa102161d0' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```

10. Read person with access token for another record 2.0_rc2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 2283056e-6a4a-4c80-b3a0-beaa102161d0' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/person' -L -i -k
    ```

11. Read activities with access token for another record 2.0_rc2. Check that nothing is returned.

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 2283056e-6a4a-4c80-b3a0-beaa102161d0' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/activities' -L -i -k
    ```

12. Read email with access token for another record 2.0_rc2. Check that nothing is returned.

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 2283056e-6a4a-4c80-b3a0-beaa102161d0' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/email' -L -i -k
    ```

13. Read record with access token with activities/update scope 1.2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 064e64ef-6c49-4634-b09b-a38d8d75c774' -X GET 'https://api.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```

14. Read record with access token with activities/update scope 2.0_rc2. Check that an error is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 064e64ef-6c49-4634-b09b-a38d8d75c774' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```

14. Read person with access token with activities/update scope 2.0_rc2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 064e64ef-6c49-4634-b09b-a38d8d75c774' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/person' -L -i -k
    ```

15. Read activities with access token with activities/create scope 2.0_rc2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 064e64ef-6c49-4634-b09b-a38d8d75c774' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/activities' -L -i -k
    ```


16. Read email with access token with activities/create scope 2.0_rc2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 064e64ef-6c49-4634-b09b-a38d8d75c774' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/email' -L -i -k
    ```

17. Read with access token with orcid-profile/create scope 1.2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 262192b4-3dac-4d29-9897-b02823ac3618' -X GET 'https://api.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```

18. Read record with access token with orcid-profile/create scope 2.0_rc2. Check that an error is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 262192b4-3dac-4d29-9897-b02823ac3618' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```

18. Read person with access token with orcid-profile/create scope 2.0_rc2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 262192b4-3dac-4d29-9897-b02823ac3618' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/person' -L -i -k
    ```

19. Read activities with access token with orcid-profile/create scope 2.0_rc2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 262192b4-3dac-4d29-9897-b02823ac3618' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/activities' -L -i -k
    ```

20. Read email section with access token with orcid-profile/create scope 2.0_rc2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 262192b4-3dac-4d29-9897-b02823ac3618' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/email' -L -i -k
    ```

21. Read the record with a working token 1.2. Check that all fields are returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 1fcda8a0-1af3-4b35-8825-e4c53dae8953' -X GET 'https://api.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```

22. Read record with a working token 2.0_rc2. Check that all fields are returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 1fcda8a0-1af3-4b35-8825-e4c53dae8953' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```

22. Read person with a working token 2.0_rc2. Check that the personal information fields are returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 1fcda8a0-1af3-4b35-8825-e4c53dae8953' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/person' -L -i -k
    ```

23. Read activities with a working token 2.0_rc2. Check that affiliations, funding, peer-review and work summaries are returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 1fcda8a0-1af3-4b35-8825-e4c53dae8953' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/activities' -L -i -k
    ```


24. Read email with a working token 2.0_rc2. Check that the email address is returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 1fcda8a0-1af3-4b35-8825-e4c53dae8953' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/email' -L -i -k
    ```


###Private Record

This record has every fields set to private. Check that no information is displayed in the UI or via the API when accessing it.

1. Visit http://qa.orcid.org/0000-0003-2366-2712

2. Check that no information is displayed other than the ORCID iD

3. Read with public API 1.2. Check that nothing is returned

    ```
    curl -H 'Accept: application/orcid+xml' 'http://pub.qa.orcid.org/v1.2/0000-0003-2366-2712/orcid-works' -L -i -k
    ```

4. Read record with public API 2.0_rc2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 80e4aa5a-6ccc-44b3-83bb-3d9e315cda22' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/0000-0003-2366-2712/record' -L -i -k
    ```

5. Read record with /read-limited token API 1.2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 6ae41a5b-abf9-4922-bbb4-08ed8508b4ce' -X GET 'https://api.qa.orcid.org/v1.2/0000-0003-2366-2712/orcid-profile' -L -i -k
    ```

6. Read record with /read-limited token API 2.0_rc2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 6ae41a5b-abf9-4922-bbb4-08ed8508b4ce' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0003-2366-2712/record' -L -i -k
    ```

6. Read person with /read-limited token API 2.0_rc2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 6ae41a5b-abf9-4922-bbb4-08ed8508b4ce' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0003-2366-2712/person' -L -i -k
    ```

7. Read activities with /read-limited token API 2.0_rc2. Check that nothing is returned 

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 6ae41a5b-abf9-4922-bbb4-08ed8508b4ce' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0003-2366-2712/activities' -L -i -k
    ```

6. Read email with /read-limited token API 2.0_rc2. Check that nothing is returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 6ae41a5b-abf9-4922-bbb4-08ed8508b4ce' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0003-2366-2712/email' -L -i -k
    ```

##Scopes/Methods

This section checks that clients can only get access based on the allowed scopes for that client type.

1. Attempt to get a /read-limited token with a public client at https://qa.orcid.org/oauth/authorize?client_id=[public client id]&response_type=code&scope=/read-limited&redirect_uri=https://developers.google.com/oauthplayground


2. Attempt to get an /activities/update token with a public client at https://qa.orcid.org/oauth/authorize?client_id=[public client id]&response_type=code&scope=/activities/update&redirect_uri=https://developers.google.com/oauthplayground


3. Attempt to get a /read-limited token via 2 step OAuth - this should fail

```
curl -i -L -H 'Accept: application/json' -d '[client id]' -d 'client_secret=[client secret]' -d 'scope=/read-limited' -d 'grant_type=client_credentials' 'http://api.qa.orcid.org/oauth/token'
```

4. Attempt to get an /activities/update token via 2 step - this should fail

```
curl -i -L -H 'Accept: application/json' -d '[client id]' -d 'client_secret=[client secret]' -d 'scope=/activities/update' -d 'grant_type=client_credentials' 'http://api.qa.orcid.org/oauth/token'

```

5. Attempt to get a /webhooks token with a basic client - this should fail

```
curl -i -L -H 'Accept: application/json' -d '[client id]' -d 'client_secret=[client secret]' -d 'scope=/web-hook' -d 'grant_type=client_credentials' 'http://api.qa.orcid.org/oauth/token'
```

6. Attempt to get a /orcid-profile/create token with a non-institution client - this should fail

```
curl -i -L -H 'Accept: application/json' -d '[client id]' -d 'client_secret=[client secret]' -d 'scope=/orcid-profile/create' -d 'grant_type=client_credentials' 'http://api.qa.orcid.org/oauth/token'
```


* Finally help out by improving these instructions!      
   
