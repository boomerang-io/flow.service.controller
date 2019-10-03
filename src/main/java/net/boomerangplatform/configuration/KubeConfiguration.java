package net.boomerangplatform.configuration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.auth.ApiKeyAuth;
import io.kubernetes.client.util.Config;

@Configuration
public class KubeConfiguration {

	@Value("${kube.api.base.path}")
	private String kubeApiBasePath;

	@Value("${kube.api.token}")
	private String kubeApiToken;
	
	@Value("${kube.api.debug}")
	private String kubeApiDebug;
	
	@Value("${kube.api.type}")
	private String kubeApiType;
	
	@Value("${kube.api.timeout}")
	private Integer kubeApiTimeOut;
	
	@Bean
	public ApiClient connectToKube() {
//		https://github.com/kubernetes-client/java/blob/master/util/src/main/java/io/kubernetes/client/util/Config.java#L57
			
		ApiClient defaultClient = null;
		if (kubeApiType.equals("cluster")) {
			try {
				defaultClient = Config.fromCluster().setVerifyingSsl(false).setDebugging(kubeApiDebug.isEmpty() ? false : Boolean.valueOf(kubeApiDebug));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			defaultClient = io.kubernetes.client.Configuration.getDefaultApiClient().setVerifyingSsl(false).setBasePath(kubeApiBasePath).setDebugging(kubeApiDebug.isEmpty() ? false : Boolean.valueOf(kubeApiDebug));
		}
		
		if (!kubeApiToken.isEmpty()) {
			ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("BearerToken");
			apiKeyAuth.setApiKey(kubeApiToken);
			apiKeyAuth.setApiKeyPrefix("Bearer");
		}

		defaultClient.getHttpClient().setReadTimeout(kubeApiTimeOut.longValue(), TimeUnit.SECONDS);
		io.kubernetes.client.Configuration.setDefaultApiClient(defaultClient);
		return defaultClient;

//		ApiClient defaultClient = Config.fromToken(kubeApiBasePath, kubeApiToken, false).setVerifyingSsl(false).setDebugging(kubeApiDebug.isEmpty() ? false : Boolean.valueOf(kubeApiDebug));
//		defaultClient.getHttpClient().setReadTimeout(60, TimeUnit.SECONDS);
	}
}
