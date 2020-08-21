pipeline {
	agent any

	tools { 
		jdk 'Oracle JDK 9' 
	}

	stages {

		stage('Build') {
			steps {
				sh 'mvn clean compile -U'
			}
		}

		stage('Test') {
			steps {
				sh 'mvn test -U'
			}
			post {  
				always {
					junit 'shippable/testresults/*.xml'
				}
			}
		}


		stage('Deploy'){
			when { 
				anyOf { branch 'master'; branch 'develop' } 
			}
			steps {
				configFileProvider(
				[configFile(fileId: '1d7d4c57-de41-4f04-8e95-9f3bb6382327', variable: 'MAVEN_SETTINGS')]) {
					sh 'cd CryptoAnalysis; mvn -s $MAVEN_SETTINGS clean deploy -DskipTests'
					sh 'cd CryptoAnalysis-Android; mvn -s $MAVEN_SETTINGS clean deploy -DskipTests'
				}
			}
		}

	}
}
