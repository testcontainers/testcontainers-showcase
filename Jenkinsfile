pipeline {
   agent any

   triggers { pollSCM 'H/2 * * * *' } // poll every 2 mins

    environment {
        DOCKER_HOST = 'tcp://docker:2376'
    }

   stages {
       stage('Build') {
           steps {
               sh 'java -version'
               sh 'docker version'
               sh './mvnw --ntp verify'
           }
       }
   }
}
