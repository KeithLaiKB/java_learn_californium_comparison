package com.learn.californium.server_dtls.psk;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.util.Date;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.config.Configuration.DefinitionsProvider;
import org.eclipse.californium.elements.util.SslContextUtil;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.CertificateType;
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedMultiPskStore;
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedSinglePskStore;
import org.eclipse.californium.scandium.dtls.x509.SingleCertificateProvider;
import org.eclipse.californium.scandium.dtls.x509.StaticNewAdvancedCertificateVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.learn.californium.server_dtls.Com_MyObserverResource_Con_Mwe;



/**
 * 
 * 
 * <p>
 * 							description:																</br>	
 * &emsp;						MWE means minimal working example										</br>
 * &emsp;						MWE 意思就是  简化的例子														</br>
 * &emsp;						for testing the observer												</br>
 * 																										</br>
 * 
 * </p>
 *
 *
 * @author laipl
 * 
 * psk可以不用 公私钥  直接自己写好 允许的客户端钥匙就可以  server这里用AdvancedMultiPskStore 记录允许客户端使用的 presharedkey, 
 * 例如用户 可以 使用不同的 preshared key
 * 
 * pre-shared key 不需要那些自己做的公私钥的文件
 *
 */
public class TestObserverMain_Mwe  {
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//
		long startTime			=System.nanoTime();   		//nanoTime 会比 currentTimeMillis更加精确  
		System.out.println(new Date(System.currentTimeMillis()));
		//
		//
	    String resourceName     = "Resource1";			// resourceName 	vs topic
	    String brokerAddress  	= "127.0.0.1";				// broker address
	    int serverPort			= 5656;						// server port 		vs broker port
	    String clientId     	= "JavaSample_sender";		// client Id
	    String content     	 	= "你好";
	    
	    
	    
	    
	    
		
		int DEFAULT_PORT = 5684;
		final Logger LOG = LoggerFactory.getLogger(TestObserverMain_Mwe.class.getName());
		
		final String KEY_STORE_LOCATION = "mycerts/my_own/mykeystore.jks";
		//private static final char[] KEY_STORE_PASSWORD = "myKeyStoreAdministrator".toCharArray();
		final char[] KEY_STORE_PASSWORD = "SksOneAdmin".toCharArray();
		final String TRUST_STORE_LOCATION = "mycerts/my_own/mykeystore_truststore.jks";
		//private static final char[] TRUST_STORE_PASSWORD = "myTrustStoreAdministrator".toCharArray();
		final char[] TRUST_STORE_PASSWORD = "StsOneAdmin".toCharArray();
		
		
		DTLSConnector dtlsConnector;
		

		//psk
		final String PSK_IDENTITY = "password";
		final byte[] PSK_SECRET = "sesame".getBytes();

		final String OPEN_PSK_IDENTITY = "Client_identity";
		final byte[] OPEN_PSK_SECRET = "secretPSK".getBytes();
				

		try {
			//------------------------prepare for dtls-------------------------------------		
			DefinitionsProvider DEFAULTS = new DefinitionsProvider() {

				@Override
				public void applyDefinitions(Configuration config) {
					config.set(DtlsConfig.DTLS_CONNECTION_ID_LENGTH, 6);
					config.set(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY, false);
				}

			};
			
			

			// load the key store
			String myusr_path = System.getProperty("user.dir");
			//注意 虽然我创建的时候是有 大小写 mykeystoreAlias
			//但 貌似 使用的时候 在这里需要全部小写， 才能对应的到
			
			
			SslContextUtil.Credentials serverCredentials = SslContextUtil.loadCredentials(
					myusr_path + "\\" + KEY_STORE_LOCATION, "mykeystorealias", KEY_STORE_PASSWORD,
					KEY_STORE_PASSWORD);
			Certificate[] trustedCertificates = SslContextUtil.loadTrustedCertificates(
					myusr_path + "\\" + TRUST_STORE_LOCATION, "mytruststorealias", TRUST_STORE_PASSWORD);
			
			Configuration configuration = Configuration.createWithFile(Configuration.DEFAULT_FILE, "DTLS example server", DEFAULTS);
			DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder(configuration);
		
			builder.setAddress(new InetSocketAddress(DEFAULT_PORT));
			
			
			

			//psk
			AdvancedMultiPskStore pskStore = new AdvancedMultiPskStore();
			// put in the PSK store the default identity/psk for tinydtls tests
			//pskStore.setKey("Client_identity", "secretPSK".getBytes());
			pskStore.setKey(PSK_IDENTITY, PSK_SECRET);
			pskStore.setKey(OPEN_PSK_IDENTITY, OPEN_PSK_SECRET);
			
			//builder.setAsList(DtlsConfig.DTLS_CIPHER_SUITES, CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA256, CipherSuite.TLS_PSK_WITH_AES_128_CCM_8);
			//builder.setAsList(DtlsConfig.DTLS_CIPHER_SUITES, CipherSuite.TLS_PSK_WITH_AES_128_CCM_8);
			builder.setAdvancedPskStore(pskStore);

			
			
			//builder.setCertificateIdentityProvider(new SingleCertificateProvider(serverCredentials.getPrivateKey(), serverCredentials.getCertificateChain(), CertificateType.RAW_PUBLIC_KEY, CertificateType.X_509));
			//因为我自己生成的证书 我是 RAW_PUBLIC_KEY 所以 我可以不加上 CertificateType.X_509, 我觉得 它多加一个 CertificateType.X_509 应该是为了 以防 例如我们证书不是  RAW_PUBLIC_KEY 他就考虑你认为可能的的证书类型 
			//builder.setCertificateIdentityProvider(new SingleCertificateProvider(serverCredentials.getPrivateKey(), serverCredentials.getCertificateChain(), CertificateType.));
			
			//builder.setAdvancedCertificateVerifier(StaticNewAdvancedCertificateVerifier.builder().setTrustedCertificates(trustedCertificates).setTrustAllRPKs().build());
			
			
			
			dtlsConnector = new DTLSConnector(builder.build());
		
			
			
			// ref: californium/demo-apps/cf-secure/src/main/java/org/eclipse/californium/examples/SecureServer.java /
			//------------------------------------for observe---------------------------
			//去掉这句
			//dtlsConnector.setRawDataReceiver(new MyRawDataChannelImpl(dtlsConnector));
			CoapEndpoint.Builder coapBuilder = new CoapEndpoint.Builder()
					.setConfiguration(configuration)
					.setConnector(dtlsConnector);
			// ref: californium/demo-apps/cf-secure/src/main/java/org/eclipse/californium/examples/SecureServer.java /
			//------------------------------------for observe---------------------------
			CoapServer server = new CoapServer();
			Com_MyObserverResource_Con_Mwe myobResc1 = new Com_MyObserverResource_Con_Mwe("Resource1");
			//--------------------------------------------------------------------------
			server.addEndpoint(coapBuilder.build());
			server.add(myobResc1);
			server.start(); // does all the magic
		
			
			
			
			//--------------------------------------------------------------------	
			//------------------------running-------------------------------------	

			while(myobResc1.resourceFinished==false) {
				// 停留一段时间 让server继续运行, 这里用 sleep 是为了减少loop的时间
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//----------------------------- give some time to run ------------------------
			// 因为它和main是不同线程的, 所以我要让我的main 等到 resource发布了我所需要测量的 数据报个数 
			// 才去stop resource
			// 然后
			// 才去destroy我们的server

			//---------------------------------------------------------------------------
			//
			//
			// 因为我们的resource用了 timer,
			// 所以我们 destroy 了server以后 , resource还是在运行的
			// in my opinion, we should apply a standard process
			// so we need to stop the resource
			myobResc1.stopMyResource();
			//
			//
			// 再让Main函数 运行一段时间, 我们可以发现resource没有输出了, 也就意味着 确实结束了
			// 其实 这后面的可以不用, 只是用来判断resource是否结束了,
			// 如果resource 没关掉, 就可以 在这段时间内 发现有resource的输出
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// destroy server
			// because the resource use the timer
			server.destroy();
			System.out.println("destroy the server and stop the resource timer finished!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		
		
	        long endTime			=System.nanoTime();   		//nanoTime 会比 currentTimeMillis更加精确
	        long usedTime			= endTime - startTime;
	        System.out.println("usedTime:"+usedTime);			//初步测试 貌似californium 比 hivemq要快
	        // (51839933100-51426617900)/1000000000 = 0.4133152			10条
	        // (152356597100-151234731200)/1000000000 = 1.1218659		30条
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (GeneralSecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	
	


}