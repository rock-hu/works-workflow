## works-batch ##

### works-batch ###

### What ###
	
### Why ###
	

### How ###

### Reuired Tool ###
		1. Jdk 1.6
		2. Spring Tool Suite
			EGit(Eclipse Market)/Propedit(http://propedit.sourceforge.jp/eclipse/updates/)
		3. Maven 2
		4. Git
		5. Spring Batch 2.x
		6. PostgreSQL
		
###	Running locally ###
		1. Make sure project no compile error
		2. mvn install to download dependency via Maven

###	Building and Deploying ###		
		1. Maven
			mvn clean install -Dmaven.test.skip=true -X > install.log
			mvn dependency:copy-dependencies
			mvn dependency:tree > tree.txt
								
###	Development guidelines ###
		1. Clone it (git clone https://Rock-Hu@bitbucket.org/Rock-Hu/one-transact.git)
		2. Import project into your eclipse workspace
		3. Write your code and unit tests
		4. Ensure all tests still pass (grunt test)
		5. Checking data change is expected
		6. Commit & Push your changes			