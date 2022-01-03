package com.learn.californium.server;

import org.eclipse.californium.core.CoapServer;



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
 */
public class TestObserverMain_Mwe  {
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//
	    String resourceName     = "hello_observer";			// resourceName 	vs topic
	    String brokerAddress  	= "127.0.0.1";				// broker address
	    int serverPort			= 1883;						// server port 		vs broker port
	    String clientId     	= "JavaSample_sender";		// client Id
	    String content     	 	= "你好";
	    //
		CoapServer server = new CoapServer(serverPort);
		Com_MyObserverResource_Con_Mwe myobResc1 = new Com_MyObserverResource_Con_Mwe(resourceName);
		//------------------------operate server-------------------------------------
		server.add(myobResc1);
		server.start(); // does all the magic
		//----------------------------- give some time to run ------------------------
		// 停留一段时间 让server继续运行
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// destroy server
		// because the resource use the timer
		server.destroy();
		System.out.println("destroy the server and stop the resource timer finished!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}

	
	


}