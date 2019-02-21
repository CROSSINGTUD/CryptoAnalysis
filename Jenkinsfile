pipeline {
    agent any
    stages {

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

	    stage('Test') {
	        steps {
	            sh 'mvn test'
	        }
		}


		stage('Deploy'){
		    when { 
		    	branch 'master'
			}
	        steps {
				configFileProvider(
	        		[configFile(fileId: 'd8345989-7f12-4d8f-ae12-0fe9ce025188', variable: 'MAVEN_SETTINGS')]) {
	      		  		sh 'mvn -s $MAVEN_SETTINGS clean deploy -DskipTests'
				}
	        }
		}

    }
}