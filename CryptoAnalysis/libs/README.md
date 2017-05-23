SETUP
=====

The jars in this folder have to be installed to the local repository.

In general a jar is installed with the following command:


``mvn install:install-file -DgroupId={groupId}  -DartifactId={artifactId} -Dversion={version} -Dpackaging=jar -Dfile={pathToJar}``
    
    
Below are the concrete commands to be executed.

``mvn install:install-file -DgroupId=de.fraunhofer.iem  -DartifactId=boomerang -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar -Dfile=libs\boomerang-0.0.1-SNAPSHOT.jar``

``mvn install:install-file -DgroupId=de.fraunhofer.iem  -DartifactId=IDEViz -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar -Dfile=libs\IDEViz-0.0.1-SNAPSHOT.jar``

``mvn install:install-file -DgroupId=heros  -DartifactId=heros-ideal -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar -Dfile=libs\heros-ideal-0.0.1-SNAPSHOT.jar``

``mvn install:install-file -DgroupId=soot  -DartifactId=soot -Dversion=trunk -Dpackaging=jar -Dfile=libs\soot-trunk.jar``
 
``mvn install:install-file -DgroupId=ideal  -DartifactId=ideal -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar -Dfile=libs\ideal-0.0.1-SNAPSHOT.jar``
		
		
For adding test dependencies, we also need the following to be installed.

		
``mvn install:install-file -DgroupId=de.fraunhofer.iem  -DartifactId=boomerang -Dversion=0.0.1-SNAPSHOT -Dpackaging=test-jar -Dclassifier=tests -Dfile=libs\boomerang-0.0.1-SNAPSHOT-tests.jar``

``mvn install:install-file -DgroupId=ideal  -DartifactId=ideal -Dversion=0.0.1-SNAPSHOT -Dpackaging=test-jar -Dclassifier=tests -Dfile=libs\ideal-0.0.1-SNAPSHOT-tests.jar``

    