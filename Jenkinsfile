pipeline {
   agent any

   triggers { pollSCM 'H/2 * * * *' } // poll every 2 mins

   stages {
       stage('Build') {
           steps {
               sh 'java -version'
               sh "echo $DOCKER_HOST"
               sh 'docker version'
               sh './mvnw --ntp verify'
           }
       }
   }
}
