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

		stage('Performance') {
            environment {
                 GOOGLE_SHEET_CREDS = credentials("GOOGLE_SHEET_CREDENTIALS")
            }
            steps {
                script{
                    def scmVars = checkout([
                        $class: 'GitSCM',
                        branches: [[name: '*/develop'], [name: '*/master']],
                        userRemoteConfigs: [[url: 'https://github.com/CROSSINGTUD/CryptoAnalysis.git/']],
                    ])
                    env.GIT_COMMIT = scmVars.GIT_COMMIT
                    env.GIT_BRANCH = scmVars.GIT_BRANCH
                    env.GIT_URL = scmVars.GIT_URL
                }
                sh 'cd CryptoAnalysis; mvn -Dtest=PerformanceTest test -DcommitId=${GIT_COMMIT} -DbranchName=${GIT_BRANCH} -DgitUrl=${GIT_URL} -DgoogleSheetCredentials=${GOOGLE_SHEET_CREDS}'
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
