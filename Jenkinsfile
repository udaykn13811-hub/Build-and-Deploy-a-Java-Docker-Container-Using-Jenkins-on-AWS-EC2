
pipeline {
    agent any

    environment {
        APP_NAME = 'java-docker-demo'
        IMAGE_NAME = 'java-docker-demo:latest'
        CONTAINER_NAME = 'java-docker-demo'
        APP_PORT = '8081'
        JAVA_HOME = '/usr/lib/jvm/java-21-amazon-corretto.x86_64'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout Source Code') {
            steps {
                echo 'Checking out source code from GitHub...'
                checkout scm
            }
        }

        stage('Verify Java and Maven') {
            steps {
                sh '''
                    echo "Java version:"
                    java -version

                    echo "Maven version:"
                    mvn -version

                    echo "Docker version:"
                    docker --version
                '''
            }
        }

        stage('Build Java Application') {
            steps {
                sh '''
                    set -e
                    mvn -B clean package
                    ls -lh target/
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                sh '''
                    set -e
                    docker build -t "$IMAGE_NAME" .
                    docker image inspect "$IMAGE_NAME"
                '''
            }
        }

        stage('Deploy Docker Container') {
            steps {
                sh '''
                    set -e

                    # Stop and remove the previous container if it exists
                    if docker ps -a --format '{{.Names}}' | grep -qx "$CONTAINER_NAME"; then
                        docker stop "$CONTAINER_NAME" || true
                        docker rm "$CONTAINER_NAME" || true
                    fi

                    # Start the newly built container
                    docker run -d \
                        --name "$CONTAINER_NAME" \
                        --restart unless-stopped \
                        -p "$APP_PORT:$APP_PORT" \
                        "$IMAGE_NAME"

                    echo "Container deployed successfully."
                    docker ps
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                    set -e

                    echo "Waiting for application startup..."
                    sleep 5

                    echo "Testing application endpoint..."
                    curl --fail --show-error \
                        "http://127.0.0.1:$APP_PORT/"

                    echo ""
                    echo "Application deployment verified."
                '''
            }
        }
    }

    post {
        success {
            echo 'SUCCESS: Java application built and deployed in Docker.'
            echo 'Access the application at http://YOUR_EC2_PUBLIC_IP:8081'
        }

        failure {
            echo 'FAILURE: Check the Jenkins Console Output for the error.'
            sh 'docker logs java-docker-demo || true'
        }

        always {
            echo 'Pipeline execution completed.'
        }
    }
}
