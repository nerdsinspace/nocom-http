#!groovy

node {
  try {
    // Checkout the proper revision into the workspace.
    stage('checkout') {
      checkout scm
    }

    // write dummy sensitive file
    writeFile file: 'sensitive.properties', text: '''postgres.host=localhost
postgres.port=5432
postgres.database=ci
postgres.user=ci
postgres.password=none
'''

    // Execute `cibuild` wrapped within a plugin that translates
    // ANSI color codes to something that renders inside the Jenkins
    // console.
    stage('cibuild') {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        sh './scripts/cibuild'
        archiveArtifacts artifacts: 'dist/*', fingerprint: true
      }
    }

    withCredentials([usernamePassword(credentialsId: 'nocom-http-gitlab-docker-registry', passwordVariable: 'GITLAB_PASSWORD', usernameVariable: 'GITLAB_USER')]) {
      stage('cipublish') {
        wrap([$class: 'AnsiColorBuildWrapper']) {
          sh './scripts/cipublish'
        }
      }
    }
  } catch (err) {
    // Re-raise the exception so that the failure is propagated to
    // Jenkins.
    throw err
  } finally {
    // Pass or fail, ensure that the services and networks
    // created by Docker Compose are torn down.
    sh 'docker-compose -f docker-compose.ci.yml down -v'
  }
}
