pipeline {
    agent any

    triggers {
            pollSCM('* * * * *')
    }

    stages {
        stage('TCC SetUp') {
            steps {
                echo "The TC_Token is ${env.TC_CLOUD_TOKEN}"
                script {
                    sh "curl -fsSL https://get.testcontainers.cloud/bash | sh "
                }
            }
        }
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