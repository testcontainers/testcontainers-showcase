pipeline {
   agent any

   triggers { pollSCM 'H/2 * * * *' } // poll every 2 mins

   stages {
       stage('Build') {
           steps {
               sh './mvnw verify'
           }
       }
   }
}
