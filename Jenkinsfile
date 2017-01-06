node {

	stage ('Checkout') {
		checkout scm
	}

	stage ('Build') {
		sh 'mvn clean package'
	}

	stage ('QA') {
		withSonarQubeEnv('default') {
			sh 'mvn ${SONAR_MAVEN_GOAL}'
		}
	}

}
