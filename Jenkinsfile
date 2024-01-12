pipeline {
   agent any

   triggers { pollSCM 'H/2 * * * *' } // poll every 2 mins

    environment {
        TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE="/var/run/docker.sock"
    }

   stages {
       stage('Build') {
           steps {
               sh 'java -version'
               sh './mvnw --ntp verify'
           }
       }
   }
}
