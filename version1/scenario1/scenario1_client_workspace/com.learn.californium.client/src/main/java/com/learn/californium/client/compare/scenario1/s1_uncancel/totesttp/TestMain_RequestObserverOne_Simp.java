package com.learn.californium.client.compare.scenario1.s1_uncancel.totesttp;


import java.io.IOException;
import java.util.Scanner;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.AddressEndpointContext;
import org.eclipse.californium.elements.EndpointContext;
import org.eclipse.californium.elements.exception.ConnectorException;

/**
 * 
 * 
 * <p>
 * 							description:																			</br>	
 * &emsp;						client to observe																	</br>
 * &emsp;						in this demo you don't have too much selection operation such as delete				</br>
 * &emsp;						before shutting down, it does not use cancel subscription here						</br>
 * 
 * 							ref:																					</br>	
 * &emsp;						californium/demo-apps/cf-plugtest-client/src/main/java/org/eclipse/californium/plugtests/PlugtestClient.java  	</br>	
 *  																												</br>
 * 
 * </p>
 * @author laipl
 *
 */
public class TestMain_RequestObserverOne_Simp {
	private static int receivedMessageNum 					= 0;
	private static int expectedReceivedMessageNum			= 10;

	public static void main(String[] args) {
    	//
    	String myuri1 	     					= "coap://192.168.239.137:5683/Resource1";
    	CoapObserveRelation coapObRelation1		= null;
    	CoapHandler myObserveHandler 			= null;
    	//
    	//
    	// new client
    	CoapClient client = new CoapClient(myuri1);
        // set handler

		//
    	// set handler for observer method, because observe method needs asynchronous operation
		myObserveHandler = new CoapHandler() {

            @Override
            public void onLoad(CoapResponse response) {
            	System.out.println(response.getResponseText());
            	receivedMessageNum = receivedMessageNum +1;
            }

            @Override
            public void onError() {
            }
        };

        //
       
        /*
        // 测试 cf 的单独get请求   是否可以带 payload 
        //coapObRelation1 = client.observe(myObserveHandler);
    	Request rq1 = new Request(Code.GET, Type.CON).setPayload("cs");
    	//Request rq1 = new Request(Code.PUT, Type.CON).setPayload("cs");
    	rq1.setURI(myuri1);
    	rq1.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN);
    	rq1.send();
    	*/
        

		/*
		//测试cf 的单独get请求(甚至可以在   不带resource名字(自己把resource名字 从uri中移掉 从而只留下 addr 和 port)) 可以带多个option
		Request rq1 = new Request(Code.GET, Type.CON);						//测试单独get请求 是否可以带 多个option
    	rq1.setURI(myuri1);
    	rq1.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN);
    	rq1.getOptions().setMaxAge(10L);
    	rq1.send();
		*/
        

        /*
        //测试observe 当中 Get_CON是否可以带 多个option
        Request rq1 = new Request(Code.GET, Type.CON);
        rq1.setObserve();
    	rq1.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN);		//测试observe 当中 Get_CON是否可以带 多个option
		client.observe(rq1, myObserveHandler);
    	*/
		
		
		
		
        coapObRelation1 = client.observe(myObserveHandler);
        //
        //
        //---------------------------------------------
		// 停留一段时间 让server继续运行
        while(receivedMessageNum < expectedReceivedMessageNum) {
        	try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        //
		//------------------- close --------------------------
		// cancel subscription
		// coapObRelation1.proactiveCancel();
		// shutdown client
        client.shutdown();
        //
		//		
    }

}
/*
//测试cf 的单独get请求  可以带多个option
try {
	client.get(1);
} catch (ConnectorException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
} catch (IOException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
}*/