if (currentBuild.getBuildCauses().toString().contains('indexing')) {
  print "INFO: Build skipped due to trigger being Branch Indexing"
  currentBuild.result = 'ABORTED' // optional, gives a better hint to the user that it's been skipped, rather than the default which shows it's successful
  return
}

pipeline {
  agent {
    node {
      label 'nodejs'
    }

  }
  environment {
    REGISTRY = 'har.gagogroup.cn'
    KUBE_NAMESPACE = 'basic-platform'
    DOCKERHUB_NAMESPACE = 'basic-platform'
    DOCKER_USERNAME = 'basic-platform'
    DOCKER_PASSWORD = 'Gago@@123'
    APP_NAME = 'gago-fast-web'
    APP_DESC = '快速开发平台-前端'
    KUBECONFIG_CREDENTIAL_ID = 'kube'
    QKE_DOCKER_CREDENTICAL = 'harbor'
    DOCKERFILE = 'web.Dockerfile'
    TIME_VAL = sh(script: 'date -d "8 hours" "+%Y%m%d%H%M"',returnStdout: true).trim()
    TIME_VERSION =  "${GIT_BRANCH}-${TIME_VAL}"
  }

  stages {
    stage('拉取代码') {
      steps {
        checkout(scm)
      }
    }
    stage('构建镜像并推送至仓库') {
	  steps {
		withCredentials([usernamePassword(credentialsId : "$QKE_DOCKER_CREDENTICAL" ,passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,)]){
		  container('nodejs') {
			sh 'docker build -f $DOCKERFILE -t $REGISTRY/$DOCKERHUB_NAMESPACE/$APP_NAME:$TIME_VERSION .'
			sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
			sh 'docker push $REGISTRY/$DOCKERHUB_NAMESPACE/$APP_NAME:$TIME_VERSION'
		  }
		}
	  }
	}
    stage('部署测试环境') {
      steps {
        kubernetesDeploy(configs: 'deploy-web/**', enableConfigSubstitution: true, kubeconfigId: "$KUBECONFIG_CREDENTIAL_ID")
      }
    }

  }
}
