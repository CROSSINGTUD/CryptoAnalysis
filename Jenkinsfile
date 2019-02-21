pipeline {
    agent any
    stages {

        stage('Build') {
            steps {
                sh 'cd CryptoAnalysis; mvn clean compile'
            }
        }

	    stage('Test') {
	        steps {
	            sh 'cd CryptoAnalysis; mvn test'
	        }
		}


		stage('Deploy'){
		    when { 
		    	branch 'master'
			}
	        steps {
				configFileProvider(
	        		[configFile(fileId: 'd8345989-7f12-4d8f-ae12-0fe9ce025188', variable: 'MAVEN_SETTINGS')]) {
	      		  		sh 'cd CryptoAnalysis; mvn -s $MAVEN_SETTINGS clean deploy -DskipTests'
				}
	        }
		}

    }
}