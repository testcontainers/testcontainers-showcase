pipeline {
    agent any

    triggers {
            pollSCM('* * * * *')
    }

    stages {
        stage('Unit Test') {
            steps {
                sh './mvnw verify'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
    }
}