Setup Crafter Studio
	Create website with corporate blueprint
	Create author user
	Add author user to created site with 'Collaborator' Role
Install PhantomJS Stack
	Location: http://phantomjs.org/
Clone crafter-selenium-tests project from github
	Temporary Location: https://github.com/pelineni/crafter-selenium-tests
	UPDATE WITH GITHUB LOCATION ONCE CONFIRMED
Edit and update required properties in selenium.properties file (src/test/resources/selenium.properties)
	craftercms.phantomjs.path : Location where phantom JS is installed
	craftercms.preview.deployer.path : Location where preview content is deployed
	craftercms.live.deployer.path : Location where live content is deployed
	Server URL's and credentials
	Website Name and Title
	Pages to Edit and content type
Run required testcases to run above scenarios
	Location: crafter-selenium-tests/src/test/java/org/craftercms/web/