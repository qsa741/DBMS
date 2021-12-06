package com.jySystem.common.config;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.ha.session.ClusterSessionListener;
import org.apache.catalina.ha.session.DeltaManager;
import org.apache.catalina.ha.session.JvmRouteBinderValve;
import org.apache.catalina.ha.tcp.ReplicationValve;
import org.apache.catalina.ha.tcp.SimpleTcpCluster;
import org.apache.catalina.tribes.group.GroupChannel;
import org.apache.catalina.tribes.group.interceptors.MessageDispatchInterceptor;
import org.apache.catalina.tribes.group.interceptors.TcpFailureDetector;
import org.apache.catalina.tribes.membership.McastService;
import org.apache.catalina.tribes.transport.ReplicationTransmitter;
import org.apache.catalina.tribes.transport.nio.NioReceiver;
import org.apache.catalina.tribes.transport.nio.PooledParallelSender;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatClusterConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory>{
	
	@Override
	public void customize(TomcatServletWebServerFactory factory) {
		factory.addContextCustomizers(new TomcatClusterContextCustomizer());
	}

}

class TomcatClusterContextCustomizer implements TomcatContextCustomizer {
	
	@Override
	public void customize(Context context) {
		context.setDistributable(true);
		
		DeltaManager manager = new DeltaManager();
		manager.setExpireSessionsOnShutdown(false);
		manager.setNotifyListenersOnReplication(true);
		context.setManager(manager);
		
		configureCluster((Engine)context.getParent().getParent());
	}
	
	private void configureCluster(Engine engine) {
		SimpleTcpCluster cluster = new SimpleTcpCluster();
		cluster.setChannelSendOptions(6);
		
		GroupChannel channel = new GroupChannel();
		
		McastService mcastService = new McastService();
		mcastService.setAddress("228.0.0.4");
		mcastService.setPort(45560);
		mcastService.setFrequency(500);
		mcastService.setDropTime(3000);
		channel.setMembershipService(mcastService);
		
		NioReceiver receiver = new NioReceiver();
		receiver.setAddress("auto");
		receiver.setPort(4001);
		receiver.setAutoBind(100);
		receiver.setMaxThreads(6);
		channel.setChannelReceiver(receiver);
		
		ReplicationTransmitter sender = new ReplicationTransmitter();
		sender.setTransport(new PooledParallelSender());
		channel.setChannelSender(sender);
		
		channel.addInterceptor(new TcpFailureDetector());
		channel.addInterceptor(new MessageDispatchInterceptor());
		
		cluster.setChannel(channel);
		cluster.addValve(new ReplicationValve());
		cluster.addValve(new JvmRouteBinderValve());
		cluster.addClusterListener(new ClusterSessionListener());
		
		engine.setCluster(cluster);
	}
}
