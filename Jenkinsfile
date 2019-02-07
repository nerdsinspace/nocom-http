#!groovy

// Reference the GitLab connection name from your Jenkins Global configuration (http://JENKINS_URL/configure, GitLab section)
properties([gitLabConnection('nocom-http')])

node {
	try {
	    // Update the commit status in GitLab.
	    updateGitlabCommitStatus name: 'build', state: 'running'
	
	    // Checkout the proper revision into the workspace.
		stage('checkout') {
			checkout scm 
		}
		
        // Execute `update` wrapped within a plugin that translates
        // ANSI color codes to something that renders inside the Jenkins
        // console.
        stage('update') {
            wrap([$class: 'AnsiColorBuildWrapper']) {
                sh './scripts/update'
            }
        }
    
        // Execute `cibuild` wrapped within a plugin that translates
        // ANSI color codes to something that renders inside the Jenkins
        // console.
        stage('cibuild') {
            wrap([$class: 'AnsiColorBuildWrapper']) {
                sh './scripts/cibuild'
                archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true 
            }
        }		
        stage('cipublish') {
            wrap([$class: 'AnsiColorBuildWrapper']) {
                withCredentials([usernamePassword(credentialsId: 'nocom-gitlab-registry', usernameVariable: 'GITLAB_USER', passwordVariable: 'GITLAB_PASSWORD')]) {
                    sh './scripts/cipublish'
                }
            }
        }
        stage('deploy') {
            wrap([$class: 'AnsiColorBuildWrapper']) {
                withCredentials([
                    usernamePassword(credentialsId: 'nocom-gitlab-registry', usernameVariable: 'GITLAB_DEPLOY_TOKEN_USERNAME', passwordVariable: 'GITLAB_DEPLOY_TOKEN_PASSWORD'),
                    sshUserPrivateKey(credentialsId: 'linuxauto-private-key-nhack', keyFileVariable: 'SSH_PRIVATE_KEY_PATH', passphraseVariable: 'SSH_PRIVATE_KEY_PASSWORD', usernameVariable: 'SSH_USERNAME')
                ]) {
                    sh './scripts/deploy'
                }
            }
        }
	} catch (err) {
	    // Update the commit status in GitLab.
	    updateGitlabCommitStatus name: 'build', state: 'failed'
	    
	    // Re-raise the exception so that the failure is propagated to
	    // Jenkins.
	    throw err
	} finally {
	    // Pass or fail, ensure that the services and networks
	    // created by Docker Compose are torn down.
	    sh 'docker-compose -f docker-compose.ci.yml down -v'
	    
	    // Update the commit status in GitLab.
	    updateGitlabCommitStatus name: 'build', state: 'success'
	}
}
