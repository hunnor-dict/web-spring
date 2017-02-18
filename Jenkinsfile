node {

	stage ('Checkout') {
		checkout scm
	}

	stage ('Build') {
		withMaven(maven: 'Default') {
			sh 'mvn clean package'
		}
	}

	stage ('QA') {
		withSonarQubeEnv('Default') {
			withMaven(maven: 'Default') {
				sh 'mvn ${SONAR_MAVEN_GOAL}'
			}
		}
	}

}
