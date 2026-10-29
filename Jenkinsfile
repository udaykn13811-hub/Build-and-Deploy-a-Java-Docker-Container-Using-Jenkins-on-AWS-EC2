
pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        APP_NAME = 'java-docker-demo'
        APP_PORT = '8081'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Check Tools') {
            steps {
                sh '''
                    set -eu
                    java -version
                    mvn -version
                    docker --version
                '''
            }
        }

        stage('Build Java Application') {
            steps {
                sh 'mvn -B clean package'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh '''
                    set -eu
                    docker build \
                      -t ${APP_NAME}:${BUILD_NUMBER} \
                      -t ${APP_NAME}:latest .
                '''
            }
        }

        stage('Deploy Container') {
            steps {
                sh '''
                    set -eu

                    docker rm -f ${APP_NAME} || true

                    docker run -d \
                      --name ${APP_NAME} \
                      --restart unless-stopped \
                      -p ${APP_PORT}:${APP_PORT} \
                      -e PORT=${APP_PORT} \
                      ${APP_NAME}:${BUILD_NUMBER}
                '''
            }
        }

        stage('Verify Application') {
            steps {
                sh '''
                    set -eu

                    for attempt in $(seq 1 15); do
                        if curl --fail --silent \
                          http://127.0.0.1:${APP_PORT}/; then
                            echo "Application is responding."
                            exit 0
                        fi

                        sleep 2
                    done

                    echo "Application health check failed."
                    docker logs ${APP_NAME}
                    exit 1
                '''
            }
        }
    }

    post {
        success {
            echo 'SUCCESS: Java application built and deployed in Docker.'
        }

        failure {
            echo 'FAILED: Review the stage logs and Docker container logs.'
        }

        always {
            sh 'docker images java-docker-demo || true'
        }
    }
}
